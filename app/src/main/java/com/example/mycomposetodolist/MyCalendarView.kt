package com.example.mycomposetodolist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposetodolist.ui.theme.MyComposeTodoListTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*


/*@Preview(showBackground = true)*/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyCalendarView() { //최종 UI
    val yearRange: IntRange = IntRange(1970, 2100)
    // 현재 연도와 월에 해당하는 페이지를 나타냅니다.
    val initialPage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (LocalDate.now().year - yearRange.first) * 12 + LocalDate.now().monthValue - 1
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    var currentPage by remember { mutableStateOf(initialPage) } //현재 페이지
    var currentMonth by remember {
        mutableStateOf(LocalDate.now().month)
    }

    val localDate = LocalDate.now()
    val yearMonth = YearMonth.from(localDate)
    val lastDayOfMonth = yearMonth.lengthOfMonth() //월의 마지막 날짜


    //페이지의 초기 위치를 설정합니다. 위에서 계산한 initialPage 값을 사용하여 현재 연도와 월에 해당하는 페이지로 설정합니다.
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        //, 0.0은 페이지의 왼쪽 끝, 1.0은 페이지의 오른쪽 끝을 나타냅니다.
        //이 값은 초기 페이지의 시작 위치를 설정하는 데 사용됩니다
        //한 마디로 달력(HorizontalPager)의 위치
        initialPageOffsetFraction = 0f
    ) {
        // 월 단위로 페이징되는 경우 각 월의 일 수에 따라 페이지 수가 다를 수 있습니다. 그걸 동적으로 세팅하는곳
        lastDayOfMonth
    }

    //페이지 유지 변수의 현재 페이지 값이 변경될때 실행됨
    LaunchedEffect(pagerState.currentPage) {
        //추가될 월
        val addMonth = (pagerState.currentPage - currentPage).toLong()
        //현재 월 체인지
        currentMonth = currentMonth.plus(addMonth)
        currentPage = pagerState.currentPage
    }

    HorizontalPager(state = pagerState) {
        Column(modifier = Modifier
            .wrapContentSize()
            .padding(8.dp))
        {
            CalendarHeader(title = "${LocalDate.now().year}년 ${LocalDate.now().monthValue}월")
            DayOfWeek()
            CalendarBody(currentDate = localDate)
        }
    }
}

@Composable
fun CalendarHeader(title : String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarBody(
    currentDate : LocalDate
) {
    val lastDay by remember { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        mutableStateOf(currentDate.lengthOfMonth())
    } else {
        TODO("VERSION.SDK_INT < O")
    }}

    val firstDayOfWeek by remember { mutableStateOf(currentDate.dayOfWeek.value) }
    val days by remember { mutableStateOf(IntRange(1, lastDay).toList()) }
    val today = LocalDate.now().toString().substring(8..9).toInt() //오늘날짜

    //한줄에 7개로 고정
    LazyVerticalGrid(
        modifier = Modifier.wrapContentSize(),
        columns = GridCells.Fixed(7)
    ) {
        for (i in 1 until firstDayOfWeek) { // 처음 날짜가 시작하는 요일 전까지 빈 박스 생성
            item {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 10.dp)
                )
            }
        }

        items(days) {dayItem ->
            if (dayItem == today) {
                CalendarItem(date = dayItem, isToday = true)
            } else {
                CalendarItem(date = dayItem, isToday = false)
            }
        }
    }
}


@Composable
fun DayOfWeek( //요일 표시
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        DayOfWeek.values().forEach { dayOfWeek ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f),
                    text = dayOfWeek.getDisplayName(java.time.format.TextStyle.NARROW, Locale.KOREAN),
                    style = TextStyle.Default,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}


@Composable
fun CalendarItem(date : Int?, isToday : Boolean) {
    Column(modifier = Modifier
        .wrapContentSize()
        .padding(top = 5.dp)
        .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
        .size(24.dp)
        .clip(shape = RoundedCornerShape(10.dp))
        .background(
            if (isToday) {
                Color.Black
            } else {
                Color.White
            }
        ),// 둥근 모서리 모양으로 잘라내는 역할 border없이 사용하고싶을때
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier,
            text = date.toString(),
            color = if (isToday) {
                Color.White
            } else {
                Color.Black
            },
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun UIPreview() {
    val currentDate : LocalDate = LocalDate.now()
    MyComposeTodoListTheme {
        MyCalendarView()
    }
}