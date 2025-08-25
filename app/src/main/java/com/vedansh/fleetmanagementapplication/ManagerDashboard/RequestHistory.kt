import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vedansh.fleetmanagementapplication.ManagerDashboard.RequisitionRepository
import com.vedansh.fleetmanagementapplication.StatusFilter.StatusFilterDropdown
import com.vedansh.fleetmanagementapplication.StatusFilter.filterByStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestHistoryScreen(
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }

    // Filter requisitions from repository
    val filteredRequests = filterByStatus(
        items = RequisitionRepository.requisitions,
        selectedStatus = selectedFilter,
        statusSelector = { it.status }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .padding(8.dp)
        ) {
            // 🔹 Filter dropdown at top
            StatusFilterDropdown(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            if (filteredRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No history found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredRequests) { request ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE3F2FD))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = request.equipmentName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Quantity: ${request.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1976D2)
                                )
                                Text(
                                    text = "Remarks: ${request.remarks}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF424242)
                                )
                                Text(
                                    text = "Status: ${request.status}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = when (request.status) {
                                        "Pending" -> Color(0xFFFFC107)
                                        "Approved" -> Color(0xFF4CAF50)
                                        "Rejected" -> Color(0xFFF44336)
                                        else -> Color.Gray
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}