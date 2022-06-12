package com.kiluss.bookrate.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.data.model.MyTags
import com.kiluss.bookrate.data.model.Tag
import com.kiluss.bookrate.databinding.ItemAddCategoryBinding

class AddCategoryAdapter(
    private val categories: ArrayList<Tag>,
    private val myCategories: ArrayList<MyTags>,
    private val categoryNameAdapterInterface: CategoryNameAdapterInterface
) : RecyclerView.Adapter<AddCategoryAdapter.CategoryHolder>() {

    interface CategoryNameAdapterInterface {
        fun onCategoryClick(adapterPosition: Int, id: Int?)
        fun onPostMyTagClick(adapterPosition: Int, id: Int?)
        fun onDeleteMyTagClick(adapterPosition: Int, id: Int?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding =
            ItemAddCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bindView(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class CategoryHolder(
        val binding: ItemAddCategoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(category: Tag) {
            binding.tvString.text = category.name
            binding.tvString.setOnClickListener {
                categoryNameAdapterInterface.onCategoryClick(adapterPosition, category.id)
            }
            binding.checkbox.setOnClickListener{
                if (binding.checkbox.isChecked) {
                    categoryNameAdapterInterface.onPostMyTagClick(adapterPosition, category.id)
                    myCategories.add(MyTags(null, null, category.id))
                } else {
                    categoryNameAdapterInterface.onDeleteMyTagClick(adapterPosition, category.id)
                    val newList = arrayListOf<MyTags>()
                    newList.addAll(myCategories)
                    myCategories.forEach {
                        if (it.iDTag == category.id) {
                            newList.remove(it)
                            return@forEach
                        }
                    }
                    myCategories.clear()
                    myCategories.addAll(newList)
                }
            }
            myCategories.forEach {
                if (category.id == it.iDTag) {
                    binding.checkbox.isChecked = true
                    return
                } else binding.checkbox.isChecked = false
            }
        }
    }
}
