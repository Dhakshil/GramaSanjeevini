package com.example.grama_sanjeevini.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.PrimaryColor

sealed class BottomNavItem(val route: String, val label: String, val iconRes: Int) {
    object Home   : BottomNavItem("home",    "Home",    R.drawable.ic_home)
    object Search : BottomNavItem("search",  "Search",  R.drawable.ic_search)
    object Profile: BottomNavItem("profile", "Profile", R.drawable.ic_account)
}

val bottomNavItems = listOf(BottomNavItem.Home, BottomNavItem.Search, BottomNavItem.Profile)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = androidx.compose.ui.unit.Dp(8f)
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(item.label, fontSize = 11.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = PrimaryColor,
                    selectedTextColor   = PrimaryColor,
                    indicatorColor      = PrimaryColor.copy(alpha = 0.12f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
