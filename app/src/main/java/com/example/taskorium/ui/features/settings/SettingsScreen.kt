package com.example.taskorium.ui.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigateToAuth: ()-> Unit,
    innerPadding: PaddingValues
){
    SettingsContent(
        modifier = Modifier.padding(innerPadding),
        onClickLogOut = {
            viewModel.onEvent(SettingsUiEvent.ClickLogOutButton)
            navigateToAuth()
        }
    )
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    onClickLogOut: ()-> Unit,
){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClickLogOut,
            modifier = Modifier
                .padding(20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)

        ) {
            Text(text = "LogOut")
        }
    }
}

@Preview
@Composable
private fun SettingsPreview(){}