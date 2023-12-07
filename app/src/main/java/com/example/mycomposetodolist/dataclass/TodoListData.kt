package com.example.mycomposetodolist.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoListData(
    var id : Int?,
    var title : String?,
    var content : String?,
    var date : String?,
    var isComplete : Boolean?
) : Parcelable
