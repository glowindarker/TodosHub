package com.todo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.todo.data.dao.TodoDao
import com.todo.data.model.TodoEntity

@Database(entities = [TodoEntity::class], version = 1)
abstract class TodoDB : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}