package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-precision vector rendering of the App Logo.
 * Renders the circle in the primary theme blue (#0066EE)
 * with sharp stylized geometric monogram cutouts.
 */
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    circleColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            val w = this.size.width
            val h = this.size.height
            val scaleX = w / 512f
            val scaleY = h / 512f

            // 1. Draw Primary Blue Emblem Circle
            drawCircle(
                color = circleColor,
                radius = 172f * scaleX,
                center = Offset(256f * scaleX, 256f * scaleY)
            )

            // 2. Draw Left '7' Element Cutout (punches clean transparent hole)
            val leftPath = Path().apply {
                moveTo(82f * scaleX, 164f * scaleY)
                lineTo(250f * scaleX, 164f * scaleY)
                lineTo(140f * scaleX, 388f * scaleY)
                lineTo(86f * scaleX, 388f * scaleY)
                lineTo(132f * scaleX, 222f * scaleY)
                lineTo(82f * scaleX, 222f * scaleY)
                close()
            }
            drawPath(path = leftPath, color = Color.Black, blendMode = BlendMode.Clear, style = Fill)

            // 3. Draw Right 'T' Element Cutout (punches clean transparent hole)
            val rightPath = Path().apply {
                moveTo(278f * scaleX, 136f * scaleY)
                lineTo(416f * scaleX, 136f * scaleY)
                lineTo(416f * scaleX, 198f * scaleY)
                lineTo(318f * scaleX, 198f * scaleY)
                lineTo(254f * scaleX, 356f * scaleY)
                lineTo(192f * scaleX, 356f * scaleY)
                close()
            }
            drawPath(path = rightPath, color = Color.Black, blendMode = BlendMode.Clear, style = Fill)
        }
    }
}

