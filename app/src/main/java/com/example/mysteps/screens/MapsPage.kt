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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mysteps.R
import com.example.mysteps.ui.theme.Green_btn
import com.example.mysteps.ui.theme.Green_but
import org.osmdroid.tileprovider.modules.SqlTileWriter
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("ClickableViewAccessibility")
@Composable
fun MapsPage(
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onLongPress: (Double, Double) -> Unit
) {
    remember {
        runCatching { SqlTileWriter().purgeCache("Mapnik") }
        true
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val map = MapView(ctx)
                val source = org.osmdroid.tileprovider.tilesource.XYTileSource(
                    "OSM_FR", 0, 19, 256, ".png",
                    arrayOf("https://a.tile.openstreetmap.fr/hot/")
                )

                map.setTileSource(source)
                map.setMultiTouchControls(true)
                map.controller.setZoom(14.0)
                map.controller.setCenter(GeoPoint(62.0397, 129.7422))

                map.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        v.tag = System.currentTimeMillis()
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        val startTime = v.tag as? Long ?: 0L
                        if (System.currentTimeMillis() - startTime > 500) {
                            val geoPoint = map.projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                            map.overlays.add(Marker(map).apply { position = geoPoint })
                            map.invalidate()
                            onLongPress(geoPoint.latitude, geoPoint.longitude)
                        }
                    }
                    false
                }
                map
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.TopCenter),
            colors = CardDefaults.cardColors(
                containerColor = Green_but
            ), shape = RectangleShape
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.padding(0.dp, 10.dp, 10.dp, 0.dp)) {
                    Text(
                        "MySteps",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                    Spacer(Modifier.width(150.dp))
                    Image(
                        painter = painterResource(id = R.drawable.image_33),
                        contentDescription = "",
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 25.dp)
                .align(Alignment.BottomCenter), // Привязываем к низу
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Card(
                modifier = Modifier.width(320.dp).height(60.dp).clip(CircleShape),
                colors = CardDefaults.cardColors(
                    containerColor = Green_but
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {
                        IconButton(
                            onClick = onNavigateToHome,
                            modifier = Modifier
                                .size(50.dp)
                                .background(color = Green_btn, shape = CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.home_png),
                                contentDescription = "Домой",
                                modifier = Modifier.size(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = onNavigateToSearch,
                            modifier = Modifier
                                .size(50.dp)
                                .background(color = Green_btn, shape = CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.search_png),
                                contentDescription = "Поиск",
                                modifier = Modifier.size(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(50.dp)
                                .background(color = Green_btn, shape = CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.maps_png),
                                contentDescription = "Карта",
                                modifier = Modifier.size(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = onNavigateToAdd,
                            modifier = Modifier
                                .size(50.dp)
                                .background(color = Green_btn, shape = CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.add_png),
                                contentDescription = "Добавления",
                                modifier = Modifier.size(28.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
    }
}