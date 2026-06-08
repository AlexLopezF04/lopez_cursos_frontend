package com.alexlopez.cursosapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")

    data object Home : Screen("home")
    data object Catalog : Screen("catalog")
    data class Course(val id: Int = 0) : Screen("course/{id}") {
        fun createRoute(id: Int) = "course/$id"
    }

    data object Enrollments : Screen("enrollments")
    data class EnrollmentDetail(val id: Int = 0) : Screen("enrollments/{id}") {
        fun createRoute(id: Int) = "enrollments/$id"
    }
    data class LessonViewer(val cursoId: Int = 0, val leccionId: Int = 0, val matriculaId: Int? = null) : Screen("lesson/{cursoId}/{leccionId}?matriculaId={matriculaId}") {
        fun createRoute(cursoId: Int, leccionId: Int, matriculaId: Int? = null) =
            if (matriculaId != null) "lesson/$cursoId/$leccionId?matriculaId=$matriculaId"
            else "lesson/$cursoId/$leccionId"
    }
    data object Profile : Screen("profile")

    data object AdminDashboard : Screen("admin")
    data object AdminCategories : Screen("admin/categories")
    data object AdminCourses : Screen("admin/courses")
    data object AdminUsers : Screen("admin/users")
    data object AdminLessons : Screen("admin/lessons")
    data object AdminEnrollments : Screen("admin/enrollments")
    data object AdminProgress : Screen("admin/progress")
    data object AdminReviews : Screen("admin/reviews")
}
