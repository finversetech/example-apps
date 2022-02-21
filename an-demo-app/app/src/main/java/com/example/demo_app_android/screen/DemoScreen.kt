package com.example.demo_app_android.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demo_app_android.UserState
import com.example.demo_app_android.ui.theme.AppTheme

@Composable
fun Greetings() {
    val vm = UserState.current
    Scaffold(topBar = { TopAppBar(title = { Text("Demo") }) }) {
        if (vm.isBusy) {
            Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                items(items = vm.userData) { data ->
                    Greeting(key = data.name, value = data.value ?: "")
                }
            }
        }
    }
}

@Composable
private fun Greeting(key: String, value: String) {
    Card(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            elevation = 2.dp,
            backgroundColor = MaterialTheme.colors.primarySurface
    ) { CardContent(key, value) }
}

@Composable
private fun CardContent(key: String, value: String) {
    Row(modifier = Modifier.padding(12.dp)) {
        Column(modifier = Modifier.weight(1f).padding(12.dp)) {
            Text(text = key, style = MaterialTheme.typography.h6)
            Text(text = (value))
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    AppTheme(darkTheme = true) { Greetings() }
}
