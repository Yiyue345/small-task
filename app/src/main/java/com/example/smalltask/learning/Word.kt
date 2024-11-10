package com.example.smalltask.learning

import com.squareup.moshi.Json

data class Word(
    @Json(name = "word") val word: String,
    @Json(name = "accent") val accent: String,
    @Json(name = "mean_cn") val meanCn: String,
    @Json(name = "mean_en") val meanEn: String,
    @Json(name = "sentence") val sentence: String,
    @Json(name = "sentence_trans") val sentenceTrans: String
)