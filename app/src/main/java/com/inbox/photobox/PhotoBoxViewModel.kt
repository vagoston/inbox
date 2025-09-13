package com.inbox.photobox

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PhotoBoxUiState(
    val isSignedIn: Boolean = false,
    val userEmail: String = "",
    val message: String = "",
    val lastPhotoUri: Uri? = null,
    val isUploading: Boolean = false
)

class PhotoBoxViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PhotoBoxUiState())
    val uiState: StateFlow<PhotoBoxUiState> = _uiState.asStateFlow()
    
    private lateinit var googleSignInClient: GoogleSignInClient
    private var driveService: Drive? = null
    private var signInLauncher: ActivityResultLauncher<Intent>? = null
    
    fun setSignInLauncher(launcher: ActivityResultLauncher<Intent>) {
        signInLauncher = launcher
    }
    
    fun initializeGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
        
        // Check if already signed in
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            _uiState.value = _uiState.value.copy(
                isSignedIn = true,
                userEmail = account.email ?: "",
                message = "Welcome back!"
            )
            setupDriveService(context)
        }
    }
    
    fun signIn(context: Context) {
        if (!::googleSignInClient.isInitialized) {
            initializeGoogleSignIn(context)
        }
        
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher?.launch(signInIntent) ?: run {
            _uiState.value = _uiState.value.copy(
                message = "Sign in launcher not initialized"
            )
        }
    }
    
    fun handleSignInResult(task: Task<GoogleSignInAccount>, context: Context) {
        try {
            val account = task.getResult()
            _uiState.value = _uiState.value.copy(
                isSignedIn = true,
                userEmail = account.email ?: "",
                message = "Successfully signed in!"
            )
            setupDriveService(context)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                message = "Sign in failed: ${e.message}"
            )
        }
    }
    
    private fun setupDriveService(context: Context) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            val credential = GoogleAccountCredential.usingOAuth2(
                context,
                listOf(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = account.account
            
            driveService = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential
            )
                .setApplicationName("PhotoBox")
                .build()
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                googleSignInClient.signOut()
                driveService = null
                _uiState.value = PhotoBoxUiState(message = "Signed out successfully")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Sign out failed: ${e.message}"
                )
            }
        }
    }
    
    fun setLastPhotoUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            lastPhotoUri = uri,
            message = "Photo captured successfully! Ready to upload."
        )
    }
    
    fun setMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }
    
    fun uploadToDrive(context: Context) {
        val uri = _uiState.value.lastPhotoUri ?: return
        val service = driveService ?: run {
            _uiState.value = _uiState.value.copy(
                message = "Please sign in first to upload photos"
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(
            isUploading = true,
            message = "Uploading photo to Google Drive..."
        )
        
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Create PhotoBox folder if it doesn't exist
                    val folderName = "PhotoBox"
                    val folderId = createOrGetFolder(service, folderName)
                    
                    // Upload the photo
                    val contentResolver = context.contentResolver
                    val inputStream = contentResolver.openInputStream(uri)
                    
                    val fileMetadata = File().apply {
                        name = "photo_${System.currentTimeMillis()}.jpg"
                        parents = listOf(folderId)
                    }
                    
                    val mediaContent = com.google.api.client.http.InputStreamContent(
                        "image/jpeg",
                        inputStream
                    )
                    
                    service.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute()
                }
                
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    message = "Photo uploaded to Google Drive successfully! Check your PhotoBox folder."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    message = "Upload failed: ${e.message}"
                )
            }
        }
    }
    
    private fun createOrGetFolder(service: Drive, folderName: String): String {
        // Check if folder already exists
        val result = service.files().list()
            .setQ("name='$folderName' and mimeType='application/vnd.google-apps.folder' and trashed=false")
            .setSpaces("drive")
            .execute()
        
        return if (result.files.isNotEmpty()) {
            result.files[0].id
        } else {
            // Create the folder
            val folderMetadata = File().apply {
                name = folderName
                mimeType = "application/vnd.google-apps.folder"
            }
            
            val folder = service.files().create(folderMetadata)
                .setFields("id")
                .execute()
            
            folder.id
        }
    }
}