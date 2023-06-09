package com.projectlily.wonderreader.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.projectlily.wonderreader.R
import com.projectlily.wonderreader.types.QnA

@Composable
fun QnAList(
    data: List<QnA>,
    modifier: Modifier = Modifier,
    isChoosing: Boolean = false,
    chosenIndex: Int = -1,
    onClick: (index: Int, question: String, answer: String) -> Unit
) {

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.padding(top = 4.dp)
    ) {
        itemsIndexed(items = data) { index, data ->
            QnAElement(
                index = index + 1,
                question = data.question,
                answer = data.answer,
                isChoosing = isChoosing,
                chosenItemIndex = chosenIndex,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun QnAElement(
    index: Int,
    question: String,
    answer: String,
    isChoosing: Boolean,
    chosenItemIndex: Int = -1,
    onClick: (index: Int, question: String, answer: String) -> Unit
) {
    val isChosenCheck = chosenItemIndex == index
    val border = if (isChosenCheck) BorderStroke(4.dp, Color.Black) else null

    if (isChoosing) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = {
                onClick(index, question, answer)
            },
            border = border,
            modifier = Modifier.padding(8.dp)
        ) {
            QnAContent(index = index, question = question, answer = answer)
        }
    } else {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(8.dp)
        ) {
            QnAContent(index = index, question = question, answer = answer)
        }
    }
}

@Composable
private fun QnAContent(index: Int, question: String, answer: String) {
    val expanded = rememberSaveable { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Text(
                text = "$index.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                )
                if (expanded.value) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = (answer),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }
            }
            IconButton(onClick = { expanded.value = !expanded.value }) {
                Icon(
                    imageVector = if (expanded.value) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded.value) stringResource(R.string.show_less) else stringResource(
                        R.string.show_more
                    )
                )
            }
        }
    }
}