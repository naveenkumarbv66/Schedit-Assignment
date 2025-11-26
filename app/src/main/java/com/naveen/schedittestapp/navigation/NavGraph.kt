package com.naveen.schedittestapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.naveen.schedittestapp.ui.assignment.AssignmentScreen
import com.naveen.schedittestapp.ui.employees.EmployeeDetailScreen
import com.naveen.schedittestapp.ui.employees.EmployeesScreen
import com.naveen.schedittestapp.ui.schedule.ScheduleScreen
import com.naveen.schedittestapp.ui.shifts.ShiftDetailScreen
import com.naveen.schedittestapp.ui.shifts.ShiftsScreen
import com.naveen.schedittestapp.ui.templates.ShiftTemplatesScreen

sealed class Screen(val route: String) {
    object Shifts : Screen("shifts")
    object ShiftDetail : Screen("shift_detail/{shiftId}") {
        fun createRoute(shiftId: Long) = "shift_detail/$shiftId"
    }
    object Employees : Screen("employees")
    object EmployeeDetail : Screen("employee_detail/{employeeId}") {
        fun createRoute(employeeId: Long) = "employee_detail/$employeeId"
    }
    object Schedule : Screen("schedule")
    object Assignment : Screen("assignment")
    object Templates : Screen("templates")
    object TemplateDetail : Screen("template_detail/{templateId}") {
        fun createRoute(templateId: Long) = "template_detail/$templateId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Shifts.route
    ) {
        composable(Screen.Shifts.route) {
            ShiftsScreen(
                onShiftClick = { shiftId ->
                    navController.navigate(Screen.ShiftDetail.createRoute(shiftId))
                },
                onNavigateToSchedule = {
                    navController.navigate(Screen.Schedule.route)
                },
                onNavigateToEmployees = {
                    navController.navigate(Screen.Employees.route)
                },
                onNavigateToAssignment = {
                    navController.navigate(Screen.Assignment.route)
                },
                onNavigateToTemplates = {
                    navController.navigate(Screen.Templates.route)
                }
            )
        }
        
        composable(Screen.ShiftDetail.route) { backStackEntry ->
            val shiftId = backStackEntry.arguments?.getString("shiftId")?.toLongOrNull() ?: 0L
            ShiftDetailScreen(
                shiftId = shiftId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEmployee = { employeeId ->
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                }
            )
        }
        
        composable(Screen.Employees.route) {
            EmployeesScreen(
                onEmployeeClick = { employeeId ->
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                },
                onNavigateToSchedule = {
                    navController.navigate(Screen.Schedule.route)
                },
                onNavigateToShifts = {
                    navController.navigate(Screen.Shifts.route)
                },
                onNavigateToAssignment = {
                    navController.navigate(Screen.Assignment.route)
                }
            )
        }
        
        composable(Screen.EmployeeDetail.route) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId")?.toLongOrNull() ?: 0L
            EmployeeDetailScreen(
                employeeId = employeeId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToShift = { shiftId ->
                    navController.navigate(Screen.ShiftDetail.createRoute(shiftId))
                }
            )
        }
        
        composable(Screen.Schedule.route) {
            ScheduleScreen(
                onNavigateToShift = { shiftId ->
                    navController.navigate(Screen.ShiftDetail.createRoute(shiftId))
                },
                onNavigateToEmployee = { employeeId ->
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Assignment.route) {
            AssignmentScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToShift = { shiftId ->
                    navController.navigate(Screen.ShiftDetail.createRoute(shiftId))
                },
                onNavigateToEmployee = { employeeId ->
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                }
            )
        }
        
        composable(Screen.Templates.route) {
            ShiftTemplatesScreen(
                onNavigateBack = { navController.popBackStack() },
                onTemplateClick = { templateId ->
                    // Could navigate to template detail if needed
                }
            )
        }
    }
}

