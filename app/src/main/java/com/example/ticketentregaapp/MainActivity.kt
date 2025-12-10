package com.example.ticketentregaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ticketentregaapp.data.AppDatabase
import com.example.ticketentregaapp.data.repository.*
import com.example.ticketentregaapp.ui.screen.MainMenuScreen
import com.example.ticketentregaapp.ui.theme.TicketEntregaAppTheme
import com.example.ticketentregaapp.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        val facultadViewModel = FacultadViewModel(FacultadRepository(database.facultadDao()))
        val programaViewModel = ProgramaProfesionalViewModel(ProgramaProfesionalRepository(database.programaProfesionalDao()))
        val estudiantesViewModel = EstudiantesViewModel(EstudiantesRepository(database.estudiantesDao()))
        val ticketViewModel = TicketViewModel(TicketRepository(database.ticketDao()))

        setContent {
            TicketEntregaAppTheme {
                MainMenuScreen(
                    facultadViewModel = facultadViewModel,
                    programaViewModel = programaViewModel,
                    estudiantesViewModel = estudiantesViewModel,
                    ticketViewModel = ticketViewModel
                )
            }
        }
    }
}