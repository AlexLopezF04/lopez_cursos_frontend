package com.alexlopez.cursosapp.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexlopez.cursosapp.presentation.components.LoadingScreen
import com.alexlopez.cursosapp.presentation.components.RolBadge
import com.alexlopez.cursosapp.presentation.theme.*
import com.alexlopez.cursosapp.presentation.viewmodel.AuthViewModel
import com.alexlopez.cursosapp.presentation.viewmodel.UserViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val profileState by userViewModel.profileState.collectAsState()

    LaunchedEffect(Unit) { userViewModel.loadProfile() }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Accent, AccentLight)
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = AccentOnDark,
                    modifier = Modifier.size(40.dp),
                )
            }
            Spacer(Modifier.height(16.dp))

            val displayName = profileState.user?.username ?: currentUser?.username ?: "Usuario"
            val displayRol = profileState.user?.rol ?: currentUser?.rol ?: ""

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            RolBadge(rol = displayRol)

            Spacer(Modifier.height(24.dp))

            Surface(
                color = Surface,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (profileState.isLoading) {
                        LoadingScreen()
                    } else {
                        profileState.user?.let { user ->
                            ProfileField("Email", user.email)
                            ProfileField("Nombre", "${user.firstName} ${user.lastName}".trim())
                            ProfileField("Bio", user.bio.ifBlank { "Sin descripci\u00f3n" })
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            OutlinedButton(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Error)
                ),
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesi\u00f3n")
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )
    }
    HorizontalDivider(color = Border, thickness = 0.5.dp)
}
