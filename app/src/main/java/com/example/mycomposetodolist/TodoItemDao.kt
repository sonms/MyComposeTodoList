package com.example.mycomposetodolist

import androidx.room.*
import com.example.mycomposetodolist.dataclass.TodoListData
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao { //Room 데이터베이스에 액세스하는 데 사용할 Data Access Object (DAO)를 정의합니다.
    @Query("SELECT * FROM todo_items")
    fun getAllTodoItems(): Flow<List<TodoListData>>

    @Insert
    suspend fun insertTodoItem(todoItem: TodoListData)

    @Update
    suspend fun update(todoItem: TodoListData)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoListData)
}