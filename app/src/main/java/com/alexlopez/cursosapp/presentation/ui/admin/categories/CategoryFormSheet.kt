package com.alexlopez.cursosapp.presentation.ui.admin.categories

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
import com.alexlopez.cursosapp.domain.model.CategoryPayload
import com.alexlopez.cursosapp.presentation.components.CursosButton
import com.alexlopez.cursosapp.presentation.components.CursosTextField
import com.alexlopez.cursosapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormSheet(
    editingCategory: Category?,
    onDismiss: () -> Unit,
    onSave: (CategoryPayload) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onErrorDismiss: () -> Unit = {},
) {
    var nombre by remember(editingCategory) { mutableStateOf(editingCategory?.nombre ?: "") }
    var slug by remember(editingCategory) { mutableStateOf(editingCategory?.slug ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = if (editingCategory != null) "Editar Categor\u00eda" else "Nueva Categor\u00eda",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(24.dp))

            CursosTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre",
                placeholder = "Nombre de la categor\u00eda",
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = slug,
                onValueChange = { if (it.length <= 100) slug = it },
                label = "Slug",
                placeholder = "nombre-categoria",
            )
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
                text = if (editingCategory != null) "Guardar cambios" else "Crear categor\u00eda",
                onClick = { onSave(CategoryPayload(nombre, slug.trim())) },
                enabled = nombre.isNotBlank() && slug.isNotBlank(),
                isLoading = isLoading,
            )
        }
    }
}
