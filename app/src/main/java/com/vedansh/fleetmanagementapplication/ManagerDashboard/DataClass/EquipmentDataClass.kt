package com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val equipmentList = listOf(
    EquipmentType(
        "Loaders",
        List(20) { index ->
            EquipmentItem(
                id = index + 1,
                name = "Loader ${index + 1}",
                status = when (index % 3) {
                    0 -> "Available"
                    1 -> "In Use"
                    else -> "Under Maintenance"
                }
            )
        }
    ),
    EquipmentType(
        "Dumpers",
        List(15) { index ->
            EquipmentItem(
                id = index + 101,
                name = "Dumper ${index + 1}",
                status = when (index % 3) {
                    0 -> "Available"
                    1 -> "In Use"
                    else -> "Under Maintenance"
                }
            )
        }
    ),
    EquipmentType(
        "Trucks",
        List(25) { index ->
            EquipmentItem(
                id = index + 201,
                name = "Truck ${index + 1}",
                status = when (index % 3) {
                    0 -> "Available"
                    1 -> "In Use"
                    else -> "Under Maintenance"
                }
            )
        }
    )
)

@Parcelize
data class EquipmentItem(
    val id: Int,
    val name: String,
    val status: String,
    val currentAllocation: AllocationInfo? = null, // NEW
    val maintenanceSchedule: String? = null // NEW
) : Parcelable

data class EquipmentType(
    val typeName: String,
    val items: List<EquipmentItem>
) {
    // ADD these new functions
    @RequiresApi(Build.VERSION_CODES.O)
    fun canFulfillRequest(requestedQuantity: Int, requestedDate: String): AllocationResult {
        val available = items.count { it.status == "Available" }

        return when {
            available >= requestedQuantity -> AllocationResult.FullyAvailable(available)
            available > 0 -> AllocationResult.PartiallyAvailable(available, requestedQuantity)
            else -> AllocationResult.NotAvailable(LocalDate.now().plusDays(1).format(
                DateTimeFormatter.ISO_LOCAL_DATE))
        }
    }

    fun getAvailabilitySummary(): EquipmentAvailability {
        val available = items.count { it.status == "Available" }
        val inUse = items.count { it.status == "In Use" }
        val maintenance = items.count { it.status == "Under Maintenance" }
        val total = items.size

        return EquipmentAvailability(
            typeName = typeName,
            totalUnits = total,
            availableUnits = available,
            inUseUnits = inUse,
            maintenanceUnits = maintenance,
            utilizationRate = if (total > 0) (inUse.toFloat() / total * 100).toInt() else 0
        )
    }
}

// ADD this new data class
data class EquipmentAvailability(
    val typeName: String,
    val totalUnits: Int,
    val availableUnits: Int,
    val inUseUnits: Int,
    val maintenanceUnits: Int,
    val utilizationRate: Int
)

// ADD this new allocation info class
@Parcelize
data class AllocationInfo(
    val allocatedTo: String,
    val allocatedDate: String,
    val expectedReturnDate: String,
    val requestId: String
) : Parcelable

// ADD these new result classes
sealed class AllocationResult {
    data class FullyAvailable(val availableUnits: Int) : AllocationResult()
    data class PartiallyAvailable(val availableUnits: Int, val requestedUnits: Int) : AllocationResult()
    data class NotAvailable(val nextAvailableDate: String) : AllocationResult()
}
