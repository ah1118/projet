package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class NOcLessonAdapter(
    private val lessons: List<NOcLesson>
) : RecyclerView.Adapter<NOcLessonAdapter.LessonViewHolder>() {

    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.lessonButton)
        val description: TextView = itemView.findViewById(R.id.lessonDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]

        holder.button.text = lesson.title
        holder.description.text = lesson.description

        // 👉 On click: open NOdLessonPractice and pass lesson.id
        holder.button.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NOdLessonPractice::class.java)
            intent.putExtra("lesson_id", lesson.id) // 👈 pass the lesson ID
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = lessons.size
}
