package com.example.taskorium.ui.features.editDeleteCat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.taskorium.domain.model.Category
import com.example.taskorium.domain.repository.CategoryRepository
import com.example.taskorium.domain.repository.TaskRepository
import com.example.taskorium.route.EditDeleteCategoryRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDeleteCatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
): ViewModel(){

    private val _state = MutableStateFlow(EditDeleteCatState())
    val state = _state.asStateFlow()
    private val _event = MutableSharedFlow<EditDeleteCatUiEventEffect>()
    val event = _event.asSharedFlow()
    init {
        val route = savedStateHandle.toRoute<EditDeleteCategoryRoute>()
        val catId = route.catId
        Log.d("cat id",  catId.toString())
        catId?.let {
            loadData(it)
        }
    }

    private fun loadData(catId: String) {
        viewModelScope.launch {
            categoryRepository.getCategory(catId)
                .let { category ->
                    _state.update {
                        it.copy(
                            oldCatValue = category.name,
                            catValue = category.name,
                            categoryId = catId,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onEvent(event: EditDeleteCatUiEvent){
        when(event){
            is EditDeleteCatUiEvent.CategoryChanged -> {
                _state.update {
                    it.copy(
                        catValue = event.value
                    )
                }
            }
            is EditDeleteCatUiEvent.ClickDeleteButton -> {
                viewModelScope.launch {
                    _event.emit(EditDeleteCatUiEventEffect.NavigateToHome)
                    categoryRepository.deleteCategory(event.categoryId)
                    taskRepository.deleteTasksByCategory(event.categoryId)
                }
            }
            EditDeleteCatUiEvent.ClickSaveButton -> { updateCategory() }
        }
    }
    private fun updateCategory(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            categoryRepository.insertCategory(
                Category(
                    id = _state.value.categoryId,
                    name = _state.value.catValue
                )
            )
            _event.emit(EditDeleteCatUiEventEffect.NavigateToHome)
        }
    }
}