package com.projectlily.wonderreader.ui.components

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
    val expanded = items.find { it.route == currentDestination }?.action != ""

    if (expanded) {
        ExtendedFloatingActionButton(
            onClick = { /*TODO*/ },
            expanded = true,
            icon = {
                items.find { it.route == currentDestination }?.let {
                    Icon(
                        imageVector = it.actionButton,
                        contentDescription = null
                    )
                }
            },
            text = {
                items.find { it.route == currentDestination }?.let { Text(text = it.action) }
            },
            modifier = modifier
        )
    }
}