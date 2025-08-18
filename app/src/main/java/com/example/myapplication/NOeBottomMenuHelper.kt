package com.example.myapplication

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

class NOeBottomMenuHelper(private val activity: Activity) {

    fun setupBottomNavigation(bottomNav: BottomNavigationView, selectedId: Int) {
        bottomNav.selectedItemId = selectedId

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (activity !is NObHomeActivity) {
                        activity.startActivity(Intent(activity, NObHomeActivity::class.java))
                        activity.finish()
                    }
                    true
                }

                R.id.nav_course -> {
                    if (activity !is NOcLessonList) {
                        activity.startActivity(Intent(activity, NOcLessonList::class.java))
                        activity.finish()
                    }
                    true
                }

                R.id.nav_profile -> {
                    if (activity !is NOfProfileActivity) {
                        activity.startActivity(Intent(activity, NOfProfileActivity::class.java))
                        activity.finish()
                    }
                    true
                }

                else -> false
            }
        }
    }
}
