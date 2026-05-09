package com.example.grama_sanjeevini.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.AppStrings
import com.example.grama_sanjeevini.constants.theme.Poppins
import com.example.grama_sanjeevini.ui.auth.LoginScreen
import com.example.grama_sanjeevini.ui.auth.OtpScreen
import com.example.grama_sanjeevini.ui.auth.RegisterScreen
import com.example.grama_sanjeevini.ui.home.HomeScreen
import com.example.grama_sanjeevini.ui.pharmacist.AddListingScreen
import com.example.grama_sanjeevini.ui.pharmacist.DashboardScreen
import com.example.grama_sanjeevini.ui.pharmacist.PrescriptionsScreen
import com.example.grama_sanjeevini.ui.profile.ProfileScreen
import com.example.grama_sanjeevini.ui.search.SearchScreen
import com.example.grama_sanjeevini.ui.shop.ShopDetailScreen
import com.example.grama_sanjeevini.ui.splash.SplashScreen
import com.example.grama_sanjeevini.viewmodel.AuthViewModel
import com.example.grama_sanjeevini.viewmodel.HomeViewModel
import com.example.grama_sanjeevini.viewmodel.PharmacistViewModel
import com.example.grama_sanjeevini.viewmodel.UserRole
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Splash       : Screen("splash")
    object Login        : Screen("login")
    object Register     : Screen("register")
    object Otp          : Screen("otp/{phone}/{role}/{isNewUser}") {
        fun createRoute(phone: String, role: String, isNewUser: Boolean) =
            "otp/$phone/$role/$isNewUser"
    }
    object CustomerMain : Screen("customer_main")
    object Home         : Screen("home")
    object Search       : Screen("search")
    object Profile      : Screen("profile")
    object ShopDetail   : Screen("shop/{pharmacyId}") {
        fun createRoute(id: String) = "shop/$id"
    }
    object PharmacistMain   : Screen("pharmacist_main")
    object PharmacistDash   : Screen("pharmacist_dash")
    object PharmacistRx     : Screen("pharmacist_rx")
    object AddListing        : Screen("add_listing")
}

