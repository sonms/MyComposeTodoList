package com.example.mycomposetodolist.dataclass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mycomposetodolist.AppDatabase
import com.example.mycomposetodolist.TodoItemDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoRepository
    val allTodos: Flow<List<TodoListData>>
    init {
        val todoItemDao = AppDatabase.getDatabase(application).todoItemDao()
        repository = TodoRepository(todoItemDao)
        allTodos = repository.getAllTodoItems()
    }

    fun getAllTodoItems(): Flow<List<TodoListData>> {
        return repository.getAllTodoItems()
    }

    suspend fun insertTodoItem(todoItem: TodoListData) {
        repository.insertTodoItem(todoItem)
    }

    suspend fun updateTodoItem(todoItem: TodoListData) {
        repository.updateTodoItem(todoItem)
    }

    suspend fun deleteTodoItem(todoItem: TodoListData) {
        repository.deleteTodoItem(todoItem)
    }
}

class TodoRepository(private val todoItemDao: TodoItemDao) {
    val allTodoItemDao = MutableLiveData<List<TodoItemDao>>()
    val foundTodoItemDao = MutableLiveData<TodoItemDao>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    fun getAllTodoItems(): Flow<List<TodoListData>> {
        return todoItemDao.getAllTodoItems()
    }

    fun insertTodoItem(todoItem: TodoListData) {
        coroutineScope.launch(Dispatchers.IO) {
            todoItemDao.insertTodoItem(todoItem)
        }
    }

    fun updateTodoItem(todoItem: TodoListData) {
        coroutineScope.launch(Dispatchers.IO) {
            todoItemDao.update(todoItem)
        }
    }

    fun deleteTodoItem(todoItem: TodoListData) {
        coroutineScope.launch(Dispatchers.IO) {
            todoItemDao.deleteTodoItem(todoItem)
        }
    }
}