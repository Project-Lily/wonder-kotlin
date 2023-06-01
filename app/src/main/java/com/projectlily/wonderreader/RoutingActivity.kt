package com.projectlily.wonderreader

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.projectlily.wonderreader.services.AuthService
import com.projectlily.wonderreader.services.QNACommunicationService
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private var qnaCommunicationService: QNACommunicationService? = null

    private fun testCallback(data: JSONObject) {
        Log.i("Service Test", "Got data ${data.getString("data")}")
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

        // Bluetooth Permissions
        // For this, I thank Bolt UIX. But modified
        // https://stackoverflow.com/a/69972855
        val sdkPermissions =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } else {
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }

        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    Log.d("BT Service", "${it.key} = ${it.value}")
                }
            }

        val gattIntent = Intent(this, QNACommunicationService::class.java)

        when (ContextCompat.checkSelfPermission(
            this,
            sdkPermissions[0]
        )) {
            PackageManager.PERMISSION_GRANTED
            -> {
                Log.d("BLE Time", "Permission granted")
                bindService(gattIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                Log.d("BLE Time", "bindService() called")
            }

            PackageManager.PERMISSION_DENIED -> {
                Log.d("BLE Time", "Permission requested")
                requestMultiplePermissions.launch(
                    sdkPermissions
                )
            }
        }
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
    object QnA : Screen("Quiz", R.string.qna, Icons.Default.QuestionAnswer)
    object Debug : Screen("Debug", R.string.debug, Icons.Default.Build)
    object Profile : Screen("Profile", R.string.profile, Icons.Default.AccountBox)

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
        R.string.language,
        action = "Send",
        actionButton = Icons.Filled.Send,
        actionButtonDestination = "Send To Device"
    )

    object FolderScience : Screen(
        "Science",
        R.string.science,
        action = "Send",
        actionButton = Icons.Filled.Send,
        actionButtonDestination = "Send To Device"
    )

    object FolderSocialScience : Screen(
        "Social Science",
        R.string.social_science,
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
    Screen.Profile,
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
    Screen.Profile
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
