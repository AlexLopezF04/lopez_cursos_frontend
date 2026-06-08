package com.alexlopez.cursosapp.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.theme.Surface
import com.alexlopez.cursosapp.presentation.theme.TextSecondary
import com.alexlopez.cursosapp.presentation.ui.admin.AdminScaffold
import com.alexlopez.cursosapp.presentation.ui.admin.categories.CategoriesAdminScreen
import com.alexlopez.cursosapp.presentation.ui.admin.courses.CoursesAdminScreen
import com.alexlopez.cursosapp.presentation.ui.admin.dashboard.DashboardScreen
import com.alexlopez.cursosapp.presentation.ui.admin.enrollments.EnrollmentsAdminScreen
import com.alexlopez.cursosapp.presentation.ui.admin.lessons.LessonsAdminScreen
import com.alexlopez.cursosapp.presentation.ui.admin.progress.ProgressAdminScreen
import com.alexlopez.cursosapp.presentation.ui.admin.reviews.ReviewsAdminScreen
import com.alexlopez.cursosapp.presentation.ui.admin.users.UsersAdminScreen
import com.alexlopez.cursosapp.presentation.ui.auth.LoginScreen
import com.alexlopez.cursosapp.presentation.ui.auth.RegisterScreen
import com.alexlopez.cursosapp.presentation.ui.catalog.CatalogScreen
import com.alexlopez.cursosapp.presentation.ui.course.CourseDetailScreen
import com.alexlopez.cursosapp.presentation.ui.enrollment.EnrollmentDetailScreen
import com.alexlopez.cursosapp.presentation.ui.enrollment.EnrollmentsScreen
import com.alexlopez.cursosapp.presentation.ui.home.HomeScreen
import com.alexlopez.cursosapp.presentation.ui.lesson.LessonViewerScreen
import com.alexlopez.cursosapp.presentation.ui.profile.ProfileScreen
import com.alexlopez.cursosapp.presentation.viewmodel.AuthViewModel

