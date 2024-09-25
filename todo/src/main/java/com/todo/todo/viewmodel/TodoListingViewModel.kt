package com.todo.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todo.data.model.TodoEntity
import com.todo.data.repo.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListingViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()


    internal fun fetchTodos(){
        viewModelScope.launch{
            todoRepository.fetchTodos().firstOrNull()?.let{todos->
                _state.update {
                    it.copy(todosList = todos)
                }
            }
        }
    }


    internal fun onSearchQueryChange(text: String) {

    }

    internal fun dismissErrorPopup(){
        _state.update {
            it.copy(isError = false)
        }
    }

    internal fun showErrorPopup(){
        _state.update {
            it.copy(isError = true)
        }
    }

    data class UiState(
        val isLoading:Boolean = false,
        val query:String ="",
        val todosList:List<TodoEntity>?=null,
        val isError:Boolean = false
    )
}