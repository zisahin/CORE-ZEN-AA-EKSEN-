package com.bysoftware.aaeksen.presentation.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bysoftware.aaeksen.domain.model.ShortsFeedState
import com.bysoftware.aaeksen.domain.model.VideoInteractionType
import com.bysoftware.aaeksen.domain.model.VideoPlaybackState
import com.bysoftware.aaeksen.domain.model.VideoShort
import com.bysoftware.aaeksen.domain.usecase.GetYouTubeShortsUseCase
import com.bysoftware.aaeksen.domain.usecase.HandleVideoInteractionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShortsViewModel @Inject constructor(
    private val getYouTubeShortsUseCase: GetYouTubeShortsUseCase,
    private val handleVideoInteractionUseCase: HandleVideoInteractionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShortsUiState())
    val uiState: StateFlow<ShortsUiState> = _uiState.asStateFlow()

    private val _playbackState = MutableStateFlow(VideoPlaybackState())
    val playbackState: StateFlow<VideoPlaybackState> = _playbackState.asStateFlow()

    // Paging data flow for infinite scroll
    val videosFlow: Flow<PagingData<VideoShort>> = getYouTubeShortsUseCase()
        .cachedIn(viewModelScope)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                // Initial loading is handled by Paging3
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    /**
     * Handle video interaction (like, dislike, save, share, go to news)
     */
    fun onVideoInteraction(videoId: String, interactionType: VideoInteractionType) {
        viewModelScope.launch {
            try {
                val result = handleVideoInteractionUseCase(videoId, interactionType)
                
                result.fold(
                    onSuccess = { updatedVideo ->
                        // Update the current video in UI state if it matches
                        val currentState = _uiState.value
                        if (currentState.currentVideo?.id == videoId) {
                            _uiState.value = currentState.copy(
                                currentVideo = updatedVideo
                            )
                        }
                        
                        // Handle specific interaction types
                        when (interactionType) {
                            VideoInteractionType.SHARE -> {
                                // Trigger share action
                                _uiState.value = _uiState.value.copy(
                                    shareVideoId = videoId
                                )
                            }
                            VideoInteractionType.GO_TO_NEWS -> {
                                // Trigger navigation to news detail
                                _uiState.value = _uiState.value.copy(
                                    navigateToNewsId = videoId
                                )
                            }
                            else -> {
                                // Like, dislike, save are handled by the updated video state
                            }
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = error.message ?: "Interaction failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Interaction failed"
                )
            }
        }
    }

    /**
     * Update current video when user swipes
     */
    fun onVideoChanged(video: VideoShort) {
        _uiState.value = _uiState.value.copy(
            currentVideo = video,
            currentVideoIndex = _uiState.value.currentVideoIndex + 1
        )
        
        // Reset playback state for new video
        _playbackState.value = VideoPlaybackState()
    }

    /**
     * Update video playback state
     */
    fun updatePlaybackState(
        isPlaying: Boolean = _playbackState.value.isPlaying,
        currentPosition: Long = _playbackState.value.currentPosition,
        duration: Long = _playbackState.value.duration,
        isBuffering: Boolean = _playbackState.value.isBuffering,
        hasError: Boolean = _playbackState.value.hasError,
        errorMessage: String? = _playbackState.value.errorMessage
    ) {
        _playbackState.value = VideoPlaybackState(
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            isBuffering = isBuffering,
            hasError = hasError,
            errorMessage = errorMessage
        )
    }

    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        val currentState = _playbackState.value
        _playbackState.value = currentState.copy(
            isPlaying = !currentState.isPlaying
        )
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Clear share action
     */
    fun clearShareAction() {
        _uiState.value = _uiState.value.copy(shareVideoId = null)
    }

    /**
     * Clear navigation action
     */
    fun clearNavigationAction() {
        _uiState.value = _uiState.value.copy(navigateToNewsId = null)
    }

    /**
     * Trigger scroll to specific video index
     */
    fun onScrollToVideo(targetIndex: Int) {
        _uiState.value = _uiState.value.copy(scrollToIndex = targetIndex)
    }

    /**
     * Clear scroll action after it's handled
     */
    fun clearScrollAction() {
        _uiState.value = _uiState.value.copy(scrollToIndex = null)
    }

    /**
     * Handle system back press
     */
    fun onBackPressed(): Boolean {
        // Return false to let the system handle back press (exit shorts)
        return false
    }

    /**
     * Handle video error
     */
    fun onVideoError(error: String) {
        _playbackState.value = _playbackState.value.copy(
            hasError = true,
            errorMessage = error,
            isPlaying = false,
            isBuffering = false
        )
    }

    /**
     * Retry video loading
     */
    fun retryVideo() {
        _playbackState.value = VideoPlaybackState()
    }
}

/**
 * UI State for Shorts screen
 */
data class ShortsUiState(
    val currentVideo: VideoShort? = null,
    val currentVideoIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val shareVideoId: String? = null,
    val navigateToNewsId: String? = null,
    val scrollToIndex: Int? = null
)
