package com.kiluss.bookrate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.data.model.Tags
import com.kiluss.bookrate.databinding.ItemTextStringBinding

class CategoryNameAdapter(
    private val categories: ArrayList<Tags>,
    private val categoryNameAdapterInterface: CategoryNameAdapterInterface
) : RecyclerView.Adapter<CategoryNameAdapter.CategoryHolder>() {

    interface CategoryNameAdapterInterface {
        fun onCategoryClick(adapterPosition: Int, id: Int?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding =
            ItemTextStringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bindView(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class CategoryHolder(
        val binding: ItemTextStringBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(category: Tags) {
            binding.tvString.text = category.tag?.name
            binding.tvString.setOnClickListener {
                categoryNameAdapterInterface.onCategoryClick(adapterPosition, category.iDTag)
            }
        }
    }
}