// ── Root NavGraph ──────────────────────────────────────────────────────────────
@Composable
fun NavGraph(
    navController: NavHostController,
    isDark: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    val authViewModel: AuthViewModel      = viewModel()
    val pharmacistViewModel: PharmacistViewModel = viewModel()

    // State for the "no account found" dialog shown on the OTP screen
    var showUserNotFoundDialog by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ── Splash ────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = { destination ->
                    // destination is one of: "customer_main", "pharmacist_main", "login"
                    navController.navigate(destination) {
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
                    authViewModel.resetState()
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

            // React to auth state changes
            LaunchedEffect(authViewModel.authState) {
                when (val state = authViewModel.authState) {
                    is AuthViewModel.AuthState.Success -> {
                        val dest = if (state.role == UserRole.PHARMACIST)
                            Screen.PharmacistMain.route else Screen.CustomerMain.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                    is AuthViewModel.AuthState.UserNotFound -> {
                        // Show the dialog FIRST; navigation happens from dialog button
                        showUserNotFoundDialog = true
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

            // ── "No account found" dialog ──────────────────────────
            if (showUserNotFoundDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showUserNotFoundDialog = false
                        authViewModel.resetState()
                    },
                    title = {
                        Text(
                            "Account Not Found",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    },
                    text = {
                        Text(
                            AppStrings.ERR_LOGIN_NOT_FOUND,
                            fontFamily = Poppins,
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showUserNotFoundDialog = false
                                authViewModel.resetState()
                                navController.navigate(Screen.Register.route) {
                                    popUpTo(Screen.Otp.route) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Sign Up", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showUserNotFoundDialog = false
                                authViewModel.resetState()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Otp.route) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Back to Login", fontFamily = Poppins)
                        }
                    }
                )
            }
        }

        // ── Customer Main (with bottom nav) ───────────────────────
        composable(Screen.CustomerMain.route) {
            val homeViewModel: HomeViewModel = viewModel()
            CustomerMainScreen(
                homeViewModel       = homeViewModel,
                isDark              = isDark,
                onToggleDarkMode    = onToggleDarkMode,
                onNavigateToLogin   = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToShop    = { pharmacyId ->
                    navController.navigate(Screen.ShopDetail.createRoute(pharmacyId))
                }
            )
        }

        // ── Shop Detail ───────────────────────────────────────────
        composable(Screen.ShopDetail.route) { backStackEntry ->
            val pharmacyId = backStackEntry.arguments?.getString("pharmacyId") ?: ""
            ShopDetailScreen(
                pharmacyId = pharmacyId,
                onBack     = { navController.popBackStack() }
            )
        }

        // ── Pharmacist Main (with bottom nav) ─────────────────────
        composable(Screen.PharmacistMain.route) {
            PharmacistMainScreen(
                viewModel           = pharmacistViewModel,
                onNavigateToLogin   = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAddListing = {
                    navController.navigate(Screen.AddListing.route)
                }
            )
        }

        // ── Add Listing (pushed on top of pharmacist main) ────────
        composable(Screen.AddListing.route) {
            AddListingScreen(
                onBack    = { navController.popBackStack() },
                viewModel = pharmacistViewModel
            )
        }
    }
}

// ── Customer Main Screen (Bottom Nav Scaffold) ─────────────────────────────────
@Composable
fun CustomerMainScreen(
    homeViewModel: HomeViewModel,
    isDark: Boolean,
    onToggleDarkMode: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToShop: (String) -> Unit
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry  by innerNavController.currentBackStackEntryAsState()
    val currentRoute       = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate   = { route ->
                    innerNavController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController    = innerNavController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSearch = {
                        innerNavController.navigate(Screen.Search.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToShop = onNavigateToShop,
                    viewModel        = homeViewModel
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(viewModel = homeViewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    isDark           = isDark,
                    onToggleDarkMode = onToggleDarkMode,
                    onLogOut         = onNavigateToLogin
                )
            }
        }
    }
}

// ── Pharmacist Main Screen (Bottom Nav Scaffold) ───────────────────────────────
@Composable
fun PharmacistMainScreen(
    viewModel: PharmacistViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToAddListing: () -> Unit
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry  by innerNavController.currentBackStackEntryAsState()
    val currentRoute       = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Screen.PharmacistDash.route,
                    onClick  = {
                        innerNavController.navigate(Screen.PharmacistDash.route) {
                            popUpTo(Screen.PharmacistDash.route) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    icon    = {
                        Icon(
                            painter = painterResource(R.drawable.ic_inventory),
                            contentDescription = "Inventory"
                        )
                    },
                    label   = {
                        Text("Inventory", fontFamily = Poppins, fontSize = 11.sp)
                    }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.PharmacistRx.route,
                    onClick  = {
                        innerNavController.navigate(Screen.PharmacistRx.route) {
                            popUpTo(Screen.PharmacistDash.route) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    icon    = {
                        Icon(
                            painter = painterResource(R.drawable.ic_prescription),
                            contentDescription = "Prescriptions"
                        )
                    },
                    label   = {
                        BadgedBox(
                            badge = {
                                val pendingCount = viewModel.prescriptions.count { it.status == "pending" }
                                if (pendingCount > 0) {
                                    Badge { Text(pendingCount.toString()) }
                                }
                            }
                        ) {
                            Text("Rx", fontFamily = Poppins, fontSize = 11.sp)
                        }
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick  = {
                        // Sign out of Firebase before navigating to Login,
                        // so the next app launch won't auto-restore this session.
                        FirebaseAuth.getInstance().signOut()
                        onNavigateToLogin()
                    },
                    icon    = {
                        Icon(
                            painter = painterResource(R.drawable.ic_account),
                            contentDescription = "Log Out"
                        )
                    },
                    label   = {
                        Text("Log Out", fontFamily = Poppins, fontSize = 11.sp)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = innerNavController,
            startDestination = Screen.PharmacistDash.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.PharmacistDash.route) {
                DashboardScreen(
                    onAddListing = onNavigateToAddListing,
                    viewModel    = viewModel
                )
            }
            composable(Screen.PharmacistRx.route) {
                PrescriptionsScreen(viewModel = viewModel)
            }
        }
    }
}