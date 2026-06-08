package com.alexlopez.cursosapp.presentation.ui.admin.courses

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
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.components.NivelBadge
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.CourseViewModel

@Composable
fun CoursesAdminScreen(
    isAdmin: Boolean = true,
    currentUserId: Int? = null,
    viewModel: CourseViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var deleteConfirmId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { viewModel.loadCourses() }

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
                        text = "Cursos",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    if (isAdmin) {
                        FilledTonalButton(
                            onClick = { viewModel.showCreateForm() },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Accent),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AccentOnDark)
                            Spacer(Modifier.width(4.dp))
                            Text("Nuevo", color = AccentOnDark)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            uiState.error?.let { error ->
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

            if (uiState.isLoading) {
                item { LoadingScreen() }
            } else if (uiState.courses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("No hay cursos", color = TextSecondary) }
                }
            } else {
                items(uiState.courses) { course ->
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
                                Text(
                                    text = course.titulo,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    NivelBadge(nivel = course.nivel)
                                    Text(
                                        text = "$${String.format("%.2f", course.precio)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Accent,
                                    )
                                }
                            }
                            if (isAdmin || course.instructorId == currentUserId) {
                                IconButton(onClick = { viewModel.showEditForm(course) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Info)
                                }
                                IconButton(onClick = { deleteConfirmId = course.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
                                }
                            }
                        }
                    }
                }

                if (uiState.hasMore) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (uiState.isLoadingMore) {
                                CircularProgressIndicator(color = Accent, modifier = Modifier.size(24.dp))
                            } else {
                                TextButton(onClick = { viewModel.loadMore() }) {
                                    Text("Cargar m\u00e1s", color = Accent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deleteConfirmId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmId = null },
            title = { Text("Confirmar eliminaci\u00f3n", fontWeight = FontWeight.Bold) },
            text = { Text("\u00bfEst\u00e1s seguro de eliminar este curso?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCourse(deleteConfirmId!!)
                    deleteConfirmId = null
                }) { Text("Eliminar", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) { Text("Cancelar") }
            },
        )
    }

    if (uiState.showForm) {
        CourseFormSheet(
            editingCourse = uiState.editingCourse,
            categories = uiState.categories,
            onDismiss = { viewModel.hideForm() },
            isLoading = uiState.isLoading,
            error = uiState.error,
            onErrorDismiss = { viewModel.clearError() },
            onSave = { payload ->
                val edit = uiState.editingCourse
                if (edit != null) {
                    viewModel.updateCourse(edit.id, payload) { viewModel.hideForm() }
                } else {
                    viewModel.createCourse(payload) { viewModel.hideForm() }
                }
            },
        )
    }
}
