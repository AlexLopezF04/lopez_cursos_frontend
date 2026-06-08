package com.alexlopez.cursosapp.presentation.ui.admin.reviews

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
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.CourseViewModel
import com.alexlopez.cursosapp.presentation.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsAdminScreen(
    isAdmin: Boolean = true,
    reviewViewModel: ReviewViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel(),
) {
    val reviewState by reviewViewModel.uiState.collectAsState()
    val courseState by courseViewModel.uiState.collectAsState()

    var selectedCursoId by remember { mutableStateOf(0) }
    var courseDropdown by remember { mutableStateOf(false) }
    var deleteConfirmReview by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { courseViewModel.loadCourses() }

    LaunchedEffect(selectedCursoId) {
        if (selectedCursoId > 0) reviewViewModel.loadReviews(selectedCursoId)
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
                        text = "Rese\u00f1as",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    if (isAdmin && selectedCursoId > 0) {
                        FilledTonalButton(
                            onClick = { reviewViewModel.showCreateForm() },
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

            reviewState.error?.let { error ->
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
                    ) { Text("Selecciona un curso para ver sus rese\u00f1as", color = TextSecondary) }
                }
            } else if (reviewState.isLoading) {
                item { LoadingScreen() }
            } else if (reviewState.reviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("No hay rese\u00f1as para este curso", color = TextSecondary) }
                }
            } else {
                items(reviewState.reviews) { review ->
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
                                    repeat(5) { i ->
                                        Icon(
                                            imageVector = if (i < review.calificacion) Icons.Default.Star else Icons.Default.StarOutline,
                                            contentDescription = null,
                                            tint = if (i < review.calificacion) Warning else TextFaint,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "${review.calificacion}/5",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                if (review.comentario.isNotBlank()) {
                                    Text(
                                        text = review.comentario,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                Text(
                                    text = "${review.usuarioNombre} \u2022 ${review.createdAt}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextFaint,
                                )
                            }
                            if (isAdmin) {
                                IconButton(onClick = { reviewViewModel.showEditForm(review) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Info)
                                }
                                IconButton(onClick = { deleteConfirmReview = review.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (deleteConfirmReview != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmReview = null },
            title = { Text("Confirmar eliminaci\u00f3n", fontWeight = FontWeight.Bold) },
            text = { Text("\u00bfEst\u00e1s seguro de eliminar esta rese\u00f1a?") },
            confirmButton = {
                TextButton(onClick = {
                    reviewViewModel.deleteReview(selectedCursoId, deleteConfirmReview!!)
                    deleteConfirmReview = null
                }) { Text("Eliminar", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmReview = null }) { Text("Cancelar") }
            },
        )
    }

    if (reviewState.showForm && isAdmin) {
        ReviewFormSheet(
            editingReview = reviewState.editingReview,
            onDismiss = { reviewViewModel.hideForm() },
            isLoading = reviewState.isLoading,
            error = reviewState.error,
            onErrorDismiss = { reviewViewModel.clearError() },
            onSave = { payload ->
                val edit = reviewState.editingReview
                if (edit != null) {
                    reviewViewModel.updateReview(selectedCursoId, edit.id, payload)
                } else {
                    reviewViewModel.createReview(selectedCursoId, payload)
                }
                reviewViewModel.hideForm()
            },
        )
    }
}
