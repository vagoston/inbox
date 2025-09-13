package com.inbox.photobox

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PhotoBoxViewModelTest {
    
    @Test
    fun `initial state should be not signed in`() {
        val viewModel = PhotoBoxViewModel()
        val initialState = viewModel.uiState.value
        
        assertFalse("Initial state should show user as not signed in", initialState.isSignedIn)
        assertEquals("Initial user email should be empty", "", initialState.userEmail)
        assertEquals("Initial message should be empty", "", initialState.message)
        assertNull("Initial photo URI should be null", initialState.lastPhotoUri)
        assertFalse("Initial uploading state should be false", initialState.isUploading)
    }
    
    @Test
    fun `setMessage should update state message`() {
        val viewModel = PhotoBoxViewModel()
        val testMessage = "Test message"
        
        viewModel.setMessage(testMessage)
        
        assertEquals("Message should be updated", testMessage, viewModel.uiState.value.message)
    }
}