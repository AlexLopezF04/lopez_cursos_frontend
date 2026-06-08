package com.alexlopez.cursosapp.presentation.ui.admin.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexlopez.cursosapp.domain.model.Review
import com.alexlopez.cursosapp.domain.model.ReviewPayload
import com.alexlopez.cursosapp.presentation.components.CursosButton
import com.alexlopez.cursosapp.presentation.components.CursosTextField
import com.alexlopez.cursosapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewFormSheet(
    editingReview: Review?,
    onDismiss: () -> Unit,
    onSave: (ReviewPayload) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onErrorDismiss: () -> Unit = {},
) {
    var calificacion by remember(editingReview) { mutableStateOf(editingReview?.calificacion?.toString() ?: "5") }
    var comentario by remember(editingReview) { mutableStateOf(editingReview?.comentario ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = if (editingReview != null) "Editar Rese\u00f1a" else "Nueva Rese\u00f1a",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(24.dp))

            Text("Calificaci\u00f3n", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                (1..5).forEach { star ->
                    IconButton(
                        onClick = { calificacion = star.toString() },
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "$star estrellas",
                            tint = if (star <= (calificacion.toIntOrNull() ?: 5)) Warning else TextFaint,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            CursosTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = "Comentario",
                placeholder = "Escribe un comentario...",
            )
            Spacer(Modifier.height(24.dp))

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
                text = if (editingReview != null) "Guardar cambios" else "Crear rese\u00f1a",
                onClick = {
                    onSave(
                        ReviewPayload(
                            calificacion = calificacion.toIntOrNull() ?: 5,
                            comentario = comentario,
                        )
                    )
                },
                isLoading = isLoading,
            )
        }
    }
}
