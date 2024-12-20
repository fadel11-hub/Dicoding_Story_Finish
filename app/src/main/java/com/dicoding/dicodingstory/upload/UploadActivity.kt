package com.dicoding.dicodingstory.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.dicodingstory.R
import com.dicoding.dicodingstory.databinding.ActivityUploadBinding
import com.dicoding.dicodingstory.main.MainActivity
import com.dicoding.storyapp.upload.compressImage
import com.dicoding.storyapp.upload.convertUriToFile
import com.dicoding.storyapp.upload.getImageUri

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null
    private lateinit var viewModel: UploadViewModel

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(UploadViewModel::class.java)

        // Menyembunyikan progress bar saat pertama kali aplikasi dibuka
        showLoading(false)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.UploadButton.setOnClickListener { uploadImage() }

        val allPermissionsGranted = ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }

        if (!allPermissionsGranted) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        setupView()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
            }
        }

    private fun startCamera() {
        val uri = getImageUri(this)
        currentImageUri = uri
        launcherIntentCamera.launch(uri)
    }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                showImage()
            } else {
                currentImageUri = null
            }
        }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = convertUriToFile(uri, this).compressImage()
            val description = binding.StoryDesc.text.toString()

            // Menampilkan progress bar saat upload dimulai
            showLoading(true)

            val token = intent.getStringExtra(EXTRA_TOKEN)

            viewModel.uploadImage(
                imageFile,
                description,
                token,
                onSuccess = { successResponse ->
                    showToast(successResponse.message)
                    // Menyembunyikan progress bar setelah upload berhasil
                    showLoading(false)
                    val intent = Intent(this@UploadActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                },
                onError = { errorMessage ->
                    showToast(errorMessage)
                    // Menyembunyikan progress bar setelah upload gagal
                    showLoading(false)
                }
            )
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        // Menampilkan atau menyembunyikan progress bar berdasarkan status isLoading
        binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val EXTRA_TOKEN = "extra_token"
    }

    private fun setupView() {
        supportActionBar?.apply {
            title = getString(R.string.add_story)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}