package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class LessonsFragment : Fragment() {

    private var lessonTitle: String? = null
    private var lessonCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lessonTitle = it.getString(ARG_TITLE)
            lessonCategory = it.getString(ARG_CATEGORY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Prepare JSON for LessonView
        val lessonJson = """
            {
                "lesson": {
                    "title": "${lessonTitle ?: "No Title"}",
                    "definition": "${lessonCategory ?: "No Category"}",
                    "vocabulary": []
                }
            }
        """.trimIndent()

        // Create LessonView instance and return its view
        val lessonView = NObLessonView(requireContext(), lessonJson)
        return lessonView.createView(container)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private const val ARG_TITLE = "lesson_title"
        private const val ARG_CATEGORY = "lesson_category"

        fun newInstance(title: String, category: String) =
            LessonsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_CATEGORY, category)
                }
            }
    }
}
