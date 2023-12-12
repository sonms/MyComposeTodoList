package com.example.mycomposetodolist.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposetodolist.BottomNavItem
import com.example.mycomposetodolist.dataclass.TodoListData
import com.example.mycomposetodolist.ui.theme.MyComposeTodoListTheme


/*@Composable
fun StatisticsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        VerticalLinesColumn()
        
    }
}*/

/*@Composable
fun NumberedColumn() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        repeat(3) { index ->
            // 숫자를 왼쪽에 표시하는 행 그리기
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                // 왼쪽에 숫자 표시
                Text(
                    text = "${(index + 1) * 10}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .width(30.dp)
                        .padding(end = 8.dp),
                    textAlign = TextAlign.End
                )

                // 나머지 내용은 여기에 추가
                Text(
                    text = "Your Content Here",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}*/

@Composable
fun StatisticsScreen(data : List<TodoListData>) {
    // 수직선을 그리기 위한 높이
    val lineHeight = 48.dp

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 왼쪽에 선을 그림
                val canvasWidth = size.width
                val canvasHeight = size.height

                // drawing a line between start(x,y) and end(x,y)
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = 0f, y = canvasHeight),
                    color = Color.Black,
                    strokeWidth = 10F
                )

                /*repeat(10) {
                    drawLine(
                        start = Offset(x = 0f, y = canvasHeight / it / 2),
                        end = Offset(x = canvasWidth, y = canvasHeight / it / 2),
                        color = Color.Black,
                        strokeWidth = 5f
                    )
                }*/
                // "-" 의 개수
                val numberOfDashes = 10

                // 왼쪽에 수직선을 그림
                /*val canvasWidth = size.width
                val canvasHeight = size.height*/

                // "-" 의 간격 비율
                val dashSpacing = canvasHeight / numberOfDashes.toFloat()

                for (i in 0 until numberOfDashes) {
                    val yPosition = i * dashSpacing

                    // drawing a line between start(x,y) and end(x,y)
                    drawLine(
                        start = Offset(x = 0f, y = yPosition + dashSpacing / 2),
                        end = Offset(x = canvasWidth, y = yPosition + dashSpacing / 2),
                        color = Color.Black,
                        strokeWidth = 5f
                    )
                }
            }

            Text(
                text = "총 Todo 수 ${data.size}개",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 36.dp, top = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Column(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 72.dp, bottom = 24.dp)) {
                repeat(data.size) {
                    StatisticsItem()
                }
            }


            Text(
                text = "완료한 Todo 수 ${data.filter { it.isComplete == true }.size}개",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 36.dp, top = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Column(modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(start = 72.dp, bottom = 24.dp)) {
                repeat(data.filter { it.isComplete == true }.size) {
                    StatisticsItem()
                }
            }
        }
    }
}

@Composable
fun StatisticsItem() {
    Box(modifier = Modifier
        .wrapContentSize()
        .padding(3.dp)
        .clip(MaterialTheme.shapes.medium)
        .size(12.dp)
        .background(
            /*when {
                isSelected -> Color.Black
                isInCurrentMonth -> Color.Transparent
                else -> Color.Gray
            }*/
            Color.Black
        ),
        //.clickable(onClick = onClick)
        contentAlignment = Alignment.Center
    ) {

    }
}


@Preview(showBackground = true)
@Composable
fun StatisticsUIPreview() {
    var data = listOf<TodoListData>()
    data = data + listOf<TodoListData>(TodoListData(0, "1", "1", "!", false))
    MyComposeTodoListTheme {
        StatisticsScreen(data)
    }
}