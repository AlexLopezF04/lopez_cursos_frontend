package com.alexlopez.cursosapp.presentation.ui.admin.lessons

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexlopez.cursosapp.domain.model.Lesson
import com.alexlopez.cursosapp.domain.model.LessonPayload
import com.alexlopez.cursosapp.presentation.components.CursosButton
import com.alexlopez.cursosapp.presentation.components.CursosTextField
import com.alexlopez.cursosapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonFormSheet(
    editingLesson: Lesson?,
    onDismiss: () -> Unit,
    onSave: (LessonPayload) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onErrorDismiss: () -> Unit = {},
) {
    var titulo by remember(editingLesson) { mutableStateOf(editingLesson?.titulo ?: "") }
    var contenido by remember(editingLesson) { mutableStateOf(editingLesson?.contenido ?: "") }
    var videoUrl by remember(editingLesson) { mutableStateOf(editingLesson?.videoUrl ?: "") }
    var orden by remember(editingLesson) { mutableStateOf(editingLesson?.orden?.toString() ?: "0") }
    var duracionMin by remember(editingLesson) { mutableStateOf(editingLesson?.duracionMin?.toString() ?: "0") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = if (editingLesson != null) "Editar Lecci\u00f3n" else "Nueva Lecci\u00f3n",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(24.dp))

            CursosTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = "T\u00edtulo",
                placeholder = "T\u00edtulo de la lecci\u00f3n",
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = "Contenido",
                placeholder = "Contenido de la lecci\u00f3n",
            )
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                label = "URL del video",
                placeholder = "https://...",
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CursosTextField(
                    value = orden,
                    onValueChange = { orden = it.filter { c -> c.isDigit() } },
                    label = "Orden",
                    placeholder = "0",
                    modifier = Modifier.weight(1f),
                )
                CursosTextField(
                    value = duracionMin,
                    onValueChange = { duracionMin = it.filter { c -> c.isDigit() } },
                    label = "Duraci\u00f3n (min)",
                    placeholder = "0",
                    modifier = Modifier.weight(1f),
                )
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
                text = if (editingLesson != null) "Guardar cambios" else "Crear lecci\u00f3n",
                onClick = {
                    onSave(
                        LessonPayload(
                            titulo = titulo,
                            contenido = contenido,
                            videoUrl = videoUrl,
                            orden = orden.toIntOrNull() ?: 0,
                            duracionMin = duracionMin.toIntOrNull() ?: 0,
                        )
                    )
                },
                enabled = titulo.isNotBlank(),
                isLoading = isLoading,
            )
        }
    }
}
