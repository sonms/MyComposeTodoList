package com.example.mycomposetodolist

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

class CalendarActivity() {

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CalendarScreen() {
    //현재 선택된 날짜를 나타내는 변수입니다.
    //초기값으로 현재 날짜(LocalDate.now())가 설정되어 있습니다.
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    //현재 년도와 월을 나타내는 YearMonth 객체를 나타내는 변수입니다
    //초기값으로는 현재 년도와 월(YearMonth.now())이 설정되어 있습니다.
    var yearMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    yearMonth = yearMonth.minusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Month"
                )
            }

            Text(
                text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
                style = MaterialTheme.typography.h5
            )

            IconButton(
                onClick = {
                    yearMonth = yearMonth.plusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Month"
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7)
        ) {
            //여긴 요일 표시하는 곳
            items(7) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color.Transparent)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = DayOfWeek.values()[index].getDisplayName(java.time.format.TextStyle.NARROW, Locale.KOREAN), //요일의 값들중 앞의 두글자만 따옴
                        color = Color.Black,
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            //월의 마지막 날짜
            val daysInMonth = yearMonth.lengthOfMonth()

            // 현재 월의 이전 달을 나타내는 YearMonth 객체를 생성 후 그 이전 달의 마지막 날짜
            val daysInPreviousMonth =
                YearMonth.of(yearMonth.year, yearMonth.month.minus(1)).lengthOfMonth()

            //현재 월의 첫 번째 날
            val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value //5 friday값은 5

            val daysBefore = firstDayOfWeek - 1 //4 -> 현재 월 1일 전에 날짜 수 7일기준
            val daysAfter = 7 - (daysInMonth + daysBefore) % 7 //7 -> 현재 월 31일 후에 날짜 수 기준

            val totalDays = daysInMonth + daysBefore + daysAfter //42 이번달일수31+위숫자두개4+7

            items(totalDays) { dayIndex ->
                val day: Int
                val isInCurrentMonth: Boolean //현재 월의 날짜인지 체크

                if (dayIndex < daysBefore) { //이전 월의 날짜 4개 처리
                    //이전달의 마지막 날짜 30일에서 - 4 (30,29,28,27) + 0~4 + 1

                    //daysInPreviousMonth: 현재 월의 이전 달의 총 일 수  /30
                    //daysBefore: 현재 월의 첫 날이 속한 주의 첫 번째 요일까지의 이전 달의 일 수 / 1일은 금요일이므로 월화수목까지 4개
                    //dayIndex: 현재 월에서 현재 날짜가 속한 주의 첫 번째 요일부터 시작하는 인덱스 / 현재날짜가 속한 주는 첫주니 첫번째요일
                    //day = (daysInPreviousMonth - daysBefore) + dayIndex + 1: 각 날짜의 값을 계산
                    day = (daysInPreviousMonth - daysBefore) + dayIndex + 1
                    isInCurrentMonth = false
                } else if (dayIndex < daysBefore + daysInMonth) {
                    day = (dayIndex - daysBefore) + 1
                    isInCurrentMonth = true
                } else {
                    day = dayIndex - (daysBefore + daysInMonth) + 1
                    isInCurrentMonth = false
                }

                //위에서 구한 day를 토대로 어떤 날짜인지 역할
                //2023-12-01 이런식의 값을 가짐
                val date = LocalDate.of(yearMonth.year, yearMonth.month, day)
                val isSelected = date == selectedDate //현재 날짜(오늘)인지 판별

                DayItem(
                    day = day,
                    isInCurrentMonth = isInCurrentMonth,
                    isSelected = isSelected,
                    onClick = {
                        selectedDate = date
                    }
                )
            }
        }

        //todoList 추가할 곳
    }
}

@Composable
fun DayItem(
    day: Int,
    isInCurrentMonth: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
            .clip(MaterialTheme.shapes.medium)
            .size(36.dp)
            .background(
                when {
                    isSelected -> Color.Black
                    isInCurrentMonth -> Color.Transparent
                    else -> Color.Gray
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            style = MaterialTheme.typography.body1.copy(
                color = when {
                    isSelected -> Color.White
                    else -> LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                }
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen()
}