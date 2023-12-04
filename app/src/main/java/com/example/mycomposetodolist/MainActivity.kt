package com.example.mycomposetodolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mycomposetodolist.ui.theme.MyComposeTodoListTheme




class MainActivity : ComponentActivity() {
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
fun MainScreenView() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) }
    ) {
        Box(Modifier.padding(it)){
            NavigationGraph(navController = navController)
        }
    }
}


@Composable
fun BottomNavigation(navController: NavHostController) {
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
                            .height(24.dp)
                    )
                },
                // label 선택 안했을 때도 보이는 메뉴 이름
                label = { Text(stringResource(id = item.title), fontSize = 9.sp) },
                selectedContentColor = MaterialTheme.colors.primary, //선택됐을 때 icon 색상
                unselectedContentColor = Black, //선택되지 않았을때 icon 색상
                selected = currentRoute == item.screenRoute, //언제 selected 상태가 될 지
                alwaysShowLabel = true, //항상 라벨을 보여줄건지에 대한 참거짓
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

/*-----------------바텀 메뉴 화면 설정--------------------*/
@Composable
fun BottomNavigationHomeView() {
    Box(modifier = Modifier //Box == FrameLayout?
        .fillMaxSize()
        .border(1.dp, Color.Cyan)
        .background(Color.Cyan, RoundedCornerShape(24.dp))
    ) {
        Text(text = stringResource(id = R.string.text_home),
            style = MaterialTheme.typography.h1,
            textAlign = TextAlign.Center, //textalign -> 글자정렬
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun BottomNavigationCalendarView() {
    Box(modifier = Modifier //Box == FrameLayout?
        .fillMaxSize()
        .border(1.dp, Color.Cyan)
        .background(Color.White, RoundedCornerShape(24.dp))
    ) {
        Text(text = stringResource(id = R.string.text_calendar),
            style = MaterialTheme.typography.h1,
            textAlign = TextAlign.Center, //textalign -> 글자정렬
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun BottomNavigationStatisticsView() { //통계분석, 일주일마다
    Box(modifier = Modifier //Box == FrameLayout?
        .fillMaxSize()
        .border(1.dp, Color.Cyan)
        .background(Color.White, RoundedCornerShape(24.dp))
    ) {
        Text(text = stringResource(id = R.string.text_statistics),
            style = MaterialTheme.typography.h1,
            textAlign = TextAlign.Center, //textalign -> 글자정렬
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun BottomNavigationSettingView() {
    Box(modifier = Modifier //Box == FrameLayout?
        .fillMaxSize()
        .border(1.dp, Color.Cyan)
        .background(Color.White, RoundedCornerShape(24.dp))
    ) {
        Text(text = stringResource(id = R.string.text_setting),
            style = MaterialTheme.typography.h1,
            textAlign = TextAlign.Center, //textalign -> 글자정렬
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
/*----------------------------------------------------------------*/


//NavHost는 navigation의 navigation을 관리하는 핵심 구성요소로 여기서 대상이 교체됩니다.
//다시말해, NavHost는 빈 컨테이너에서 어떤 화면으로 교체시킬지 관장하는 역할을 합니다.
//class내에 있는 함수로 어떤 route(위치)에서 어떤 화면을 보여줄 지 결정합니다.
//NavController는 대상을 이동시키는 요소입니다. 이는 NavHost내에서 사용됩니다.
//즉 이제 각 item이 각 화면과 연결되었습니다.
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.screenRoute) {
        composable(BottomNavItem.Home.screenRoute) {
            BottomNavigationHomeView()
        }
        composable(BottomNavItem.Calendar.screenRoute) {
            BottomNavigationCalendarView()
        }
        composable(BottomNavItem.Statistics.screenRoute) {
            BottomNavigationStatisticsView()
        }
        composable(BottomNavItem.Setting.screenRoute) {
            BottomNavigationSettingView()
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyComposeTodoListTheme {
        BottomNavigationHomeView()
    }
}
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
