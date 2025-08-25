package com.vedansh.fleetmanagementapplication.ManagerDashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass.EquipmentType
import com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass.equipmentList
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllocationDashboardScreen(
    onBack: () -> Unit,
    onViewRequests: () -> Unit,
    onEquipmentDetails: (EquipmentType) -> Unit
) {
    var selectedDate by remember {
        mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Allocation Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onViewRequests) {
                        Icon(Icons.Default.List, contentDescription = "View Requests")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Selector
            item {
                DateSelectorCard(
                    selectedDate = selectedDate,
                    onDateChange = { selectedDate = it }
                )
            }

            // Overall Summary
            item {
                OverallSummaryCard(equipmentList)
            }

            // Equipment Type Cards
            items(equipmentList) { equipmentType ->
                EquipmentAllocationCard(
                    equipmentType = equipmentType,
                    onClick = { onEquipmentDetails(equipmentType) }
                )
            }

            // Daily Allocation Summary
            item {
                DailyAllocationCard(selectedDate)
            }
        }
    }
}

@Composable
private fun DateSelectorCard(
    selectedDate: String,
    onDateChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Allocation Date",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = Color(0xFF1976D2)
                    )
                    Text(
                        text = selectedDate,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallSummaryCard(equipmentList: List<EquipmentType>) {
    val totalEquipment = equipmentList.sumOf { it.items.size }
    val totalAvailable = equipmentList.sumOf { it.items.count { item -> item.status == "Available" } }
    val totalInUse = equipmentList.sumOf { it.items.count { item -> item.status == "In Use" } }
    val totalMaintenance = equipmentList.sumOf { it.items.count { item -> item.status == "Under Maintenance" } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Fleet Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    title = "Total",
                    value = totalEquipment.toString(),
                    color = Color(0xFF757575),
                    icon = Icons.Default.Build
                )
                SummaryItem(
                    title = "Available",
                    value = totalAvailable.toString(),
                    color = Color(0xFF4CAF50),
                    icon = Icons.Default.CheckCircle
                )
                SummaryItem(
                    title = "In Use",
                    value = totalInUse.toString(),
                    color = Color(0xFFFF9800),
                    icon = Icons.Default.PlayArrow
                )
                SummaryItem(
                    title = "Maintenance",
                    value = totalMaintenance.toString(),
                    color = Color(0xFFF44336),
                    icon = Icons.Default.Build
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EquipmentAllocationCard(
    equipmentType: EquipmentType,
    onClick: () -> Unit
) {
    val availability = equipmentType.getAvailabilitySummary()

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Equipment Icon and Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = availability.typeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${availability.availableUnits}/${availability.totalUnits} Available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Status indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusPill("${availability.availableUnits} Free", Color(0xFF4CAF50))
                    StatusPill("${availability.inUseUnits} Busy", Color(0xFFFF9800))
                    if (availability.maintenanceUnits > 0) {
                        StatusPill("${availability.maintenanceUnits} Maintenance", Color(0xFFF44336))
                    }
                }
            }

            // Utilization Chart
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UtilizationChart(availability.utilizationRate)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${availability.utilizationRate}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = getUtilizationColor(availability.utilizationRate)
                )
            }
        }
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun UtilizationChart(utilizationRate: Int) {
    val color = getUtilizationColor(utilizationRate)

    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(40.dp)
        ) {
            val strokeWidth = 6.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val sweepAngle = (utilizationRate / 100f) * 360f

            // Background circle
            drawCircle(
                color = Color.Gray.copy(alpha = 0.2f),
                radius = radius,
                style = Stroke(strokeWidth)
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(strokeWidth)
            )
        }
    }
}

@Composable
private fun DailyAllocationCard(selectedDate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Daily Allocation Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mock data - replace with real data
            AllocationSummaryItem("Pending Requests", "5", Color(0xFFFF9800))
            AllocationSummaryItem("Approved Today", "12", Color(0xFF4CAF50))
            AllocationSummaryItem("Rejected Today", "2", Color(0xFFF44336))
            AllocationSummaryItem("Partial Approvals", "3", Color(0xFF9C27B0))
        }
    }
}

@Composable
private fun AllocationSummaryItem(title: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getUtilizationColor(utilizationRate: Int): Color {
    return when {
        utilizationRate < 30 -> Color(0xFF4CAF50) // Green - underutilized
        utilizationRate < 70 -> Color(0xFFFF9800) // Orange - optimal
        else -> Color(0xFFF44336) // Red - overutilized
    }
}