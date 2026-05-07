package com.example.grama_sanjeevini.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.grama_sanjeevini.ui.auth.LoginScreen
import com.example.grama_sanjeevini.ui.auth.OtpScreen
import com.example.grama_sanjeevini.ui.auth.RegisterScreen
import com.example.grama_sanjeevini.ui.home.HomeScreen
import com.example.grama_sanjeevini.ui.pharmacist.AddListingScreen
import com.example.grama_sanjeevini.ui.pharmacist.DashboardScreen
import com.example.grama_sanjeevini.ui.profile.ProfileScreen
import com.example.grama_sanjeevini.ui.search.SearchScreen
import com.example.grama_sanjeevini.ui.shop.ShopDetailScreen
import com.example.grama_sanjeevini.ui.splash.SplashScreen
import com.example.grama_sanjeevini.viewmodel.AuthViewModel
import com.example.grama_sanjeevini.viewmodel.HomeViewModel
import com.example.grama_sanjeevini.viewmodel.PharmacistViewModel
import com.example.grama_sanjeevini.viewmodel.UserRole

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Login       : Screen("login")
    object Register    : Screen("register")
    object Otp         : Screen("otp/{phone}/{role}/{isNewUser}") {
        fun createRoute(phone: String, role: String, isNewUser: Boolean) =
            "otp/$phone/$role/$isNewUser"
    }
    object CustomerMain: Screen("customer_main")
    object Home        : Screen("home")
    object Search      : Screen("search")
    object Profile     : Screen("profile")
    object ShopDetail  : Screen("shop/{pharmacyId}") {
        fun createRoute(id: String) = "shop/$id"
    }
    object Pharmacist  : Screen("pharmacist")
    object AddListing  : Screen("add_listing")
}

// ── Root NavGraph ──────────────────────────────────────────────────────────────
@Composable
fun NavGraph(navController: NavHostController) {

    val authViewModel: AuthViewModel = viewModel()
    val pharmacistViewModel: PharmacistViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ── Splash ────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ─────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToOtp = { phone, role ->
                    authViewModel.resetState()
                    navController.navigate(Screen.Otp.createRoute(phone, role.name, false))
                },
                onNavigateToRegister = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // ── Register ──────────────────────────────────────────────
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToOtp = { phone, role, name ->
                    authViewModel.setPendingName(name)
                    authViewModel.resetState()          // ← clear stale UserNotFound state
                    navController.navigate(Screen.Otp.createRoute(phone, role.name, true))
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ── OTP ───────────────────────────────────────────────────
        composable(Screen.Otp.route) { backStackEntry ->
            val phone     = backStackEntry.arguments?.getString("phone") ?: ""
            val role      = UserRole.valueOf(
                backStackEntry.arguments?.getString("role") ?: "CUSTOMER"
            )
            val isNewUser = backStackEntry.arguments?.getString("isNewUser").toBoolean()

            // React to AuthViewModel state changes
            LaunchedEffect(authViewModel.authState) {
                when (val state = authViewModel.authState) {
                    is AuthViewModel.AuthState.Success -> {
                        val dest = if (state.role == UserRole.PHARMACIST)
                            Screen.Pharmacist.route else Screen.CustomerMain.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                    is AuthViewModel.AuthState.UserNotFound -> {
                        navController.navigate(Screen.Register.route) {
                            popUpTo(Screen.Otp.route) { inclusive = true }
                        }
                    }
                    is AuthViewModel.AuthState.AlreadyExists -> {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Otp.route) { inclusive = true }
                        }
                    }
                    else -> {}
                }
            }

            OtpScreen(
                phone = phone,
                onOtpVerified = { firebaseUser ->
                    if (isNewUser) {
                        authViewModel.onRegisterOtpSuccess(
                            user = firebaseUser,
                            name = authViewModel.pendingName,
                            role = role
                        )
                    } else {
                        authViewModel.onLoginOtpSuccess(firebaseUser)
                    }
                }
            )
        }

        // ── Customer Main (with bottom nav) ───────────────────────
        composable(Screen.CustomerMain.route) {
            val homeViewModel: HomeViewModel = viewModel()
            CustomerMainScreen(
                homeViewModel = homeViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToShop = { pharmacyId ->
                    navController.navigate(Screen.ShopDetail.createRoute(pharmacyId))
                }
            )
        }

        // ── Shop Detail ───────────────────────────────────────────
        composable(Screen.ShopDetail.route) { backStackEntry ->
            val pharmacyId = backStackEntry.arguments?.getString("pharmacyId") ?: ""
            ShopDetailScreen(
                pharmacyId = pharmacyId,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Pharmacist Dashboard ──────────────────────────────────
        composable(Screen.Pharmacist.route) {
            DashboardScreen(
                onAddListing = { navController.navigate(Screen.AddListing.route) },
                viewModel = pharmacistViewModel
            )
        }

        // ── Add Listing ───────────────────────────────────────────
        composable(Screen.AddListing.route) {
            AddListingScreen(
                onBack = { navController.popBackStack() },
                viewModel = pharmacistViewModel
            )
        }
    }
}

// ── Customer Main Screen (Bottom Nav Scaffold) ─────────────────────────────────
@Composable
fun CustomerMainScreen(
    homeViewModel: HomeViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToShop: (String) -> Unit
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    innerNavController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSearch = {
                        innerNavController.navigate(Screen.Search.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToShop = onNavigateToShop,
                    viewModel = homeViewModel
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(viewModel = homeViewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onLogOut = onNavigateToLogin)
            }
        }
    }
}