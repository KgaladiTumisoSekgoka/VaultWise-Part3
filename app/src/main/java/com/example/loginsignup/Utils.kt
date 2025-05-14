package com.example.loginsignup

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getTodayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.now().format(formatter)
}

fun getYesterdayDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.now().minusDays(1).format(formatter)
}
