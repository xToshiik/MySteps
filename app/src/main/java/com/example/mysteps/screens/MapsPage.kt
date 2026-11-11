package com.example.mysteps.screens

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mysteps.R
import com.example.mysteps.data.SupabaseClient
import com.example.mysteps.data.VisitedPlaceDto
import com.example.mysteps.ui.theme.Green_btn
import com.example.mysteps.ui.theme.Green_but
import io.github.jan.supabase.postgrest.postgrest
import org.osmdroid.tileprovider.modules.SqlTileWriter
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.events.MapEventsReceiver

@SuppressLint("ClickableViewAccessibility")
@Composable
fun MapsPage(
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onLongPress: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Загружаем сохраненные места из базы при открытии экрана
    LaunchedEffect(Unit) {
        runCatching {
            SupabaseClient.client.postgrest["visited_places"].select().decodeList<VisitedPlaceDto>()
        }.onSuccess { places ->
            mapViewRef?.let { map ->
                map.overlays.removeAll { it is Marker }
                places.forEach { place ->
                    val marker = Marker(map).apply {
                        position = GeoPoint(place.latitude, place.longitude)
                        title = place.title
                        snippet = place.description
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    map.overlays.add(marker)
                }
                map.invalidate()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val map = MapView(ctx)
                mapViewRef = map

                val source = org.osmdroid.tileprovider.tilesource.XYTileSource(
                    "OSM_FR", 0, 19, 256, ".png",
                    arrayOf("https://a.tile.openstreetmap.fr/hot/")
                )
                map.setTileSource(source)
                map.setMultiTouchControls(true)
                map.controller.setZoom(14.0)
                map.controller.setCenter(GeoPoint(62.0397, 129.7422))

                map.overlayManager.add(
                    org.osmdroid.views.overlay.MapEventsOverlay(
                        object : org.osmdroid.events.MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false
                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                if (p != null) {
                                    val newMarker = Marker(map).apply {
                                        position = p
                                        title = "Новое место"
                                    }
                                    map.overlays.add(newMarker)
                                    map.invalidate()
                                    onLongPress(p.latitude, p.longitude)
                                }
                                return true
                            }
                        }
                    )
                )
                map
            }
        )
        Card(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            colors = CardDefaults.cardColors(containerColor = Green_but),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("MySteps", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Image(painter = painterResource(id = R.drawable.image_33), contentDescription = "", modifier = Modifier.size(60.dp))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Card(
                modifier = Modifier.width(320.dp).height(60.dp).clip(CircleShape),
                colors = CardDefaults.cardColors(containerColor = Green_but)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateToHome, modifier = Modifier.size(50.dp)) {
                        Image(painter = painterResource(id = R.drawable.home_png), contentDescription = "Главная", modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = onNavigateToSearch, modifier = Modifier.size(50.dp)) {
                        Image(painter = painterResource(id = R.drawable.search_png), contentDescription = "Поиск", modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = {}, modifier = Modifier.size(50.dp)) {
                        Image(painter = painterResource(id = R.drawable.maps_png), contentDescription = "Карта", modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = onNavigateToAdd, modifier = Modifier.size(50.dp)) {
                        Image(painter = painterResource(id = R.drawable.add_png), contentDescription = "Добавить", modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}