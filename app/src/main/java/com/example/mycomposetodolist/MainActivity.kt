package com.example.mycomposetodolist

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mycomposetodolist.dataclass.TodoListData
import com.example.mycomposetodolist.ui.theme.MyComposeTodoListTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycomposetodolist.component.SettingScreen
import com.example.mycomposetodolist.component.SettingsViewModel
import com.example.mycomposetodolist.component.StatisticsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeTodoListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreenView()
                }
            }
        }
    }
}

//바텀 메뉴 아이템 설정
sealed class BottomNavItem(
    val title: Int, val icon: Int, val screenRoute: String
) {
    object Calendar : BottomNavItem(R.string.text_calendar, R.drawable.baseline_calendar_month_24, "CALENDAR")
    object Home : BottomNavItem(R.string.text_home, R.drawable.baseline_home_24, "HOME")
    object Statistics : BottomNavItem(R.string.text_statistics, R.drawable.baseline_statistics_24, "STATISTICS")
    object Setting : BottomNavItem(R.string.text_setting, R.drawable.baseline_settings_24, "SETTINGS")
}


@Composable
fun InitRecyclerViewUI( //fragment안에 recyclerview UI를 그리는 곳
    todoData: List<TodoListData>,
    onClicked: (TodoListData) -> Unit,
    onLongClicked: (Boolean, TodoListData) -> Unit,
    ) {

    LazyColumn(
        contentPadding = PaddingValues(16.dp, 8.dp)
    ) {
        /*items(
            items = todoListData,
            itemContent = { RecyclerViewItemLayout(it) }
        )*/
        itemsIndexed(items = todoData) {
            index, item ->
            RecyclerViewItemLayout(
                data = item,
                modifier = Modifier.fillMaxSize(),
                onClicked,
                onLongClicked
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecyclerViewItemLayout(data: TodoListData, modifier: Modifier, onClicked: (TodoListData) -> Unit, onLongClicked: (Boolean, TodoListData) -> Unit) { //리사이클러뷰 아이템 그리는곳
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 12.dp),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        border = BorderStroke(1.dp, Color.Black),
        elevation = 4.dp, //그림자 alpha
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .combinedClickable(
                    onClick = { onClicked(data) },
                    onLongClick = { onLongClicked(true, data) },
                ),
            /*clickable {
                    Log.d("MainActivity", "ClickTest")
                    onClicked()
                }*/
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

//모든 뷰를 가진 최종 UI코드
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenView() { //바텀네비게이션 바와 그 기능을 가진 최종 UI 코드
    //기본적으로 Compose에서 어떠한 상태 값이 바뀌게 되면 재구성(Recomposition)이 일어나게 된다.
    //여기서 재구성이란, 말 그대로 재 생성한다는 뜻이다.
    //만약 재구성하게 되면 기본값이 a인 텍스트뷰가 버튼 클릭 시 b로 바뀔 때 재구성되는데 이때 b로바뀌는게 아니라 a를 그대로 가지고 있게됨 재구성되었으니까
    // 재구성이 되었을 때도 값을 저장할 수 있도록 하기 위하여 Compose에서는 remember 키워드를 제공한다.

    //Compose에서 상태를 저장하고 상태가 변경 되었을 때 재구성하기 위해서는 관찰 가능한 객체를 사용해야 하는데
    // MutableState클래스는 Compose에서 읽기와 쓰기를 관찰하기 위해 만들어진 클래스라고 생각하면 된다.

    //val favourites = remember { mutableStateListOf<Track>()}
    //대신에
    //
    //var favourites: MutableList<Track> by mutableStateOf(mutableListOf())
    //var favourites by mutableStateOf(listOf<FavoriteItem>())
    //그런 다음 add/remove/update:-를 사용하여 목록을 작성하십시오.
    //
    //favourites = favourites + newList // add
    //favourites = favourites.toMutableList().also { it.remove(item) } // remov
    //var list: List<TodoListData> by rememberSaveable { mutableStateOf(listOf()) }

    //remember가 리컴포지션(재구성) 과정 전체에서 상태를 유지하는 데 도움은 되지만
    //
    //구성 변경시에 유지가 되지 않습니다.
    //
    //( the state is not retained across configuration changes.)
    //이 경우에는 rememberSaveable을 사용해야 합니다.
    //
    // rememberSaveable은 Bundle에 저장할 수 있는 모든 값을 자동으로 저장합니다.

    val todoAllData = ArrayList<TodoListData>()

    var todoListData by rememberSaveable { mutableStateOf(listOf<TodoListData>()) }
    fun addTodo(todo: TodoListData) {
        todoListData = todoListData + listOf(todo)
    }

    val calendarTodoListData by rememberSaveable { mutableStateOf(hashMapOf<String, List<TodoListData>>()) }
    fun addCalendarTodo(key : String, todo: TodoListData) {
        // 기존에 키에 대한 값이 있는지 확인
        val existingList = calendarTodoListData[key] ?: emptyList()

        // 새로운 할일을 추가한 새로운 리스트 생성
        val updatedList = existingList + todo

        // 새로운 리스트로 기존 맵 업데이트
        calendarTodoListData[key] = updatedList
    }


    fun editTodo(i: Int?, todo: TodoListData) {
        val index : Int = i ?: 0

        todoListData = todoListData.toMutableList().also { it[index] = todo }
    }


    fun deleteTodo(deleteItem: TodoListData?) {
        if (deleteItem != null) {
            todoListData = todoListData.toMutableList().also { it.removeIf { filterIt ->
                filterIt.title==deleteItem.title && filterIt.content==deleteItem.content && filterIt.date==deleteItem.date }
            }
        }
    }

    LaunchedEffect(todoListData, calendarTodoListData) {
        todoAllData.clear()
        todoAllData.addAll(todoListData)

        calendarTodoListData.values.forEach { list ->
            todoAllData.addAll(list)
        }
    }



    val navController = rememberNavController()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        when (activityResult.resultCode) {
            Activity.RESULT_OK -> {
                when(activityResult.data?.getIntExtra("flag", -1)) {
                    0 -> {
                        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            activityResult.data?.getParcelableExtra("data", TodoListData::class.java)
                        } else {
                            activityResult.data?.getParcelableExtra<TodoListData>("data")
                        }
                        if (data != null) {
                            addTodo(data)
                        }
                    }

                    1-> {
                        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            activityResult.data?.getParcelableExtra("data", TodoListData::class.java)
                        } else {
                            activityResult.data?.getParcelableExtra<TodoListData>("data")
                        }
                        if (data != null) {
                            editTodo(data.id, data)
                        }
                    }

                    3 -> {
                        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            activityResult.data?.getParcelableExtra("data", TodoListData::class.java)
                        } else {
                            activityResult.data?.getParcelableExtra<TodoListData>("data")
                        }

                        val date = activityResult.data?.getStringExtra("selectedDate")

                        if (data != null && date != null) {
                            addCalendarTodo(date, data)
                        }
                    }
                }
            }
        }
    }

    val moveAddEditTodoList: () -> Unit = {
        val intent = Intent(context, EditTodoListActivity::class.java).apply {
            putExtra("type", "ADD")
        }
        launcher.launch(intent)
    }

    val moveEditEditTodoList: (TodoListData) -> Unit = {
        val intent = Intent(context, EditTodoListActivity::class.java).apply {
            putExtra("type", "EDIT")
            putExtra("data", it)
        }
        launcher.launch(intent)
    }

    //dialog 통제 변수
    /*var isShow by remember {
        mutableStateOf(false)
    }*/

    var showDialog by remember {
        mutableStateOf(false)
    }
    var deleteItem by remember {
        mutableStateOf(TodoListData(null, null, null, null, null))
    }
    var dialogConfirm by remember {
        mutableStateOf(false)
    }
    //삭제 여기서 하면 안되고 dialog 확인일때 삭제하도록해야함 Todo
    /*val moveRemoveTodoList : (Boolean, TodoListData) -> Unit = { isLongClicked, item ->
        showDialog = isLongClicked
        if (isLongClicked) {
            deleteItem = item
            deleteTodo(item)
        }
    }*/

    //그럴려면 그냥 또 다른 변수 만들어서 false랑 true 받도록 해야할듯? == 얘가 long클릭 받기
    val showDialogCheck : (Boolean, TodoListData) -> Unit = { isLongClicked, item ->
        showDialog = isLongClicked
        if (showDialog) {
            deleteItem = item
        }
    }

    //얘가 다이얼로그 확인버튼 누른거
    val dialogCheck : (Boolean) -> Unit = { isConfirmed ->
        dialogConfirm = isConfirmed
        if (dialogConfirm) {
            deleteTodo(deleteItem)
        }
    }

    if(showDialog) {
        DialogScreen(setShowDialog = showDialogCheck, dialogCheck)
    }

    Scaffold( //material ui 기본 틀로 bottomBar, topbar, floatingbtn, drawer등을 포함하며 그려준다
        topBar = {
                 MyTopAppBar(onAction = { moveAddEditTodoList() })
        },
        bottomBar = { BottomNavigation(navController = navController) }
    ) {

        Box(Modifier.padding(it)){
            NavigationGraph(navController = navController, todoListData, moveEditEditTodoList, showDialogCheck)
        }
    }
}

