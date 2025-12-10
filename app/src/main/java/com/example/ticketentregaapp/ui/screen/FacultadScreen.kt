package com.example.ticketentregaapp.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ticketentregaapp.data.entity.FacultadEntity
import com.example.ticketentregaapp.ui.viewmodel.FacultadViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultadScreen(
    viewModel: FacultadViewModel,
    onBack: () -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<FacultadEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortByNombre by remember { mutableStateOf(false) }

    val items by viewModel.items.collectAsState()

    LaunchedEffect(searchQuery) { viewModel.setSearchQuery(searchQuery) }
    LaunchedEffect(sortByNombre) { viewModel.setSortByNombre(sortByNombre) }

    if (showForm) {
        FacultadFormScreen(
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
                    title = { Text("Facultades", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
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
                    onClick = {
                        editingItem = null
                        showForm = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .animateContentSize()
            ) {

                // -----------------------------
                //      BUSCADOR PREMIUM
                // -----------------------------
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar facultad") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --------------------------------------
                //          CHIPS DE FILTRO BONITOS
                // --------------------------------------
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    FilterChip(
                        selected = !sortByNombre,
                        onClick = { sortByNombre = false },
                        label = { Text("Código") },
                        leadingIcon = { if (!sortByNombre) Icon(Icons.Default.CheckCircle, null) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    FilterChip(
                        selected = sortByNombre,
                        onClick = { sortByNombre = true },
                        label = { Text("Nombre") },
                        leadingIcon = { if (sortByNombre) Icon(Icons.Default.CheckCircle, null) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --------------------------------------
                //        LISTA CON CARDS DINÁMICAS
                // --------------------------------------
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
                                .clickable {
                                    editingItem = item
                                    showForm = true
                                },
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
                                        Icon(Icons.Default.School, contentDescription = null, tint = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "Código: ${item.codigo}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(item.nombre)
                                }

                                if (isInactive) {
                                    Text(
                                        "INACTIVO",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultadFormScreen(
    viewModel: FacultadViewModel,
    item: FacultadEntity?,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var codigo by remember { mutableStateOf(item?.codigo ?: "") }
    var nombre by remember { mutableStateOf(item?.nombre ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val isEditing = item != null
    val isInactive = item?.estadoRegistro == "I"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Facultad" else "Nueva Facultad",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancelar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            OutlinedTextField(
                value = codigo,
                onValueChange = {
                    codigo = it
                    errorMessage = ""
                },
                label = { Text("Código") },
                enabled = !isEditing && !isInactive,
                isError = errorMessage.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                enabled = !isInactive,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isInactive) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    Button(
                        onClick = {
                            if (codigo.isBlank() || nombre.isBlank()) {
                                errorMessage = "Todos los campos son obligatorios"
                                return@Button
                            }

                            scope.launch {
                                if (!isEditing && viewModel.codigoExists(codigo)) {
                                    errorMessage = "El código ya existe"
                                    return@launch
                                }

                                if (isEditing) {
                                    viewModel.update(FacultadEntity(codigo, nombre, item!!.estadoRegistro))
                                } else {
                                    viewModel.insert(FacultadEntity(codigo, nombre, "A"))
                                }
                                onSave()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar")
                    }

                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }

                if (isEditing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.delete(codigo)
                                    onSave()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Eliminar")
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.inactivate(codigo)
                                    onSave()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Inactivar")
                        }
                    }
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.reactivate(codigo)
                            onSave()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CheckCircle, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Activar")
                }

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.delete(codigo)
                            onSave()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar")
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
