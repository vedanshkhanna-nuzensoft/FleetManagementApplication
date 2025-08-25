import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedansh.fleetmanagementapplication.StakeHolderDashboard.DataClass.RequestedEquipment
import com.vedansh.fleetmanagementapplication.StatusFilter.StatusFilterDropdown
import com.vedansh.fleetmanagementapplication.StatusFilter.filterByStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestedEquipmentDashboardScreen(
    requests: List<RequestedEquipment>,
    onBack: () -> Unit,
    onApprove: (RequestedEquipment) -> Unit,
    onReject: (RequestedEquipment, String) -> Unit,
    onApproveAll: (List<RequestedEquipment>) -> Unit,
    onRejectAll: (List<RequestedEquipment>) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    val filteredRequests = filterByStatus(requests, selectedFilter) { it.status }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Requested Equipment") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (filteredRequests.isNotEmpty()) {
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
                        Text("Approve All", color = Color.White)
                    }

                    Button(
                        onClick = { showRejectDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject All", color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            // Status Filter Dropdown
            StatusFilterDropdown(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )

            // Requests List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredRequests) { request ->
                    EquipmentRequestCard(
                        request = request,
                        onApprove = onApprove,
                        onReject = onReject
                    )
                }
            }
        }
    }

    // Approve All Confirmation
    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Confirm Approval") },
            text = { Text("Are you sure you want to approve ALL requests?") },
            confirmButton = {
                TextButton(onClick = {
                    showApproveDialog = false
                    onApproveAll(filteredRequests)
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

    // Reject All Confirmation
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Confirm Rejection") },
            text = { Text("Are you sure you want to reject ALL requests?") },
            confirmButton = {
                TextButton(onClick = {
                    showRejectDialog = false
                    onRejectAll(filteredRequests)
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

@Composable
fun EquipmentRequestCard(
    request: RequestedEquipment,
    onApprove: (RequestedEquipment) -> Unit,
    onReject: (RequestedEquipment, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Equipment: ${request.equipmentName}", fontSize = 18.sp)
            Text("Quantity: ${request.quantity}", fontSize = 16.sp)
            Text("Requested by: ${request.requester}", fontSize = 14.sp, color = Color.DarkGray)
            Text("Request Date: ${request.requestDate}", fontSize = 14.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(8.dp))

            val statusColor = when (request.status) {
                "Pending" -> Color(0xFFFFC107) // Amber
                "Approved" -> Color(0xFF4CAF50) // Green
                "Rejected" -> Color(0xFFF44336) // Red
                else -> Color.Gray
            }
            Text("Status: ${request.status}", color = statusColor, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(12.dp))

            if (request.status == "Pending") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onApprove(request) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Approve", color = Color.White)
                    }

                    Button(
                        onClick = { onReject(request, "Not enough allocation") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text("Reject", color = Color.White)
                    }
                }
            }
        }
    }
}

