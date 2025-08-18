package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NOcCategoryAdapter(
    private val categories: List<String>,
    private val getSelectedCategory: () -> String,
    private val getHighlightMode: () -> String,
    private val getVisibleCategories: () -> Set<String>, // 👈 add this
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<NOcCategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.categoryText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.text.text = category

        // ✅ Color logic
        val selected = getSelectedCategory()
        val mode = getHighlightMode()
        val visibleCats = getVisibleCategories()

        when {
            category == "All" && selected == "All" -> {
                holder.text.setBackgroundColor(0xFF2196F3.toInt()) // blue
            }
            category in visibleCats && selected == "All" -> {
                holder.text.setBackgroundColor(0xFF4CAF50.toInt()) // green
            }
            category == selected && mode == "manual" -> {
                holder.text.setBackgroundColor(0xFFF44336.toInt()) // red
            }
            category == selected && mode == "auto" -> {
                holder.text.setBackgroundColor(0xFF4CAF50.toInt()) // green
            }
            else -> {
                holder.text.setBackgroundColor(0xFFE0E0E0.toInt()) // gray
            }
        }

        holder.text.setOnClickListener {
            onCategoryClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
