package com.dicoding.dicodingstory.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.dicodingstory.data.response.ItemStoryResponse
import com.dicoding.dicodingstory.databinding.ItemStoryBinding

class ListStoryAdapter :
    PagingDataAdapter<ItemStoryResponse, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickcallBack: OnItemClickcallBack? = null

    fun setOnItemClickCallback (onItemClickcallBack: OnItemClickcallBack) {
        this.onItemClickcallBack = onItemClickcallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class MyViewHolder(private val binding: ItemStoryBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ItemStoryResponse) {

            binding.root.setOnClickListener {
                onItemClickcallBack?.onItemClicked(data)
            }

            binding.uploader.text = data.name
            val maxLines = 1
            val maxCharsPerLine = 25

            val originalDescription = data.description
            val lines = originalDescription!!.split("\n")

            var truncatedDescription = ""

            for (i in 0 until maxLines) {
                if (i < lines.size) {
                    val line = lines[i]
                    if (line.length <= maxCharsPerLine) {
                        truncatedDescription += line
                    } else {
                        truncatedDescription += line.substring(0, maxCharsPerLine) + "..."
                    }
                    if (i != lines.size - 1) {
                        truncatedDescription += "...\n"
                    }
                }
            }

            binding.deskripsi.text = truncatedDescription.trim()
            Glide.with(itemView)
                .load(data.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.imageItem)

        }
    }

    interface OnItemClickcallBack{
        fun onItemClicked(data: ItemStoryResponse)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemStoryResponse>() {
            override fun areItemsTheSame(oldItem: ItemStoryResponse, newItem: ItemStoryResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ItemStoryResponse, newItem: ItemStoryResponse): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}