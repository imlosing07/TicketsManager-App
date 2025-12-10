package com.example.ticketentregaapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ticket",
    foreignKeys = [
        ForeignKey(
            entity = EstudiantesEntity::class,
            parentColumns = ["codigo"],
            childColumns = ["estudiante"]
        ),
        ForeignKey(
            entity = FacultadEntity::class,
            parentColumns = ["codigo"],
            childColumns = ["facultad"]
        ),
        ForeignKey(
            entity = ProgramaProfesionalEntity::class,
            parentColumns = ["codigo"],
            childColumns = ["programaProfesional"]
        )
    ]
)
data class TicketEntity(
    @PrimaryKey val numeroTicket: String,
    val fecha: String,
    val estudiante: String,
    val facultad: String,
    val programaProfesional: String,
    val descripcion: String,
    val estadoRegistro: String = "A"
)