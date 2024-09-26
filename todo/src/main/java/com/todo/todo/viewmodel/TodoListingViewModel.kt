package com.todo.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todo.data.model.TodoEntity
import com.todo.data.repo.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
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
    private val _searchQuery = MutableStateFlow<String?>(null)
    private var _todos: List<TodoEntity>? = null

    init {
        viewModelScope.launch {
            _searchQuery
                .filterNotNull()
                .debounce(2000)
                .collect { text ->
                    val todos = if (text.isEmpty()) _todos else _todos?.filter {
                        it.todoDesc.contains(
                            text,
                            true
                        )
                    }
                    _state.update { state ->
                        state.copy(todosList = todos)
                    }
                }
        }
    }


    internal fun fetchTodos() {
        viewModelScope.launch {
            try{
                todoRepository.fetchTodos().firstOrNull()?.let { todos ->
                    _todos = todos
                    _state.update {
                        it.copy(isLoading = false, todosList = todos)
                    }
                }
            }catch (exception:Exception){
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }


    internal fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _state.update {
            it.copy(searchQuery = query)
        }
    }


    internal fun dismissErrorPopup() {
        _state.update {
            it.copy(isError = false)
        }
    }

    internal fun showErrorPopup() {
        _state.update {
            it.copy(isError = true)
        }
    }

    data class UiState(
        val isLoading: Boolean = true,
        val searchQuery: String = "",
        val todosList: List<TodoEntity>? = null,
        val isError: Boolean = false
    )
}