@Composable
fun NavGraph(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isAdmin by authViewModel.isAdmin.collectAsState()
    val isInstructor by authViewModel.isInstructor.collectAsState()
    val canManageContent by authViewModel.canManageContent.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    if (isCheckingSession) {
        LoadingScreen("Iniciando CursosOnline...")
        return
    }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            val route = navController.currentBackStackEntry?.destination?.route
            if (route != Screen.Login.route && route != Screen.Register.route) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val startDestination = when {
        !isAuthenticated -> Screen.Login.route
        isAdmin -> Screen.AdminDashboard.route
        else -> Screen.Home.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Catalog.route,
        Screen.Enrollments.route,
        Screen.Profile.route,
    )

    Scaffold(
        containerColor = Surface,
        bottomBar = {
            if (showBottomBar && isAuthenticated) {
                BottomNavBar(navController = navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { rol ->
                        val dest = if (rol == "admin") Screen.AdminDashboard.route else Screen.Home.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    viewModel = authViewModel,
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { rol ->
                        val dest = if (rol == "admin") Screen.AdminDashboard.route else Screen.Home.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                    viewModel = authViewModel,
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onCourseClick = { id -> navController.navigate(Screen.Course(id).createRoute(id)) },
                    onCatalogClick = { navController.navigate(Screen.Catalog.route) },
                )
            }

            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onCourseClick = { id -> navController.navigate(Screen.Course(id).createRoute(id)) },
                )
            }

            composable(
                route = "course/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                CourseDetailScreen(
                    courseId = id,
                    onBack = { navController.popBackStack() },
                    onEnroll = { navController.navigate(Screen.Enrollments.route) },
                    onLoginRequired = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    isAuthenticated = isAuthenticated,
                    currentUserId = currentUser?.id,
                    isAdmin = isAdmin,
                )
            }

            composable(Screen.Enrollments.route) {
                if (!isAuthenticated) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                } else {
                    EnrollmentsScreen(
                        onEnrollmentClick = { id ->
                            navController.navigate(Screen.EnrollmentDetail(id).createRoute(id))
                        },
                    )
                }
            }

            composable(
                route = "enrollments/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                EnrollmentDetailScreen(
                    enrollmentId = id,
                    onBack = { navController.popBackStack() },
                    onLessonClick = { cursoId, leccionId ->
                        navController.navigate("lesson/$cursoId/$leccionId?matriculaId=$id")
                    },
                )
            }

            composable(
                route = "lesson/{cursoId}/{leccionId}?matriculaId={matriculaId}",
                arguments = listOf(
                    navArgument("cursoId") { type = NavType.IntType },
                    navArgument("leccionId") { type = NavType.IntType },
                    navArgument("matriculaId") { type = NavType.IntType; defaultValue = -1 },
                ),
            ) { backStackEntry ->
                val cursoId = backStackEntry.arguments?.getInt("cursoId") ?: return@composable
                val leccionId = backStackEntry.arguments?.getInt("leccionId") ?: return@composable
                val rawMatriculaId = backStackEntry.arguments?.getInt("matriculaId") ?: -1
                val matriculaId = if (rawMatriculaId > 0) rawMatriculaId else null
                LessonViewerScreen(
                    cursoId = cursoId,
                    leccionId = leccionId,
                    matriculaId = matriculaId,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Screen.Profile.route) {
                if (!isAuthenticated) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                } else {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                    )
                }
            }

            composable(Screen.AdminDashboard.route) {
                if (!isAdmin) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }

                AdminScaffold(
                    currentRoute = Screen.AdminDashboard.route,
                    user = currentUser,
                    title = "Dashboard",
                    isAdmin = isAdmin,
                    onNavClick = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        DashboardScreen(
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }
                }
            }

            composable("admin/categories") {
                if (!canManageContent) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }

                AdminScaffold(
                    currentRoute = "admin/categories",
                    user = currentUser,
                    title = "Categor\u00edas",
                    isAdmin = isAdmin,
                    onNavClick = { route ->
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        CategoriesAdminScreen(
                            isAdmin = isAdmin,
                        )
                    }
                }
            }

            composable("admin/courses") {
                if (!canManageContent) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }

                AdminScaffold(
                    currentRoute = "admin/courses",
                    user = currentUser,
                    title = "Cursos",
                    isAdmin = isAdmin,
                    onNavClick = { route ->
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        CoursesAdminScreen(
                            isAdmin = isAdmin,
                            currentUserId = currentUser?.id,
                        )
                    }
                }
            }

            composable("admin/reviews") {
                if (!canManageContent) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }

                AdminScaffold(
                    currentRoute = "admin/reviews",
                    user = currentUser,
                    title = "Rese\u00f1as",
                    isAdmin = isAdmin,
                    onNavClick = { route ->
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        ReviewsAdminScreen(
                            isAdmin = isAdmin,
                        )
                    }
                }
            }

            composable("admin/progress") {
                if (!canManageContent) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }

                AdminScaffold(
                    currentRoute = "admin/progress",
                    user = currentUser,
                    title = "Progreso",
                    isAdmin = isAdmin,
                    onNavClick = { route ->
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        ProgressAdminScreen()
                    }
                }
            }

            composable("admin/enrollments") {
                if (!canManageContent) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }

                AdminScaffold(
                    currentRoute = "admin/enrollments",
                    user = currentUser,
                    title = "Matr\u00edculas",
                    isAdmin = isAdmin,
                    onNavClick = { route ->
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        EnrollmentsAdminScreen(
                            isAdmin = isAdmin,
                        )
                    }
                }
            }

            composable(Screen.AdminLessons.route) {
                if (!canManageContent) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }

                AdminScaffold(
                    currentRoute = "admin/lessons",
                    user = currentUser,
                    title = "Lecciones",
                    isAdmin = isAdmin,
                    onNavClick = { route ->
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        LessonsAdminScreen(
                            isAdmin = isAdmin,
                        )
                    }
                }
            }

            composable("admin/users") {
                if (!isAdmin) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }

                AdminScaffold(
                    currentRoute = "admin/users",
                    user = currentUser,
                    title = "Usuarios",
                    isAdmin = isAdmin,
                    onNavClick = { route ->
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        UsersAdminScreen()
                    }
                }
            }
        }
    }
}
