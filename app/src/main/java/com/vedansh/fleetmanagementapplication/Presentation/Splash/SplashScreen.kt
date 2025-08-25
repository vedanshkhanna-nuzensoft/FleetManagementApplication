package com.vedansh.fleetmanagementapplication.Presentation.Splash

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vedansh.fleetmanagementapplication.R
import com.vedansh.fleetmanagementapplication.Notification.NotificationHelper
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    // Navigate after 2 seconds
    LaunchedEffect(true) {
        NotificationHelper.createNotificationChannel(context)
        delay(2000)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true } // remove splash from back stack
        }
    }

    SplashScreenContent()
}

@Composable
fun SplashScreenContent() {
    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 1000)
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(true) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1), // dark top
                        Color(0xFF1976D2)  // medium bottom
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Logo with bright shadow/glow for better visibility
        Box(
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
                .offset(y = offsetY)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = Color.White.copy(alpha = 0.6f),
                    spotColor = Color.White.copy(alpha = 0.6f)
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.dbl_logo),
                contentDescription = "Company Logo",
                modifier = Modifier
                    .height(160.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreenContent()
}
