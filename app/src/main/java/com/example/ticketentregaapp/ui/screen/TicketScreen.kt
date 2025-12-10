package com.example.ticketentregaapp.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketentregaapp.data.entity.TicketEntity
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
    var sortByNumero by remember { mutableStateOf(true) }

    val items by viewModel.items.collectAsState()

    LaunchedEffect(searchQuery) { viewModel.setSearchQuery(searchQuery) }
    // Si tu ViewModel tiene setSortByNumero, descomenta esta línea:
    // LaunchedEffect(sortByNumero) { viewModel.setSortByNumero(sortByNumero) }

    if (showForm) {
        TicketFormScreen(
            viewModel = viewModel,
            facultadViewModel = facultadViewModel,
            programaViewModel = programaViewModel,
            estudiantesViewModel = estudiantesViewModel,
            item = editingItem,
            onSave = { showForm = false; editingItem = null },
            onCancel = { showForm = false; editingItem = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tickets", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { editingItem = null; showForm = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) { Icon(Icons.Default.Add, null) }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .animateContentSize()
            ) {

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar ticket") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = sortByNumero,
                        onClick = { sortByNumero = true },
                        label = { Text("Número") },
                        leadingIcon = { if (sortByNumero) Icon(Icons.Default.CheckCircle, null) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    FilterChip(
                        selected = !sortByNumero,
                        onClick = { sortByNumero = false },
                        label = { Text("Fecha") },
                        leadingIcon = { if (!sortByNumero) Icon(Icons.Default.CheckCircle, null) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn {
                    items(items) { item ->
                        val isInactive = item.estadoRegistro == "I"

                        val bgColor by animateColorAsState(
                            targetValue = if (isInactive) Color(0xFFF1E9E9) else MaterialTheme.colorScheme.surface,
                            animationSpec = tween(400)
                        )

                        val scaleAnim by animateFloatAsState(
                            targetValue = if (isInactive) 0.96f else 1f,
                            animationSpec = tween(400)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .scale(scaleAnim)
                                .clickable { editingItem = item; showForm = true },
                            shape = RoundedCornerShape(22.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(containerColor = bgColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(50.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Person, null, tint = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(Modifier.weight(1f)) {
                                    Text("Ticket: ${item.numeroTicket}", fontWeight = FontWeight.Bold)
                                    Text("Fecha: ${item.fecha}")
                                    Text("Descripción: ${item.descripcion}")
                                }

                                if (isInactive) {
                                    Text("INACTIVO", color = Color.Red, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    // Cargar datos relacionados para edición
    LaunchedEffect(Unit) {
        if (!isEditing) numeroTicket = viewModel.getNextTicketNumber()
        else {
            estudiantesViewModel.getByCodigo(item!!.estudiante)?.let { estudianteNombre = it.nombre }
            facultadViewModel.getByCodigo(item.facultad)?.let { facultadNombre = it.nombre }
            programaViewModel.getByCodigo(item.programaProfesional)?.let { programaNombre = it.nombre }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo semi-transparente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(
                    onClick = onCancel,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        )

        // Card principal centrada
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isEditing) {
                        if (isInactive) "Ticket Inactivo" else "Editar Ticket"
                    } else "Nuevo Ticket",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isInactive) Color.Gray else MaterialTheme.colorScheme.primary
                )

                // Número de Ticket - solo lectura
                OutlinedTextField(
                    value = numeroTicket,
                    onValueChange = {},
                    label = { Text("Número de Ticket") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                // Fecha
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (AAAAMMDD)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isInactive,
                    shape = RoundedCornerShape(16.dp)
                )

                // Selección elegante: Estudiante
                SelectionField(
                    label = "Seleccionar Estudiante",
                    code = estudianteCodigo,
                    name = estudianteNombre,
                    enabled = !isInactive,
                    onClick = { showEstudiantesSelector = true }
                )

                // Selección elegante: Facultad
                SelectionField(
                    label = "Seleccionar Facultad",
                    code = facultadCodigo,
                    name = facultadNombre,
                    enabled = !isInactive,
                    onClick = { showFacultadSelector = true }
                )

                // Selección elegante: Programa
                SelectionField(
                    label = "Seleccionar Programa",
                    code = programaCodigo,
                    name = programaNombre,
                    enabled = !isInactive,
                    onClick = { showProgramaSelector = true }
                )

                // Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = !isInactive,
                    shape = RoundedCornerShape(16.dp)
                )

                // Botones dinámicos
                if (!isInactive) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                if (fecha.isBlank() || estudianteCodigo.isBlank() || facultadCodigo.isBlank() ||
                                    programaCodigo.isBlank() || descripcion.isBlank()
                                ) return@Button

                                scope.launch {
                                    if (isEditing) {
                                        viewModel.update(
                                            TicketEntity(
                                                numeroTicket, fecha, estudianteCodigo,
                                                facultadCodigo, programaCodigo, descripcion, item!!.estadoRegistro
                                            )
                                        )
                                    } else {
                                        viewModel.insert(
                                            TicketEntity(
                                                numeroTicket, fecha, estudianteCodigo,
                                                facultadCodigo, programaCodigo, descripcion, "A"
                                            )
                                        )
                                    }
                                    onSave()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Guardar") }

                        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    }

                    if (isEditing) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { scope.launch { viewModel.delete(numeroTicket); onSave() } },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.weight(1f)
                            ) { Text("Eliminar") }

                            Button(
                                onClick = { scope.launch { viewModel.inactivate(numeroTicket); onSave() } },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                modifier = Modifier.weight(1f)
                            ) { Text("Inactivar") }
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { scope.launch { viewModel.reactivate(numeroTicket); onSave() } },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.weight(1f)
                        ) { Text("Activar") }

                        Button(
                            onClick = { scope.launch { viewModel.delete(numeroTicket); onSave() } },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.weight(1f)
                        ) { Text("Eliminar") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
                }
            }
        }
    }

    // --------------------------
    // SELECTORES DINÁMICOS
    // --------------------------
    if (showEstudiantesSelector) {
        SelectorScreen(
            title = "Seleccionar Estudiante",
            items = estudiantesViewModel.items.collectAsState().value.filter { it.estadoRegistro == "A" },
            onSelect = {
                estudianteCodigo = it.codigo
                estudianteNombre = it.nombre
                showEstudiantesSelector = false
            },
            onCancel = { showEstudiantesSelector = false },
            getCode = { it.codigo },
            getName = { it.nombre }
        )
    }

    if (showFacultadSelector) {
        SelectorScreen(
            title = "Seleccionar Facultad",
            items = facultadViewModel.items.collectAsState().value.filter { it.estadoRegistro == "A" },
            onSelect = {
                facultadCodigo = it.codigo
                facultadNombre = it.nombre
                showFacultadSelector = false
            },
            onCancel = { showFacultadSelector = false },
            getCode = { it.codigo },
            getName = { it.nombre }
        )
    }

    if (showProgramaSelector) {
        SelectorScreen(
            title = "Seleccionar Programa",
            items = programaViewModel.items.collectAsState().value.filter { it.estadoRegistro == "A" },
            onSelect = {
                programaCodigo = it.codigo
                programaNombre = it.nombre
                showProgramaSelector = false
            },
            onCancel = { showProgramaSelector = false },
            getCode = { it.codigo },
            getName = { it.nombre }
        )
    }
}

@Composable
fun SelectionField(
    label: String,
    code: String,
    name: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(if (name.isEmpty()) "Seleccionar..." else "$code - $name", style = MaterialTheme.typography.bodyLarge)
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
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(searchQuery, items) {
        items.filter {
            getCode(it).contains(searchQuery, ignoreCase = true) ||
                    getName(it).contains(searchQuery, ignoreCase = true)
        }
    }

    // Fondo semi-transparente
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onCancel, indication = null, interactionSource = remember { MutableInteractionSource() }),
        contentAlignment = Alignment.Center
    ) {
        // Modal centrado
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.7f)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                // Título
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lista filtrable con scroll
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredItems) { item ->
                        var isPressed by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(item) }
                                .animateContentSize(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${getCode(item)} - ${getName(item)}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Toque para seleccionar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón de cerrar
                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        }
    }
}
