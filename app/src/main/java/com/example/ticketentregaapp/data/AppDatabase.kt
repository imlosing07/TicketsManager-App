package com.example.ticketentregaapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ticketentregaapp.data.dao.*
import com.example.ticketentregaapp.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FacultadEntity::class,
        ProgramaProfesionalEntity::class,
        EstudiantesEntity::class,
        TicketEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun facultadDao(): FacultadDao
    abstract fun programaProfesionalDao(): ProgramaProfesionalDao
    abstract fun estudiantesDao(): EstudiantesDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ticket_entrega_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            val facultadDao = database.facultadDao()
            val programaDao = database.programaProfesionalDao()
            val estudiantesDao = database.estudiantesDao()
            val ticketDao = database.ticketDao()

            facultadDao.insert(FacultadEntity("17", "FIPS", "A"))
            facultadDao.insert(FacultadEntity("18", "Educación", "A"))
            facultadDao.insert(FacultadEntity("19", "Ciencias", "A"))
            facultadDao.insert(FacultadEntity("20", "Ingeniería Civil", "A"))

            programaDao.insert(ProgramaProfesionalEntity("70", "Industrial", "A"))
            programaDao.insert(ProgramaProfesionalEntity("71", "Sistemas", "A"))
            programaDao.insert(ProgramaProfesionalEntity("72", "Telecomunicaciones", "A"))
            programaDao.insert(ProgramaProfesionalEntity("73", "Mecánica", "A"))
            programaDao.insert(ProgramaProfesionalEntity("74", "Electrónica", "A"))

            estudiantesDao.insert(EstudiantesEntity("2020001", "Juan Pérez López", "A"))
            estudiantesDao.insert(EstudiantesEntity("2020002", "María García Ruiz", "A"))
            estudiantesDao.insert(EstudiantesEntity("2020003", "Carlos Mendoza Silva", "A"))
            estudiantesDao.insert(EstudiantesEntity("2020004", "Ana Torres Vargas", "A"))
            estudiantesDao.insert(EstudiantesEntity("2020005", "Luis Ramos Castro", "A"))

            ticketDao.insert(TicketEntity("T001", "20240115", "2020001", "17", "71", "Solicitud de constancia de estudios", "A"))
            ticketDao.insert(TicketEntity("T002", "20240116", "2020002", "18", "70", "Revisión de notas del semestre", "A"))
            ticketDao.insert(TicketEntity("T003", "20240117", "2020003", "17", "72", "Cambio de horario de clases", "A"))
            ticketDao.insert(TicketEntity("T004", "20240118", "2020004", "19", "73", "Solicitud de beca académica", "A"))
        }
    }
}