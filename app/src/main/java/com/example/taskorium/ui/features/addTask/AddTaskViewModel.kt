package com.example.taskorium.ui.features.addTask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.taskorium.core.util.Priority
import com.example.taskorium.core.util.toCalculateDueDate
import com.example.taskorium.domain.alarm.AlarmScheduler
import com.example.taskorium.domain.model.Category
import com.example.taskorium.domain.model.Task
import com.example.taskorium.domain.repository.CategoryRepository
import com.example.taskorium.domain.repository.TaskRepository
import com.example.taskorium.route.AddEditTaskRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val alarmScheduler: AlarmScheduler,

    ): ViewModel() {
    private val _userInputsState = MutableStateFlow(AddTaskUiState())
    private val _event = MutableSharedFlow<AddUiEffectEvent>()
    val event = _event.asSharedFlow()
    init {
        val route = savedStateHandle.toRoute<AddEditTaskRoute>()
        val taskId = route.taskId
        if (taskId == null){
            _userInputsState.update {
                it.copy(
                    screenMode = AddEditScreenMode.ADD,
                    welcomeText = "Stay organised by creating a new task for your day."
                )
            }

        }else{
            loadTaskData(taskId)
        }

    }
    val state = combine(
        _userInputsState,
        categoryRepository.getCategories()
    ) { inputs, categoriesList ->
        inputs.copy(categories = categoriesList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AddTaskUiState()
    )

    private fun loadTaskData(taskId: String){

        viewModelScope.launch {
            taskRepository.getTaskById(taskId)
                .let {oldTask ->
                    oldTask?.let {
                        _userInputsState.update {
                            it.copy(
                                screenMode = AddEditScreenMode.EDIT,
                                taskEditId = oldTask.id,
                                title = oldTask.title,
                                description = oldTask.description,
                                priority = oldTask.priority,
                                categoryId = oldTask.categoryId,
                                welcomeText = "Update your task details and save your changes."
                            )
                        }

                    }
            }
        }
    }

    fun onEvent(event: AddTaskUiEvent){
        when(event){
            is AddTaskUiEvent.TitleChanged -> {
                _userInputsState.update {it.copy(title = event.value)}
            }
            is AddTaskUiEvent.DescriptionChanged -> {
                _userInputsState.update {
                    it.copy(
                        description = event.value
                    )
                }
            }
            is AddTaskUiEvent.CategoryChangedName -> {
                _userInputsState.update {it.copy(categoryName = event.value)}
            }
            is AddTaskUiEvent.ChangeCategory -> {
                _userInputsState.update {
                    it.copy(
                        categoryId = event.categoryId
                    )
                }
            }
            is AddTaskUiEvent.ChangePriority -> {
                _userInputsState.update {
                    it.copy(
                        priority = event.priority
                    )
                }
            }
            AddTaskUiEvent.ToggleCategoryMode -> {
                _userInputsState.update {
                    it.copy(
                        showCategoryFields = !it.showCategoryFields
                    )
                }
            }
            is AddTaskUiEvent.ClickAddButton -> {addTaskAndCategory()}





        }
    }

    private fun addTaskAndCategory(){
        _userInputsState.update { it.copy(isLoading = true) }

        val currentState = _userInputsState.value
        viewModelScope.launch {
            val calculatedDueDate = calculateDueDate(currentState.priority)
            val finalCategoryId = if (currentState.showCategoryFields) {
                val newCatId = UUID.randomUUID().toString()
                categoryRepository.insertCategory(
                    Category(
                        id = newCatId,
                        name = currentState.categoryName,
                    )
                )
                newCatId
            }else{
                currentState.categoryId
            }
            val taskId = if (_userInputsState.value.screenMode == AddEditScreenMode.ADD) UUID.randomUUID().toString()
            else _userInputsState.value.taskEditId
            val newTask = Task(
                    id = taskId,
                    title = currentState.title,
                    description = currentState.description,
                    publishedAt = System.currentTimeMillis(),
                dueDate = calculatedDueDate,
                    priority = currentState.priority,
                    categoryId = finalCategoryId,
                    isCompleted = false,
                )
            taskRepository.insertTask(newTask)
            _event.emit(AddUiEffectEvent.NavigateToHome)
            alarmScheduler.schedule(newTask)


            _userInputsState.update { it.copy(isLoading = false) }

        }
    }

    private fun calculateDueDate(priority: Priority): Long?{
        val publishedAt = System.currentTimeMillis()

//        return if (!task.isCompleted){
//            val publishedAt = if (task.publishedAt < System.currentTimeMillis()) System.currentTimeMillis() else task.publishedAt
            return when(priority){
                Priority.LOW -> publishedAt.toCalculateDueDate(3)
                Priority.MEDIUM -> publishedAt.toCalculateDueDate(2)
                Priority.HIGH -> publishedAt.toCalculateDueDate(1)
            }
//        }else null

    }


}