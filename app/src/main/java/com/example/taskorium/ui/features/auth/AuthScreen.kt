package com.example.taskorium.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskorium.ui.composable.CustomOutLineTextField

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToHome: ()-> Unit,
    innerPadding: PaddingValues
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effectEvent.collect {effectEvent->
            when(effectEvent){
                AuthUiEffectEvent.NavigateToHome -> navigateToHome()
            }
        }
    }
    AuthContent(
        modifier = Modifier.padding(innerPadding),
        state = state,
        onEmailChanged = { viewModel.onEvent(AuthUiEvent.EmailChanged(it)) },
        onPasswordChanged = { viewModel.onEvent(AuthUiEvent.PasswordChanged(it)) },
        onClickAuthButton = {
            if (state.screenMode == AuthScreenMode.Login) {
                viewModel.onEvent(AuthUiEvent.LoginClicked)
            }else{
                viewModel.onEvent(AuthUiEvent.RegisterClicked)
            }
        },
        onClickToggleButton = {viewModel.onEvent(AuthUiEvent.ToggleScreenMode)},

    )
}

@Composable
private fun AuthContent(
    modifier: Modifier = Modifier,
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onClickAuthButton: () -> Unit,
    onClickToggleButton: () -> Unit,
){
    val isLogin = state.screenMode == AuthScreenMode.Login
    val toggleMessage = if (isLogin) "Don't have an account? " else "Already have an account? "
    val toggleActionText = if (isLogin) "Register" else "Login"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = if (isLogin) "Welcome Back" else "Create Account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Text(
            text = if (isLogin) "Sign in to continue your tasks" else "Get started with Taskorium today",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            textAlign = TextAlign.Start
        )

        CustomOutLineTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = "Email Address",
            placeholder = "name@example.com",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutLineTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = "Password",
            placeholder = "Enter your password",
            leadingIcon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (state.errorMessage.isNotEmpty()) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onClickAuthButton,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (isLogin) "Sign In" else "Sign Up",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = toggleMessage,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            )
            Text(
                text = toggleActionText,
                modifier = Modifier.clickable(onClick = onClickToggleButton),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Preview
@Composable
private fun AuthPreview(){
    AuthContent(
        modifier = Modifier,
        state = AuthUiState(),
        onEmailChanged = {},
        onPasswordChanged = {},
        onClickAuthButton = {},
        onClickToggleButton = {},
    )
}