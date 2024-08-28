package br.com.brunorodrigues.tasklist.commons.extension

import java.text.SimpleDateFormat
import java.util.Locale

const val PATTERN = "dd 'de' MMM 'de' yyyy"
const val PATTERN_BRAZIL = "dd/MM/yyyy"
const val TIME_ZONE_AMERICA_SP = "America/Sao_Paulo"
val LOCALE_BRAZIL = Locale("pt", "BR")

fun String.formatterDateBrazilian(): String {
    val simpleDateFormatBrazilian = SimpleDateFormat(PATTERN_BRAZIL, LOCALE_BRAZIL)
    val simpleDateFormat = SimpleDateFormat(PATTERN, LOCALE_BRAZIL)
    val date = simpleDateFormatBrazilian.parse(this)
    return date?.let { simpleDateFormat.format(it) } ?: "Data inválida"
}