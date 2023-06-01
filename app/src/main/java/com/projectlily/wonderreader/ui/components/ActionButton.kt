package com.projectlily.wonderreader.ui.components

import android.util.Log
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.projectlily.wonderreader.Screen

@Composable
fun ActionButton(
    navController: NavController,
    items: List<Screen>,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val currentScreen = items.find { it.route == currentDestination }
    val expanded = currentScreen?.action != ""

    if (expanded) {
        ExtendedFloatingActionButton(
            modifier = modifier,
            expanded = true,
            icon = {
                currentScreen?.let {
                    Icon(
                        imageVector = it.actionButton,
                        contentDescription = null
                    )
                }
            },
            text = {
                currentScreen?.let { Text(text = it.action) }
            },
            onClick = {
                currentScreen?.let {
                    navController.navigate(it.actionButtonDestination) {
                        // Navigate to the it.actionButtonDestination only if we’re not already on
                        // the it.actionButtonDestination, avoiding multiple copies on the top of the
                        // back stack
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}
