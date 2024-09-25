package com.todo.data.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.todo.data.dao.TodoDao
import com.todo.data.db.TodoDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TodoModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext app: Context): TodoDB {
        return databaseBuilder(
            app,
            TodoDB::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    fun provideTodoDao(db: TodoDB): TodoDao {
        return db.todoDao()
    }
}