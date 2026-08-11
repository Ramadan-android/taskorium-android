package com.example.taskorium.ui.features.splashView

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskorium.core.session.SessionManager
import com.example.taskorium.data.sync.SyncManager
import com.example.taskorium.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val syncManager: SyncManager

): ViewModel(){

    private val _state = MutableStateFlow(SplashViewState())
    val state = _state.asStateFlow()
    init {
        checkAuthToken()
    }
    private fun checkAuthToken(){
        viewModelScope.launch {
            val token = authRepository.getAuthToken().firstOrNull()
            val refreshToken = authRepository.getRefreshToken().firstOrNull()
            if (token != null && refreshToken != null){
                _state.update {
                    it.copy(
                        startDestination = StartDestination.Home,
                    )
                }
                sessionManager.token = token
                sessionManager.refreshToken = refreshToken
                syncManager.scheduleOneTimeSync()

            }
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

}

data class SplashViewState(
    val startDestination: StartDestination  = StartDestination.Auth,
    val isLoading: Boolean = true
)

sealed interface StartDestination {
    data object Auth: StartDestination
    data object Home: StartDestination
}