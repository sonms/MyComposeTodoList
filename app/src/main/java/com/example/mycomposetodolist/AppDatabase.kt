package com.example.mycomposetodolist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mycomposetodolist.dataclass.TodoListData


//데이터베이스를 실제로 생성하는 클래스를 정의합니다.
@Database(entities = [TodoListData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
    //Room의 데이터베이스를 가져오기 위해서는 Room.databaseBuilder 또는 Room.inMemoryDatabaseBuilder를 사용
    //위의 코드에서 databaseBuilder 메서드를 사용하여 데이터베이스를 빌드하고, INSTANCE 변수를 사용하여 이미 데이터베이스가 생성되었는지 확인합니다.
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}