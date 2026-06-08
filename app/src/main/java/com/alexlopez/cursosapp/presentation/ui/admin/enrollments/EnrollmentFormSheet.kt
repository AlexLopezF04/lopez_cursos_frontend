package com.alexlopez.cursosapp.presentation.ui.admin.enrollments

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexlopez.cursosapp.domain.model.Enrollment
import com.alexlopez.cursosapp.domain.model.EnrollmentStatus
import com.alexlopez.cursosapp.presentation.components.CursosButton
import com.alexlopez.cursosapp.presentation.components.CursosTextField
import com.alexlopez.cursosapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentFormSheet(
    editingEnrollment: Enrollment?,
    onDismiss: () -> Unit,
    onSave: (cursoId: Int, montoPagado: Double) -> Unit,
    onUpdateStatus: (id: Int, estado: String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onErrorDismiss: () -> Unit = {},
) {
    var cursoId by remember(editingEnrollment) { mutableStateOf(editingEnrollment?.cursoId?.toString() ?: "") }
    var montoPagado by remember(editingEnrollment) { mutableStateOf(editingEnrollment?.montoPagado?.toString() ?: "") }
    var estado by remember(editingEnrollment) { mutableStateOf(editingEnrollment?.estado?.value ?: "activa") }
    var estadoDropdown by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = if (editingEnrollment != null) "Editar Matr\u00edcula" else "Nueva Matr\u00edcula",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(24.dp))

            if (editingEnrollment != null) {
                ExposedDropdownMenuBox(
                    expanded = estadoDropdown,
                    onExpandedChange = { estadoDropdown = !estadoDropdown },
                ) {
                    OutlinedTextField(
                        value = EnrollmentStatus.fromValue(estado).label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado", color = TextFaint) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = Border,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = estadoDropdown,
                        onDismissRequest = { estadoDropdown = false },
                    ) {
                        EnrollmentStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.label, color = TextPrimary) },
                                onClick = { estado = status.value; estadoDropdown = false },
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
                text = "Actualizar estado",
                    onClick = { onUpdateStatus(editingEnrollment.id, estado) },
                    enabled = estado != editingEnrollment.estado.value,
                    isLoading = isLoading,
                )
            } else {
                CursosTextField(
                    value = cursoId,
                    onValueChange = { cursoId = it.filter { c -> c.isDigit() } },
                    label = "ID del curso",
                    placeholder = "1",
                )
                Spacer(Modifier.height(12.dp))

                CursosTextField(
                    value = montoPagado,
                    onValueChange = { montoPagado = it.filter { c -> c.isDigit() || c == '.' } },
                    label = "Monto pagado",
                    placeholder = "0.00",
                )
                Spacer(Modifier.height(24.dp))

                CursosButton(
                    text = "Crear matr\u00edcula",
                    onClick = { onSave(cursoId.toIntOrNull() ?: 0, montoPagado.toDoubleOrNull() ?: 0.0) },
                    enabled = cursoId.isNotBlank() && montoPagado.isNotBlank(),
                    isLoading = isLoading,
                )
            }
        }
    }
}
