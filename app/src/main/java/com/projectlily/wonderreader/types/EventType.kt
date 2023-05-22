package com.projectlily.wonderreader.types

enum class EventType {
    RECEIVE_ANSWER, QUESTION_RECEIVED;

    companion object {
//        Returns enum if string is found. Null if not.
//        Ignores case
        fun fromString(input: String): EventType? {
            return values().firstOrNull { it.name.equals(input, true)}
        }
    }
}