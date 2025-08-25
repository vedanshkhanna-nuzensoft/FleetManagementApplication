import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass.AllocationResult
import com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass.EquipmentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedManagerPendingRequestsScreen(
    requests: List<ManagerRequest>,
    approvalHistory: List<ManagerRequest>,
    equipmentList: List<EquipmentType>, // NEW: Access to equipment data
    onBack: () -> Unit,
    onApprove: (ManagerRequest) -> Unit,
    onPartialApprove: (ManagerRequest, Int) -> Unit, // NEW: Partial approval
    onReject: (ManagerRequest, String) -> Unit, // NEW: Rejection with reason
    onViewHistory: () -> Unit,
    onApproveAll: (List<ManagerRequest>) -> Unit,
    onRejectAll: (List<ManagerRequest>) -> Unit,
    onViewAllocation: () -> Unit // NEW: View allocation dashboard
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var selectedRequest by remember { mutableStateOf<ManagerRequest?>(null) }
    var showPartialApprovalDialog by remember { mutableStateOf(false) }

    // Sort requests by priority
    val sortedRequests = requests.sortedWith(
        compareByDescending<ManagerRequest> { it.priority.weight }
            .thenBy { it.requestedDate }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Approvals (${requests.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onViewAllocation) {
                        Icon(Icons.Default.Info, contentDescription = "Allocation Dashboard")
                    }
                    IconButton(onClick = onViewHistory) {
                        Icon(Icons.Default.List, contentDescription = "Approval History")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (requests.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showApproveDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Approve All")
                    }

                    Button(
                        onClick = { showRejectDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject All")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedRequests) { request ->
                EnhancedRequestCard(
                    request = request,
                    equipmentList = equipmentList,
                    onApprove = { onApprove(request) },
                    onPartialApprove = {
                        selectedRequest = request
                        showPartialApprovalDialog = true
                    },
                    onReject = {
                        selectedRequest = request
                        // For now, reject with default reason
                        onReject(request, "Manager decision")
                    }
                )
            }
        }
    }

    // Partial Approval Dialog
    if (showPartialApprovalDialog && selectedRequest != null) {
        PartialApprovalDialog(
            request = selectedRequest!!,
            equipmentType = equipmentList.find { it.typeName == selectedRequest!!.equipmentName },
            onDismiss = {
                showPartialApprovalDialog = false
                selectedRequest = null
            },
            onPartialApprove = { quantity ->
                onPartialApprove(selectedRequest!!, quantity)
                showPartialApprovalDialog = false
                selectedRequest = null
            }
        )
    }

    // Existing dialogs...
    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Confirm Approval") },
            text = { Text("Are you sure you want to approve ALL requests? This will check equipment availability.") },
            confirmButton = {
                TextButton(onClick = {
                    showApproveDialog = false
                    onApproveAll(requests)
                }) {
                    Text("Yes", color = Color(0xFF4CAF50))
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Confirm Rejection") },
            text = { Text("Are you sure you want to reject ALL requests?") },
            confirmButton = {
                TextButton(onClick = {
                    showRejectDialog = false
                    onRejectAll(requests)
                }) {
                    Text("Yes", color = Color(0xFFF44336))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EnhancedRequestCard(
    request: ManagerRequest,
    equipmentList: List<EquipmentType>,
    onApprove: () -> Unit,
    onPartialApprove: () -> Unit,
    onReject: () -> Unit
) {
    // Get equipment availability
    val equipmentType = equipmentList.find { it.typeName == request.equipmentName }
    val allocationResult = equipmentType?.canFulfillRequest(request.quantity, request.requestedDate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (request.priority) {
                Priority.URGENT -> Color(0xFFFFF3E0) // Light orange background for urgent
                Priority.HIGH -> Color(0xFFF3E5F5) // Light purple for high
                else -> Color.White
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with priority and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority Badge
                PriorityBadge(request.priority)

                // Request Date
                Text(
                    text = "Requested: ${request.requestedDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Request Details
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Equipment: ${request.equipmentName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Qty: ${request.quantity}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1976D2)
                    )
                }

                Text("Project: ${request.projectName}", fontSize = 14.sp, color = Color.DarkGray)
                Text("Duration: ${request.requiredDuration} days", fontSize = 14.sp, color = Color.DarkGray)
                Text("Remark: ${request.remark}", fontSize = 14.sp, color = Color.DarkGray)
                Text("Supervisor: ${request.supervisorName}", fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Availability Status
            allocationResult?.let { result ->
                AvailabilityStatusCard(result, request.quantity)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                when (allocationResult) {
                    is AllocationResult.FullyAvailable -> {
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve")
                        }
                    }

                    is AllocationResult.PartiallyAvailable -> {
                        Button(
                            onClick = onPartialApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Partial (${allocationResult.availableUnits})")
                        }
                    }

                    is AllocationResult.NotAvailable -> {
                        Button(
                            onClick = onReject,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Not Available")
                        }
                    }

                    null -> {
                        // Fallback buttons when equipment type not found
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Approve")
                        }
                    }
                }

                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject")
                }
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: Priority) {
    val (backgroundColor, textColor) = when (priority) {
        Priority.URGENT -> Color(0xFFD32F2F) to Color.White
        Priority.HIGH -> Color(0xFFFF9800) to Color.White
        Priority.MEDIUM -> Color(0xFF1976D2) to Color.White
        Priority.LOW -> Color(0xFF757575) to Color.White
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = priority.displayName.uppercase(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AvailabilityStatusCard(result: AllocationResult, requestedQty: Int) {
    val (backgroundColor, iconColor, statusText, icon) = when (result) {
        is AllocationResult.FullyAvailable -> {
            Tuple4(
                Color(0xFFE8F5E8),
                Color(0xFF4CAF50),
                "✓ ${result.availableUnits} units available - Can fulfill completely",
                Icons.Default.CheckCircle
            )
        }
        is AllocationResult.PartiallyAvailable -> {
            Tuple4(
                Color(0xFFFFF3E0),
                Color(0xFFFF9800),
                "⚠ Only ${result.availableUnits}/${requestedQty} available - Partial approval possible",
                Icons.Default.Warning
            )
        }
        is AllocationResult.NotAvailable -> {
            Tuple4(
                Color(0xFFFFEBEE),
                Color(0xFFF44336),
                "✗ No units available - Next available: ${result.nextAvailableDate}",
                Icons.Default.Close
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                fontSize = 13.sp,
                color = iconColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PartialApprovalDialog(
    request: ManagerRequest,
    equipmentType: EquipmentType?,
    onDismiss: () -> Unit,
    onPartialApprove: (Int) -> Unit
) {
    val availableUnits = equipmentType?.canFulfillRequest(request.quantity, request.requestedDate)
        ?.let { result ->
            when (result) {
                is AllocationResult.PartiallyAvailable -> result.availableUnits
                is AllocationResult.FullyAvailable -> result.availableUnits
                else -> 0
            }
        } ?: 0

    var approvalQuantity by remember { mutableIntStateOf(availableUnits) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Partial Approval") },
        text = {
            Column {
                Text("Requested: ${request.quantity} ${request.equipmentName}")
                Text("Available: $availableUnits units")

                Spacer(modifier = Modifier.height(16.dp))

                Text("Approve quantity:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (approvalQuantity > 0) approvalQuantity-- },
                        enabled = approvalQuantity > 0
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                    }

                    Text(
                        text = approvalQuantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = { if (approvalQuantity < availableUnits) approvalQuantity++ },
                        enabled = approvalQuantity < availableUnits
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPartialApprove(approvalQuantity) },
                enabled = approvalQuantity > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("Approve $approvalQuantity")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper class for multiple return values
private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)