//다이얼로그는 기본적으로 별도의 surface에 그려짐
//해당 surface의 배경색은 투명한 검정으로 되어있음
//따라서 dialog content를 위한 별도의 surface를 설정해야함

//DialogProperties
//1.dismissOnBackPress = back 버튼 처리로 true 를 주면 back 버튼 누를경우 onDismissRequest 가 호출됨.
//2.dismissOnClickOutside = Dialog 외부 클릭시 처리로 true를 주면  Dialog 외부 클릭시 onDismissRequest 가 호출됨.
//3.securePolicy = SecureFlagPolicy.SecureOn 를 대입하면 화면 캡쳐기능이 동작안함.
@Composable
fun DialogScreen(setShowDialog : (Boolean, TodoListData) -> Unit, dialogCheck : (Boolean) -> Unit) {
    Dialog(onDismissRequest = { setShowDialog(false, TodoListData(null, null, null, null, null)) }) {
        Surface(
            modifier = Modifier.background(White)
        ) {
            DialogContent(setShowDialog, dialogCheck)
        }
    }
}

@Composable
fun DialogContent(setShowDialog : (Boolean, TodoListData) -> Unit, dialogCheck: (Boolean) -> Unit) {
    Column(modifier = Modifier
        .wrapContentSize()
        .border(1.dp, Black, RoundedCornerShape(8.dp))
        .padding(16.dp)
    ) {
      Text(
          text = "Todo 삭제",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = Color.Black,
          textAlign = TextAlign.Start
      )
      Text(
          text = "삭제하시겠습니까?",
          fontWeight = FontWeight.Medium,
          fontSize = 8.sp,
          color = Black,
          textAlign = TextAlign.Start
      )

        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(5.dp)
        ) {
            Button(onClick = { setShowDialog(false, TodoListData(null, null, null, null, null)) },
            modifier = Modifier
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "취소",
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(onClick = {
                setShowDialog(false, TodoListData(null, null, null, null, null))
                dialogCheck(true)
                },
                modifier = Modifier
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White,
                    ),
            ) {
                Text(
                    text = "확인",
                )
            }
        }
    }
}

