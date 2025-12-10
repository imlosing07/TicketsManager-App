package com.example.ticketentregaapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketentregaapp.ui.viewmodel.*

@Composable
fun MainMenuScreen(
    facultadViewModel: FacultadViewModel,
    programaViewModel: ProgramaProfesionalViewModel,
    estudiantesViewModel: EstudiantesViewModel,
    ticketViewModel: TicketViewModel
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.MainMenu) }

    when (currentScreen) {
        Screen.MainMenu -> MainMenu(
            onNavigateToFacultad = { currentScreen = Screen.Facultad },
            onNavigateToPrograma = { currentScreen = Screen.Programa },
            onNavigateToEstudiantes = { currentScreen = Screen.Estudiantes },
            onNavigateToTicket = { currentScreen = Screen.Ticket }
        )
        Screen.Facultad -> FacultadScreen(
            viewModel = facultadViewModel,
            onBack = { currentScreen = Screen.MainMenu }
        )
        Screen.Programa -> ProgramaProfesionalScreen(
            viewModel = programaViewModel,
            onBack = { currentScreen = Screen.MainMenu }
        )
        Screen.Estudiantes -> EstudiantesScreen(
            viewModel = estudiantesViewModel,
            onBack = { currentScreen = Screen.MainMenu }
        )
        Screen.Ticket -> TicketScreen(
            viewModel = ticketViewModel,
            facultadViewModel = facultadViewModel,
            programaViewModel = programaViewModel,
            estudiantesViewModel = estudiantesViewModel,
            onBack = { currentScreen = Screen.MainMenu }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(
    onNavigateToFacultad: () -> Unit,
    onNavigateToPrograma: () -> Unit,
    onNavigateToEstudiantes: () -> Unit,
    onNavigateToTicket: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ticket de Entrega",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Sistema de Gestión",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Selecciona una opción para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Menu Cards
            MenuCard(
                icon = Icons.Default.School,
                title = "Facultades",
                description = "Gestionar facultades",
                onClick = onNavigateToFacultad
            )

            MenuCard(
                icon = Icons.Default.BookmarkBorder,
                title = "Programas Profesionales",
                description = "Gestionar programas",
                onClick = onNavigateToPrograma
            )

            MenuCard(
                icon = Icons.Default.Person,
                title = "Estudiantes",
                description = "Gestionar estudiantes",
                onClick = onNavigateToEstudiantes
            )

            MenuCard(
                icon = Icons.Default.Description,
                title = "Tickets",
                description = "Gestionar tickets de entrega",
                onClick = onNavigateToTicket
            )
        }
    }
}

@Composable
fun MenuCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

sealed class Screen {
    object MainMenu : Screen()
    object Facultad : Screen()
    object Programa : Screen()
    object Estudiantes : Screen()
    object Ticket : Screen()
}