package com.example.storyapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.model.Story
import com.example.storyapp.ui.viewmodel.DetailViewModel
import com.example.storyapp.utils.Constanta.EXTRA_STORY
import com.example.storyapp.utils.Preferences
import com.example.storyapp.utils.Setting
import com.example.storyapp.utils.getAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var pref: Preferences
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY) as ListStoryItem

        pref = Preferences(this)
        val token = pref.token.toString()

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getDetailWithLocation("Bearer $token", story.id)
                .observe(this@DetailStoryActivity) {
                    setDetailData(it.story)
                }
            viewModel.isLoading.observe(this@DetailStoryActivity) {
                showLoading(it)
            }
            viewModel.error.observe(this@DetailStoryActivity) {
                it?.let {
                    if (it.isNotEmpty()) {
                        Toast.makeText(this@DetailStoryActivity, getString(R.string.failed) + it, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            lat = story.lat?.toDouble() ?: 0.0
            lon = story.lon?.toDouble() ?: 0.0
        }
    }

    private fun setDetailData(detail: Story?) {
        binding.apply {
            Glide.with(this@DetailStoryActivity)
                .load(detail?.photoUrl)
                .into(ivStory)
            tvUploadTime.text =
                Setting.getStoryTime(
                    this@DetailStoryActivity,
                    detail?.createdAt ?: "0"
                )
            tvUsername.text = detail?.name
            tvDesc.text = detail?.description
            try {
                binding.tvLocation.text = getAddress(this@DetailStoryActivity, lat, lon)
                binding.storyLocation.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.storyLocation.visibility = View.GONE
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.root.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}