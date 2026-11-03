package com.example.mysteps.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.mysteps.data.SupabaseClient
import com.example.mysteps.data.VisitedPlaceDto
import com.example.mysteps.ui.theme.Green_but
import io.github.jan.supabase.postgrest.postgrest
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mysteps.R

@Composable
fun SearchPage(
    onNavigateToHome: () -> Unit,
    onNavigateToMaps: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onPlaceClick: (Long) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    var allPlaces by remember { mutableStateOf<List<VisitedPlaceDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        runCatching {
            SupabaseClient.client.postgrest["visited_places"].select().decodeList<VisitedPlaceDto>()
        }.onSuccess { places ->
            allPlaces = places
            isLoading = false
        }.onFailure {
            isLoading = false
        }
    }
    val filteredPlaces = allPlaces.filter { place ->
        place.title.contains(searchQuery, ignoreCase = true) ||
                (place.description?.contains(searchQuery, ignoreCase = true) == true)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 100.dp, start = 20.dp, end = 20.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск мест...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск", tint = Green_but) },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Green_but,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Green_but)
            } else if (filteredPlaces.isEmpty()) {
                Text(
                    text = "Ничего не найдено",
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredPlaces) { place ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (place.visit_id != null) onPlaceClick(place.visit_id!!)
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column {
                                if (!place.image_url.isNullOrBlank()) {
                                    AsyncImage(
                                        model = place.image_url.split(",").first(),
                                        contentDescription = "Фото",
                                        modifier = Modifier.fillMaxWidth().height(120.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray)) {
                                        Text("Нет фото", modifier = Modifier.align(Alignment.Center), color = Color.White)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(place.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("★", color = Color(0xFFFFC107), fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${place.rating}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
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
                IconButton(onClick = {}, modifier = Modifier.size(50.dp)) {
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