package com.alexlopez.cursosapp.presentation.ui.lesson

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.LessonViewModel
import com.alexlopez.cursosapp.presentation.viewmodel.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonViewerScreen(
    cursoId: Int,
    leccionId: Int,
    matriculaId: Int? = null,
    onBack: () -> Unit,
    lessonViewModel: LessonViewModel = hiltViewModel(),
    progressViewModel: ProgressViewModel = hiltViewModel(),
) {
    val lessonState by lessonViewModel.uiState.collectAsState()
    val progressState by progressViewModel.uiState.collectAsState()

    LaunchedEffect(cursoId, leccionId) {
        lessonViewModel.loadLesson(cursoId, leccionId)
    }

    LaunchedEffect(matriculaId, leccionId) {
        if (matriculaId != null) {
            progressViewModel.loadProgress(matriculaId)
        }
    }

    val autoMarkAttempted = remember { mutableStateOf(false) }
    LaunchedEffect(progressState.loadedOnce, leccionId, matriculaId) {
        if (matriculaId != null && progressState.loadedOnce && !autoMarkAttempted.value) {
            autoMarkAttempted.value = true
            progressViewModel.autoMarkComplete(matriculaId, leccionId)
        }
    }

    val lesson = lessonState.currentLesson

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson?.titulo ?: "Lecci\u00f3n", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                },
                actions = {
                    if (matriculaId != null) {
                        val isCompleted = progressState.progressList.any { it.leccionId == leccionId && it.completada }
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = if (isCompleted) "Completada" else "Pendiente",
                            tint = if (isCompleted) Success else TextFaint,
                            modifier = Modifier.padding(end = 12.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
            )
        },
        containerColor = Background,
    ) { padding ->
        when {
            lessonState.isLoading -> LoadingScreen()
            lesson == null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { Text(lessonState.error ?: "Error", color = Error) }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                if (lesson.videoUrl.isNotBlank()) {
                    Surface(
                        color = Surface2,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Video: ${lesson.videoUrl}", color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = lesson.contenido,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
