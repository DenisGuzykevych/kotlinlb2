package com.example.lb2

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
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
class TooEarlyException(message: String) : Exception(message)

@Composable
fun Clicker(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var reactionTimes by remember { mutableStateOf(listOf<Double>()) }
    var currentAttempt by remember { mutableStateOf(1) }
    var showBox by remember { mutableStateOf(false) }
    var trigger by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    var errorMessage by remember { mutableStateOf("") }
    val randomSeconds = remember(trigger) { Random.nextInt(1, 5) }

    LaunchedEffect(trigger) {
        if (trigger) {
            delay(randomSeconds * 1000L)
            showBox = true
            startTime = System.currentTimeMillis()
        }
    }

    fun onBoxClick() {
        try {
            if (showBox) {
                val endTime = System.currentTimeMillis()
                val reactionTime = (endTime - startTime) / 1000.0
                reactionTimes = reactionTimes + reactionTime
                if (currentAttempt < 5) {
                    currentAttempt++
                }
                showBox = false
                trigger = false
                errorMessage = ""
            } else if (trigger && !showBox) {
                throw TooEarlyException("Too early! You clicked before the box appeared.")
            }
        } catch (e: TooEarlyException) {
            errorMessage = e.message ?: "Too early!"
            showBox = false
            trigger = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = currentAttempt <= 5) { onBoxClick() }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Attempt: $currentAttempt / 5")

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (currentAttempt <= 5) {
                        showBox = false
                        trigger = true
                        errorMessage = ""
                    }
                },
                enabled = currentAttempt <= 5
            ) {
                Text("Start Attempt")
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (showBox) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Red, shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (reactionTimes.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Reaction times:")
                    reactionTimes.forEachIndexed { index, time ->
                        Text("Attempt ${index + 1}: ${"%.2f".format(time)}s")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (reactionTimes.size == 5) {
                        val avg = reactionTimes.average()
                        val best = reactionTimes.minOrNull()
                        val worst = reactionTimes.maxOrNull()
                        Text("Average: ${"%.2f".format(avg)}s")
                        Text("Best: ${"%.2f".format(best)}s")
                        Text("Worst: ${"%.2f".format(worst)}s")
                    }
                }
            }
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
