package com.example.lb2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lb2.ui.theme.LB2Theme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LB2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Clicker(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Clicker(modifier: Modifier = Modifier) {
    val randomSeconds = remember { Random.nextInt(1, 5) }
    var reactionTime by remember { mutableStateOf(0.0) }
    var showBox by remember { mutableStateOf(false) }
    var trigger by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current

    LaunchedEffect(trigger) {
        if (trigger) {
            delay(randomSeconds * 1000L)
            showBox = true
            startTime = System.currentTimeMillis()
        }
    }


    fun onBoxClick() {
        if (showBox) {
            val endTime = System.currentTimeMillis()
            reactionTime = (endTime - startTime) / 1000.0
            showBox = false
            trigger = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Test your reaction time!")

        Spacer(modifier = Modifier.padding(10.dp))


        Button(onClick = {
            showBox = false
            trigger = true
        }) {
            Text("Launch the timer!")
        }

        Spacer(modifier = Modifier.padding(20.dp))


        if (showBox) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Red, shape = CircleShape)
                    .clickable { onBoxClick() }
            )
        }

        if (reactionTime > 0) {
            Text("Your reaction time is ${"%.2f".format(reactionTime)} seconds")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LB2Theme {
        Clicker()
    }
}