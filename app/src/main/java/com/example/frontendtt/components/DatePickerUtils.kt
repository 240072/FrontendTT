package com.example.frontendtt.components

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

fun showDatePicker(
    context: Context,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val fecha = "%04d-%02d-%02d".format(
                year,
                month + 1,
                day
            )
            onDateSelected(fecha)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
