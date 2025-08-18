package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NObHomeFragment : Fragment() {

    private lateinit var newLayout: LinearLayout
    private lateinit var featuredLayout: LinearLayout
    private lateinit var fragmentContainer: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_n_ob_home, container, false)

        newLayout = root.findViewById(R.id.newLayout)
        featuredLayout = root.findViewById(R.id.featuredLayout)
        fragmentContainer = root.findViewById(R.id.lesson_detail_container)

        // Fetch lessons from Activity
        (requireActivity() as NObHomeActivity).fetchNewLessons(
            onSuccess = { json ->
                populateLessons(json)
            },
            onError = { it.printStackTrace() }
        )

        return root
    }

    private fun populateLessons(json: JSONObject) {
        val courses = json.getJSONObject("courses")
        val categories = courses.keys()

        while (categories.hasNext()) {
            val category = categories.next()
            val lessonsArray = courses.getJSONArray(category)

            for (i in 0 until lessonsArray.length()) {
                val lessonObj = lessonsArray.getJSONObject(i)
                val lessonId = lessonObj.getString("id")
                val lessonTitle = lessonObj.getString("title")
                val lessonDesc = lessonObj.getString("description")

                addLessonButton(category, lessonId, lessonTitle, lessonDesc, newLayout)
            }
        }
    }

    private fun addLessonButton(
        category: String,
        lessonId: String,
        lessonTitle: String,
        lessonDescription: String,
        container: LinearLayout
    ) {
        val btn = Button(requireContext()).apply {
            text = "[$category] $lessonTitle"
            layoutParams = LinearLayout.LayoutParams(300, 300).apply {
                marginEnd = 16
            }
            setOnClickListener { fetchLessonDetail(lessonId) }
        }
        container.addView(btn)
    }

    private fun fetchLessonDetail(lessonId: String) {
        // Get base URL from activity and remove trailing slashes
        val baseUrl = (requireActivity() as NObHomeActivity).getBaseUrl().trimEnd('/')
        if (baseUrl.isEmpty()) return

        // Build the full lesson URL
        val lessonUrl = "$baseUrl/$lessonId"

        // Create a Volley request queue
        val queue = Volley.newRequestQueue(requireContext())

        // Create the GET request
        val request = StringRequest(
            Request.Method.GET,
            lessonUrl,
            { response ->
                // Create and display the lesson view
                val lessonView = NObLessonView(requireContext(), response)
                val view = lessonView.createView(fragmentContainer)
                fragmentContainer.removeAllViews()
                fragmentContainer.addView(view)
            },
            { error ->
                // Print error for debugging
                error.printStackTrace()
            }
        )

        // Add the request to the queue
        queue.add(request)
    }
}
