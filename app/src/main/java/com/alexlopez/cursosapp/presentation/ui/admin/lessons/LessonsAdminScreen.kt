package com.alexlopez.cursosapp.presentation.ui.admin.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.domain.model.Course
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.CourseViewModel
import com.alexlopez.cursosapp.presentation.viewmodel.LessonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsAdminScreen(
    isAdmin: Boolean = true,
    lessonViewModel: LessonViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel(),
) {
    val lessonState by lessonViewModel.uiState.collectAsState()
    val courseState by courseViewModel.uiState.collectAsState()

    var selectedCursoId by remember { mutableStateOf(0) }
    var courseDropdown by remember { mutableStateOf(false) }
    var deleteConfirmLesson by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { courseViewModel.loadCourses() }

    LaunchedEffect(selectedCursoId) {
        if (selectedCursoId > 0) lessonViewModel.loadLessons(selectedCursoId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Lecciones",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    if (isAdmin && selectedCursoId > 0) {
                        FilledTonalButton(
                            onClick = { lessonViewModel.showCreateForm() },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Accent),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AccentOnDark)
                            Spacer(Modifier.width(4.dp))
                            Text("Nueva", color = AccentOnDark)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = courseDropdown,
                    onExpandedChange = { courseDropdown = !courseDropdown },
                ) {
                    val selectedCourse = courseState.courses.find { it.id == selectedCursoId }
                    OutlinedTextField(
                        value = selectedCourse?.let { "${it.titulo} (ID: ${it.id})" } ?: "Seleccionar curso...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso", color = TextFaint) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = Border,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = courseDropdown,
                        onDismissRequest = { courseDropdown = false },
                    ) {
                        courseState.courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text("${course.titulo} (ID: ${course.id})", color = TextPrimary) },
                                onClick = {
                                    selectedCursoId = course.id
                                    courseDropdown = false
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            lessonState.error?.let { error ->
                item {
                    Surface(
                        color = Error.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = error,
                            color = Error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }
            }

            if (selectedCursoId == 0) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("Selecciona un curso para ver sus lecciones", color = TextSecondary) }
                }
            } else if (lessonState.isLoading) {
                item { LoadingScreen() }
            } else if (lessonState.lessons.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("No hay lecciones en este curso", color = TextSecondary) }
                }
            } else {
                items(lessonState.lessons) { lesson ->
                    Surface(
                        color = Surface,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${lesson.orden}.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Accent,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = lesson.titulo,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${lesson.duracionMin} min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                            if (isAdmin) {
                                IconButton(onClick = { lessonViewModel.showEditForm(lesson) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Info)
                                }
                                IconButton(onClick = { deleteConfirmLesson = lesson.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deleteConfirmLesson != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmLesson = null },
            title = { Text("Confirmar eliminaci\u00f3n", fontWeight = FontWeight.Bold) },
            text = { Text("\u00bfEst\u00e1s seguro de eliminar esta lecci\u00f3n?") },
            confirmButton = {
                TextButton(onClick = {
                    lessonViewModel.deleteLesson(selectedCursoId, deleteConfirmLesson!!)
                    deleteConfirmLesson = null
                }) { Text("Eliminar", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmLesson = null }) { Text("Cancelar") }
            },
        )
    }

    if (lessonState.showForm && isAdmin) {
        LessonFormSheet(
            editingLesson = lessonState.editingLesson,
            onDismiss = { lessonViewModel.hideForm() },
            isLoading = lessonState.isLoading,
            error = lessonState.error,
            onErrorDismiss = { lessonViewModel.clearError() },
            onSave = { payload ->
                val edit = lessonState.editingLesson
                if (edit != null) {
                    lessonViewModel.updateLesson(selectedCursoId, edit.id, payload) {
                        lessonViewModel.hideForm()
                    }
                } else {
                    lessonViewModel.createLesson(selectedCursoId, payload) {
                        lessonViewModel.hideForm()
                    }
                }
            },
        )
    }
}
