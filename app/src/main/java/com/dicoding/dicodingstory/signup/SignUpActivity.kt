package com.dicoding.dicodingstory.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.dicodingstory.R
import com.dicoding.dicodingstory.data.ViewModelFactory
import com.dicoding.dicodingstory.data.pref.UserPreferences
import com.dicoding.dicodingstory.data.pref.dataStore
import com.dicoding.dicodingstory.data.response.SignupResponse
import com.dicoding.dicodingstory.databinding.ActivitySignUpBinding
import com.dicoding.dicodingstory.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    // Lazy initialization of ViewModel using a factory
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this, UserPreferences.getInstance(dataStore))
    }

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeView()
        setupActionListeners()
        executeAnimation()
    }

    private fun initializeView() {
        supportActionBar?.hide()
    }

    private fun setupActionListeners() {
        binding.signupButton.setOnClickListener {
            // Tampilkan ProgressBar saat tombol register diklik
            toggleLoading(true)

            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Lakukan permintaan registrasi
            viewModel.register(name, email, password).enqueue(object : Callback<SignupResponse> {
                override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                    // Sembunyikan ProgressBar setelah respons diterima
                    toggleLoading(false)
                    Log.d("SignupActivity", "onResponse: $response")
                    if (response.isSuccessful) {
                        handleSignupSuccess(response.body(), email)
                    } else {
                        showErrorDialog(getString(R.string.signup_failed), getString(R.string.recheck_the_data))
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    // Sembunyikan ProgressBar jika terjadi kesalahan
                    toggleLoading(false)
                    Log.d("SignupActivity", "onFailure: ${t.message}")
                    showErrorDialog(getString(R.string.signup_failed), getString(R.string.gagal_memuat_data))
                }
            })
        }
    }

    private fun handleSignupSuccess(response: SignupResponse?, email: String) {
        if (response != null && !response.error) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.signup_success))
                setMessage("Akun dengan $email sudah terdaftar!")
                setPositiveButton(R.string.oke) { _, _ ->
                    navigateToLogin()
                }
                create()
                show()
            }
        } else {
            Toast.makeText(this, R.string.signup_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.oke, null)
            create()
            show()
        }
    }

    private fun navigateToLogin() {
        Intent(this, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

    private fun executeAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val animations = listOf(
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(110),
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(110),
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(110),
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(110),
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(110),
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(110),
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(110),
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(110)
        )

        AnimatorSet().apply {
            playSequentially(animations)
            start()
        }
    }

    private fun toggleLoading(state: Boolean) {
        if (state) {
            binding.progressBar4.visibility = View.VISIBLE  // Menampilkan ProgressBar
        } else {
            binding.progressBar4.visibility = View.GONE  // Menyembunyikan ProgressBar
        }
    }
}