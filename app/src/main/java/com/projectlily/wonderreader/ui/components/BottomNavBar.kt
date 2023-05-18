package com.projectlily.wonderreader.ui.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavBar(modifier: Modifier = Modifier) {
    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.secondary, modifier = modifier) {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null
                )
            },
            label = { Text(text = "Home") },
            selected = true,
//            selectedContentColor = Color.White,
            onClick = { /*TODO*/ }
        )
        BottomNavigationItem(icon = {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null
            )
        },
            label = { Text(text = "Debug") },
            selected = false,
//            selectedContentColor = Color.White,
            onClick = { /*TODO*/ }
        )
    }
}