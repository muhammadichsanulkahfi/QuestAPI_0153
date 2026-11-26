package com.example.mydatasiswa.uicontroller.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mydatasiswa.view.DetailSiswaScreen
import com.example.mydatasiswa.view.EditSiswaScreen
import com.example.mydatasiswa.view.EntrySiswaScreen
import com.example.mydatasiswa.view.HomeScreen

@Composable
fun DataSiswaApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    HostNavigasi(navController = navController, modifier = modifier)
}

@Composable
fun HostNavigasi(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Kembali ke Home dan bersihkan back stack agar daftar dimuat ulang
    val kembaliKeHome: () -> Unit = {
        navController.navigate(DestinasiHome.route) {
            popUpTo(DestinasiHome.route) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = modifier
    ) {
        composable(DestinasiHome.route) {
            HomeScreen(
                navigateToItemEntry = { navController.navigate(DestinasiEntry.route) },
                onDetailClick = { itemId ->
                    navController.navigate("${DestinasiDetail.route}/$itemId")
                }
            )
        }
        composable(DestinasiEntry.route) {
            EntrySiswaScreen(navigateBack = kembaliKeHome)
        }
        composable(
            route = DestinasiDetail.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetail.itemIdArg) { type = NavType.IntType })
        ) {
            DetailSiswaScreen(
                navigateToEditItem = { navController.navigate("${DestinasiEdit.route}/$it") },
                navigateBack = kembaliKeHome
            )
        }
        composable(
            route = DestinasiEdit.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEdit.itemIdArg) { type = NavType.IntType })
        ) {
            EditSiswaScreen(navigateBack = kembaliKeHome)
        }
    }
}