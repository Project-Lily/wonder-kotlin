package com.projectlily.wonderreader.types

data class QnaJsonDTO (
    val question: String? = null,
    val answer: String? = null
)

data class QnaListDTO (
    val Math : List<QnaJsonDTO>? = null,
    val Language : List<QnaJsonDTO>? = null,
    val Science : List<QnaJsonDTO>? = null,
    val SocialScience : List<QnaJsonDTO>? = null
)