package com.example.taskorium.ui.features.editDeleteCat

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.sharp.HdrEnhancedSelect
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskorium.ui.composable.CustomOutLineTextField

@Composable
fun EditDeleteCatHome(
    viewModel: EditDeleteCatViewModel = hiltViewModel(),
    navigateToHomeScreen: ()-> Unit,
    innerPadding: PaddingValues
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    EditDeleteCatContent(
        modifier = Modifier.padding(innerPadding),
        state = state,
        onChangeCatValue = { viewModel.onEvent(EditDeleteCatUiEvent.CategoryChanged(it)) },
        onClickSaveButton = { viewModel.onEvent(EditDeleteCatUiEvent.ClickSaveButton) },
        onClickDeleteButton = { viewModel.onEvent(EditDeleteCatUiEvent.ClickDeleteButton(it)) }
    )
    LaunchedEffect(Unit) {
        viewModel.event.collect { event->
            when(event){
                EditDeleteCatUiEventEffect.NavigateToHome -> navigateToHomeScreen()
            }
        }
    }
}

@Composable
private fun EditDeleteCatContent(
    modifier: Modifier = Modifier,
    state: EditDeleteCatState,
    onChangeCatValue: (String)-> Unit,
    onClickSaveButton: ()-> Unit,
    onClickDeleteButton: (String)-> Unit

){
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CustomOutLineTextField(
            value = state.catValue,
            shape = 16.dp,
            onValueChange = { onChangeCatValue(it) },
            label = "change cat",
            placeholder = "write your fav cat...",
            leadingIcon = Icons.Sharp.HdrEnhancedSelect,
        )
        Spacer(modifier = Modifier.height(20.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ElevatedButton(
                onClick = onClickSaveButton,
                enabled = state.enabledButton,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save Category",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            FilledIconButton (
                onClick = { onClickDeleteButton(state.categoryId) },
                modifier = Modifier
                    .size(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Category"
                )
            }
        }

    }
}


@Preview
@Composable
private fun EditDeleteCatPreview(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffffffff))
    ) {
        EditDeleteCatContent(state = EditDeleteCatState(),
            onClickSaveButton = {},
            onChangeCatValue = {},
            onClickDeleteButton = {}
        )
    }

}