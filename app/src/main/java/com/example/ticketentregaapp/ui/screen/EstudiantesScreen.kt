package com.example.ticketentregaapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ticketentregaapp.R
import com.example.ticketentregaapp.data.entity.EstudiantesEntity
import com.example.ticketentregaapp.ui.viewmodel.EstudiantesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudiantesScreen(
    viewModel: EstudiantesViewModel,
    onBack: () -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<EstudiantesEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortByNombre by remember { mutableStateOf(false) }

    val items by viewModel.items.collectAsState()

    LaunchedEffect(searchQuery) {
        viewModel.setSearchQuery(searchQuery)
    }

    LaunchedEffect(sortByNombre) {
        viewModel.setSortByNombre(sortByNombre)
    }

    if (showForm) {
        EstudiantesFormScreen(
            viewModel = viewModel,
            item = editingItem,
            onSave = {
                showForm = false
                editingItem = null
            },
            onCancel = {
                showForm = false
                editingItem = null
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Estudiantes") },
                    navigationIcon = {
                        TextButton(onClick = onBack) {
                            Text("Salir")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    editingItem = null
                    showForm = true
                }) {
                    Text("+")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text("Ordenar por: ")
                    TextButton(onClick = { sortByNombre = false }) {
                        Text(if (!sortByNombre) "Código ✓" else "Código")
                    }
                    TextButton(onClick = { sortByNombre = true }) {
                        Text(if (sortByNombre) "Nombre ✓" else "Nombre")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(items) { item ->
                        val isInactive = item.estadoRegistro == "I"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    editingItem = item
                                    showForm = true
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isInactive)
                                    Color.LightGray.copy(alpha = 0.3f)
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Código: ${item.codigo}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isInactive) Color.Gray else Color.Unspecified
                                    )
                                    Text(
                                        "Nombre: ${item.nombre}",
                                        color = if (isInactive) Color.Gray else Color.Unspecified
                                    )
                                }
                                if (isInactive) {
                                    Text(
                                        "INACTIVO",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstudiantesFormScreen(
    viewModel: EstudiantesViewModel,
    item: EstudiantesEntity?,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var codigo by remember { mutableStateOf(item?.codigo ?: "") }
    var nombre by remember { mutableStateOf(item?.nombre ?: "") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val isEditing = item != null
    val isInactive = item?.estadoRegistro == "I"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp)
    ) {
        Text(
            text = if (isEditing) {
                if (isInactive) "Estudiante Inactivo" else "Editar Estudiante"
            } else {
                "Nuevo Estudiante"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = if (isInactive) Color.Gray else Color.Unspecified
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = codigo,
            onValueChange = {
                codigo = it
                errorMessage = ""
            },
            label = { Text("Código") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isEditing && !isInactive,
            isError = errorMessage.isNotEmpty(),
            supportingText = if (errorMessage.isNotEmpty()) {
                { Text(errorMessage, color = Color.Red) }
            } else null
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isInactive
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Imagen decorativa - Coloca tu imagen en res/drawable/estudiante_icon.png
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
                Image(
                    painter = painterResource(id = R.drawable.studen2),
                    contentDescription = "Icono Estudiante",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isInactive) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (codigo.isBlank() || nombre.isBlank()) {
                            errorMessage = "Todos los campos son obligatorios"
                            return@Button
                        }

                        scope.launch {
                            if (!isEditing && viewModel.codigoExists(codigo)) {
                                errorMessage = "El código '$codigo' ya existe. Ingrese otro código."
                                return@launch
                            }

                            if (isEditing) {
                                viewModel.update(EstudiantesEntity(codigo, nombre, item!!.estadoRegistro))
                            } else {
                                viewModel.insert(EstudiantesEntity(codigo, nombre, "A"))
                            }
                            onSave()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }

            if (isEditing) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.delete(codigo)
                                onSave()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.inactivate(codigo)
                                onSave()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("Inactivar")
                    }
                }
            }
        } else {
            // Opciones para registros inactivos
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.reactivate(codigo)
                            onSave()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Activar")
                }
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.delete(codigo)
                            onSave()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}