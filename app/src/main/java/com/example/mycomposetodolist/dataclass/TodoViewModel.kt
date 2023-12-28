package com.example.mycomposetodolist.dataclass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mycomposetodolist.AppDatabase
import com.example.mycomposetodolist.TodoItemDao
import kotlinx.coroutines.flow.Flow

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoRepository

    init {
        val todoItemDao = AppDatabase.getDatabase(application).todoItemDao()
        repository = TodoRepository(todoItemDao)
    }

    fun getAllTodoItems(): Flow<List<TodoListData>> {
        return repository.getAllTodoItems()
    }

    suspend fun insertTodoItem(todoItem: TodoListData) {
        repository.insertTodoItem(todoItem)
    }

    suspend fun deleteTodoItem(todoItem: TodoListData) {
        repository.deleteTodoItem(todoItem)
    }
}

class TodoRepository(private val todoItemDao: TodoItemDao) {
    fun getAllTodoItems(): Flow<List<TodoListData>> {
        return todoItemDao.getAllTodoItems()
    }

    suspend fun insertTodoItem(todoItem: TodoListData) {
        todoItemDao.insertTodoItem(todoItem)
    }

    suspend fun deleteTodoItem(todoItem: TodoListData) {
        todoItemDao.deleteTodoItem(todoItem)
    }
}