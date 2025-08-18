package com.example.myapplication

import java.io.Serializable

data class NOdVocabularyItem(
    val word: String,
    val definition: String,
    val lessonTitle: String
) : Serializable