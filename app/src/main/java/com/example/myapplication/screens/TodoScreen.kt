package com.example.myapplication.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Task
import com.example.myapplication.ui.theme.TaskItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TodoScreen() {
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }

    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDeadline by remember { mutableStateOf<Date?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var editingTask by remember { mutableStateOf<Task?>(null) }
    var editingTaskDeadline by remember { mutableStateOf<Date?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ma Liste de Tâches") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.inverseOnSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(Icons.Filled.Add, "Add task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false; showError = false; newTaskTitle = ""; isEditing = false; editingTask = null; editingTaskDeadline = null
                    },
                    title = { Text(if (isEditing) "Modifier la tâche" else "Nouvelle tâche") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = newTaskTitle,
                                onValueChange = { newTaskTitle = it; showError = false },
                                label = { Text("Titre") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth(),
                                isError = showError
                            )
                            if (showError) {
                                Text(
                                    text = "Le titre ne peut pas être vide",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Text(
                                text = if (newTaskDeadline != null) SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                                ).format(newTaskDeadline!!) else "Sélectionner une date limite",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .clickable {
                                        val datePickerDialog = DatePickerDialog(
                                            context,
                                            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                                calendar.set(Calendar.YEAR, year)
                                                calendar.set(Calendar.MONTH, month)
                                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                newTaskDeadline = calendar.time
                                            },
                                            year,
                                            month,
                                            day
                                        )
                                        datePickerDialog.datePicker.minDate =
                                            Calendar.getInstance().timeInMillis
                                        datePickerDialog.show()
                                    }
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newTaskTitle.isNotEmpty()) {
                                    if (isEditing) {
                                        tasks = tasks.map { task ->
                                            if (task.id == editingTask?.id) {
                                                task.copy(title = newTaskTitle, deadline = newTaskDeadline)
                                            } else {
                                                task
                                            }
                                        }
                                        isEditing = false
                                        editingTask = null
                                        editingTaskDeadline = null
                                    } else {
                                        val newTask = Task(
                                            id = tasks.size + 1,
                                            title = newTaskTitle,
                                            deadline = newTaskDeadline
                                        )
                                        tasks = tasks + newTask
                                    }
                                    newTaskTitle = ""
                                    newTaskDeadline = null
                                    showDialog = false
                                    showError = false
                                } else {
                                    showError = true
                                }
                            }
                        ) {
                            Text(if (isEditing) "Mettre à jour" else "Ajouter")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showDialog = false; showError = false; newTaskTitle = ""; isEditing = false; editingTask = null; editingTaskDeadline = null
                        }) {
                            Text("Annuler")
                        }
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onTaskChecked = { isChecked ->
                            tasks =
                                tasks.map { if (it.id == task.id) it.copy(isCompleted = isChecked) else it }
                        },
                        onEditTask = {
                            isEditing = true
                            editingTask = task
                            newTaskTitle = task.title
                            newTaskDeadline = task.deadline
                            showDialog = true
                            showError = false
                        },
                        onDeleteTask = {
                            tasks = tasks.filter { it.id != task.id }
                        },
                        onDateChange = { date ->
                            editingTaskDeadline = date
                        }
                    )
                }
            }
        }
    }
}