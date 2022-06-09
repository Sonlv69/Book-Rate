package com.kiluss.bookrate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.databinding.ItemStringRequestBinding

class ListRequestAdapter(
    private val listRequests: List<String>,
    private val listRequestAdapterInterface: ListRequestAdapterInterface,
    private val isNewItem: Boolean
) : RecyclerView.Adapter<ListRequestAdapter.MyViewHolder>() {

    interface ListRequestAdapterInterface {
        fun onClearItemClick(adapterPosition: Int, category: String)
        fun onClearNewItemClick(adapterPosition: Int, category: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemStringRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(listRequests[position])
    }

    override fun getItemCount(): Int {
        return listRequests.size
    }

    inner class MyViewHolder(
        val binding: ItemStringRequestBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: String) {
            binding.tvString.text = item

            binding.ivClear.setOnClickListener{
                if (isNewItem) {
                    listRequestAdapterInterface.onClearNewItemClick(adapterPosition, item)
                } else {
                    listRequestAdapterInterface.onClearItemClick(adapterPosition, item)
                }
            }
        }
    }
}
