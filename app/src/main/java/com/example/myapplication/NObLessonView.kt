package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject

class NObLessonView(private val context: Context, private val jsonData: String) {

    private lateinit var rootView: View
    private lateinit var lessonTitle: TextView
    private lateinit var lessonDefinition: TextView
    private lateinit var wordsContainer: LinearLayout

    fun createView(container: ViewGroup?): View {
        // Inflate your layout manually (replace with your actual layout name)
        rootView = LayoutInflater.from(context).inflate(R.layout.activity_lesson_view, container, false)

        // Initialize views
        lessonTitle = rootView.findViewById(R.id.lessonTitle)
        lessonDefinition = rootView.findViewById(R.id.lessonDefinition)
        wordsContainer = rootView.findViewById(R.id.wordsContainer)

        displayLesson(jsonData)
        return rootView
    }

    private fun displayLesson(jsonString: String) {
        try {
            val jsonObject = JSONObject(jsonString)
            val lessonObj = jsonObject.getJSONObject("lesson")

            val title = lessonObj.getString("title")
            val definition = lessonObj.getString("definition")
            val vocabularyArray: JSONArray = lessonObj.getJSONArray("vocabulary")

            lessonTitle.text = title
            lessonDefinition.text = definition
            wordsContainer.removeAllViews()

            for (i in 0 until vocabularyArray.length()) {
                val wordObj = vocabularyArray.getJSONObject(i)
                val word = wordObj.getString("word")
                val wordDef = wordObj.getString("definition")

                val textView = TextView(context).apply {
                    text = "$word: $wordDef"
                    textSize = 16f
                    setPadding(0, 8, 0, 8)
                }
                wordsContainer.addView(textView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            wordsContainer.removeAllViews()
            lessonTitle.text = "Error loading lesson"
            lessonDefinition.text = ""
        }
    }
}
