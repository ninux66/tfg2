package com.example.crm.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DatePicker(onDateSelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    var selected by remember { mutableStateOf(today) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (0..6).forEach { i ->
            val date = today.plusDays(i.toLong())
            Column(
                modifier = Modifier
                    .clickable {
                        selected = date
                        onDateSelected(date)
                    }
                    .padding(8.dp)
            ) {
                Text(date.dayOfWeek.name.take(3))
                Text(date.dayOfMonth.toString())
            }
        }
    }
}
