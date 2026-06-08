package com.alexlopez.cursosapp.presentation.ui.admin.categories

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.CategoryViewModel

@Composable
fun CategoriesAdminScreen(
    isAdmin: Boolean = true,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var deleteConfirmId by remember { mutableStateOf<Int?>(null) }

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
                        text = "Categor\u00edas",
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
            } else if (uiState.categories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("No hay categor\u00edas", color = TextSecondary) }
                }
            } else {
                items(uiState.categories) { category ->
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
                                    text = category.nombre,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = category.slug,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                            if (isAdmin) {
                                IconButton(onClick = { viewModel.showEditForm(category) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Info)
                                }
                                IconButton(onClick = { deleteConfirmId = category.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
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
            text = { Text("\u00bfEst\u00e1s seguro de eliminar esta categor\u00eda?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(deleteConfirmId!!)
                    deleteConfirmId = null
                }) { Text("Eliminar", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) { Text("Cancelar") }
            },
        )
    }

    if (uiState.showForm && isAdmin) {
        CategoryFormSheet(
            editingCategory = uiState.editingCategory,
            onDismiss = { viewModel.hideForm() },
            isLoading = uiState.isLoading,
            error = uiState.error,
            onErrorDismiss = { viewModel.clearError() },
            onSave = { payload ->
                val edit = uiState.editingCategory
                if (edit != null) {
                    viewModel.updateCategory(edit.id, payload)
                } else {
                    viewModel.createCategory(payload)
                }
            },
        )
    }
}
