package com.vedansh.fleetmanagementapplication.Navigation

import EnhancedManagerPendingRequestsScreen
import ManagerApprovalHistoryScreen
import ManagerRequest
import RequestHistoryScreen
import RequestStatusScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vedansh.fleetmanagementapplication.ManagerDashboard.*
import com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass.equipmentList
import com.vedansh.fleetmanagementapplication.Presentation.Login.LoginScreen
import com.vedansh.fleetmanagementapplication.Presentation.Splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object EquipmentTypeList : Screen("equipment_type_list")
    object EquipmentDetail : Screen("equipment_detail/{typeName}") {
        fun createRoute(typeName: String) = "equipment_detail/$typeName"
    }
    object RequisitionForm : Screen("requisition_form")
    object RequestStatus : Screen("request_status")
    object RequestHistory : Screen("request_history")
    object ManagerPendingRequests : Screen("manager_pending_requests")
    object ManagerApprovalHistory : Screen("manager_approval_history")
    // ADD this line in your Screen sealed class
    object AllocationDashboard : Screen("allocation_dashboard")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    var requestToEdit by remember { mutableStateOf<Requisition?>(null) }

    // FIXED: Single source of truth for approval history
    val approvalHistory = remember { mutableStateListOf<ManagerRequest>() }

    // FIXED: Single source of truth for pending requests
    val pendingRequests = remember {
        mutableStateListOf(
            ManagerRequest("1", "Excavator", 2, "Urgent requirement", "John Doe", "Pending"),
            ManagerRequest("2", "Forklift", 1, "Needed for warehouse", "Alice Smith", "Pending"),
            ManagerRequest("3", "Crane", 1, "Project site use", "Bob Johnson", "Pending")
        )
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        // Equipment Type List Screen (Dashboard)
        composable(Screen.EquipmentTypeList.route) {
            EquipmentTypeListScreen(
                equipmentList = equipmentList, // pass your equipment list
                onTypeClick = { type ->
                    navController.navigate(Screen.EquipmentDetail.createRoute(type.typeName))
                },
                onAddClick = {
                    navController.navigate(Screen.RequisitionForm.route)
                },
                onRequestClick = {
                    // Navigate to manager pending requests
                    navController.navigate(Screen.ManagerPendingRequests.route)
                },
                onLogoutClick = {
                    // Clear backstack and go to Login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Requisition Form
        composable(Screen.RequisitionForm.route) {
            RequisitionFormScreen(
                onBack = {
                    requestToEdit = null
                    navController.popBackStack()
                },
                onRequestStatusClick = { navController.navigate(Screen.RequestStatus.route) },
                onRequestHistoryClick = { navController.navigate(Screen.RequestHistory.route) },
                existingRequest = requestToEdit
            )
        }

        // FIXED: Manager Pending Requests - using shared state
        // REPLACE your existing manager pending requests composable
        composable(Screen.ManagerPendingRequests.route) {
            EnhancedManagerPendingRequestsScreen(
                requests = pendingRequests,
                approvalHistory = approvalHistory,
                equipmentList = equipmentList, // ADD this line
                onBack = { navController.popBackStack() },
                onApprove = { request ->
                    pendingRequests.remove(request)
                    approvalHistory.add(request.copy(status = "Approved"))
                },
                onPartialApprove = { request, quantity -> // NEW
                    pendingRequests.remove(request)
                    approvalHistory.add(request.copy(
                        status = "Partially Approved",
                        allocatedQuantity = quantity
                    ))
                },
                onReject = { request, reason -> // MODIFIED
                    pendingRequests.remove(request)
                    approvalHistory.add(request.copy(status = "Rejected"))
                },
                onViewHistory = { navController.navigate(Screen.ManagerApprovalHistory.route) },
                onApproveAll = { allRequests ->
                    approvalHistory.addAll(allRequests.map { it.copy(status = "Approved") })
                    pendingRequests.clear()
                },
                onRejectAll = { allRequests ->
                    approvalHistory.addAll(allRequests.map { it.copy(status = "Rejected") })
                    pendingRequests.clear()
                },
                onViewAllocation = { // NEW
                    navController.navigate(Screen.AllocationDashboard.route)
                }
            )
        }

        // ADD this new composable in your NavHost
        composable(Screen.AllocationDashboard.route) {
            AllocationDashboardScreen(
                onBack = { navController.popBackStack() },
                onViewRequests = { navController.navigate(Screen.ManagerPendingRequests.route) },
                onEquipmentDetails = { equipmentType ->
                    navController.navigate(Screen.EquipmentDetail.createRoute(equipmentType.typeName))
                }
            )
        }

        // FIXED: Manager Approval History Screen - using shared state
        composable(Screen.ManagerApprovalHistory.route) {
            ManagerApprovalHistoryScreen(
                requests = approvalHistory, // Now using the same state that gets updated
                onBack = { navController.popBackStack() }
            )
        }

        // Request Status
        composable(Screen.RequestStatus.route) {
            RequestStatusScreen(
                onBack = { navController.popBackStack() },
                onEditRequest = { request ->
                    requestToEdit = request
                    navController.navigate(Screen.RequisitionForm.route)
                }
            )
        }

        // Request History
        composable(Screen.RequestHistory.route) {
            RequestHistoryScreen(onBack = { navController.popBackStack() })
        }

        // Equipment Detail
        composable(
            Screen.EquipmentDetail.route,
            arguments = listOf(navArgument("typeName") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeName = backStackEntry.arguments?.getString("typeName")
            val selectedType = equipmentList.find { it.typeName == typeName }
            selectedType?.let {
                EquipmentDetailScreen(equipmentType = it) { navController.popBackStack() }
            }
        }
    }
}