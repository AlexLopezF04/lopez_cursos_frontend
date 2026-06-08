package com.alexlopez.cursosapp.presentation.ui.admin.enrollments

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
import com.alexlopez.cursosapp.presentation.viewmodel.EnrollmentViewModel

@Composable
fun EnrollmentsAdminScreen(
    isAdmin: Boolean = true,
    viewModel: EnrollmentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.listState.collectAsState()
    var deleteConfirmId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { viewModel.loadEnrollments() }

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
                        text = "Matr\u00edculas",
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
                            Text("Nueva", color = AccentOnDark)
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
            } else if (uiState.enrollments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("No hay matr\u00edculas", color = TextSecondary) }
                }
            } else {
                items(uiState.enrollments) { enrollment ->
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
                                    text = enrollment.cursoTitulo,
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
                                    Surface(
                                        color = when (enrollment.estado.value) {
                                            "activa" -> Success.copy(alpha = 0.15f)
                                            "vencida" -> Error.copy(alpha = 0.15f)
                                            else -> Warning.copy(alpha = 0.15f)
                                        },
                                        shape = MaterialTheme.shapes.extraSmall,
                                    ) {
                                        Text(
                                            text = enrollment.estado.label,
                                            color = when (enrollment.estado.value) {
                                                "activa" -> Success
                                                "vencida" -> Error
                                                else -> Warning
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        )
                                    }
                                    Text(
                                        text = "$${String.format("%.2f", enrollment.montoPagado)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Accent,
                                    )
                                }
                                Text(
                                    text = enrollment.usuarioEmail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextFaint,
                                )
                            }
                            if (isAdmin) {
                                IconButton(onClick = { viewModel.showEditForm(enrollment) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Info)
                                }
                                IconButton(onClick = { deleteConfirmId = enrollment.id }) {
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
            text = { Text("\u00bfEst\u00e1s seguro de eliminar esta matr\u00edcula?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEnrollment(deleteConfirmId!!)
                    deleteConfirmId = null
                }) { Text("Eliminar", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) { Text("Cancelar") }
            },
        )
    }

    if (uiState.showForm && isAdmin) {
        EnrollmentFormSheet(
            editingEnrollment = uiState.editingEnrollment,
            onDismiss = { viewModel.hideForm() },
            isLoading = uiState.isLoading,
            error = uiState.error,
            onErrorDismiss = { viewModel.clearError() },
            onSave = { cursoId, montoPagado ->
                viewModel.createEnrollment(cursoId, montoPagado)
                viewModel.hideForm()
            },
            onUpdateStatus = { id, estado ->
                viewModel.updateEnrollmentStatus(id, estado)
                viewModel.hideForm()
            },
        )
    }
}
