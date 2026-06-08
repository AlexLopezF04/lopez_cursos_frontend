package com.alexlopez.cursosapp.presentation.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexlopez.cursosapp.domain.model.LoggedUser
import com.alexlopez.cursosapp.presentation.theme.*
import kotlinx.coroutines.launch

data class AdminNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

val ADMIN_NAV_ITEMS = listOf(
    AdminNavItem("Dashboard",     Icons.Default.Dashboard, "admin"),
    AdminNavItem("Categor\u00edas", Icons.Default.Category,  "admin/categories"),
    AdminNavItem("Cursos",        Icons.Default.MenuBook,  "admin/courses"),
    AdminNavItem("Lecciones",     Icons.Default.MenuBook,  "admin/lessons"),
    AdminNavItem("Matr\u00edculas", Icons.Default.Assignment, "admin/enrollments"),
    AdminNavItem("Progreso",     Icons.Default.TrendingUp, "admin/progress"),
    AdminNavItem("Rese\u00f1as",  Icons.Default.Star,       "admin/reviews"),
    AdminNavItem("Usuarios",      Icons.Default.People,    "admin/users"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    currentRoute: String,
    user: LoggedUser?,
    onNavClick: (String) -> Unit,
    onStoreClick: () -> Unit,
    onLogout: () -> Unit,
    title: String,
    isAdmin: Boolean = true,
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Surface,
                modifier = Modifier.width(280.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Surface2)
                        .padding(24.dp),
                ) {
                    Text(
                        text = "Cursos Online",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Accent,
                    )
                    Text(
                        text = "Panel de administraci\u00f3n",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                HorizontalDivider(color = Border, thickness = 0.5.dp)

                if (user != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Accent, AccentLight)
                                    ),
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = user.username.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                                color = AccentOnDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = user.username,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                            )
                            Surface(
                                color = Accent.copy(alpha = 0.15f),
                                shape = MaterialTheme.shapes.extraSmall,
                            ) {
                                Text(
                                    text = user?.rol?.replaceFirstChar { it.uppercase() } ?: "Admin",
                                    color = Accent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Border, thickness = 0.5.dp)
                    Spacer(Modifier.height(8.dp))
                }

                val navItems = if (isAdmin) ADMIN_NAV_ITEMS else ADMIN_NAV_ITEMS.filter { it.route in listOf("admin/categories", "admin/courses") }
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route || currentRoute.startsWith("${item.route}/")
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) Accent else TextSecondary,
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = if (isSelected) Accent else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavClick(item.route)
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Accent.copy(alpha = 0.12f),
                            unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    )
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider(color = Border, thickness = 0.5.dp)
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Salir", tint = Error) },
                    label = { Text("Cerrar sesi\u00f3n", color = Error, fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Men\u00fa", tint = TextPrimary)
                        }
                    },
                    actions = {
                        TextButton(onClick = onStoreClick) {
                            Text(
                                "\u2190 Inicio",
                                color = Accent,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
                )
            },
            containerColor = Background,
            content = content,
        )
    }
}
