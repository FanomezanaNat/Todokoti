package com.example.myapplication.ui.theme

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TaskItem(
    task: Task,
    onTaskChecked: (Boolean) -> Unit,
    onEditTask: () -> Unit,
    onDeleteTask: () -> Unit,
    onDateChange: (Date?) -> Unit
) {

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onTaskChecked
            )

            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (task.deadline != null) {
                    Text(
                        text = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(task.deadline!!),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (task.deadline != null) {
                    Text(
                        text = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(task.deadline!!),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                IconButton(onClick = {
                    onEditTask()
                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            onDateChange(calendar.time)
                        },
                        year,
                        month,
                        day
                    )
                    datePickerDialog.show()
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Éditer"
                    )
                }

                IconButton(onClick = onDeleteTask) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer"
                    )
                }
            }
        }
    }
}