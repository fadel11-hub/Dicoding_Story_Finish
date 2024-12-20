package com.dicoding.dicodingstory.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.dicodingstory.R
import com.dicoding.dicodingstory.data.ViewModelFactory
import com.dicoding.dicodingstory.data.pref.UserModel
import com.dicoding.dicodingstory.data.pref.UserPreferences
import com.dicoding.dicodingstory.data.pref.dataStore
import com.dicoding.dicodingstory.data.response.LoginResponse
import com.dicoding.dicodingstory.databinding.ActivityLoginBinding
import com.dicoding.dicodingstory.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this, UserPreferences.getInstance(dataStore))
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            // Tampilkan loading sebelum memulai proses login
            showLoading(true)

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Mulai proses login
            viewModel.login(email, password).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    // Pastikan progress bar disembunyikan setelah menerima response
                    showLoading(false)

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null && !loginResponse.error) {
                            val loginResult = loginResponse.loginResult
                            val user = UserModel(loginResult.userId, loginResult.name, loginResult.token)
                            viewModel.saveSession(user)

                            // Dialog dan Intent setelah login berhasil
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle(R.string.yayy)
                                setMessage(R.string.login_successful)
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                                create()
                                show()
                            }
                            Toast.makeText(this@LoginActivity, R.string.login_successful, Toast.LENGTH_SHORT).show()
                        } else {
                            // Jika login gagal, tampilkan pesan error
                            showErrorDialog()
                        }
                    } else {
                        // Jika response gagal, tampilkan error
                        showErrorDialog()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Gagal mengambil data, sembunyikan progress bar dan tampilkan error
                    showLoading(false)
                    showErrorDialog()
                }
            })
        }
    }

    private fun showErrorDialog() {
        // Menampilkan error dialog
        AlertDialog.Builder(this@LoginActivity).apply {
            setTitle(R.string.login_failed)
            setMessage(R.string.username_password_salah)
            setPositiveButton(R.string.oke) { _, _ -> }
            create()
            show()
        }
        Toast.makeText(this@LoginActivity, R.string.login_failed, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        // Animasi untuk UI
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val text = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(120)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(120)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(120)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(120)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(120)
        val passwordEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(120)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(120)

        val together = AnimatorSet().apply {
            playTogether(login)
        }

        AnimatorSet().apply {
            playSequentially(text, message, email, emailEdit, password, passwordEdit, together)
            start()
        }
    }

    private fun showLoading(state: Boolean) {
        // Mengatur visibilitas progress bar
        if (state) {
            binding.progressBar3.visibility = View.VISIBLE // Menampilkan ProgressBar
        } else {
            binding.progressBar3.visibility = View.GONE // Menyembunyikan ProgressBar
        }
    }
}