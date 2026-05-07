sealed class Screen(val route: String) {
    object Splash       : Screen("splash")
    object Login        : Screen("login")
    object Otp          : Screen("otp/{phone}/{role}") {
        fun createRoute(phone: String, role: String) = "otp/$phone/$role"
    }
    object Register     : Screen("register/{phone}")
    object Home         : Screen("home")
    object Search       : Screen("search")
    object ShopDetail   : Screen("shop/{pharmacyId}") {
        fun createRoute(id: String) = "shop/$id"
    }
    object Profile      : Screen("profile")
    object Pharmacist   : Screen("pharmacist")
    object AddListing   : Screen("add_listing")
}