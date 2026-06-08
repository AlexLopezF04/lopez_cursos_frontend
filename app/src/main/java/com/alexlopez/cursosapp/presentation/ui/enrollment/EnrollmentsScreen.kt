package com.alexlopez.cursosapp.presentation.ui.enrollment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.domain.model.Enrollment
import com.alexlopez.cursosapp.presentation.components.EnrollmentStatusBadge
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.EnrollmentViewModel

@Composable
fun EnrollmentsScreen(
    onEnrollmentClick: (Int) -> Unit,
    viewModel: EnrollmentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.listState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Mis Cursos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))
            }

            when {
                uiState.isLoading -> item { LoadingScreen() }
                uiState.enrollments.isEmpty() -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No tienes inscripciones", color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Explora el cat\u00e1logo e inscrbete en un curso",
                                color = TextFaint,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                else -> {
                    items(uiState.enrollments) { enrollment ->
                        EnrollmentCard(
                            enrollment = enrollment,
                            onClick = { onEnrollmentClick(enrollment.id) },
                        )
                    }

                    if (uiState.hasMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (uiState.isLoadingMore) {
                                    CircularProgressIndicator(color = Accent, modifier = Modifier.size(24.dp))
                                } else {
                                    TextButton(onClick = { viewModel.loadMore() }) {
                                        Text("Cargar m\u00e1s", color = Accent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnrollmentCard(enrollment: Enrollment, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = Surface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EnrollmentStatusBadge(status = enrollment.estado)
                Text(
                    text = "$${String.format("%.2f", enrollment.montoPagado)}",
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = enrollment.cursoTitulo,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Inscrito: ${enrollment.fechaPago.take(10)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}
