package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject



class NOdLessonPractice : AppCompatActivity() {

    private var lessonId: String? = null
    private var baseUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nod_lesson_practice)

        lessonId = intent.getStringExtra("lesson_id")
        baseUrl = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getString("lesson_url", "")

        fetchLessonDetail(lessonId!!)
    }

    private fun fetchLessonDetail(lessonId: String) {
        val url = baseUrl!!.trimEnd('/') + "/" + lessonId
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    val lesson = json.getJSONObject("lesson")

                    val vocabularyList = mutableListOf<NOdVocabularyItem>()
                    val vocabArray = lesson.getJSONArray("vocabulary")
                    for (i in 0 until vocabArray.length()) {
                        val obj = vocabArray.getJSONObject(i)
                        vocabularyList.add(
                            NOdVocabularyItem(
                                obj.getString("word"),
                                obj.getString("definition"),
                                lesson.getString("title")
                            )
                        )
                    }

                    // ✅ Get Messages JSON
                    val messages = lesson.getJSONObject("Messages")

                    // ✅ Pass both vocab + messages into the fragment
                    val fragment = NOdDefinition.newInstance(vocabularyList, messages)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error -> error.printStackTrace() }
        )
        queue.add(request)
    }
}