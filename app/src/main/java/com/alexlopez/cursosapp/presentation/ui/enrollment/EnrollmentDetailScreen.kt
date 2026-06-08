package com.alexlopez.cursosapp.presentation.ui.enrollment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.domain.model.Lesson
import com.alexlopez.cursosapp.domain.model.Progress
import com.alexlopez.cursosapp.presentation.components.EnrollmentStatusBadge
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.EnrollmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentDetailScreen(
    enrollmentId: Int,
    onBack: () -> Unit,
    onLessonClick: (cursoId: Int, leccionId: Int) -> Unit,
    viewModel: EnrollmentViewModel = hiltViewModel(),
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(enrollmentId) { viewModel.loadEnrollmentDetail(enrollmentId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Curso", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
            )
        },
        containerColor = Background,
    ) { padding ->
        when {
            detailState.isLoading -> LoadingScreen()
            detailState.enrollment == null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { Text(detailState.error ?: "Error", color = Error) }
            else -> {
                val enrollment = detailState.enrollment!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Surface(
                            color = Surface2,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
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
                                    fontWeight = FontWeight.Bold,
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

                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Lecciones",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    if (detailState.lessons.isEmpty()) {
                        item {
                            Text(
                                "No hay lecciones disponibles",
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 16.dp),
                            )
                        }
                    } else {
                        items(detailState.lessons) { lesson ->
                            val progress = detailState.progressList.find { it.leccionId == lesson.id }
                            LessonItem(
                                lesson = lesson,
                                isCompleted = progress?.completada == true,
                                onClick = { onLessonClick(enrollment.cursoId, lesson.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonItem(lesson: Lesson, isCompleted: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = Surface,
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isCompleted) Success else TextFaint,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.titulo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                )
                Text(
                    text = "${lesson.duracionMin} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                )
            }
        }
    }
}
