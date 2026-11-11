package com.example.mysteps.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysteps.R
import com.example.mysteps.data.AuthViewModel
import com.example.mysteps.ui.theme.Green_but
import com.example.mysteps.ui.theme.MyStepsTheme

@Composable
fun MenuMain(
    onNavigateToSignup: () -> Unit,
    onNavigateToSignin: () -> Unit,
){
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fon),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
            ) {
            Text(
                text = "MySteps",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                modifier = Modifier
                    .width(250.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green_but),
                onClick = onNavigateToSignin,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "Sign in",
                    fontSize = 18.sp,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .width(250.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                onClick = onNavigateToSignup,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "Sign up",
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Composable
fun SignUp(
    onNavigateToSignin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
){
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    LaunchedEffect(isSuccess){
        if(isSuccess) {
            onNavigateToSignin()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearFields()
        viewModel.isLoginMode.value = false
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fon),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 64.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "MySteps",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 40.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(54.dp))
                Text(
                    text = "email",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.value = it },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.width(275.dp)
                        .height(54.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "password",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.password.value = it },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.width(275.dp)
                        .height(54.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color(0xFFFFCDD2),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLoading){
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Button(
                        modifier = Modifier
                            .width(275.dp)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green_but),
                        onClick = { viewModel.authenticate() },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = "Sign up",
                            fontSize = 18.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onNavigateToSignin) {
                    Text(text = "Уже есть аккаунт? Sign in", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SignIn(
    onNavigateToSignup: () -> Unit,
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
){
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    LaunchedEffect(isSuccess){
        if(isSuccess) {
            onAuthSuccess()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearFields()
        viewModel.isLoginMode.value = true
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fon),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 64.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "MySteps",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 40.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(54.dp))
                Text(
                    text = "email",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.value = it },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.width(275.dp)
                        .height(54.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "password",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.password.value = it },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.width(275.dp)
                        .height(54.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color(0xFFFFCDD2),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLoading){
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Button(
                        modifier = Modifier
                            .width(275.dp)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green_but),
                        onClick = { viewModel.authenticate() },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = "Sign in",
                            fontSize = 18.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onNavigateToSignup) {
                    Text(text = "Нет аккаунта? Sign up", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyStepsTheme {
        SignUp(onNavigateToSignin = {})
    }
}