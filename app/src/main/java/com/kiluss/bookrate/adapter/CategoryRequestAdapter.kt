package com.kiluss.bookrate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.databinding.ItemCategoryRequestBinding

class CategoryRequestAdapter(
    private val categorys: List<String>,
    private val categoryRequestAdapterInterface: CategoryRequestAdapterInterface
) : RecyclerView.Adapter<CategoryRequestAdapter.BookPreviewHolder>() {

    interface CategoryRequestAdapterInterface {
        fun onClearCategoryClick(adapterPosition: Int, category: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookPreviewHolder {
        val binding =
            ItemCategoryRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookPreviewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookPreviewHolder, position: Int) {
        holder.bindView(categorys[position])
    }

    override fun getItemCount(): Int {
        return categorys.size
    }

    inner class BookPreviewHolder(
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
