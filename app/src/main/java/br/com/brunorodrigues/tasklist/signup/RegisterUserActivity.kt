package br.com.brunorodrigues.tasklist.signup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import br.com.brunorodrigues.tasklist.R
import br.com.brunorodrigues.tasklist.databinding.ActivityRegisterUserBinding
import br.com.brunorodrigues.tasklist.extension.isEmailValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserBinding
    private lateinit var auth: FirebaseAuth
    private val email get() = binding.textInputEmail.text.toString()
    private val password get() = binding.textInputPassword.text.toString()
    private val passwordConfirmation get() = binding.textInputPasswordConfirmation.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setListener()
    }

    private fun setListener() = with(binding) {
        binding.imgBack.setOnClickListener { finish() }

        buttonSignup.setOnClickListener {
            validateFields()
        }

        textInputEmail.addTextChangedListener {
            textInputLayoutEmail.error = null
        }
        textInputPassword.addTextChangedListener {
            textInputLayoutPassword.error = null
        }
        textInputPasswordConfirmation.addTextChangedListener {
            textInputLayoutPasswordConfirmation.error = null
        }

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

        if (passwordConfirmation.isBlank()) {
            textInputLayoutPasswordConfirmation.error = getString(R.string.required_password)
            return
        }

        if (password.length < 6) {
            textInputLayoutPassword.error = getString(R.string.required_minimun_length_password)
            return
        }

        if (password != passwordConfirmation) {
            textInputLayoutPasswordConfirmation.error = getString(R.string.different_password)
            return
        }

        createAccount()
    }

    private fun createAccount() {
        showLoading()
        auth.createUserWithEmailAndPassword(email, passwordConfirmation)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.created_account),
                        Toast.LENGTH_SHORT,
                    ).show()
                    auth.signOut()
                    finish()
                    hideLoading()
                } else {
                    hideLoading()
                    Toast.makeText(
                        baseContext,
                        getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun showLoading() = with(binding) {
        constraintFields.isVisible = false
        progressBar.isVisible = true
    }

    private fun hideLoading() = with(binding) {
        constraintFields.isVisible = true
        progressBar.isVisible = false
    }
}