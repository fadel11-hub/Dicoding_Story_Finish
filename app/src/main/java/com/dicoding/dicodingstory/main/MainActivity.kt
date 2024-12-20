package com.dicoding.dicodingstory.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingstory.R
import com.dicoding.dicodingstory.customview.maps.MapsActivity
import com.dicoding.dicodingstory.data.ViewModelFactory
import com.dicoding.dicodingstory.data.adapter.ListStoryAdapter
import com.dicoding.dicodingstory.data.adapter.LoadingStateAdapter
import com.dicoding.dicodingstory.data.pref.UserPreferences
import com.dicoding.dicodingstory.data.pref.dataStore
import com.dicoding.dicodingstory.data.response.ItemStoryResponse
import com.dicoding.dicodingstory.databinding.ActivityMainBinding
import com.dicoding.dicodingstory.detailstory.DetailActivity
import com.dicoding.dicodingstory.upload.UploadActivity
import com.dicoding.dicodingstory.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, UserPreferences.getInstance(dataStore))
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(true)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                getData()
                showLoading(false)
            }
        }

        binding.fabNewStory.setOnClickListener {
            lifecycleScope.launch {
                val token = UserPreferences.getInstance(dataStore).getToken()
                Intent(this@MainActivity, UploadActivity::class.java).also {
                    it.putExtra(UploadActivity.EXTRA_TOKEN, token)
                    startActivity(it)
                }
            }
        }

        binding.apply {
            rvUser.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUser.setHasFixedSize(true)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true // Pastikan return true, bukan super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                // Fungsi log out
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun logoutUser() {
        lifecycleScope.launch {
            // Menghapus sesi pengguna
            UserPreferences.getInstance(dataStore).logout()

            // Menavigasi ke halaman selamat datang setelah log out
            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()  // Menutup MainActivity agar tidak bisa kembali
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getData() {
        val adapter = ListStoryAdapter()
        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickcallBack {
            override fun onItemClicked(data: ItemStoryResponse) {
                lifecycleScope.launch {
                    val token = UserPreferences.getInstance(dataStore).getToken() ?: ""
                    val intentDetail = Intent(this@MainActivity, DetailActivity::class.java)
                    intentDetail.putExtra(DetailActivity.EXTRA_ID, data.id) // Kirim ID sebagai String
                    intentDetail.putExtra(DetailActivity.EXTRA_TOKEN, token) // Kirim token
                    startActivity(intentDetail)
                }
            }
        })

        binding.rvUser.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.story.observe(this, {
            adapter.submitData(lifecycle, it)
        })
        showLoading(false)
    }
}