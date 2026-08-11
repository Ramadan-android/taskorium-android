package com.example.taskorium.ui.features.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.Priority
import com.example.taskorium.core.util.toRelativeTimeString
import com.example.taskorium.domain.model.Category
import com.example.taskorium.domain.model.Task
import com.example.taskorium.ui.composable.CustomOutLineTextField
import com.example.taskorium.ui.composable.SwipeableActionsBox
import com.example.taskorium.ui.uiComponents.DragAnchors
import com.example.taskorium.ui.uiComponents.TaskoriumFilterChip
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToAddScreen: (String)-> Unit,
    navigateToEditDeleteCatScreen: (String)-> Unit,
    navigateToSettingsScreen: ()-> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.event.collect {event ->
            when(event){
                is HomeUiEffectEvent.NavigateToAddEdit -> navigateToAddScreen(event.taskId)
                is HomeUiEffectEvent.NavigateToCat -> navigateToEditDeleteCatScreen(event.catId)
                HomeUiEffectEvent.NavigateToSettings -> navigateToSettingsScreen()
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.secondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.secondaryContainer
                ),


                title = {
                    Text(text = "Taskorium", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
                },
                actions = {
                    IconButton(
                    onClick = viewModel::onClickSettingsButton
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "settings icon"
                    )
                }
                }
            )
        }
    ) { innerPaddingHome ->
        HomeContent(
            state = state,
            onEvent = { viewModel.onEvent(it) },
            onNavigateToAdd = viewModel::onNavigateAddEditScreen,
            onNavigateToCat = viewModel::onClickCatChip,
            onClickTask = viewModel::onClickTask,
            innerPadding = innerPaddingHome

        )

    }
}

