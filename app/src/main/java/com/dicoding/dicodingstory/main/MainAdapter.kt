package com.dicoding.dicodingstory.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.dicodingstory.data.response.StoryItem
import com.dicoding.dicodingstory.databinding.ItemStoryBinding

class MainAdapter : RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    private val storyList = ArrayList<StoryItem>()
    private var itemClickCallback: OnItemClickCallback? = null

    /**
     * Mengatur callback untuk item yang diklik.
     */
    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        this.itemClickCallback = callback
    }

    /**
     * Memperbarui daftar cerita dengan data baru.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setList(newStoryList: ArrayList<StoryItem>) {
        storyList.clear()
        storyList.addAll(newStoryList)
        notifyDataSetChanged()
    }

    /**
     * ViewHolder untuk menampilkan data pada setiap item cerita.
     */
    inner class DataViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(storyItem: StoryItem) {
            // Mengatur callback klik item
            binding.root.setOnClickListener {
                itemClickCallback?.onItemClicked(storyItem)
            }

            // Menampilkan data ke tampilan
            binding.apply {
                uploader.text = storyItem.name
                deskripsi.text = getTruncatedDescription(storyItem.description)
                Glide.with(itemView)
                    .load(storyItem.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageItem)
            }
        }

        /**
         * Memotong deskripsi agar lebih ringkas.
         */
        private fun getTruncatedDescription(description: String): String {
            val maxLines = 1
            val maxCharsPerLine = 25
            val lines = description.split("\n")
            val truncatedDescription = StringBuilder()

            for (i in 0 until maxLines) {
                if (i < lines.size) {
                    val line = lines[i]
                    truncatedDescription.append(
                        if (line.length <= maxCharsPerLine) line
                        else line.substring(0, maxCharsPerLine) + "..."
                    )
                    if (i != maxLines - 1 && i < lines.size - 1) {
                        truncatedDescription.append("...\n")
                    }
                }
            }
            return truncatedDescription.toString().trim()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount(): Int = storyList.size

    /**
     * Interface untuk menangani klik item.
     */
    interface OnItemClickCallback {
        fun onItemClicked(data: StoryItem)
    }
}