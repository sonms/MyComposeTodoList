package com.example.mycomposetodolist

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposetodolist.dataclass.TodoListData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(data: Map<String, List<TodoListData>>) {
    //데이터 관리
    var todoListData by rememberSaveable { mutableStateOf(data) }
    // TodoItemScreen에 전달할 MutableMap
    val todoData = remember { mutableStateOf(todoListData.toMap()) }


    fun addTodo(key : String, todo: TodoListData) {
        // 기존 데이터를 가져오기
        val existingList = todoListData.toMutableMap()

        // key에 해당하는 List를 가져오기
        val updatedList = existingList[key]?.toMutableList() ?: mutableListOf()

        //리스트에 변경사항 적용 지금은 추가
        updatedList.add(todo)

        // 변경된 List를 다시 맵에 할당
        existingList[key] = updatedList

        // 변경된 데이터로 MutableState 업데이트
        todoListData = existingList.toMap()

        //전달할 데이터도 업데이트
        todoData.value = todoListData.toMap()
    }

    //현재 선택된 날짜를 나타내는 변수입니다.
    //초기값으로 현재 날짜(LocalDate.now())가 설정되어 있습니다.
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    //날짜 클릭 시 해당 날짜를 기억하는 변수
    var clickedDate by remember {
        mutableStateOf("")
    }

    //현재 년도와 월을 나타내는 YearMonth 객체를 나타내는 변수입니다
    //초기값으로는 현재 년도와 월(YearMonth.now())이 설정되어 있습니다.
    var yearMonth by remember { mutableStateOf(YearMonth.now()) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        when (activityResult.resultCode) {
            Activity.RESULT_OK -> {
                when(activityResult.data?.getIntExtra("flag", -1)) {
                    3 -> {
                        val getData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            activityResult.data?.getParcelableExtra("data", TodoListData::class.java)
                        } else {
                            activityResult.data?.getParcelableExtra<TodoListData>("data")
                        }

                        val date = activityResult.data?.getStringExtra("selectedDate")

                        if (getData != null && date != null) {
                            addTodo(date, getData)
                        }
                    }

                    /*1-> {
                        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            activityResult.data?.getParcelableExtra("data", TodoListData::class.java)
                        } else {
                            activityResult.data?.getParcelableExtra<TodoListData>("data")
                        }
                        if (data != null) {
                            //editTodo(data.id, data)
                        }
                    }*/
                }
            }
        }
    }

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

            //현재 월의 마지막 날짜
            val daysInMonth = yearMonth.lengthOfMonth()

            // 현재 월의 이전 달을 나타내는 YearMonth 객체를 생성 후 그 이전 달의 마지막 날짜
            val daysInPreviousMonth =
                YearMonth.of(yearMonth.year, yearMonth.month.minus(1)).lengthOfMonth()

            //현재 월의 첫 번째 날
            val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value //5 friday값은 5

            val daysBefore = firstDayOfWeek - 1 //4 -> 현재 월 1일 전에 날짜 수 7일기준
            val daysAfter = 7 - (daysInMonth + daysBefore) % 7 //7 -> 현재 월 31일 후에 날짜 수 기준

            val totalDays = daysInMonth + daysBefore + daysAfter //42 이번달일수31+위숫자두개4+7

            val today = LocalDate.now().toString().substring(8..9).toInt() //오늘날짜
            val todayMonth = LocalDate.now().toString().substring(5..6).toInt()

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
                //val date = LocalDate.of(yearMonth.year, yearMonth.month, day)
                val date = LocalDate.now()
                var isSelected = false //현재 날짜(오늘)인지 판별

                if (day == today && yearMonth.monthValue == todayMonth) { //현재 날짜 체크, 월과 날짜로 판별
                    isSelected = true
                }
                

                DayItem(
                    day = day,
                    isInCurrentMonth = isInCurrentMonth,
                    isSelected = isSelected,
                    onClick = {
                        selectedDate = date
                        clickedDate = "${yearMonth.year}-${yearMonth.monthValue}-${day}"
                        Toast.makeText(context, clickedDate, Toast.LENGTH_SHORT).show()
                        println(clickedDate)
                        addTodo(clickedDate, TodoListData(1,"s","g","cl",false))
                        val intent = Intent(context, EditTodoListActivity::class.java).apply {
                            putExtra("type", "CalendarAdd")
                            putExtra("selectedDate", date.toString())
                        }
                        launcher.launch(intent)
                    },
                    onLongClick = {
                        Toast.makeText(context, "Long Click", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        } //vertical grid

        //hm["2023-12-12"] = listOf(TodoListData(1, "제목", "내용", "2023-12-11", false))
        println(todoListData)
        // Lazycolumn 아래에 상태 변수 추가
        var selectedItemIndex by remember { mutableStateOf(-1) }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),

            ) {
            //toList()는 매번 리스트를 새로 생성하므로 인덱스가 변경될 때마다
            // 다른 리스트를 참조하게 되어 문제가 발생할 수 있습니다.
            /*itemsIndexed(items = todoListData.toList()) {
                    index, item ->
                TodoItemLayout(
                    data = item.second[index],
                    modifier = Modifier.fillMaxSize(),
                    isClicked = index == selectedItemIndex,
                    onClick = {
                        selectedItemIndex = index
                    }
                )
            }*/
            // itemsIndexed함수는 각 항목에 대해 컴포저블을 생성할 때 인덱스 정보를 제공합니다.
            //items는 제공하지 않음
            /*
            * 인덱스 정보가 필요하지 않고 각 항목을 그냥 표시하면 items를 사용하면 됩니다.
            *  하지만 특정 인덱스에 따른 동작이 필요하거나 인덱스를 기반으로 추가적인
            *  작업을 해야 할 때는 itemsIndexed를 사용*/

            itemsIndexed(items = todoListData[clickedDate].orEmpty()) {
                    index, item ->
                println(item)
                TodoItemLayout(
                    data = item,
                    modifier = Modifier.fillMaxSize(),
                    isClicked = index == selectedItemIndex,
                    onClick = {
                        selectedItemIndex = index
                    }
                )
            }
        }
    } //column
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayItem(
    day: Int,
    isInCurrentMonth: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick : () -> Unit
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
            ).combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
            /*.pointerInput(Unit) {
                detectTapGestures { offset ->
                    onClick() // Handle regular click
                }
                detectTransformGestures { _, pan, _, _ ->
                    if (pan.x != 0f || pan.y != 0f) {
                        // Reset state when user starts panning
                        // to avoid long click being triggered while panning
                        onLongClick()
                    }
                }

            }*/
            /*.combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )*/
        /*
        * .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onClick() // Handle regular click
                }

                detectTransformGestures(
                * onLongPress = {
                             // perform some action here..
                     })
            }
        * */
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

