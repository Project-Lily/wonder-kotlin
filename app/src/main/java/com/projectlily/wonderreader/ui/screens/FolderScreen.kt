package com.projectlily.wonderreader.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.projectlily.wonderreader.SampleData
import com.projectlily.wonderreader.ui.components.Folder

@Composable
fun FolderScreen(navController: NavController, modifier: Modifier = Modifier) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        itemsIndexed(items = SampleData.folders) { _, item ->
            Folder(name = item.name, onClick = {
                navController.navigate(item.name)
            })
        }
    }
}
