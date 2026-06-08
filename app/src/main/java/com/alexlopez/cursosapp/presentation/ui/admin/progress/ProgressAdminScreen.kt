package com.alexlopez.cursosapp.presentation.ui.admin.progress

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
import com.alexlopez.cursosapp.domain.model.Progress
import com.alexlopez.cursosapp.domain.model.ProgressPayload
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.ProgressViewModel

@Composable
fun ProgressAdminScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var matriculaFilter by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadAllProgress() }

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
                        text = "Progreso",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    FilledTonalButton(
                        onClick = { viewModel.showCreateForm() },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Accent),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AccentOnDark)
                        Spacer(Modifier.width(4.dp))
                        Text("Nuevo", color = AccentOnDark)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = matriculaFilter,
                    onValueChange = {
                        matriculaFilter = it.filter { c -> c.isDigit() }
                        val id = it.filter { c -> c.isDigit() }.toIntOrNull()
                        if (id != null) viewModel.loadProgress(id)
                        else viewModel.loadAllProgress()
                    },
                    label = { Text("ID de matr\u00edcula", color = TextFaint) },
                    placeholder = { Text("Filtrar por matr\u00edcula...", color = TextFaint) },
                    trailingIcon = {
                        if (matriculaFilter.isNotBlank()) {
                            IconButton(onClick = {
                                matriculaFilter = ""
                                viewModel.loadAllProgress()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                    singleLine = true,
                )
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

            if (uiState.isLoading && uiState.progressList.isEmpty()) {
                item { LoadingScreen() }
            } else if (uiState.progressList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("No hay progreso registrado", color = TextSecondary) }
                }
            } else {
                items(uiState.progressList) { progress ->
                    ProgressCard(
                        progress = progress,
                        onToggle = { viewModel.toggleComplete(progress.id, it) },
                        onDelete = { viewModel.showDeleteConfirm(progress.id) },
                    )
                }
            }
        }
    }

    if (uiState.deleteConfirmId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirm() },
            title = { Text("Confirmar eliminaci\u00f3n", fontWeight = FontWeight.Bold) },
            text = { Text("\u00bfEst\u00e1s seguro de eliminar este registro de progreso?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProgress(uiState.deleteConfirmId!!)
                    viewModel.hideDeleteConfirm()
                }) { Text("Eliminar", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteConfirm() }) { Text("Cancelar") }
            },
        )
    }

    if (uiState.showForm) {
        ProgressFormSheet(
            onDismiss = { viewModel.hideForm() },
            onSave = { matriculaId, leccionId ->
                viewModel.createProgress(ProgressPayload(matriculaId, leccionId, completada = false))
                viewModel.hideForm()
            },
        )
    }
}

@Composable
private fun ProgressCard(
    progress: Progress,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        color = Surface,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (progress.completada) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (progress.completada) "Completada" else "Pendiente",
                tint = if (progress.completada) Success else TextFaint,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = progress.leccionTitulo ?: "Lecci\u00f3n ID: ${progress.leccionId}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${progress.cursoTitulo ?: "Curso #${progress.cursoId ?: "?"}"} \u00b7 Matr\u00edcula #${progress.matriculaId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (progress.fechaCompletado != null) {
                    Text(
                        text = "Completado: ${progress.fechaCompletado.take(19).replace("T", " ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                    )
                }
            }
            Switch(
                checked = progress.completada,
                onCheckedChange = onToggle,
            )
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ProgressFormSheet(
    onDismiss: () -> Unit,
    onSave: (matriculaId: Int, leccionId: Int) -> Unit,
) {
    var matriculaIdText by remember { mutableStateOf("") }
    var leccionIdText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo progreso", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = matriculaIdText,
                    onValueChange = { matriculaIdText = it.filter { c -> c.isDigit() } },
                    label = { Text("ID de matr\u00edcula", color = TextFaint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                )
                OutlinedTextField(
                    value = leccionIdText,
                    onValueChange = { leccionIdText = it.filter { c -> c.isDigit() } },
                    label = { Text("ID de lecci\u00f3n", color = TextFaint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val mid = matriculaIdText.toIntOrNull()
                    val lid = leccionIdText.toIntOrNull()
                    if (mid != null && lid != null) {
                        onSave(mid, lid)
                    }
                },
                enabled = matriculaIdText.toIntOrNull() != null && leccionIdText.toIntOrNull() != null,
            ) { Text("Crear", color = Accent) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}
