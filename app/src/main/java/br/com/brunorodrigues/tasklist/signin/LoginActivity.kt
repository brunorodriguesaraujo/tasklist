package br.com.brunorodrigues.tasklist.signin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import br.com.brunorodrigues.tasklist.R
import br.com.brunorodrigues.tasklist.commons.extension.isEmailValid
import br.com.brunorodrigues.tasklist.commons.extension.showToast
import br.com.brunorodrigues.tasklist.databinding.ActivityLoginBinding
import br.com.brunorodrigues.tasklist.home.HomeActivity
import br.com.brunorodrigues.tasklist.signup.RegisterUserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val email get() = binding.textInputEmail.text.toString()
    private val password get() = binding.textInputPassword.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setFieldsListener()
        goSingUp()
        goHome()
        goForgotPassword()
    }

    private fun setFieldsListener() = with(binding) {
        textInputEmail.addTextChangedListener { textInputLayoutEmail.error = null }
        textInputPassword.addTextChangedListener { textInputLayoutPassword.error = null }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goHomeActivity()
            finish()
        }
    }

    private fun singIn() {
        showLoading()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goHomeActivity()
                    hideLoading()
                } else {
                    hideLoading()
                    showToast(this, getString(R.string.authentication_failed))
                }
            }
    }


    private fun goHome() {
        binding.buttonEnter.setOnClickListener { validateFields() }
    }

    private fun goForgotPassword() {
        binding.textViewForgotPassword.setOnClickListener { goForgotPasswordActivity() }
    }

    private fun validateFields() = with(binding) {
        if (email.isBlank()) {
            textInputLayoutEmail.error = getString(R.string.required_email)
            return
        }

        if (email.isEmailValid()) {
            textInputLayoutEmail.error = getString(R.string.invalid_email)
            return
        }

        if (password.isBlank()) {
            textInputLayoutPassword.error = getString(R.string.required_password)
            return
        }

        singIn()
    }

    private fun goSingUp() {
        binding.buttonSignup.setOnClickListener {
            startActivity(Intent(this, RegisterUserActivity::class.java))
        }
    }

    private fun showLoading() = with(binding) {
        progressBar.isVisible = true
        buttonEnter.text = ""
    }

    private fun hideLoading() = with(binding) {
        progressBar.isVisible = false
        buttonEnter.text = getString(R.string.enter_label)
    }

    private fun goHomeActivity() = startActivity(Intent(this, HomeActivity::class.java))

    private fun goForgotPasswordActivity() =
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
}