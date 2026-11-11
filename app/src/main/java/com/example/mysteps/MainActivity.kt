package com.example.mysteps

import android.os.Bundle
import android.view.Menu
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.example.mysteps.data.SupabaseClient
import com.example.mysteps.screens.AddPage
import com.example.mysteps.screens.HomePage
import com.example.mysteps.screens.MapsPage
import com.example.mysteps.screens.MenuMain
import com.example.mysteps.screens.PlaceDetailPage
import com.example.mysteps.screens.SearchPage
import com.example.mysteps.screens.SignIn
import com.example.mysteps.screens.SignUp
import com.example.mysteps.ui.theme.MyStepsTheme
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf("menu") }
            var isLoadingSession by remember { mutableStateOf(true) }
            val coroutineScope = rememberCoroutineScope()
            var lat by remember { mutableDoubleStateOf(62.0397) }
            var lon by remember { mutableDoubleStateOf(129.7422) }
            var selectedPlaceId by remember { mutableStateOf<Long?>(null) }
            var editPlaceId by remember { mutableStateOf<Long?>(null) }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1000)
                val currentUser = SupabaseClient.client.auth.currentUserOrNull()
                if (currentUser != null)
                {
                    currentScreen = "home"
                }
                isLoadingSession = false
            }
            if (isLoadingSession) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (currentScreen) {
                    "menu" -> MenuMain(
                        onNavigateToSignup = { currentScreen = "signup" },
                        onNavigateToSignin = { currentScreen = "signin" }
                    )

                    "signup" -> SignUp(
                        onNavigateToSignin = { currentScreen = "signin" }
                    )

                    "signin" -> SignIn(
                        onNavigateToSignup = { currentScreen = "signup" },
                        onAuthSuccess = { currentScreen = "home" }
                    )

                    "home" -> HomePage(
                        onNavigateToSearch = { currentScreen = "search" },
                        onNavigateToMaps = { currentScreen = "maps" },
                        onNavigateToAdd = { currentScreen = "add" },
                        onPlaceClick = { id ->
                            selectedPlaceId = id
                            currentScreen = "detail"
                        },
                        onLogout = { coroutineScope.launch {
                            SupabaseClient.client.auth.signOut()
                            selectedPlaceId = null
                            editPlaceId = null
                            currentScreen = "menu"
                        } }
                    )

                    "detail" -> PlaceDetailPage(
                        placeId = selectedPlaceId ?: 0L,
                        onBack = { currentScreen = "home" },
                        onEdit = { id ->
                            editPlaceId = id
                            currentScreen = "add"
                        },
                        onShowOnMap = { newlat, newlon ->
                            lat = newlat
                            lon = newlon
                            currentScreen = "maps"
                        }
                    )

                    "search" -> SearchPage(
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToMaps = { currentScreen = "maps" },
                        onNavigateToAdd = { currentScreen = "add" },
                        onPlaceClick = { id ->
                            selectedPlaceId = id
                            currentScreen = "detail"
                        }
                    )

                    "maps" -> MapsPage(
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToSearch = { currentScreen = "search" },
                        onNavigateToAdd = { currentScreen = "add" },
                        onLongPress = { newLat, newLon ->
                            lat = newLat
                            lon = newLon
                            currentScreen = "add"
                        },
                    )

                    "add" -> AddPage(
                        lat = lat,
                        lon = lon,
                        editPlaceId = editPlaceId,
                        onNavigateToHome = { currentScreen = "home" },
                        onNavigateToSearch = { currentScreen = "search" },
                        onNavigateToMaps = { currentScreen = "maps" },
                        onSaved = {
                            editPlaceId = null
                            currentScreen = "home"
                        }
                    )
                }
            }
        }
    }
}