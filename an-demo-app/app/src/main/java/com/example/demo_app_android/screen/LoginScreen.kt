package com.example.demo_app_android.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo_app_android.UserState
import kotlinx.coroutines.launch

@Composable
fun UserName(value: String, onChange: (value: String) -> Unit) {
    OutlinedTextField(value, onChange, label = { Text("Username") })
}

@Composable
fun Password(value: String, onChange: (value: String) -> Unit) {
    OutlinedTextField(value, onChange, label = { Text("Password") })
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
            onClick = onClick,
            contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 12.dp)
    ) { Text("Login") }
}

@Composable
fun LoginScreen() {
    Column {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
        val vm = UserState.current
        Scaffold(topBar = { TopAppBar(title = { Text("Demo App") }) }) {
            Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (vm.isBusy) {
                    CircularProgressIndicator()
                } else {
                    UserName(email, onChange = { email = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    Password(password, onChange = { password = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    LoginButton(onClick = { coroutineScope.launch { vm.signIn(email, password) } })
                }
            }
        }
    }
}
