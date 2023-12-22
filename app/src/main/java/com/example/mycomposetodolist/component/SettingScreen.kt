package com.example.mycomposetodolist.component

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Switch
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

//이제는 preferences screen xml 대신 protoDataStore을 사용
/*DataStore: DataStore는 기본적으로 Preferences와 유사한 키-값 쌍을 저장하는 데 사용됩니다. 각 키에 대한 값으로 preferencesKey를 사용하고, 값은 여러 형식을 지원합니다.
* ProtoDataStore: ProtoDataStore는 프로토콜 버퍼 형식을 사용하여 데이터를 저장합니다. 프로토콜 버퍼는 이진 형식이며, 데이터를 직렬화하고 역직렬화하는 데 사용됩니다.
* 일반적으로 데이터가 간단한 키-값 형식이면서 별다른 구조를 갖지 않는 경우에는 DataStore를 사용하는 것이 편리합니다.
* 프로토콜 버퍼를 사용하여 데이터를 정의하고 유연성이 높아야 하는 경우에는 ProtoDataStore를 선택할 수 있습니다.
*
*
프로토콜 버퍼(Protocol Buffers 또는 protobuf)는
*  Google에서 개발한 이진 직렬화 형식이며,
* 구조화된 데이터를 직렬화하고 역직렬화하는 데 사용됩니다.
*  Protocol Buffers는 간단하고 효율적이며,
* 언어 중립적이기 때문에 여러 프로그래밍 언어 간에 데이터를 교환하는 데 사용
* */
@Composable
fun SettingScreen(viewModel: SettingsViewModel?) {
    //collectAsState는 Flow의 값을 수집하고 해당 값을 State로 변환하는 Compose의 확장 함수입니다.
    //initial 매개변수는 Flow가 값을 방출하기 전에 사용할 초기값을 지정합니다. 여기서는 false로 설정

    //Flow는 Kotlin의 코루틴 라이브러리에서 비동기 연산의 결과를 표현하는 비동기 시퀀스입니다.
    //코루틴을 사용하여 비동기적인 작업을 수행하고 그 결과를 관찰하거나 수신하기 위해 사용됩니다.

    //스코프와는 다름
    //Flow는 비동기적으로 여러 값(또는 하나의 값)을 생성하고, 이를 구독하는 코드에서 값을 받을 수 있는 메커니즘을 제공
    //var isAlarmEnabled = viewModel?.alarmSettingFlow?.collectAsState(initial = false)?.value

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp))
    {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp).align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceBetween, //row의 ui의 각 배치마다의 일정한 간격을 유지하도록 정렬
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                modifier = Modifier.padding(start = 7.dp),
                text = "알람 설정",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            
            Switch(
                checked = false,
                onCheckedChange = {
                    /* isAlarmEnabled = it
                     viewModel?.setAlarmSetting(it)*/
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val dataStore = application.dataStore
    private val alarmSettingKey = booleanPreferencesKey("alarmReceive")
    private val dataStoreScope = CoroutineScope(Dispatchers.IO + viewModelScope.coroutineContext)

    val alarmSettingFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[alarmSettingKey] ?: false
        }

    fun setAlarmSetting(value: Boolean) {
        dataStoreScope.launch {
            dataStore.edit { preferences ->
                preferences[alarmSettingKey] = value
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    SettingScreen(viewModel = null)
}


