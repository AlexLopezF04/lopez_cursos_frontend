package com.alexlopez.cursosapp.presentation.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.presentation.components.CursosButton
import com.alexlopez.cursosapp.presentation.components.CursosTextField
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: (rol: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRol by remember { mutableStateOf("estudiante") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val user = (uiState as AuthUiState.Success).user
            onRegisterSuccess(user.rol)
        }
    }

    val isLoading = uiState is AuthUiState.Loading
    val errorMsg  = (uiState as? AuthUiState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = "Cursos Online",
                fontSize   = 36.sp,
                fontWeight = FontWeight.Bold,
                color      = Accent,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Crea tu cuenta",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(32.dp))

            Surface(
                shape            = MaterialTheme.shapes.large,
                color            = Surface,
                tonalElevation   = 0.dp,
                modifier         = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (errorMsg != null) {
                        Surface(
                            color  = Error.copy(alpha = 0.1f),
                            shape  = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text     = errorMsg,
                                color    = Error,
                                style    = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    CursosTextField(
                        value         = username,
                        onValueChange = { username = it; viewModel.clearError() },
                        label         = "Usuario",
                        placeholder   = "tu_usuario",
                        enabled       = !isLoading,
                        imeAction     = ImeAction.Next,
                    )
                    Spacer(Modifier.height(12.dp))

                    CursosTextField(
                        value         = email,
                        onValueChange = { email = it; viewModel.clearError() },
                        label         = "Correo electr\u00f3nico",
                        placeholder   = "email@ejemplo.com",
                        keyboardType  = KeyboardType.Email,
                        enabled       = !isLoading,
                        imeAction     = ImeAction.Next,
                    )
                    Spacer(Modifier.height(12.dp))

                    CursosTextField(
                        value         = password,
                        onValueChange = { password = it; viewModel.clearError() },
                        label         = "Contrase\u00f1a",
                        placeholder   = "M\u00ednimo 8 caracteres",
                        isPassword    = true,
                        enabled       = !isLoading,
                        keyboardType  = KeyboardType.Password,
                        imeAction     = ImeAction.Done,
                    )
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text  = "Rol",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf("estudiante" to "Estudiante", "instructor" to "Instructor").forEach { (value, label) ->
                            FilterChip(
                                selected = selectedRol == value,
                                onClick  = { selectedRol = value },
                                label    = { Text(label) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Accent.copy(alpha = 0.15f),
                                    selectedLabelColor     = Accent,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    CursosButton(
                        text      = "Crear cuenta",
                        onClick   = { viewModel.register(username, email, password, selectedRol) },
                        isLoading = isLoading,
                        enabled   = username.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text  = "\u00bfYa tienes cuenta? ",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text  = "Inicia sesi\u00f3n",
                        color = Accent,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
