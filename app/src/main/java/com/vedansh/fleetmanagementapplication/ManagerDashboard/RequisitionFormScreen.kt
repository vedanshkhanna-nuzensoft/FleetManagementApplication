package com.vedansh.fleetmanagementapplication.ManagerDashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import com.vedansh.fleetmanagementapplication.Notification.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequisitionFormScreen(
    onBack: () -> Unit,
    onRequestStatusClick: () -> Unit,
    onRequestHistoryClick: () -> Unit,
    existingRequest: Requisition? = null
) {
    var equipmentName by remember { mutableStateOf(TextFieldValue(existingRequest?.equipmentName ?: "")) }
    var quantity by remember { mutableStateOf(TextFieldValue(existingRequest?.quantity ?: "")) }
    var remarks by remember { mutableStateOf(TextFieldValue(existingRequest?.remarks ?: "")) }
    val context = LocalContext.current
    var expandedMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingRequest != null) "Edit Request" else "Requisition Form") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Request Approval Status") },
                            onClick = {
                                expandedMenu = false
                                onRequestStatusClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Request History") },
                            onClick = {
                                expandedMenu = false
                                onRequestHistoryClick()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input fields
            OutlinedTextField(
                value = equipmentName,
                onValueChange = { equipmentName = it },
                label = { Text("Equipment Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Remarks") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Submit button
            Button(
                onClick = {
                    if (existingRequest != null) {
                        // Update existing request
                        existingRequest.equipmentName = equipmentName.text
                        existingRequest.quantity = quantity.text
                        existingRequest.remarks = remarks.text
                    } else {
                        // Add new request
                        RequisitionRepository.requisitions.add(
                            Requisition(
                                equipmentName = equipmentName.text,
                                quantity = quantity.text,
                                remarks = remarks.text
                            )
                        )
                    }

                    // Send notification
                    try {
                        NotificationHelper.sendNotification(
                            context = context,
                            title = "New Equipment Request",
                            message = "A request for ${equipmentName.text} has been raised.",
                            notificationId = RequisitionRepository.requisitions.size
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        // Optionally show a Toast to inform the user
                        Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
                    }


                    // Clear fields
                    equipmentName = TextFieldValue("")
                    quantity = TextFieldValue("")
                    remarks = TextFieldValue("")

                    // Navigate back and clear existingRequest
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text(if (existingRequest != null) "Update Request" else "Submit Request", color = Color.White)
            }
        }
    }
}
