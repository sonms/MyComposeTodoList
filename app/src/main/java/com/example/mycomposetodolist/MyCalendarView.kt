package com.example.mycomposetodolist

import android.os.Build
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.mycomposetodolist.ui.theme.MyComposeTodoListTheme
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


@Preview(showBackground = true)
@Composable
fun MyCalendarView() {
    val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
    val selectedTime = remember { mutableStateOf(Calendar.getInstance()) }

    MyComposeTodoListTheme() {
        CalendarHeader()
    }
}

@Composable
fun CalendarHeader() {
    val calendar = Calendar.getInstance()
    val currentDate = calendar.time

    // 날짜 포맷 지정 (예: "yyyy-MM-dd")

    // 날짜 포맷 지정 (예: "yyyy-MM-dd")
    val dateFormat = SimpleDateFormat("yyyy년 MM월", Locale.getDefault())
    val formattedDate: String = dateFormat.format(currentDate)

    val headerTitle = remember { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //"${LocalDate.now().year}-${LocalDate.now().month}"
        formattedDate
    } else {
        formattedDate
    }}

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = headerTitle,
            fontWeight = FontWeight.Bold,

        )
    }

}