@Composable
fun MyTopAppBar(onAction: () -> Unit) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.background,
        title = {
            Text(
                text = "MY TodoList",
                fontWeight = FontWeight.Bold // 굵은 글꼴 지정
            )
        },
        actions = {
            IconButton(onClick = { onAction() }) {
                Icon(
                    //imageVector = Icons.Filled.Add,
                    painter = painterResource(id = R.drawable.baseline_add_box_24),
                    contentDescription = stringResource(R.string.add_todo),
                )
            }
        })
}

@Composable
fun BottomNavigation(navController: NavHostController) { //바텀 뷰 그리기
    val items = listOf<BottomNavItem>(
        BottomNavItem.Home,
        BottomNavItem.Calendar,
        BottomNavItem.Statistics,
        BottomNavItem.Setting
    )

    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color(0xFF3F414E)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = { //icon image
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.title),
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp),
                        tint = if (currentRoute == item.screenRoute) {
                            colorResource(R.color.light_blue_400)
                        } else {
                            Color.Black
                        }
                    )
                },
                // label 선택 안했을 때도 보이는 메뉴 이름
                label = {
                    if (currentRoute == item.screenRoute) {
                        Text(
                            stringResource(id = item.title),
                            fontSize = 9.sp,
                            color = colorResource(R.color.light_blue_400))}
                   /* else {
                        Text(
                            text = "",
                            fontSize = 9.sp
                        )
                    }*/
                        },
                selectedContentColor = colorResource(R.color.light_blue_400), //메뉴를 클릭할 때 나오는 색상
                unselectedContentColor = Black, //선택되지 않았을때 icon 색상
                selected = currentRoute == item.screenRoute, //언제 selected 상태가 될 지
                alwaysShowLabel = currentRoute == item.screenRoute, //항상 라벨을 보여줄건지에 대한 참거짓
                onClick = { //click되면 어떤 행동을 취할지
                    //avController를 통해 navigate(이동)하게 구현해준 코드입니다.
                    //popUpTo()를 통해 startDestinationRoute만 스택에 쌓일 수 있게 만들었습니다.
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
                        //launchSingleTop=true를 통해 화면 인스턴스 하나만 만들어지게 하였고,
                        //restoreState=true를 통해 버튼을 재클릭했을 때 이전 상태가 남아있게 하였습니다.
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/*-----------------바텀 메뉴 화면 설정(FrameLayout 즉, 보여질 화면들 여기서 화면의 기능 및 디자인을 구현)--------------------*/
@Composable
fun BottomNavigationHomeView(todoData: List<TodoListData>, onClicked: (TodoListData) -> Unit, onLongClicked: (Boolean, TodoListData) -> Unit) {
    Box(modifier = Modifier //Box == FrameLayout?
        .fillMaxSize()
        .border(1.dp, Color.Cyan)
        .background(Color.White) //, RoundedCornerShape(24.dp)
    ) {
        InitRecyclerViewUI(todoData, onClicked, onLongClicked)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavigationCalendarView() {
    Box(modifier = Modifier //Box == FrameLayout?
        .fillMaxSize()
        .border(1.dp, Color.Cyan)
        .background(Color.White)
    ) {
        val hm = mutableMapOf<String, List<TodoListData>>()
        hm["2023-12-1"] = listOf(TodoListData(2, "제목1", "내용1", "clickedDate", false))

        CalendarScreen(hm)
    }
}

@Composable
fun BottomNavigationStatisticsView(todoData : List<TodoListData>) { //통계분석, 일주일마다
    Box(modifier = Modifier //Box == FrameLayout?
        .fillMaxSize()
        .border(1.dp, Color.Cyan)
        .background(Color.White)
    ) {
        /*Text(text = stringResource(id = R.string.text_statistics),
            style = MaterialTheme.typography.h1,
            textAlign = TextAlign.Center, //textalign -> 글자정렬
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )*/
        StatisticsScreen(data = todoData)
    }
}
@Composable
fun BottomNavigationSettingView() {
    val viewModel: SettingsViewModel = viewModel()

    Box(modifier = Modifier //Box == FrameLayout?
        .fillMaxSize()
        .border(1.dp, Color.Cyan)
        .background(Color.White)
    ) {

        SettingScreen(viewModel = viewModel)
    }
}
/*----------------------------------------------------------------*/


//NavHost는 navigation의 navigation을 관리하는 핵심 구성요소로 여기서 대상이 교체됩니다.
//다시말해, NavHost는 빈 컨테이너에서 어떤 화면으로 교체시킬지 관장하는 역할을 합니다.
//class내에 있는 함수로 어떤 route(위치)에서 어떤 화면을 보여줄 지 결정합니다.
//NavController는 대상을 이동시키는 요소입니다. 이는 NavHost내에서 사용됩니다.
//즉 이제 각 item이 각 화면과 연결되었습니다.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    todoData: List<TodoListData>,
    onClicked: (TodoListData) -> Unit,
    onLongClicked: (Boolean, TodoListData) -> Unit
) { //바텀 메뉴 클릭 시 이동 도와줌
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.screenRoute
    ) {
        composable(BottomNavItem.Home.screenRoute) { //composalbe안에는 보여질 메뉴의 이름
            BottomNavigationHomeView(todoData, onClicked, onLongClicked)
        }
        composable(BottomNavItem.Calendar.screenRoute) {
            BottomNavigationCalendarView()
        }
        composable(BottomNavItem.Statistics.screenRoute) {
            BottomNavigationStatisticsView(todoData)
        }
        composable(BottomNavItem.Setting.screenRoute) {
            BottomNavigationSettingView()
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyComposeTodoListTheme {
        MainScreenView()
    }
}

/*@Preview(showBackground = false)
@Composable
fun DialogPreview() {
    MyComposeTodoListTheme() {
        DialogContent()
    }
}*/

/*
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyComposeTodoListTheme {
        Greeting("Android")
    }
}*/
