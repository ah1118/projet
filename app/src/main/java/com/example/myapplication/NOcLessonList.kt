package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.File

class NOcLessonList : AppCompatActivity() {

    private lateinit var lessonAdapter: NOcLessonAdapter
    private lateinit var categoryRecycler: RecyclerView
    private val allLessons = mutableListOf<NOcLesson>()
    private val displayedLessons = mutableListOf<NOcLesson>()
    private var selectedCategory: String = "All"
    private var highlightMode: String = "default" // "default", "auto", "manual"
    private val categories = mutableListOf<String>()
    private var isAllMode = false
    private val visibleCategories = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noc_lesson_list)

        val lessonRecycler = findViewById<RecyclerView>(R.id.lessonRecyclerView)
        categoryRecycler = findViewById(R.id.categoryRecyclerView)

        // Lessons adapter
        lessonAdapter = NOcLessonAdapter(displayedLessons)
        lessonRecycler.layoutManager = LinearLayoutManager(this)
        lessonRecycler.adapter = lessonAdapter

        // Load JSON
        val jsonString = readLessonsJsonFile("lessons.json")
        if (!jsonString.isNullOrEmpty()) {
            parseJson(jsonString)

            // Setup category buttons
            setupCategoryButtons()

            // ✅ Default: select "All" and show all lessons
            selectedCategory = "All"
            highlightMode = "manual"
            showAllLessons()
        }

        // Setup BottomNavigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NOeBottomMenuHelper(this).setupBottomNavigation(bottomNav, R.id.nav_home)

        // Scroll listener to auto-highlight
        lessonRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)

                if (selectedCategory == "All") {
                    val lm = rv.layoutManager as LinearLayoutManager
                    val firstVisiblePos = lm.findFirstVisibleItemPosition()
                    val lastVisiblePos = lm.findLastVisibleItemPosition()

                    visibleCategories.clear()
                    for (i in firstVisiblePos..lastVisiblePos) {
                        if (i in displayedLessons.indices) {
                            visibleCategories.add(displayedLessons[i].category)
                        }
                    }
                    categoryRecycler.adapter?.notifyDataSetChanged()
                    return
                }

                // old auto-highlight logic if not "All"
                val lm = rv.layoutManager as LinearLayoutManager
                val firstVisiblePos = lm.findFirstVisibleItemPosition()

                if (firstVisiblePos in displayedLessons.indices) {
                    val lessonCategory = displayedLessons[firstVisiblePos].category
                    if (highlightMode != "manual") {
                        highlightMode = "auto"
                        selectedCategory = lessonCategory
                        categoryRecycler.adapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun setupCategoryButtons() {
        categoryRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        categoryRecycler.adapter = NOcCategoryAdapter(
            categories,
            { selectedCategory },
            { highlightMode },
            { visibleCategories },
        ) { clickedCategory ->
            if (clickedCategory == "All") {
                isAllMode = true
                highlightMode = "manual"
                selectedCategory = "All"
                showAllLessons()
            } else {
                isAllMode = false
                highlightMode = "manual"
                selectedCategory = clickedCategory
                filterLessonsByCategory(clickedCategory)
            }
            categoryRecycler.adapter?.notifyDataSetChanged()
        }
    }

    private fun readLessonsJsonFile(fileName: String): String? {
        return try {
            val file = File(filesDir, fileName)
            if (file.exists()) file.readText(Charsets.UTF_8) else null
        } catch (e: Exception) {
            null
        }
    }

    private fun parseJson(jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            val courses = json.getJSONObject("courses")
            val keys = courses.keys()

            categories.add("All") // 👈 Always add "All" first

            while (keys.hasNext()) {
                val category = keys.next()
                categories.add(category)
                val arr = courses.getJSONArray(category)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    allLessons.add(
                        NOcLesson(
                            id = obj.getString("id"),
                            category = category,
                            title = obj.getString("title"),
                            description = obj.getString("description")
                        )
                    )
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun filterLessonsByCategory(category: String) {
        displayedLessons.clear()
        selectedCategory = category

        val filtered = allLessons.filter { it.category == category }
        displayedLessons.addAll(filtered)

        lessonAdapter.notifyDataSetChanged()
    }

    private fun showAllLessons() {
        isAllMode = true
        displayedLessons.clear()
        selectedCategory = "All"
        highlightMode = "manual"

        // ✅ Show all lessons at once
        displayedLessons.addAll(allLessons)

        lessonAdapter.notifyDataSetChanged()
    }
}
