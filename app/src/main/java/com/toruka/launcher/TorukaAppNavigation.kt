package com.toruka.launcher

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.toruka.launcher.ui.components.NavItem
import com.toruka.launcher.ui.components.TorukaBottomNav
import com.toruka.launcher.ui.screens.*

@Composable
fun TorukaAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavItem.Home.route

    Scaffold(
        bottomBar = {
            TorukaBottomNav(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavItem.Home.route) {
                HomeScreen(
                    onNavigateToVersions = { navController.navigate(NavItem.Versions.route) },
                    onNavigateToAccounts = { /* TODO */ }
                )
            }
            composable(NavItem.Versions.route) {
                VersionsScreen()
            }
            composable(NavItem.Mods.route) {
                ModsScreen()
            }
            composable(NavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun ModsScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Моды (скоро)",
            modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
