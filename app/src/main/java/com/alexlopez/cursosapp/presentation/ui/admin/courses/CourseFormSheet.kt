package com.alexlopez.cursosapp.presentation.ui.admin.courses

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexlopez.cursosapp.domain.model.Category
import com.alexlopez.cursosapp.domain.model.Course
import com.alexlopez.cursosapp.domain.model.CoursePayload
import com.alexlopez.cursosapp.presentation.components.CursosButton
import com.alexlopez.cursosapp.presentation.components.CursosTextField
import com.alexlopez.cursosapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFormSheet(
    editingCourse: Course?,
    categories: List<Category> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (CoursePayload) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onErrorDismiss: () -> Unit = {},
) {
    var titulo by remember(editingCourse) { mutableStateOf(editingCourse?.titulo ?: "") }
    var descripcion by remember(editingCourse) { mutableStateOf(editingCourse?.descripcion ?: "") }
    var precio by remember(editingCourse) { mutableStateOf(editingCourse?.precio?.toString() ?: "") }
    var nivel by remember(editingCourse) { mutableStateOf(editingCourse?.nivel ?: "basico") }
    var publicado by remember(editingCourse) { mutableStateOf(editingCourse?.publicado ?: false) }
    var categoriaId by remember(editingCourse) { mutableStateOf(editingCourse?.categoriaId) }
    var categoriaDropdown by remember { mutableStateOf(false) }
    var nivelDropdown by remember { mutableStateOf(false) }

    val niveles = listOf("basico", "intermedio", "avanzado")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = if (editingCourse != null) "Editar Curso" else "Nuevo Curso",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(24.dp))

            CursosTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = "T\u00edtulo",
                placeholder = "T\u00edtulo del curso",
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = "Descripci\u00f3n",
                placeholder = "Descripci\u00f3n del curso",
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = precio,
                onValueChange = { precio = it.filter { c -> c.isDigit() || c == '.' } },
                label = "Precio",
                placeholder = "0.00",
            )
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = nivelDropdown,
                onExpandedChange = { nivelDropdown = !nivelDropdown },
            ) {
                OutlinedTextField(
                    value = nivel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel", color = TextFaint) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nivelDropdown) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                )
                ExposedDropdownMenu(expanded = nivelDropdown, onDismissRequest = { nivelDropdown = false }) {
                    niveles.forEach { n ->
                        DropdownMenuItem(
                            text = { Text(n.replaceFirstChar { it.uppercase() }, color = TextPrimary) },
                            onClick = { nivel = n; nivelDropdown = false },
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = categoriaDropdown,
                onExpandedChange = { categoriaDropdown = !categoriaDropdown },
            ) {
                val selectedCategory = categories.find { it.id == categoriaId }
                OutlinedTextField(
                    value = selectedCategory?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categor\u00eda", color = TextFaint) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaDropdown) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                )
                ExposedDropdownMenu(
                    expanded = categoriaDropdown,
                    onDismissRequest = { categoriaDropdown = false },
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre, color = TextPrimary) },
                            onClick = { categoriaId = cat.id; categoriaDropdown = false },
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Publicado", color = TextPrimary)
                Switch(checked = publicado, onCheckedChange = { publicado = it })
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
                text = if (editingCourse != null) "Guardar cambios" else "Crear curso",
                onClick = {
                    onSave(
                        CoursePayload(
                            titulo = titulo,
                            descripcion = descripcion,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            nivel = nivel,
                            publicado = publicado,
                            categoriaId = categoriaId,
                        )
                    )
                },
                enabled = titulo.isNotBlank() && descripcion.isNotBlank() && precio.isNotBlank(),
                isLoading = isLoading,
            )
        }
    }
}
