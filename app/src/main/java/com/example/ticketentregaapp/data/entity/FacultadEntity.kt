package com.example.ticketentregaapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facultad")
data class FacultadEntity(
    @PrimaryKey val codigo: String,
    val nombre: String,
    val estadoRegistro: String = "A"
)