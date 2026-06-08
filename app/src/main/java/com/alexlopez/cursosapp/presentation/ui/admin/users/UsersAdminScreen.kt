package com.alexlopez.cursosapp.presentation.ui.admin.users

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
import com.alexlopez.cursosapp.presentation.components.RolBadge
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.UserViewModel

@Composable
fun UsersAdminScreen(
    viewModel: UserViewModel = hiltViewModel(),
) {
    val uiState by viewModel.listState.collectAsState()
    var searchText by remember { mutableStateOf("") }
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
                        text = "Usuarios",
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

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        viewModel.loadUsers(it.ifBlank { null })
                    },
                    placeholder = { Text("Buscar usuarios...", color = TextFaint) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        cursorColor = Accent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
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

            if (uiState.isLoading) {
                item { LoadingScreen() }
            } else if (uiState.users.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("No hay usuarios", color = TextSecondary) }
                }
            } else {
                items(uiState.users) { user ->
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
                                    text = user.username,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                            RolBadge(rol = user.rol)
                            IconButton(onClick = { viewModel.showEditForm(user) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Info)
                            }
                            IconButton(onClick = { deleteConfirmId = user.id }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
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
            text = { Text("\u00bfEst\u00e1s seguro de eliminar este usuario?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteUser(deleteConfirmId!!)
                    deleteConfirmId = null
                }) { Text("Eliminar", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmId = null }) { Text("Cancelar") }
            },
        )
    }

    if (uiState.showForm) {
        UserFormSheet(
            editingUser = uiState.editingUser,
            onDismiss = { viewModel.hideForm() },
            isLoading = uiState.isLoading,
            error = uiState.error,
            onErrorDismiss = { viewModel.clearError() },
            onSave = { payload ->
                val edit = uiState.editingUser
                if (edit != null) {
                    viewModel.updateUser(edit.id, payload)
                } else {
                    viewModel.createUser(payload)
                }
            },
        )
    }
}
