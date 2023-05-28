package com.projectlily.wonderreader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.projectlily.wonderreader.services.AuthService
import com.projectlily.wonderreader.services.QNACommunicationService
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private var qnaCommunicationService: QNACommunicationService? = null

    private fun testCallback(data: JSONObject) {
        Log.i("Service Test", "Got data ${data.getJSONObject("data")}")
        qnaCommunicationService?.sendQuestion("Send question works! ${data.getJSONObject("data")}")
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            qnaCommunicationService = (service as QNACommunicationService.LocalBinder).getService()
            qnaCommunicationService?.onAnswerReceive(::testCallback)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            qnaCommunicationService?.removeOnAnswerReceive(::testCallback)
            qnaCommunicationService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
        val gattIntent = Intent(this, QNACommunicationService::class.java)
        bindService(gattIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector = Icons.Filled.Home,
    val action: String = "",
    val actionButton: ImageVector = Icons.Filled.Send,
    val actionButtonDestination: String = ""
) {
    object Home :
        Screen(
            "Home", R.string.home, Icons.Default.Home,
            "Add QnA", Icons.Filled.Add, "Add QnA"
        )

    object QnA : Screen("QnA", R.string.qna, Icons.Default.QuestionAnswer)

    object Debug : Screen("Debug", R.string.debug, Icons.Default.Build)

    //    This shouldn't be on bottom bar, just here for testing
    object Auth : Screen("Auth", R.string.auth, Icons.Default.AccountBox)

    object AddQnA : Screen("Add QnA", R.string.add_qna)
    object SendToDevice : Screen("Send To Device", R.string.send_to_device)
    object FolderMath : Screen(
        "Math",
        R.string.math,
        action = "Send",
        actionButton = Icons.Filled.Send,
        actionButtonDestination = "Send To Device"
    )

    object FolderLanguage : Screen(
        "Language",
        R.string.math,
        action = "Send",
        actionButton = Icons.Filled.Send,
        actionButtonDestination = "Send To Device"
    )

    object FolderScience : Screen(
        "Science",
        R.string.math,
        action = "Send",
        actionButton = Icons.Filled.Send,
        actionButtonDestination = "Send To Device"
    )

    object FolderSocialScience : Screen(
        "Social Science",
        R.string.math,
        action = "Send",
        actionButton = Icons.Filled.Send,
        actionButtonDestination = "Send To Device"
    )

}

val screenItems = listOf(
    Screen.Home,
    Screen.QnA,
    Screen.Debug,
    Screen.Auth,
    Screen.AddQnA,
    Screen.SendToDevice,
    Screen.FolderMath,
    Screen.FolderLanguage,
    Screen.FolderScience,
    Screen.FolderSocialScience
)

val navBarItems = listOf(
    Screen.Home,
    Screen.QnA,
    Screen.Debug,
    Screen.Auth
)

@Composable
fun MainApp() {
    WonderReaderTheme {
        val navController = rememberNavController()
        val startDestination = if (AuthService.auth.currentUser != null) "home_root" else "auth"

        NavHost(
            navController,
            startDestination = startDestination,
        ) {
            homeNavGraph(navController)
            authNavGraph(navController)
        }
    }
}