@SuppressLint("FrequentlyChangingValue")
@Composable
private fun HomeContent(
    state: HomeUiState,
    onEvent: (event: HomeUiEvent) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToCat: (String) -> Unit,
    onClickTask: (String) -> Unit,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues

) {
    if (state.searchQuery.isBlank() && state.tasksUi.isEmpty() && state.categories.isEmpty()){
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "You don't have a task",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                    )
                Text(
                    "Add one +",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onNavigateToAdd() }
                        .padding(8.dp)
                    )
            }
        }
    }else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding),

        ) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(bottom = 8.dp)
                        ,
                        verticalArrangement = Arrangement.Center

                    ) {
                        CustomOutLineTextField(
                            value = state.searchQuery,
                            onValueChange = { onEvent(HomeUiEvent.SearchQueryChange(it)) },
                            label = "Search",
                            placeholder = "search tasks...",
                            leadingIcon = Icons.TwoTone.Search,
                            shape = 50.dp,
                            modifier = Modifier.padding(bottom = 12.dp)


                        )

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(
                                items = state.categories, key = { it.id }
                            ) { category ->
                                TaskoriumFilterChip(
                                    text = category.name,
                                    isSelected = category.id == state.selectedCategoryId,
                                    onClick =  {
                                        onEvent(HomeUiEvent.CategoryChange(category.id))
                                    },
                                    onLongClick = {
                                        if (category.id != Constants.DEFAULT_CATEGORY_ID)
                                            onNavigateToCat(category.id)
                                    }
                                )
                            }

                        }
                    }
                }
                items(
                    items = state.tasksUi,
                    key = { it.task.id }
                )
                { taskUi ->
                    SwipeableActionsBox(
                        targetDp = 50.dp,
                        backgroundContent = {
                            Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete task icon",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = it
                        )},
                        onClickBackgroundContent = {
                            onEvent(HomeUiEvent.DeleteTask(taskUi.task.id, catId = taskUi.task.categoryId))
                        },
                    ) {
                        TaskItem(
                            taskUi = taskUi,
                            modifier = it.animateItem(),
                            toggleCompleteStates = {

                                onEvent(
                                    HomeUiEvent.ToggleTaskCompletion(
                                        taskId = taskUi.task.id,
                                        isCompleted = !taskUi.task.isCompleted
                                    )
                                )
                            },
                            onClick = { onClickTask(taskUi.task.id) }
                        )

                    }
                }
            }
            FloatingActionButton(
                onClick = onNavigateToAdd,
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 28.dp)
                    .align(Alignment.BottomEnd),
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary

            ) {
                Text("+",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    taskUi: TaskUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    toggleCompleteStates: () -> Unit
) {

    val priorityColor = when (taskUi.task.priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> Color(0xFFFF9800)
        Priority.LOW -> Color(0xFF43A047)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {


        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .fillMaxHeight()
                    .background(priorityColor)
            )
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = taskUi.task.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (taskUi.task.isCompleted) TextDecoration.LineThrough
                            else TextDecoration.None
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = taskUi.task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Icon(
                        imageVector = if (taskUi.task.isCompleted) Icons.Rounded.CheckCircle
                        else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = "completion task icon",
                        tint = if (taskUi.task.isCompleted) Color(0xFF4CAF50)
                        else MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                toggleCompleteStates()
                            }
                    )
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallBadge(
                        icon = Icons.Rounded.Folder, text = taskUi.categoryName
                    )

                    Spacer(Modifier.width(8.dp))

                    SmallBadge(
                        icon = Icons.Rounded.CalendarMonth, text = taskUi.task.publishedAt.toRelativeTimeString()
                    )

                    Spacer(Modifier.weight(1f))

                    Surface(
                        shape = RoundedCornerShape(50), color = priorityColor.copy(alpha = .15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp, vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        priorityColor, CircleShape
                                    )
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = taskUi.task.priority.name,
                                color = priorityColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallBadge(
    icon: ImageVector, text: String
) {

    Surface(
        shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surface
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp, vertical = 6.dp
            ), verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = text, style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Preview
@Composable
private fun HomePreview() {

    HomeContent(
        state = HomeUiState(
            tasksUi =
                listOf(
                    TaskUiModel(
                        Task(
                            id = "TODO()",
                            title = "test",
                            description = "preview",
                            publishedAt = 60000L,
                            priority = Priority.MEDIUM,
                            categoryId = "fddfkgdfngdfklnv",
                            isCompleted = true,
                        ),
                        "test"
                    ),
                    TaskUiModel(
                        Task(
                            id = "TODO()2",
                            title = "test",
                            description = "preview dlkvneflekf evekn vkjej v4 j4tv4v 4t;4kjnt2kj4b tbktjbbnb ebekjbnt;4nbb btrkjbntrbe",
                            publishedAt = 60000L,
                            priority = Priority.LOW,
                            categoryId = "tesr",
                            isCompleted = false,
                        ),
                        "tasr"
                    ),
                    TaskUiModel(
                        Task(
                            id = "TODO()3",
                            title = "test",
                            description = "preview  evekn vkjej v4  4t;  ;4nbb btrkjbntrbe",
                            publishedAt = 60000L,
                            priority = Priority.HIGH,
                            categoryId = "tesr",
                            isCompleted = true,
                        ),
                        "task2"
                    ),
                    TaskUiModel(
                        Task(
                            id = "TO()",
                            title = "test",
                            description = "preview",
                            publishedAt = 60000L,
                            priority = Priority.MEDIUM,
                            categoryId = "tesr",
                            isCompleted = true,
                        ),
                        "tesr"
                    ),
                    TaskUiModel(
                        Task(
                            id = "TOD()2",
                            title = "test",
                            description = "preview dlkvneflekf evekn vkjej v4 j4tv4v 4t;4kjnt2kj4b tbktjbbnb ebekjbnt;4nbb btrkjbntrbe",
                            publishedAt = 60000L,
                            priority = Priority.LOW,
                            categoryId = "tesr",
                            isCompleted = false,
                        ),
                        "tesr"
                    ),
                    TaskUiModel(
                        Task(
                            id = "TDO()3",
                            title = "test",
                            description = "preview  evekn vkjej v4  4t;  ;4nbb btrkjbntrbe",
                            publishedAt = 60000L,
                            priority = Priority.HIGH,
                            categoryId = "tesr",
                            isCompleted = true,
                        ),
                        "tesr"
                    ),
            ),
            categories =
                listOf(
                Category(
                    id = "test",
                    name = "test",
                )
            )
        ),
        onEvent = { },
        modifier = Modifier.background(color = Color.White),
        onNavigateToAdd = {},
        onNavigateToCat = {},
        onClickTask = {},
        innerPadding = PaddingValues(10.dp)
        )
}