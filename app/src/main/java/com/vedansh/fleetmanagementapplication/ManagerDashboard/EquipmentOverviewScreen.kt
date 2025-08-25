import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.vedansh.fleetmanagementapplication.ManagerDashboard.EquipmentDetailScreen
import com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass.EquipmentType
import com.vedansh.fleetmanagementapplication.ManagerDashboard.EquipmentTypeListScreen
import com.vedansh.fleetmanagementapplication.ManagerDashboard.DataClass.equipmentList

@Composable
fun EquipmentOverviewScreen() {
    var selectedType by remember { mutableStateOf<EquipmentType?>(null) }
    val context = LocalContext.current

    if (selectedType == null) {
        EquipmentTypeListScreen(
            equipmentList = equipmentList, // provide your list
            onTypeClick = { type -> selectedType = type },
            onAddClick = {
                Toast.makeText(context, "Add new Equipment Type", Toast.LENGTH_SHORT).show()
            },
            onRequestClick = {
                Toast.makeText(context, "Request clicked", Toast.LENGTH_SHORT).show()
            },
            onLogoutClick = {
                Toast.makeText(context, "Logout clicked", Toast.LENGTH_SHORT).show()
                // Here you can navigate back to login screen
            }
        )
    } else {
        EquipmentDetailScreen(equipmentType = selectedType!!) {
            selectedType = null
        }
    }
}
