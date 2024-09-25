package com.todo.data.repo

import com.todo.data.dao.TodoDao
import com.todo.data.model.TodoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao){
    suspend fun insertTodo(todo:TodoEntity) = todoDao.insertTodo(todo)

    fun fetchTodos():Flow<List<TodoEntity>> = todoDao.fetchTodos()
}