package com.example.ticketentregaapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estudiantes")
data class EstudiantesEntity(
    @PrimaryKey val codigo: String,
    val nombre: String,
    val estadoRegistro: String = "A"
)