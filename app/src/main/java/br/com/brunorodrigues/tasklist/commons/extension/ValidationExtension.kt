package br.com.brunorodrigues.tasklist.commons.extension

import android.util.Patterns

fun String.isEmailValid() = !Patterns.EMAIL_ADDRESS.matcher(this).matches()