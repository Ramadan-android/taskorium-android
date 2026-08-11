package com.example.taskorium.ui.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskorium.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){


    fun onEvent(event: SettingsUiEvent){
        when(event){
            SettingsUiEvent.ClickLogOutButton -> logOut()
        }
    }
    private fun logOut(){
        viewModelScope.launch {
            authRepository.logout()

        }
    }
}