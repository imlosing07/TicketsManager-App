package com.example.ticketentregaapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ticketentregaapp.R
import com.example.ticketentregaapp.data.entity.*
import com.example.ticketentregaapp.ui.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    viewModel: TicketViewModel,
    facultadViewModel: FacultadViewModel,
    programaViewModel: ProgramaProfesionalViewModel,
    estudiantesViewModel: EstudiantesViewModel,
    onBack: () -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<TicketEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortByFecha by remember { mutableStateOf(false) }

    val items by viewModel.items.collectAsState()

    LaunchedEffect(searchQuery) {
        viewModel.setSearchQuery(searchQuery)
    }

    LaunchedEffect(sortByFecha) {
        viewModel.setSortByFecha(sortByFecha)
    }

    if (showForm) {
        TicketFormScreen(
            viewModel = viewModel,
            facultadViewModel = facultadViewModel,
            programaViewModel = programaViewModel,
            estudiantesViewModel = estudiantesViewModel,
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
                    title = { Text("Tickets") },
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
                    TextButton(onClick = { sortByFecha = false }) {
                        Text(if (!sortByFecha) "Número ✓" else "Número")
                    }
                    TextButton(onClick = { sortByFecha = true }) {
                        Text(if (sortByFecha) "Fecha ✓" else "Fecha")
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
                                        "Ticket: ${item.numeroTicket}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isInactive) Color.Gray else Color.Unspecified
                                    )
                                    Text(
                                        "Fecha: ${item.fecha}",
                                        color = if (isInactive) Color.Gray else Color.Unspecified
                                    )
                                    Text(
                                        "Descripción: ${item.descripcion}",
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
fun TicketFormScreen(
    viewModel: TicketViewModel,
    facultadViewModel: FacultadViewModel,
    programaViewModel: ProgramaProfesionalViewModel,
    estudiantesViewModel: EstudiantesViewModel,
    item: TicketEntity?,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var numeroTicket by remember { mutableStateOf(item?.numeroTicket ?: "") }
    var fecha by remember { mutableStateOf(item?.fecha ?: "") }
    var estudianteCodigo by remember { mutableStateOf(item?.estudiante ?: "") }
    var estudianteNombre by remember { mutableStateOf("") }
    var facultadCodigo by remember { mutableStateOf(item?.facultad ?: "") }
    var facultadNombre by remember { mutableStateOf("") }
    var programaCodigo by remember { mutableStateOf(item?.programaProfesional ?: "") }
    var programaNombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf(item?.descripcion ?: "") }
    var showEstudiantesSelector by remember { mutableStateOf(false) }
    var showFacultadSelector by remember { mutableStateOf(false) }
    var showProgramaSelector by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val isEditing = item != null
    val isInactive = item?.estadoRegistro == "I"

    LaunchedEffect(Unit) {
        if (!isEditing) {
            // Generar número de ticket automáticamente para nuevos tickets
            numeroTicket = viewModel.getNextTicketNumber()
        } else {
            // Cargar datos relacionados para edición
            estudiantesViewModel.getByCodigo(item!!.estudiante)?.let {
                estudianteNombre = it.nombre
            }
            facultadViewModel.getByCodigo(item.facultad)?.let {
                facultadNombre = it.nombre
            }
            programaViewModel.getByCodigo(item.programaProfesional)?.let {
                programaNombre = it.nombre
            }
        }
    }

    if (showEstudiantesSelector) {
        SelectorScreen(
            title = "Seleccionar Estudiante",
            items = estudiantesViewModel.items.collectAsState().value.filter { it.estadoRegistro == "A" },
            onSelect = { estudiante ->
                estudianteCodigo = estudiante.codigo
                estudianteNombre = estudiante.nombre
                showEstudiantesSelector = false
            },
            onCancel = { showEstudiantesSelector = false },
            getCode = { it.codigo },
            getName = { it.nombre }
        )
    } else if (showFacultadSelector) {
        SelectorScreen(
            title = "Seleccionar Facultad",
            items = facultadViewModel.items.collectAsState().value.filter { it.estadoRegistro == "A" },
            onSelect = { facultad ->
                facultadCodigo = facultad.codigo
                facultadNombre = facultad.nombre
                showFacultadSelector = false
            },
            onCancel = { showFacultadSelector = false },
            getCode = { it.codigo },
            getName = { it.nombre }
        )
    } else if (showProgramaSelector) {
        SelectorScreen(
            title = "Seleccionar Programa",
            items = programaViewModel.items.collectAsState().value.filter { it.estadoRegistro == "A" },
            onSelect = { programa ->
                programaCodigo = programa.codigo
                programaNombre = programa.nombre
                showProgramaSelector = false
            },
            onCancel = { showProgramaSelector = false },
            getCode = { it.codigo },
            getName = { it.nombre }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(48.dp)
        ) {
            Text(
                text = if (isEditing) {
                    if (isInactive) "Ticket Inactivo" else "Editar Ticket"
                } else {
                    "Nuevo Ticket"
                },
                style = MaterialTheme.typography.headlineSmall,
                color = if (isInactive) Color.Gray else Color.Unspecified
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Número de Ticket - SIEMPRE DESHABILITADO
            OutlinedTextField(
                value = numeroTicket,
                onValueChange = { },
                label = { Text(if (isEditing) "Número de Ticket" else "Número asignado") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (AAAAMMDD)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isInactive
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showEstudiantesSelector = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isInactive
            ) {
                Text(
                    if (estudianteNombre.isEmpty()) "Seleccionar Estudiante"
                    else "$estudianteCodigo - $estudianteNombre"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showFacultadSelector = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isInactive
            ) {
                Text(
                    if (facultadNombre.isEmpty()) "Seleccionar Facultad"
                    else "$facultadCodigo - $facultadNombre"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showProgramaSelector = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isInactive
            ) {
                Text(
                    if (programaNombre.isEmpty()) "Seleccionar Programa"
                    else "$programaCodigo - $programaNombre"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isInactive
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Imagen decorativa - Coloca tu imagen en res/drawable/ticket_icon.png
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                    Image(
                        painter = painterResource(id = R.drawable.ticket),
                        contentDescription = "Icono Ticket",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isInactive) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            if (fecha.isBlank() || estudianteCodigo.isBlank() ||
                                facultadCodigo.isBlank() || programaCodigo.isBlank() ||
                                descripcion.isBlank()) {
                                return@Button
                            }

                            scope.launch {
                                if (isEditing) {
                                    viewModel.update(TicketEntity(
                                        numeroTicket, fecha, estudianteCodigo,
                                        facultadCodigo, programaCodigo, descripcion, item!!.estadoRegistro
                                    ))
                                } else {
                                    viewModel.insert(TicketEntity(
                                        numeroTicket, fecha, estudianteCodigo,
                                        facultadCodigo, programaCodigo, descripcion, "A"
                                    ))
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
                                    viewModel.delete(numeroTicket)
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
                                    viewModel.inactivate(numeroTicket)
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
                // Opciones para tickets inactivos
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.reactivate(numeroTicket)
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
                                viewModel.delete(numeroTicket)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectorScreen(
    title: String,
    items: List<T>,
    onSelect: (T) -> Unit,
    onCancel: () -> Unit,
    getCode: (T) -> String,
    getName: (T) -> String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Cancelar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSelect(item) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${getCode(item)} - ${getName(item)}")
                    }
                }
            }
        }
    }
}