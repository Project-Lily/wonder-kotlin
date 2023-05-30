package com.projectlily.wonderreader.ui.screens

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.projectlily.wonderreader.services.BLEService
import com.projectlily.wonderreader.services.QNACommunicationService
import org.json.JSONObject

private var qnaCommunicationService: QNACommunicationService? = null

class AnswerState() {
    var answer: String by mutableStateOf("")

        private set
    fun setAnswer(text: String ){
        this.answer = text;
    }
}

@Composable
fun SendToDeviceScreen(
    navController: NavController,
    question: String,
    realAnswer: String,
    modifier: Modifier = Modifier,
) {
    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current

    val state = remember { AnswerState() }

    fun receiveAnswer(data: JSONObject) {
        Log.d("yabe", data.getString("data"))
        Log.i("Service Test", "Got answer ${data.getJSONObject("data")}")

        state.setAnswer(data.getString("data"))
    }

    val btIntentFilter = IntentFilter().apply {
        addAction(BLEService.GATT_CONNECTED)
        addAction(BLEService.GATT_DISCONNECTED)
    }

    val btServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BLEService.GATT_CONNECTED -> {
                    Log.i("Service Test", "woo woo")
                }

                BLEService.GATT_DISCONNECTED -> {
//                    Go back on BT disconnect
                    navController.popBackStack()
                }
            }
        }
    }

    val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            qnaCommunicationService = (service as QNACommunicationService.LocalBinder).getService()
            qnaCommunicationService?.sendQuestion(question)
            qnaCommunicationService?.onAnswerReceive(::receiveAnswer)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            qnaCommunicationService?.removeOnAnswerReceive(::receiveAnswer)
            qnaCommunicationService = null
        }
    }


    DisposableEffect(lifecycle) {
        val gattIntent = Intent(context, QNACommunicationService::class.java)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                context.bindService(gattIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                context.registerReceiver(btServiceReceiver, btIntentFilter)
            } else if (event == Lifecycle.Event.ON_STOP) {
                context.unbindService(serviceConnection)
                context.unregisterReceiver(btServiceReceiver)
            }
        }

        lifecycle.lifecycle.addObserver(observer)

        onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }
    }


    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Question: $question\nReal Answer: $realAnswer\n")
            Text(text = "Question: $question\nAnswer: ${state.answer}\n")
        }
    }
}
