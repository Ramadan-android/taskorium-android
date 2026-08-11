package com.example.taskorium.ui.features.addTask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.sharp.HdrEnhancedSelect
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskorium.core.util.Priority
import com.example.taskorium.core.util.SyncStatus
import com.example.taskorium.domain.model.Category
import com.example.taskorium.ui.composable.CustomOutLineTextField
import com.example.taskorium.ui.features.home.HomeUiEffectEvent

@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel = hiltViewModel(),
    navigateToHomeScreen: ()-> Unit,
    innerPadding: PaddingValues
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.event.collect {event ->
            when(event){
                AddUiEffectEvent.NavigateToHome -> navigateToHomeScreen()
            }
        }
    }
    AddTaskContent(
        modifier = Modifier.padding(innerPadding),
        state = state,
        onChangeTitle = { viewModel.onEvent(AddTaskUiEvent.TitleChanged(it)) },
        onChangeDescription = { viewModel.onEvent(AddTaskUiEvent.DescriptionChanged(it)) },
        onChangeCategoryName = { viewModel.onEvent(AddTaskUiEvent.CategoryChangedName(it)) },
        onChangeCategory = { viewModel.onEvent(AddTaskUiEvent.ChangeCategory(it)) },
        onChangePriority = { viewModel.onEvent(AddTaskUiEvent.ChangePriority(it)) },
        onClickAddButton = { viewModel.onEvent(AddTaskUiEvent.ClickAddButton) },
        onToggleCategoryMode = { viewModel.onEvent(AddTaskUiEvent.ToggleCategoryMode) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskContent(
    state: AddTaskUiState,
    onChangeTitle: (String) -> Unit,
    onChangeDescription: (String) -> Unit,
    onChangeCategoryName: (String) -> Unit,
    onChangeCategory: (String) -> Unit,
    onChangePriority: (Priority) -> Unit,
    onToggleCategoryMode: () -> Unit,
    onClickAddButton: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val selectedCategoryName =
        state.categories.find { it.id == state.categoryId }?.name ?: "Select Category"
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = state.welcomeText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CustomOutLineTextField(
            value = state.title,
            onValueChange = onChangeTitle,
            label = "Task Title",
            placeholder = "What is your task?",
            leadingIcon = Icons.Filled.Task,
            modifier = Modifier.fillMaxWidth(),
            shape = 20.dp
        )

        CustomOutLineTextField(
            value = state.description,
            onValueChange = onChangeDescription,
            label = "Task Description",
            placeholder = "Add more details about your task",
            leadingIcon = Icons.AutoMirrored.Filled.Article,
            modifier = Modifier.fillMaxWidth(),
            shape = 20.dp

        )

        Text(
            text = "Priority",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Priority.entries.forEach { priority ->
                val priorityColor = when (priority) {
                    Priority.HIGH -> MaterialTheme.colorScheme.error
                    Priority.MEDIUM -> Color(0xFFFF9800)
                    Priority.LOW -> Color(0xFF43A047)
                }
                val isSelected = priority == state.priority
                FilterChip(
                    selected = isSelected,
                    onClick = { onChangePriority(priority) },
                    label = {
                        Text(text = priority.name)
                    },
                    modifier = Modifier.padding(4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        selectedContainerColor = priorityColor,
                        labelColor = MaterialTheme.colorScheme.onSecondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary

                    )
                )
            }
        }

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        if (!state.showCategoryFields) {
            ExposedDropdownMenuBox(
                expanded = isMenuExpanded,
                onExpandedChange = { isMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Choose Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMenuExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                        ),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    shape = RoundedCornerShape(20.dp)
                )

                ExposedDropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    state.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                onChangeCategory(category.id)
                                isMenuExpanded = false
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = {
                            Text(
                                "+ Create New Category",
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            onToggleCategoryMode()
                            isMenuExpanded = false
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomOutLineTextField(
                    value = state.categoryName,
                    onValueChange = onChangeCategoryName,
                    label = "New Category Name",
                    placeholder = "e.g., Work, Study",
                    leadingIcon = Icons.Sharp.HdrEnhancedSelect,
                    modifier = Modifier.fillMaxWidth(),
                    shape = 20.dp
                )
                TextButton(
                    onClick = onToggleCategoryMode,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Select from existing categories")
                }
            }
        }

        ElevatedButton(
            onClick = onClickAddButton,
            enabled = state.enabledButton,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Save Task",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }

}


@Preview
@Composable
private fun AddTaskPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AddTaskContent(
            state = AddTaskUiState(
                categories = listOf(
                    Category(
                        id = "test",
                        name = "test0",
                    ),
                    Category(
                        id = "test1",
                        name = "test10",
                    ),
                    Category(
                        id = "test2",
                        name = "test20",
                    ),
                    Category(
                        id = "test3",
                        name = "test30",
                    )
                )
            ),
            onChangeTitle = {},
            onChangeDescription = {},
            onChangeCategoryName = {},
            onChangeCategory = {},
            onChangePriority = {},
            onClickAddButton = {},
            onToggleCategoryMode = {}


        )

    }
}