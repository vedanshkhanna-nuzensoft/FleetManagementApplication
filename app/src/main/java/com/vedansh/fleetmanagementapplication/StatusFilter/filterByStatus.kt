package com.vedansh.fleetmanagementapplication.StatusFilter

fun <T> filterByStatus(
    items: List<T>,
    selectedStatus: String,
    statusSelector: (T) -> String
): List<T> {
    return if (selectedStatus == "All") {
        items
    } else {
        items.filter { statusSelector(it) == selectedStatus }
    }
}
