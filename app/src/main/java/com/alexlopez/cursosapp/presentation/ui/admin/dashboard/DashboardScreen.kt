package com.alexlopez.cursosapp.presentation.ui.admin.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexlopez.cursosapp.presentation.theme.*

data class DashboardKpi(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    val route: String? = null,
)

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit) {
    val kpis = listOf(
        DashboardKpi("Categor\u00edas", "Gestionar", Icons.Default.Category, Accent, "admin/categories"),
        DashboardKpi("Cursos", "Administrar", Icons.Default.MenuBook, Success, "admin/courses"),
        DashboardKpi("Usuarios", "Gestionar", Icons.Default.People, Info, "admin/users"),
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Panel de Control",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        kpis.forEach { kpi ->
            KpiCard(
                kpi = kpi,
                onClick = { kpi.route?.let { onNavigate(it) } },
            )
        }
    }
}
