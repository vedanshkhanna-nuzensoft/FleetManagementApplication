package com.vedansh.fleetmanagementapplication.StakeHolderDashboard.DataClass



data class RequestedEquipment(
    val id: String,
    val equipmentName: String,
    val quantity: Int,
    val requester: String, // Manager name
    val requestDate: String,
    val status: String // "Pending", "Approved", "Rejected"
)

val sampleRequests = listOf(
    RequestedEquipment("1", "Excavator", 2, "John Doe", "25 Aug 2025", "Pending"),
    RequestedEquipment("2", "Forklift", 1, "Alice Smith", "24 Aug 2025", "Pending"),
    RequestedEquipment("3", "Crane", 1, "Bob Johnson", "23 Aug 2025", "Pending")
)

