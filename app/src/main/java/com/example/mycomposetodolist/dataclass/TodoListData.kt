package com.example.mycomposetodolist.dataclass

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "todo_items") //먼저 데이터베이스에 저장할 엔터티를 정의합니다.
@Parcelize
data class TodoListData(
    @PrimaryKey(autoGenerate = true) var id : Int?,
    @ColumnInfo(name = "title") var title : String?,
    @ColumnInfo(name = "content") var content : String?,
    @ColumnInfo(name = "date") var date : String?,
    @ColumnInfo(name = "isComplete") var isComplete : Boolean?
) : Parcelable
