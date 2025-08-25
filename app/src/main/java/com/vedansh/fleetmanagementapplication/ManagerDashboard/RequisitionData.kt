package com.vedansh.fleetmanagementapplication.ManagerDashboard

import androidx.compose.runtime.mutableStateListOf

data class Requisition(
    var equipmentName: String,
    var quantity: String,
    var remarks: String,
    val status: String = "Pending"
)

object RequisitionRepository {
    val requisitions = mutableStateListOf<Requisition>()
}
