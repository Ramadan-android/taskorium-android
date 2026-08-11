package com.example.taskorium.ui.features.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskorium.core.util.Constants
import com.example.taskorium.core.util.Priority
import com.example.taskorium.core.util.toCalculateDueDate
import com.example.taskorium.domain.alarm.AlarmScheduler
import com.example.taskorium.domain.model.Category
import com.example.taskorium.domain.model.Task
import com.example.taskorium.domain.repository.CategoryRepository
import com.example.taskorium.domain.repository.TaskRepository
import com.example.taskorium.route.AddEditTaskRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val categoryRepository: CategoryRepository
): ViewModel(){
    private val _event = MutableSharedFlow<HomeUiEffectEvent>()
    val event = _event.asSharedFlow()
    private val _selectedCategoryId = MutableStateFlow(Constants.DEFAULT_CATEGORY_ID)
    private val _searchQuery = MutableStateFlow("")
    private val _errorMessage = MutableStateFlow("")
    private val _categoriesFlow = categoryRepository.getCategories()
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tasksFlow = _selectedCategoryId.flatMapLatest { selectedCat->
        taskRepository.getTasksByCategoryId(selectedCat)
    }


    val state = combine(
        _tasksFlow,
        _categoriesFlow,
        _selectedCategoryId,
        _searchQuery,
        _errorMessage

    ){tasks, baseCats, selectedCatInit, query, error ->

        val cats = if (baseCats.isNotEmpty()){
            listOf(
                Category(Constants.DEFAULT_CATEGORY_ID,Constants.DEFAULT_CATEGORY_ID)
            ) + baseCats
        }else {
             emptyList()
        }

        val filteredTasks = tasks.filter { task ->
            query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)
        }.map { task ->
            val categoryName = cats.firstOrNull { it.id == task.categoryId }?.name ?: Constants.DEFAULT_CATEGORY_ID
            TaskUiModel(task, categoryName)
        }.reversed()

        val isSelectedCatExist = cats.map { it.id }.contains(selectedCatInit)
        val selectedCat = if (isSelectedCatExist) selectedCatInit else {
            _selectedCategoryId.update { Constants.DEFAULT_CATEGORY_ID }
            selectedCatInit
        }
        HomeUiState(
            tasksUi = filteredTasks,
            categories = cats,
            selectedCategoryId = selectedCat,
            searchQuery = query,
            errorMessage = error,
            isLoading = false
    )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onEvent(event: HomeUiEvent){
        when(event){
            is HomeUiEvent.CategoryChange -> _selectedCategoryId.update { event.catId }
            is HomeUiEvent.SearchQueryChange -> _searchQuery.update { event.value }
            is HomeUiEvent.ToggleTaskCompletion -> {
                viewModelScope.launch {
                    val task = taskRepository.getTaskById(event.taskId)
                    task?.let {
                        alarmScheduler.cancel(task)
                    }
                    taskRepository.toggleTaskCompletion(taskId = event.taskId, isCompleted = event.isCompleted)
                }
            }
            is HomeUiEvent.DeleteTask -> {
                viewModelScope.launch {
                    val taskToDelete = taskRepository.getTaskById(event.taskId)
                    taskToDelete?.let {
                        alarmScheduler.cancel(taskToDelete)
                    }
                    taskRepository.deleteTask(event.taskId)
                    val deletedTaskIsLast = taskRepository.getTasksByCategoryId(event.catId).first().isEmpty()
                    if (deletedTaskIsLast) categoryRepository.deleteCategory(event.catId)
                }
            }
        }
    }

    fun onNavigateAddEditScreen(){
        viewModelScope.launch {
            _event.emit(HomeUiEffectEvent.NavigateToAddEdit(AddEditTaskRoute.toString()))
        }
    }
    fun onClickCatChip(catId: String){
        viewModelScope.launch {
            _event.emit(HomeUiEffectEvent.NavigateToCat(catId))
        }
    }
    fun onClickSettingsButton(){
        viewModelScope.launch {
            _event.emit(HomeUiEffectEvent.NavigateToSettings)
        }
    }
    fun onClickTask(taskId: String){
        viewModelScope.launch {
            _event.emit(HomeUiEffectEvent.NavigateToAddEdit(taskId))
        }
    }


}