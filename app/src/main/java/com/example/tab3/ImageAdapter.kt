package com.example.tab3

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tab3.databinding.ItemImageBinding
import com.example.tab3.databinding.ItemLoadMoreBinding

class ImageAdapter(private val itemClickListener : ItemClickListener) : ListAdapter<ImageItems, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<ImageItems>() {
        override fun areItemsTheSame(oldItem: ImageItems, newItem: ImageItems): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ImageItems, newItem: ImageItems): Boolean {
            return oldItem == newItem
        }

    }
) {
    override fun getItemCount(): Int {
        val originSize = currentList.size
        return if(originSize == 0) 0 else originSize.inc()  // + footer ("사진 불러오기..")
    }

    override fun getItemViewType(position: Int): Int {
        return if(itemCount.dec() == position) ITEM_LOAD_MORE else ITEM_IMAGE
    }

    // ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return when(viewType) {
            ITEM_IMAGE -> {
                val binding = ItemImageBinding.inflate(inflater, parent, false)
                ImageViewHolder(binding)
            }
            else -> {   // ITEM_LOAD_MORE
                val binding = ItemLoadMoreBinding.inflate(inflater, parent, false)
                LoadMoreViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ImageViewHolder -> {
                holder.bind(currentList[position] as ImageItems.Image)
            }
            is LoadMoreViewHolder -> {
                holder.bind(itemClickListener)
            }
        }
    }

    interface ItemClickListener {
        fun onLoadMoreClick()
    }

    companion object {
        const val ITEM_IMAGE = 0
        const val ITEM_LOAD_MORE = 1
    }
}

// various types of data (2 types)
sealed class ImageItems {
    data class Image(
        val uri : Uri,
    ) : ImageItems()
    object LoadMore : ImageItems()
}

class ImageViewHolder(private val binding : ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item : ImageItems.Image) {
        binding.previewImageView.setImageURI(item.uri)
    }
}

class LoadMoreViewHolder(binding : ItemLoadMoreBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(itemClickListener : ImageAdapter.ItemClickListener) {
        itemView.setOnClickListener {
            itemClickListener.onLoadMoreClick()
        }
    }
}