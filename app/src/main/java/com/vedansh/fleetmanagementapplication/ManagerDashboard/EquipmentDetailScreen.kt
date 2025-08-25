package com.vedansh.fleetmanagementapplication.ManagerDashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass.EquipmentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetailScreen(equipmentType: EquipmentType, onBack: () -> Unit) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Available", "In Use", "Under Maintenance")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(equipmentType.typeName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter dropdown
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextField(
                    value = selectedFilter,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filter by Status") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedFilter = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Filtered list
            val filteredItems = when (selectedFilter) {
                "Available" -> equipmentType.items.filter { it.status.equals("Available", ignoreCase = true) }
                "In Use" -> equipmentType.items.filter { it.status.equals("In Use", ignoreCase = true) }
                "Under Maintenance" -> equipmentType.items.filter { it.status.equals("Under Maintenance", ignoreCase = true) }
                else -> equipmentType.items
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                items(filteredItems) { item ->
                    // Determine colors based on status
                    val (statusColor, backgroundColor) = when (item.status.lowercase()) {
                        "available" -> Color(0xFF4CAF50) to Color(0x114CAF50)   // Green text, light green bg
                        "in use" -> Color(0xFFFF9800) to Color(0x11FF9800)      // Orange text, light orange bg
                        "under maintenance" -> Color(0xFFF44336) to Color(0x11F44336) // Red text, light red bg
                        else -> Color.Gray to Color(0x11CCCCCC)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color.White) // Keep inside card white for contrast
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Equipment name (start)
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            // Status pill tag (end)
                            Box(
                                modifier = Modifier
                                    .background(backgroundColor, shape = MaterialTheme.shapes.small)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = item.status,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = statusColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
