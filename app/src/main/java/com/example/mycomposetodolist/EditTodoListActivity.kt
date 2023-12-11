package com.example.mycomposetodolist

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposetodolist.dataclass.TodoListData
import com.example.mycomposetodolist.ui.theme.MyComposeTodoListTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditTodoListActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeTodoListTheme {
                val type = intent.getStringExtra("type")
                if (type != null) {
                    Log.d("EDITACTIVITY", type)
                }
                val selectedDate = intent.getStringExtra("selectedDate")
                val context = LocalContext.current
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    EditView(type, onClick = {
                        when (type) {
                            "ADD" -> {
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    putExtra("flag", 0)
                                    putExtra("data", it)
                                }
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                            "EDIT" -> {
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    putExtra("flag", 1)
                                    putExtra("data", it)
                                }
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                            else -> {
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    putExtra("flag", 3)
                                    putExtra("data", it)
                                    putExtra("selectedDate", selectedDate)
                                }
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                        }
                    },
                    data = if (type == "EDIT") {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra("data", TodoListData::class.java)
                        } else {
                            intent.getParcelableExtra<TodoListData>("data")
                        }
                    } else {
                        null
                    })
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditView(type: String?, onClick: ((TodoListData) -> Unit)?, data : TodoListData?) {
    var titleText by remember { mutableStateOf(TextFieldValue("")) }
    var contentText by remember { mutableStateOf(TextFieldValue("")) }
    var editTitleText by remember { mutableStateOf(TextFieldValue(data?.title.toString())) }
    var editContentText by remember { mutableStateOf(TextFieldValue(data?.content.toString())) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val todayDate = remember { LocalDate.now() }
    val formattedDate = remember(todayDate) {
        todayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    var id by remember {
        mutableStateOf(0)
    }
    val addTodo = TodoListData(id, titleText.text, contentText.text, formattedDate, false)

    //현재 소프트웨어 키보드를 제어한 다음 hide메서드를 사용할 수 있습니다.
    //val keyboardController = LocalSoftwareKeyboardController.current
    //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    //        keyboardActions = KeyboardActions(
    //                onDone = {keyboardController?.hide()})
    //)

    Column(modifier = Modifier //vertical linearlayout
        .fillMaxSize()
        .padding(8.dp) //padding : 자신을 기준으로 안쪽으로 얼만큼 공간을 비울건지(padding(start,end...))
        //offset : 자식컴포넌트의 크기까지 줄이면서 공간을 점유하는방식과 달리 자식컴포넌트에는 영향을 끼치지 않으면서 공간을 창출함
    ) {
        Text(text = "제목", fontWeight = FontWeight.Bold, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp))
        TextField( //만약 완벽하게 커스텀하고 싶으면?->BasicTextField를 사용
            value = when (type) {
                "ADD" -> titleText
                "EDIT" -> editTitleText
                else -> titleText
            }, //초기값 설정
            onValueChange = { newText ->
                titleText = newText
                editTitleText = newText
                addTodo.title = when (type) {
                    "ADD" -> titleText.text
                    "EDIT" -> editTitleText.text
                    else -> titleText.text
                }
            }, //값 변경 시 어떻게 할것인지
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.textFieldColors( //textfield의 모든 색 설정하는 법
                textColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                backgroundColor =  Color.White,
                cursorColor = Color.Black
            ),
            //hint 설정
            placeholder = { Text(stringResource(R.string.edit_Text_Title_Hint), color = Color.Gray, fontFamily = FontFamily.SansSerif) },
            /*activeColor = Color.Gray,
            inactiveColor = Color.Unspecified,*/
            singleLine = true,
            shape = RoundedCornerShape(corner = CornerSize(12.dp)),
            enabled = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, //edittext inputType
                                                imeAction = ImeAction.Next),
            //개발자는 사용자가 소프트웨어 키보드에서 작업을 트리거하는 것에 대한 응답으로 트리거될 작업을 지정할 수 있습니다.
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down)
                         Toast.makeText(context, "onNext", Toast.LENGTH_SHORT).show()
                         },
                onDone = { focusManager.clearFocus()
                    Toast.makeText(context, "onDone", Toast.LENGTH_SHORT).show()},
            )
        )

        Text(text = "내용", fontWeight = FontWeight.Bold, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, top = 50.dp))
        BasicTextField( //BasicTextField를 이용하면 Text와 TextField사이의 padding값을 조절할 수 있게 된다.
            //대신 hint가 없음
            value = when (type) {
                "ADD" -> contentText
                "EDIT" -> editContentText
                else -> contentText
            },
            onValueChange = {newText ->
                contentText = newText
                editContentText = newText
                addTodo.content = when (type) {
                    "ADD" -> contentText.text
                    "EDIT" -> editContentText.text
                    else -> contentText.text
                }
            },
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(8.dp) // margin left and right
                        .fillMaxWidth()
                        .background(color = Color.White, shape = RoundedCornerShape(size = 12.dp))
                        .padding(all = 8.dp), // inner padding
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    //hint대신 아래와 같은 방식으로 사용
                    if (contentText.text.isEmpty()) {
                        Text(
                            text = "내용 작성",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            }
        )

        Button(onClick = {
            if (onClick != null) {
                onClick(addTodo)
            }
            id++
        },
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.White,
                disabledBackgroundColor = Color.Gray,
                disabledContentColor = Color.White
            )
        ) { //이 부분이 버튼의 content부분
            Text(text = "완료", fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewTest() {
    MyComposeTodoListTheme {
        EditView("ADD", null, null)
    }
}