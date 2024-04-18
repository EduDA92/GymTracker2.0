package com.example.gymtracker.ui.model

import java.time.LocalDate

enum class FilterDates(val text: String, val time: LocalDate) {
    OneMonth("1 Month", LocalDate.now().minusMonths(1)),
    ThreeMonths("3 Months", LocalDate.now().minusMonths(3)),
    SixMonths("6 Months", LocalDate.now().minusMonths(6))
}