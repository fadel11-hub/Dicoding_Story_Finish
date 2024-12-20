package com.dicoding.dicodingstory.detailstory

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.dicodingstory.R
import com.dicoding.dicodingstory.data.response.StoryItem
import com.dicoding.dicodingstory.databinding.ActivityDetailBinding
import com.dicoding.dicodingstory.main.MainActivity
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        val token = intent.getStringExtra(EXTRA_TOKEN)
        val id = intent.getStringExtra(EXTRA_ID)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        // Periksa apakah token dan id valid
        if (!token.isNullOrEmpty() && !id.isNullOrEmpty()) {
            fetchStoryDetails(id, token)
        } else {
            showErrorDialog()
        }

        setupActionBar()
    }

    /**
     * Mengatur action bar dengan judul dan tombol kembali.
     */
    private fun setupActionBar() {
        supportActionBar?.apply {
            title = getString(R.string.detail_story)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    /**
     * Memuat detail cerita dari ViewModel.
     */
    private fun fetchStoryDetails(id: String, token: String) {
        lifecycleScope.launch {
            viewModel.setDetailStory(id, "Bearer $token")
            showLoading(true)
        }

        viewModel.getDetailStory().observe(this, { story ->
            story?.let {
                bindStoryToView(it)
                showLoading(false)
            }
        })
    }

    /**
     * Mengikat data cerita ke tampilan.
     */
    private fun bindStoryToView(story: StoryItem) {
        binding.apply {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivDetailPhoto)
        }
    }

    /**
     * Menampilkan dialog kesalahan ketika data tidak valid.
     */
    private fun showErrorDialog() {
        AlertDialog.Builder(this@DetailActivity).apply {
            setTitle(R.string.gagal_memuat)
            setMessage(R.string.gagal_memuat_data)
            setPositiveButton(R.string.oke) { _, _ ->
                navigateToMainActivity()
            }
            create()
        }.show()
    }

    /**
     * Navigasi kembali ke MainActivity.
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    /**
     * Menampilkan atau menyembunyikan indikator pemuatan.
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TOKEN = "extra_token"
    }
}