package com.alexlopez.cursosapp.presentation.ui.admin.users

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexlopez.cursosapp.domain.model.User
import com.alexlopez.cursosapp.domain.model.UserPayload
import com.alexlopez.cursosapp.presentation.components.CursosButton
import com.alexlopez.cursosapp.presentation.components.CursosTextField
import com.alexlopez.cursosapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormSheet(
    editingUser: User?,
    onDismiss: () -> Unit,
    onSave: (UserPayload) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onErrorDismiss: () -> Unit = {},
) {
    var username by remember(editingUser) { mutableStateOf(editingUser?.username ?: "") }
    var email by remember(editingUser) { mutableStateOf(editingUser?.email ?: "") }
    var password by remember(editingUser) { mutableStateOf("") }
    var rol by remember(editingUser) { mutableStateOf(editingUser?.rol ?: "estudiante") }
    var firstName by remember(editingUser) { mutableStateOf(editingUser?.firstName ?: "") }
    var lastName by remember(editingUser) { mutableStateOf(editingUser?.lastName ?: "") }
    var rolDropdown by remember { mutableStateOf(false) }

    val roles = listOf("estudiante", "instructor", "admin")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = if (editingUser != null) "Editar Usuario" else "Nuevo Usuario",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(24.dp))

            CursosTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Nombre de usuario",
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "correo@ejemplo.com",
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = password,
                onValueChange = { password = it },
                label = if (editingUser != null) "Contrase\u00f1a (dejar vac\u00edo si no cambia)" else "Contrase\u00f1a",
                placeholder = "M\u00ednimo 8 caracteres",
                isPassword = true,
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "Nombre",
                placeholder = "Nombre",
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Apellido",
                placeholder = "Apellido",
            )
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = rolDropdown,
                onExpandedChange = { rolDropdown = !rolDropdown },
            ) {
                OutlinedTextField(
                    value = rol,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol", color = TextFaint) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rolDropdown) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                )
                ExposedDropdownMenu(expanded = rolDropdown, onDismissRequest = { rolDropdown = false }) {
                    roles.forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r.replaceFirstChar { it.uppercase() }, color = TextPrimary) },
                            onClick = { rol = r; rolDropdown = false },
                        )
                    }
                }
            }
            error?.let { msg ->
                Surface(
                    color = Error.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = msg, color = Error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        IconButton(onClick = onErrorDismiss, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            CursosButton(
                text = if (editingUser != null) "Guardar cambios" else "Crear usuario",
                onClick = {
                    onSave(
                        UserPayload(
                            username = username,
                            email = email,
                            password = password.ifBlank { null },
                            rol = rol,
                            firstName = firstName,
                            lastName = lastName,
                        )
                    )
                },
                enabled = username.isNotBlank() && email.isNotBlank()
                    && (editingUser != null || password.length >= 8),
                isLoading = isLoading,
            )
        }
    }
}
