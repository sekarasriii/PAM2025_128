package com.example.fespace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fespace.ui.theme.*

@Composable
fun SuccessDialog(
    title: String = "Berhasil!",
    message: String,
    onDismiss: () -> Unit
) {
    ModernDialog(
        icon = Icons.Default.CheckCircle,
        iconColor = SageGreen,
        title = title,
        message = message,
        onDismiss = onDismiss,
        gradientColors = listOf(SageGreen.copy(alpha = 0.1f), SageGreen.copy(alpha = 0.05f))
    )
}

@Composable
fun ErrorDialog(
    title: String = "Gagal",
    message: String,
    onDismiss: () -> Unit
) {
    ModernDialog(
        icon = Icons.Default.Error,
        iconColor = AccentRed,
        title = title,
        message = message,
        onDismiss = onDismiss,
        gradientColors = listOf(AccentRed.copy(alpha = 0.1f), AccentRed.copy(alpha = 0.05f))
    )
}

@Composable
fun InfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    ModernDialog(
        icon = Icons.Default.Info,
        iconColor = AccentBlue,
        title = title,
        message = message,
        onDismiss = onDismiss,
        gradientColors = listOf(AccentBlue.copy(alpha = 0.1f), AccentBlue.copy(alpha = 0.05f))
    )
}

@Composable
private fun ModernDialog(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    gradientColors: List<Color>
) {
    // Icon animation
    val infiniteTransition = rememberInfiniteTransition(label = "icon_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCharcoal,
        shape = RoundedCornerShape(Radius.ExtraLarge),
        icon = {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .size(48.dp)
                        .scale(scale)
                )
            }
        },
        title = {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 15.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = iconColor,
                    contentColor = if (iconColor == SageGreen || iconColor == AccentGold) DarkCharcoal else Cream
                ),
                shape = RoundedCornerShape(Radius.Medium)
            ) {
                Text(
                    "OK",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
