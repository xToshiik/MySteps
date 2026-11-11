package com.example.mysteps.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mysteps.R
import com.example.mysteps.ui.theme.Green_but
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.mysteps.data.CityDto
import com.example.mysteps.data.SupabaseClient
import com.example.mysteps.data.VisitedPlaceDto
import com.example.mysteps.ui.theme.Green_btn
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    onNavigateToSearch: () -> Unit,
    onNavigateToMaps: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onPlaceClick: (Long) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var places by remember { mutableStateOf<List<VisitedPlaceDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        runCatching {
            SupabaseClient.client.postgrest["visited_places"].select().decodeList<VisitedPlaceDto>()
        }.onSuccess { loadedPlaces ->
            places = loadedPlaces.sortedByDescending { it.visit_id }
            isLoading = false
        }.onFailure { error ->
            Toast.makeText(context, "Ошибка загрузки: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
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

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Green_but)
        } else if (places.isEmpty()) {
            Text("Пока нет добавленных мест", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = 100.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(places) { place ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (place.visit_id != null)
                                {
                                    onPlaceClick(place.visit_id!!)
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            if (!place.image_url.isNullOrBlank()) {
                                AsyncImage(
                                    model = place.image_url.split(",").first(),
                                    contentDescription = "Фото",
                                    modifier = Modifier.fillMaxWidth().height(160.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(160.dp).background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Нет фото", color = Color.White)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = place.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("★", color = Color(0xFFFFC107), fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${place.rating}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Card(
                modifier = Modifier.width(320.dp).height(60.dp).clip(CircleShape),
                colors = CardDefaults.cardColors(containerColor = Green_but),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}, modifier = Modifier.size(50.dp)) {
                        Image(painter = painterResource(id = R.drawable.home_png), contentDescription = "Главная", modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = onNavigateToSearch, modifier = Modifier.size(50.dp)) {
                        Image(painter = painterResource(id = R.drawable.search_png), contentDescription = "Поиск", modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = onNavigateToMaps, modifier = Modifier.size(50.dp)) {
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

@Composable
fun PlaceDetailPage(
    placeId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onShowOnMap: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var place by remember { mutableStateOf<VisitedPlaceDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var cityName by remember { mutableStateOf("Загрузка...") }
    LaunchedEffect(placeId) {
        runCatching {
            SupabaseClient.client.postgrest["visited_places"].select {
                filter { eq("visit_id", placeId) }
            }.decodeSingle<VisitedPlaceDto>()
        }.onSuccess { loadedPlace ->
            place = loadedPlace
            try {
                val allCities = SupabaseClient.client.postgrest["city"].select().decodeList<CityDto>()
                val foundCity = allCities.find { it.cityId == loadedPlace.city_id}
                if (foundCity != null) {
                    cityName = foundCity.nameCity
                }
            } catch (e: Exception){
                cityName = "Неизвестно"
            }
            isLoading = false
        }.onFailure {
            Toast.makeText(context, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Green)
        } else if (place != null) {
            val currentPlace = place!!

            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    if (!currentPlace.image_url.isNullOrBlank()) {
                        AsyncImage(
                            model = currentPlace.image_url.split(",").first(),
                            contentDescription = "Фото",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
                    }
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .background(color = Green_btn.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .align(Alignment.TopStart)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
                    }
                    var isMenuExpanded by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier
                            .padding(16.dp)
                            .background(color = Green_btn.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Меню", tint = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Редактировать") },
                                onClick = {
                                    isMenuExpanded = false
                                    onEdit(placeId)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Удалить", color = Color.Red) },
                                onClick = {
                                    isMenuExpanded = false
                                    coroutineScope.launch {
                                        runCatching {
                                            SupabaseClient.client.postgrest["visited_places"].delete {
                                                filter { eq("visit_id", placeId) }
                                            }
                                        }.onSuccess {
                                            Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show()
                                            onBack()
                                        }.onFailure {
                                            Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Показать на карте") },
                                onClick = {
                                    isMenuExpanded = false
                                    onShowOnMap(currentPlace.latitude, currentPlace.longitude)
                                }
                            )
                        }
                    }
                }
                Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(currentPlace.title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text(cityName, fontSize = 18.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("★", color = Color(0xFFFFC107), fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${currentPlace.rating}.0", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentPlace.description ?: "Без описания",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Дата (${currentPlace.visit_date})",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun asd() {
}