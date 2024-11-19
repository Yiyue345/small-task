package com.example.smalltask.classes

data class YoudaoWord(
    val result: Result,
    val data: Data
)

data class Result(
    val msg: String,
    val code: Int
)

data class Data(
    val entries: List<Entry>?,
    val query: String?,
    val language: String?,
    val type: String?
)

data class Entry(
    val explain: String,
    val entry: String
)
