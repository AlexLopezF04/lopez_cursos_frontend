package com.alexlopez.cursosapp.presentation.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.domain.model.ReviewPayload
import com.alexlopez.cursosapp.presentation.components.CursosButton
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.components.NivelBadge
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.ui.admin.reviews.ReviewFormSheet
import com.alexlopez.cursosapp.presentation.viewmodel.CourseViewModel
import com.alexlopez.cursosapp.presentation.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Int,
    onBack: () -> Unit,
    onEnroll: () -> Unit,
    onLoginRequired: () -> Unit,
    isAuthenticated: Boolean,
    currentUserId: Int? = null,
    isAdmin: Boolean = false,
    viewModel: CourseViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel(),
) {
    val course by viewModel.detailState.collectAsState()
    val isLoading by viewModel.detailLoading.collectAsState()
    val error by viewModel.detailError.collectAsState()
    val reviewState by reviewViewModel.uiState.collectAsState()
    var showReviewForm by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) { viewModel.loadCourse(courseId) }

    LaunchedEffect(courseId) {
        reviewViewModel.loadReviews(courseId)
    }

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
            isLoading -> LoadingScreen()
            error != null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(error ?: "", color = Error)
            }
            course != null -> {
                val c = course!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    Surface(
                        color = Surface2,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                NivelBadge(nivel = c.nivel)
                                Text(
                                    text = "$${String.format("%.2f", c.precio)}",
                                    color = Accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = c.titulo,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Instructor: ${c.instructorNombre}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                            )
                            if (c.categoriaNombre != null) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Categor\u00eda: ${c.categoriaNombre}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextFaint,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Descripci\u00f3n",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = c.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )

                    Spacer(Modifier.height(24.dp))

                    if (isAdmin || c.instructorId == currentUserId) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedButton(
                                onClick = { /* TODO: navegar a editar curso */ },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent),
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Editar")
                            }
                            OutlinedButton(
                                onClick = {
                                    viewModel.deleteCourse(c.id)
                                    onBack()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Eliminar")
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    if (isAuthenticated) {
                        CursosButton(text = "Inscribirse ahora", onClick = onEnroll)
                    } else {
                        CursosButton(text = "Inicia sesi\u00f3n para inscribirte", onClick = onLoginRequired)
                    }

                    Spacer(Modifier.height(24.dp))

                    HorizontalDivider(color = Border, thickness = 0.5.dp)
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Rese\u00f1as",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        if (isAuthenticated) {
                            FilledTonalButton(
                                onClick = { showReviewForm = true },
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Accent),
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = AccentOnDark)
                                Spacer(Modifier.width(4.dp))
                                Text("Dejar rese\u00f1a", color = AccentOnDark)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    reviewState.error?.let { err ->
                        Surface(
                            color = Error.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = err,
                                color = Error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (reviewState.isLoading && reviewState.reviews.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator(color = Accent) }
                    } else if (reviewState.reviews.isEmpty()) {
                        Text(
                            text = "No hay rese\u00f1as para este curso",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else {
                        reviewState.reviews.forEach { review ->
                            Surface(
                                color = Surface,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        repeat(5) { i ->
                                            Icon(
                                                imageVector = if (i < review.calificacion) Icons.Default.Star else Icons.Default.StarOutline,
                                                contentDescription = null,
                                                tint = if (i < review.calificacion) Warning else TextFaint,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "${review.calificacion}/5",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                        )
                                    }
                                    if (review.comentario.isNotBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = review.comentario,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "${review.usuarioNombre} \u2022 ${review.createdAt}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextFaint,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    if (showReviewForm) {
        ReviewFormSheet(
            editingReview = null,
            onDismiss = { showReviewForm = false },
            isLoading = reviewState.isLoading,
            error = reviewState.error,
            onErrorDismiss = { reviewViewModel.clearError() },
            onSave = { payload ->
                reviewViewModel.createReview(courseId, payload)
                showReviewForm = false
            },
        )
    }
}
