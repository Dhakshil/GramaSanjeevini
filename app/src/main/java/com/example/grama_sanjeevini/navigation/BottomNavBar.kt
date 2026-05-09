package com.example.grama_sanjeevini.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_sanjeevini.R
import com.example.grama_sanjeevini.constants.theme.Poppins
import androidx.compose.ui.Modifier

sealed class BottomNavItem(val route: String, val label: String, val iconRes: Int) {
    object Home   : BottomNavItem("home",    "Home",    R.drawable.home)
    object Search : BottomNavItem("search",  "Search",  R.drawable.ic_search)
    object Profile: BottomNavItem("profile", "Profile", R.drawable.profile)
}

val bottomNavItems = listOf(BottomNavItem.Home, BottomNavItem.Search, BottomNavItem.Profile)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    NavigationBar(
        containerColor = cs.surface,
        tonalElevation = 8.dp
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
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(item.label, fontSize = 11.sp, fontFamily = Poppins)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = cs.primary,
                    selectedTextColor   = cs.primary,
                    indicatorColor      = cs.primary.copy(alpha = 0.12f),
                    unselectedIconColor = cs.onBackground.copy(alpha = 0.4f),
                    unselectedTextColor = cs.onBackground.copy(alpha = 0.4f)
                )
            )
        }
    }
}
