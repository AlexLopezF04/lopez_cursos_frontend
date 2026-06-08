package com.alexlopez.cursosapp.presentation.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.ui.home.CourseCard
import com.alexlopez.cursosapp.presentation.viewmodel.CourseViewModel

@Composable
fun CatalogScreen(
    onCourseClick: (Int) -> Unit,
    viewModel: CourseViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Cat\u00e1logo de Cursos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        viewModel.search(it)
                    },
                    placeholder = { Text("Buscar cursos...", color = TextFaint) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = Border,
                        cursorColor = Accent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Filtrar por nivel",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val niveles = listOf(null to "Todos", "basico" to "B\u00e1sico", "intermedio" to "Intermedio", "avanzado" to "Avanzado")
                    items(niveles) { (value, label) ->
                        FilterChip(
                            selected = uiState.selectedNivel == value,
                            onClick = { viewModel.filterByNivel(value) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent.copy(alpha = 0.15f),
                                selectedLabelColor = Accent,
                            ),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            when {
                uiState.isLoading -> item { LoadingScreen() }
                uiState.courses.isEmpty() -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("No se encontraron cursos", color = TextSecondary) }
                }
                else -> {
                    items(uiState.courses) { course ->
                        CourseCard(course = course, onClick = { onCourseClick(course.id) })
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
