package com.inbox.photobox

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoBoxApp(
    viewModel: PhotoBoxViewModel = viewModel()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    
    // Initialize Google Sign-In on first composition
    LaunchedEffect(Unit) {
        viewModel.initializeGoogleSignIn(context)
    }
    
    // Create image file URI
    val createImageUri = remember {
        {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFile = File(
                context.getExternalFilesDir(null),
                "PhotoBox_$timeStamp.jpg"
            )
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                imageFile
            )
        }
    }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                viewModel.setLastPhotoUri(uri)
                viewModel.setMessage("Photo taken successfully")
            }
        }
    }
    
    // Observe the view model state
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Display current message
        if (uiState.message.isNotEmpty()) {
            Card {
                Text(
                    text = uiState.message,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        if (!uiState.isSignedIn) {
            // Show sign-in button
            Button(
                onClick = { viewModel.signIn(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.sign_in_with_google))
            }
        } else {
            // Show signed-in user info
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.welcome),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = stringResource(R.string.signed_in_as, uiState.userEmail),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Camera button
            Button(
                onClick = {
                    if (cameraPermissionState.status.isGranted) {
                        val uri = createImageUri()
                        photoUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.take_photo))
            }
            
            // Show camera permission rationale if needed
            if (cameraPermissionState.status.shouldShowRationale) {
                Text(
                    text = stringResource(R.string.camera_permission_required),
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            // Upload button (only show if photo is taken)
            if (uiState.lastPhotoUri != null) {
                Button(
                    onClick = { viewModel.uploadToDrive(context) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isUploading
                ) {
                    if (uiState.isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(stringResource(R.string.upload_to_drive))
                }
            }
            
            // Sign out button
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = { viewModel.signOut() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.sign_out))
            }
        }
    }
}