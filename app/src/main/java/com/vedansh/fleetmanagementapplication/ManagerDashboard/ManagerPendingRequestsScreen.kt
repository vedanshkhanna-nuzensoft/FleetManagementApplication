import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.isNotEmpty

@Parcelize
data class ManagerRequest(
    val id: String,
    val equipmentName: String,
    val quantity: Int,
    val remark: String,
    val supervisorName: String,
    val status: String,
    // NEW FIELDS - add these
    val priority: Priority = Priority.MEDIUM,
    val requestedDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val requiredDuration: Int = 1,
    val projectName: String = "General",
    val allocatedQuantity: Int = 0,
    val allocationNotes: String = ""
) : Parcelable

// ADD this new enum
enum class Priority(val displayName: String, val weight: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerPendingRequestsScreen(
    requests: List<ManagerRequest>,
    approvalHistory: List<ManagerRequest>, // pass history
    onBack: () -> Unit,
    onApprove: (ManagerRequest) -> Unit,
    onReject: (ManagerRequest) -> Unit,
    onViewHistory: () -> Unit,
    onApproveAll: (List<ManagerRequest>) -> Unit,
    onRejectAll: (List<ManagerRequest>) -> Unit
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Approvals") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
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
            items(requests) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Equipment: ${request.equipmentName}", fontSize = 18.sp)
                        Text("Quantity: ${request.quantity}", fontSize = 16.sp)
                        Text("Remark: ${request.remark}", fontSize = 14.sp, color = Color.DarkGray)
                        Text("Supervisor: ${request.supervisorName}", fontSize = 14.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(12.dp))

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
                                onClick = { onReject(request) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                            ) {
                                Text("Reject", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // 🔹 Confirmation Dialog for Approve All
    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Confirm Approval") },
            text = { Text("Are you sure you want to approve ALL requests?") },
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

    // 🔹 Confirmation Dialog for Reject All
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
