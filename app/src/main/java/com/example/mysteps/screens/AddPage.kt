package com.example.mysteps.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mysteps.R
import com.example.mysteps.data.AuthRepository
import com.example.mysteps.data.CityDto
import com.example.mysteps.data.SupabaseClient
import com.example.mysteps.data.VisitedPlaceDto
import com.example.mysteps.ui.theme.Green_btn
import com.example.mysteps.ui.theme.Green_but
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPage(
    lat: Double,
    lon: Double,
    editPlaceId: Long? = null,
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMaps: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }

    var title by remember { mutableStateOf("") }
    var ratingStr by remember { mutableStateOf("5") }
    var description by remember { mutableStateOf("") }

    var cityList by remember { mutableStateOf<List<CityDto>>(emptyList()) }
    var selectedCity by remember { mutableStateOf<CityDto?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var oldImageUrl by remember { mutableStateOf<String?>(null) }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        imageUri = uri
    }
    LaunchedEffect(Unit) {
        runCatching {
            SupabaseClient.client.postgrest["city"].select().decodeList<CityDto>()
        }.onSuccess { cities ->
            cityList = cities
            if (cities.isNotEmpty() && selectedCity == null) selectedCity = cities.first()
        }
    }
    LaunchedEffect(editPlaceId) {
        if (editPlaceId != null) {
            runCatching {
                SupabaseClient.client.postgrest["visited_places"].select {
                    filter { eq("visit_id", editPlaceId) }
                }.decodeSingle<VisitedPlaceDto>()
            }.onSuccess { existingPlace ->
                title = existingPlace.title
                description = existingPlace.description ?: ""
                ratingStr = existingPlace.rating.toString()
                oldImageUrl = existingPlace.image_url
                if (cityList.isNotEmpty()) {
                    selectedCity = cityList.find { it.cityId == existingPlace.city_id }
                }
            }.onFailure {
                Toast.makeText(context, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Card(modifier = Modifier.fillMaxWidth().height(80.dp),
            colors = CardDefaults.cardColors(containerColor = Green_but),
            shape = RectangleShape
        ) {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.padding(0.dp, 10.dp, 10.dp, 0.dp)){
                    Text(if (editPlaceId == null) "MySteps" else "Редактирование",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp))
                    Spacer(Modifier.width(150.dp))
                    Image(painter = painterResource(id = R.drawable.image_33),
                        contentDescription = "",
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 100.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = if (editPlaceId == null) "Добавить место" else "Изменить место", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Координаты: ${"%.4f".format(lat)}, ${"%.4f".format(lon)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        coil.compose.AsyncImage(
                            model = imageUri,
                            contentDescription = "Новое фото",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (oldImageUrl != null) {
                        coil.compose.AsyncImage(
                            model = oldImageUrl,
                            contentDescription = "Текущее фото",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Добавить фото", color = Color.Gray)
                        }
                    }
                }
                ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                OutlinedTextField(
                    value = selectedCity?.nameCity ?: "Загрузка городов...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Город") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    cityList.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city.nameCity) },
                            onClick = {
                                selectedCity = city
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
                OutlinedTextField(
                    value = ratingStr,
                    onValueChange = { ratingStr = it },
                    label = { Text("Оценка (1-5)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val rating = ratingStr.toIntOrNull()
                        val cityId = selectedCity?.cityId

                        if (title.isBlank() || rating == null || cityId == null) {
                            Toast.makeText(context, "Выберите город и введите оценку", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        coroutineScope.launch {
                            val userId = repository.getCurrentUserOd()
                            if (userId == null) {
                                Toast.makeText(context, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                                isLoading = false
                                return@launch
                            }

                            var uploadedImageUrl: String? = null

                            if (imageUri != null) {
                                try {
                                    val bytes = withContext(Dispatchers.IO) {
                                        context.contentResolver.openInputStream(imageUri!!)?.readBytes()
                                    }
                                    val fileName = "${UUID.randomUUID()}.jpg"
                                    SupabaseClient.client.storage.from("image").upload(fileName, bytes!!)
                                    uploadedImageUrl = SupabaseClient.client.storage.from("image").publicUrl(fileName)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Ошибка фото: ${e.message}", Toast.LENGTH_LONG).show()
                                    isLoading = false
                                    return@launch
                                }
                            } else {
                                uploadedImageUrl = oldImageUrl
                            }
                            if (editPlaceId != null) {
                                runCatching {
                                    SupabaseClient.client.postgrest["visited_places"].update(
                                        VisitedPlaceDto(
                                            users_id = userId,
                                            title = title,
                                            city_id = cityId,
                                            description = description,
                                            rating = rating,
                                            visit_date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                            image_url = uploadedImageUrl,
                                            latitude = lat,
                                            longitude = lon
                                        )
                                    ) {
                                        filter { eq("visit_id", editPlaceId) }
                                    }
                                }.onSuccess {
                                    Toast.makeText(context, "Обновлено!", Toast.LENGTH_SHORT).show()
                                    onSaved()
                                }.onFailure { error ->
                                    Toast.makeText(context, "Ошибка: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                val newPlace = VisitedPlaceDto(
                                    users_id = userId,
                                    title = title,
                                    city_id = cityId,
                                    description = description,
                                    rating = rating,
                                    visit_date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                    image_url = uploadedImageUrl,
                                    latitude = lat,
                                    longitude = lon
                                )
                                runCatching {
                                    SupabaseClient.client.postgrest["visited_places"].insert(newPlace)
                                }.onSuccess {
                                    Toast.makeText(context, "Сохранено!", Toast.LENGTH_SHORT).show()
                                    title = ""
                                    description = ""
                                    onSaved()
                                }.onFailure { error ->
                                    Toast.makeText(context, "Ошибка: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green_btn, contentColor = Color.White),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (editPlaceId == null) "Добавить" else "Сохранить", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
        ) {
        Card(modifier = Modifier.width(320.dp).height(60.dp).clip(CircleShape),
            colors = CardDefaults.cardColors(containerColor = Green_but)
        ) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Row {
                    IconButton(onClick = onNavigateToHome, modifier = Modifier.size(50.dp).background(color = Green_but, shape = CircleShape)) {
                        Image(painter = painterResource(id = R.drawable.home_png), contentDescription = "Домой", modifier = Modifier.size(28.dp), contentScale = ContentScale.Fit)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onNavigateToSearch, modifier = Modifier.size(50.dp).background(color = Green_but, shape = CircleShape)) {
                        Image(painter = painterResource(id = R.drawable.search_png), contentDescription = "Поиск", modifier = Modifier.size(28.dp), contentScale = ContentScale.Fit)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onNavigateToMaps, modifier = Modifier.size(50.dp).background(color = Green_but, shape = CircleShape)) {
                        Image(painter = painterResource(id = R.drawable.maps_png), contentDescription = "Карта", modifier = Modifier.size(28.dp), contentScale = ContentScale.Fit)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = {}, modifier = Modifier.size(50.dp).background(color = Green_but, shape = CircleShape)) {
                        Image(painter = painterResource(id = R.drawable.add_png), contentDescription = "Добавить", modifier = Modifier.size(28.dp), contentScale = ContentScale.Fit)
                    }
                }
            }
        }
    }
    }
}