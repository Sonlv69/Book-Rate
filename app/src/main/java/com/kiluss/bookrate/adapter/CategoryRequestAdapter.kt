package com.kiluss.bookrate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.databinding.ItemCategoryRequestBinding

class CategoryRequestAdapter(
    private val categories: List<String>,
    private val categoryRequestAdapterInterface: CategoryRequestAdapterInterface
) : RecyclerView.Adapter<CategoryRequestAdapter.CategoryHolder>() {

    interface CategoryRequestAdapterInterface {
        fun onClearCategoryClick(adapterPosition: Int, category: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding =
            ItemCategoryRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bindView(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class CategoryHolder(
        val binding: ItemCategoryRequestBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(category: String) {
            binding.tvCategory.text = category

            binding.ivClear.setOnClickListener{
                categoryRequestAdapterInterface.onClearCategoryClick(adapterPosition, category)
            }
        }
    }
}
