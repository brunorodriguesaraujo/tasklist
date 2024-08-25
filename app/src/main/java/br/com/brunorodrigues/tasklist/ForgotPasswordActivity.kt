package br.com.brunorodrigues.tasklist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import br.com.brunorodrigues.tasklist.databinding.ActivityForgotPasswordBinding
import br.com.brunorodrigues.tasklist.extension.isEmailValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth
    private val email get() = binding.textInputEmail.text.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setButton()
    }

    private fun setButton() {
        binding.buttonSendEmail.setOnClickListener { validateField() }
        binding.imgBack.setOnClickListener { finish() }
    }

    private fun validateField() {
        if (email.isBlank()) {
            binding.textInputLayoutEmail.error = getString(R.string.required_email)
            return
        }

        if (email.isEmailValid()) {
            binding.textInputLayoutEmail.error = getString(R.string.invalid_email)
            return
        }

        sendEmail()
    }

    private fun sendEmail() {
        showLoading()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        getString(R.string.send_link_email),
                        Toast.LENGTH_SHORT
                    ).show()
                    hideLoading()
                } else {
                    hideLoading()
                    Toast.makeText(
                        baseContext, getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    private fun showLoading() = with(binding) {
        progressBar.isVisible = true
        buttonSendEmail.text = ""
    }

    private fun hideLoading() = with(binding) {
        progressBar.isVisible = false
        buttonSendEmail.text = getString(R.string.send)
    }
}