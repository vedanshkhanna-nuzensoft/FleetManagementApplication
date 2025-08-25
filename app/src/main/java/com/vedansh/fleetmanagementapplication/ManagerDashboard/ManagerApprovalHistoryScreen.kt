import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerApprovalHistoryScreen(
    requests: List<ManagerRequest>, // MutableStateList will trigger recomposition automatically
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Pending", "Approved", "Rejected")
    var expandedFilter by remember { mutableStateOf(false) }

    val filteredRequests = if (selectedFilter == "All") requests else requests.filter { it.status == selectedFilter }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Approval History") },
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
            // Filter Dropdown
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                OutlinedButton(
                    onClick = { expandedFilter = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(text = "Filter: $selectedFilter") }

                DropdownMenu(
                    expanded = expandedFilter,
                    onDismissRequest = { expandedFilter = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedFilter = option
                                expandedFilter = false
                            }
                        )
                    }
                }
            }

            if (filteredRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(text = "No requests found", color = Color.Gray) }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredRequests) { request ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color(0xFFE3F2FD))
                                    .padding(16.dp)
                            ) {
                                Text("Equipment: ${request.equipmentName}", fontWeight = FontWeight.Bold)
                                Text("Quantity: ${request.quantity}", color = Color(0xFF1976D2))
                                Text("Remark: ${request.remark}", color = Color.Gray)
                                Text("Supervisor: ${request.supervisorName}", color = Color.Gray)

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = when (request.status) {
                                                "Pending" -> Color(0xFFFFC107)
                                                "Approved" -> Color(0xFF4CAF50)
                                                "Rejected" -> Color(0xFFF44336)
                                                else -> Color.Gray
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = request.status, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
