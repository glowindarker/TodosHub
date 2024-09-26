package com.todo.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todo.data.model.TodoEntity
import com.todo.data.repo.TodoRepository
import com.todo.todo.utils.ERROR
import com.todo.todo.utils.FAILED_TO_ADD_TODO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    internal fun onTodoValueChange(text:String){
        _state.update {
            it.copy(enteredTodo = text)
        }
    }

    internal fun addTodo(onSuccess:()->Unit,onFailure:()->Unit){
        viewModelScope.launch {
            val todo = _state.value.enteredTodo
            try{
                when{
                    todo.isEmpty()-> {
                        _state.update { it.copy(
                            isError = true
                        ) }
                    }
                    todo.equals(ERROR,ignoreCase = true)->{
                        throw Exception(FAILED_TO_ADD_TODO)
                    }
                    todo.isNotEmpty()->{
                        _state.update {
                            it.copy(isError = false, isLoading = true)
                        }
                        todoRepository.insertTodo(TodoEntity(todoDesc = _state.value.enteredTodo))
                        delay(3000)
                        _state.update {
                            it.copy(isLoading = false)
                        }
                        onSuccess()
                    }
                }
            }catch (exception:Exception){
                onFailure()
            }
        }
    }

    data class UiState(
        val isLoading:Boolean = false,
        val enteredTodo:String ="",
        val isError:Boolean = false
    )
}