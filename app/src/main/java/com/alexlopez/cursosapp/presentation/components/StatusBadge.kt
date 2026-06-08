package com.alexlopez.cursosapp.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alexlopez.cursosapp.domain.model.EnrollmentStatus
import com.alexlopez.cursosapp.presentation.theme.*

@Composable
fun EnrollmentStatusBadge(status: EnrollmentStatus, modifier: Modifier = Modifier) {
    val (color, label) = when (status) {
        EnrollmentStatus.ACTIVA    -> StatusActiva to "Activa"
        EnrollmentStatus.VENCIDA   -> StatusVencida to "Vencida"
        EnrollmentStatus.CANCELADA -> StatusCancelada to "Cancelada"
    }
    StatusBadge(color = color, label = label, modifier = modifier)
}

@Composable
fun NivelBadge(nivel: String, modifier: Modifier = Modifier) {
    val (color, label) = when (nivel.lowercase()) {
        "basico"     -> NivelBasico to "B\u00e1sico"
        "intermedio" -> NivelIntermedio to "Intermedio"
        "avanzado"   -> NivelAvanzado to "Avanzado"
        else         -> TextSecondary to nivel
    }
    StatusBadge(color = color, label = label, modifier = modifier)
}

@Composable
fun RolBadge(rol: String, modifier: Modifier = Modifier) {
    val color = when (rol.lowercase()) {
        "admin"       -> Accent
        "instructor"  -> Success
        "estudiante"  -> Info
        else          -> TextSecondary
    }
    StatusBadge(color = color, label = rol, modifier = modifier)
}

@Composable
fun StatusBadge(
    color:    Color,
    label:    String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color  = color.copy(alpha = 0.15f),
        shape  = MaterialTheme.shapes.extraSmall,
        modifier = modifier,
    ) {
        Text(
            text     = label,
            color    = color,
            style    = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