@Composable
fun TodoItemScreen(todoData: MutableState<Map<String, List<TodoListData>>>) {
    /*
    * MutableState는 Compose에서 상태를 관리하기 위한 일반적인 인터페이스입니다. MutableState를 사용하여 Compose에서 상태를 정의하고 관리할 수 있습니다.

    MutableState는 읽기 및 쓰기가 가능한 상태를 나타냅니다. 상태를 변경하려면 value 속성에 직접 접근하여 수정합니다.

    MutableState 인터페이스를 구현한 클래스로 mutableStateOf 함수를 통해 간편하게 생성할 수 있습니다.
* *///개인적 생각 : MutableState 은 생성자 느낌? val s : ArrayList<T> = ArrayList()에서 : 의 생성자로 remember와 mutableStateOf를 사용하도록하는것같음
    /////////LazyColumn 관측 변수
    val todoNullCheckDataList by rememberSaveable {
        mutableStateOf(todoData.value)
    }
    // Lazycolumn 아래에 상태 변수 추가
    var selectedItemIndex by remember { mutableStateOf(-1) }

    println("NULLCHECK" + todoNullCheckDataList)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),

    ) {
        itemsIndexed(items = todoNullCheckDataList.toList()) {
                index, item ->
            TodoItemLayout(
                data = item.second[index],
                modifier = Modifier.fillMaxSize(),
                isClicked = index == selectedItemIndex,
                onClick = {
                    selectedItemIndex = index
                }
            )
        }

        /*for ((key, value) in todoNullCheckDataList.value) {
            item {
                TodoItemLayout(
                    data = value,
                    modifier = Modifier.fillMaxSize(),
                    isClicked = key == selectedItemIndex.toString(),
                    onClick = {
                        selectedItemIndex = key.toInt()
                    }
                )
            }
        }*/
    }
}

@Composable
fun TodoItemLayout(data: TodoListData, modifier: Modifier, isClicked : Boolean,  onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when {
                    isClicked -> {
                        Color.LightGray
                    }
                    else -> {
                        Color.White
                    }
                }
            )
            .padding(0.dp, 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        border = BorderStroke(1.dp, Color.Black),
        elevation = 4.dp, //그림자 alpha
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text( //제목
                text = data.title.toString(),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.h2,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis, //텍스트의 길이가 화면을 벗어날경우 처리설정, ellipsis는... / Visible 전부 표시, clip은 자르기 가로로잘림
                maxLines = 1
            )

            Text( //내용
                text = data.content.toString(),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis, //텍스트의 길이가 화면을 벗어날경우 처리설정, ellipsis는... / Visible 전부 표시, clip은 자르기 가로로잘림
                maxLines = 1
            )

            Text( //날짜
                modifier = Modifier.fillMaxWidth(), //textalign을 적용하기 위해서는 크기의 설정이 필요
                text = data.date.toString(),
                fontFamily = FontFamily.Monospace, //fontfamily는 텍스트를 렌더링할 때 사용할 글꼴 모음(폰트종류)입니다, fontstyle = 이텔릭체, 등등fontStyle = FontStyle.Italic
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.End,
                color = Color.Black,
                fontSize = 6.sp,
                overflow = TextOverflow.Ellipsis, //텍스트의 길이가 화면을 벗어날경우 처리설정, ellipsis는... / Visible 전부 표시, clip은 자르기 가로로잘림
                maxLines = 1
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val hm = kotlin.collections.HashMap<String, kotlin.collections.List<TodoListData>>()
    hm["2023-12-12"] = listOf(TodoListData(1, "제목", "내용", "2023-12-11", false))
    CalendarScreen(hm)
    //TodoItemScreen(todoData = hm)
}