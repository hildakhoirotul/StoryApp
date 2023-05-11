package com.example.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.adapter.StoryAdapter.ViewHolder.Companion.DIFF_CALLBACK
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.ui.DetailStoryActivity
import com.example.storyapp.utils.Constanta.EXTRA_STORY
import com.example.storyapp.utils.Setting

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.tvUsername.text = data.name
            binding.tvUploadTime.text =
                Setting.getStoryTime(
                    itemView.context,
                    data.createdAt
                )
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivStory)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java).apply {
                    putExtra(EXTRA_STORY, data)
                }

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.tvUsername, "name"),
                        Pair(binding.tvUploadTime, "time"),
                        Pair(binding.ivStory, "story"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        companion object {
            val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(
                    oldItem: ListStoryItem,
                    newItem: ListStoryItem,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: ListStoryItem,
                    newItem: ListStoryItem,
                ): Boolean {
                    return oldItem.id == newItem.id
                }
            }
        }
    }
}