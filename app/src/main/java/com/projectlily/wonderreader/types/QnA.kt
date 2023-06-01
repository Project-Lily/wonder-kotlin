package com.projectlily.wonderreader.types

import kotlinx.serialization.Serializable


@Serializable
data class QnA (
    var question: String,
    var answer: String,
)

