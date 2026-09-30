package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.example.R
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AvatarStyle(val id: String, val title: String, val category: String, @get:StringRes val titleRes: Int) {
    // Minimal & People (Default)
    MINIMAL_AVATAR("minimal_avatar", "Calm Emerald", "People", R.string.avatar_calm_emerald),

    // Creative & Artistic
    SUNNY_CREATIVE("sunny_creative", "Creative Sun", "Artistic", R.string.avatar_creative_sun),
    ZEN_CIRCLE("zen_circle", "Zen Flow", "Artistic", R.string.avatar_zen_flow),
    GRADIENT_BLAZE("gradient_blaze", "Sunset Blaze", "Artistic", R.string.avatar_sunset_blaze),
    AURORA_GLOW("aurora_glow", "Aurora Glow", "Artistic", R.string.avatar_aurora_glow),
    ROSE_DAWN("rose_dawn", "Rose Dawn", "People", R.string.avatar_rose_dawn),

    // Tech & Modern
    GEOMETRIC_CODE("geometric_code", "Dev Matrix", "Tech", R.string.avatar_dev_matrix),
    NIGHT_OWL("night_owl", "Night Shift", "Tech", R.string.avatar_night_shift),
    CYBER_PULSE("cyber_pulse", "Cyber Pulse", "Tech", R.string.avatar_cyber_pulse),
    TERMINAL_RUN("terminal_run", "Terminal", "Tech", R.string.avatar_terminal),
    ENERGY_BOLT("energy_bolt", "Power Bolt", "Tech", R.string.avatar_energy_bolt),

    // Work & Focus
    FOCUS_CLOCK("focus_clock", "Focus Clock", "Work", R.string.avatar_focus_clock),
    PEAK_MOUNTAIN("peak_mountain", "Peak Focus", "Work", R.string.avatar_peak_mountain),
    OCEAN_WAVE("ocean_wave", "Deep Ocean", "Work", R.string.avatar_ocean_wave),
    LEAF_FOCUS("leaf_focus", "Leaf Focus", "Work", R.string.avatar_leaf_focus),

    // Minimal & People
    PASTEL_PORTRAIT("pastel_portrait", "Pastel Bloom", "People", R.string.avatar_pastel_bloom),
    ROYAL_BADGE("royal_badge", "Royal Crown", "People", R.string.avatar_royal_crown),
    COFFEE_BREAK("coffee_break", "Coffee Cup", "People", R.string.avatar_coffee_cup)
}

@Composable
fun CustomAvatarDisplay(
    avatarId: String,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 48.dp,
    showBorder: Boolean = true
) {
    val context = LocalContext.current
    val isCustomPhoto = avatarId.startsWith("content://") || avatarId.startsWith("file://") || avatarId.startsWith("http") || avatarId.startsWith("/")
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape)
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = (sizeDp.value * 0.035f).coerceIn(1.5f, 3.5f).dp,
                        brush = Brush.linearGradient(
                            listOf(primaryColor, primaryColor.copy(alpha = 0.6f))
                        ),
                        shape = CircleShape
                    )
                } else Modifier
            )
    ) {
        if (isCustomPhoto) {
            val imageUri = try {
                if (avatarId.startsWith("/")) Uri.fromFile(File(avatarId)) else Uri.parse(avatarId)
            } catch (e: Exception) {
                null
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.avatar_cd_user_avatar),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            val currentStyle = AvatarStyle.entries.find { it.id == avatarId } ?: AvatarStyle.MINIMAL_AVATAR

            Canvas(modifier = Modifier.size(sizeDp)) {
                val w = this.size.width
                val h = this.size.height

                when (currentStyle) {
                    AvatarStyle.MINIMAL_AVATAR -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFECFDF5), Color(0xFF6EE7B7), Color(0xFF059669)),
                                center = Offset(w * 0.35f, h * 0.3f),
                                radius = w * 0.85f
                            ),
                            radius = w / 2f
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.22f),
                            radius = w * 0.18f,
                            center = Offset(w * 0.32f, h * 0.28f)
                        )
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFF047857), Color(0xFF065F46))),
                            radius = w * 0.17f,
                            center = Offset(w * 0.5f, h * 0.36f)
                        )
                        val body = Path().apply {
                            moveTo(w * 0.18f, h * 0.92f)
                            cubicTo(w * 0.22f, h * 0.58f, w * 0.78f, h * 0.58f, w * 0.82f, h * 0.92f)
                            close()
                        }
                        drawPath(
                            path = body,
                            brush = Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF047857)))
                        )
                    }

                    AvatarStyle.GEOMETRIC_CODE -> {
                        drawCircle(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF312E81), Color(0xFF1E1B4B)),
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            ),
                            radius = w / 2f
                        )
                        val stroke = (w * 0.055f).coerceAtLeast(2.2f)
                        val left = Path().apply {
                            moveTo(w * 0.42f, h * 0.28f)
                            lineTo(w * 0.26f, h * 0.5f)
                            lineTo(w * 0.42f, h * 0.72f)
                        }
                        val right = Path().apply {
                            moveTo(w * 0.58f, h * 0.28f)
                            lineTo(w * 0.74f, h * 0.5f)
                            lineTo(w * 0.58f, h * 0.72f)
                        }
                        drawPath(left, Color(0xFF818CF8), style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        drawPath(right, Color(0xFF38BDF8), style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
                        drawLine(
                            Color(0xFFC084FC),
                            Offset(w * 0.48f, h * 0.68f),
                            Offset(w * 0.56f, h * 0.32f),
                            (w * 0.045f).coerceAtLeast(2f),
                            StrokeCap.Round
                        )
                    }

                    AvatarStyle.SUNNY_CREATIVE -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFFCA5A5)),
                                center = Offset(w * 0.45f, h * 0.4f),
                                radius = w * 0.8f
                            ),
                            radius = w / 2f
                        )
                        drawCircle(
                            color = Color(0xFFFBBF24).copy(alpha = 0.35f),
                            radius = w * 0.34f,
                            center = Offset(w * 0.5f, h * 0.5f)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFDE047), Color(0xFFF59E0B)),
                                center = Offset(w * 0.45f, h * 0.42f),
                                radius = w * 0.28f
                            ),
                            radius = w * 0.22f,
                            center = Offset(w * 0.5f, h * 0.5f)
                        )
                        val stroke = (w * 0.045f).coerceAtLeast(2f)
                        for (i in 0 until 8) {
                            val rad = i * PI / 4.0
                            val inner = w * 0.30f
                            val outer = w * 0.42f
                            drawLine(
                                Color(0xFFD97706),
                                Offset(
                                    (w * 0.5f + cos(rad) * inner).toFloat(),
                                    (h * 0.5f + sin(rad) * inner).toFloat()
                                ),
                                Offset(
                                    (w * 0.5f + cos(rad) * outer).toFloat(),
                                    (h * 0.5f + sin(rad) * outer).toFloat()
                                ),
                                stroke,
                                StrokeCap.Round
                            )
                        }
                    }

                    AvatarStyle.NIGHT_OWL -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFF334155), Color(0xFF0F172A), Color(0xFF020617)),
                                center = Offset(w * 0.4f, h * 0.35f),
                                radius = w * 0.9f
                            ),
                            radius = w / 2f
                        )
                        drawCircle(
                            color = Color(0xFFFDE047).copy(alpha = 0.2f),
                            radius = w * 0.32f,
                            center = Offset(w * 0.46f, h * 0.5f)
                        )
                        drawCircle(Color(0xFFFDE047), radius = w * 0.24f, center = Offset(w * 0.46f, h * 0.5f))
                        drawCircle(Color(0xFF0F172A), radius = w * 0.20f, center = Offset(w * 0.58f, h * 0.46f))
                        listOf(
                            Offset(w * 0.78f, h * 0.28f) to w * 0.035f,
                            Offset(w * 0.22f, h * 0.30f) to w * 0.025f,
                            Offset(w * 0.30f, h * 0.72f) to w * 0.03f,
                            Offset(w * 0.72f, h * 0.70f) to w * 0.02f
                        ).forEach { (c, r) ->
                            drawCircle(Color(0xFFBFDBFE), radius = r, center = c)
                        }
                    }

                    AvatarStyle.ZEN_CIRCLE -> {
                        drawCircle(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFFFFF7ED), Color(0xFFFED7AA), Color(0xFFFDBA74)),
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            ),
                            radius = w / 2f
                        )
                        val ringStroke = (w * 0.055f).coerceAtLeast(2.2f)
                        drawCircle(
                            Color(0xFFEA580C).copy(alpha = 0.25f),
                            radius = w * 0.28f,
                            center = Offset(w * 0.42f, h * 0.44f)
                        )
                        drawCircle(
                            Color(0xFF0284C7).copy(alpha = 0.25f),
                            radius = w * 0.28f,
                            center = Offset(w * 0.58f, h * 0.56f)
                        )
                        drawCircle(
                            Color(0xFFEA580C),
                            radius = w * 0.26f,
                            center = Offset(w * 0.42f, h * 0.44f),
                            style = Stroke(ringStroke)
                        )
                        drawCircle(
                            Color(0xFF0284C7),
                            radius = w * 0.26f,
                            center = Offset(w * 0.58f, h * 0.56f),
                            style = Stroke(ringStroke)
                        )
                        drawCircle(Color(0xFFFB923C), radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.5f))
                    }

                    AvatarStyle.GRADIENT_BLAZE -> {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFF6B35),
                                    Color(0xFFFFB347),
                                    Color(0xFFFF2E63),
                                    Color(0xFFFF8A00),
                                    Color(0xFFFF6B35)
                                )
                            ),
                            radius = w / 2f
                        )
                        drawCircle(
                            Color.White.copy(alpha = 0.18f),
                            radius = w * 0.38f,
                            center = Offset(w * 0.5f, h * 0.5f),
                            style = Stroke((w * 0.05f).coerceAtLeast(2f))
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color.White, Color.White.copy(alpha = 0.85f)),
                                center = Offset(w * 0.45f, h * 0.42f),
                                radius = w * 0.22f
                            ),
                            radius = w * 0.15f,
                            center = Offset(w * 0.5f, h * 0.5f)
                        )
                    }

                    AvatarStyle.CYBER_PULSE -> {
                        drawCircle(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF022C22), Color(0xFF134E4A), Color(0xFF042F2E)),
                                start = Offset(0f, h),
                                end = Offset(w, 0f)
                            ),
                            radius = w / 2f
                        )
                        drawCircle(
                            Color(0xFF2DD4BF).copy(alpha = 0.12f),
                            radius = w * 0.36f,
                            center = Offset(w * 0.5f, h * 0.5f)
                        )
                        val pulseStroke = (w * 0.05f).coerceAtLeast(2.2f)
                        val pulsePath = Path().apply {
                            moveTo(w * 0.12f, h * 0.52f)
                            lineTo(w * 0.30f, h * 0.52f)
                            lineTo(w * 0.40f, h * 0.28f)
                            lineTo(w * 0.52f, h * 0.72f)
                            lineTo(w * 0.62f, h * 0.40f)
                            lineTo(w * 0.70f, h * 0.52f)
                            lineTo(w * 0.88f, h * 0.52f)
                        }
                        drawPath(
                            pulsePath,
                            Color(0xFF5EEAD4),
                            style = Stroke(width = pulseStroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                        drawCircle(Color(0xFF99F6E4), radius = w * 0.045f, center = Offset(w * 0.40f, h * 0.28f))
                    }

                    AvatarStyle.TERMINAL_RUN -> {
                        drawCircle(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF171717), Color(0xFF0A0A0A)),
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            ),
                            radius = w / 2f
                        )
                        drawRoundRect(
                            Color(0xFF22C55E).copy(alpha = 0.08f),
                            topLeft = Offset(w * 0.18f, h * 0.22f),
                            size = Size(w * 0.64f, h * 0.56f),
                            cornerRadius = CornerRadius(w * 0.08f)
                        )
                        val codeStroke = (w * 0.055f).coerceAtLeast(2.2f)
                        val promptPath = Path().apply {
                            moveTo(w * 0.28f, h * 0.34f)
                            lineTo(w * 0.48f, h * 0.5f)
                            lineTo(w * 0.28f, h * 0.66f)
                        }
                        drawPath(
                            promptPath,
                            Color(0xFF4ADE80),
                            style = Stroke(width = codeStroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                        drawLine(
                            Color(0xFF4ADE80),
                            Offset(w * 0.54f, h * 0.66f),
                            Offset(w * 0.72f, h * 0.66f),
                            codeStroke,
                            StrokeCap.Round
                        )
                        drawRoundRect(
                            Color(0xFF86EFAC),
                            topLeft = Offset(w * 0.74f, h * 0.58f),
                            size = Size(w * 0.06f, h * 0.14f),
                            cornerRadius = CornerRadius(w * 0.015f)
                        )
                    }

                    AvatarStyle.PASTEL_PORTRAIT -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFAF5FF), Color(0xFFDDD6FE), Color(0xFFC4B5FD)),
                                center = Offset(w * 0.4f, h * 0.3f),
                                radius = w * 0.85f
                            ),
                            radius = w / 2f
                        )
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFFA78BFA), Color(0xFF7C3AED))),
                            radius = w * 0.17f,
                            center = Offset(w * 0.5f, h * 0.34f)
                        )
                        val coat = Path().apply {
                            moveTo(w * 0.16f, h * 0.92f)
                            cubicTo(w * 0.22f, h * 0.56f, w * 0.78f, h * 0.56f, w * 0.84f, h * 0.92f)
                            close()
                        }
                        drawPath(
                            coat,
                            brush = Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF5B21B6)))
                        )
                        drawCircle(Color(0xFFE9D5FF), radius = w * 0.055f, center = Offset(w * 0.5f, h * 0.58f))
                    }

                    AvatarStyle.ROYAL_BADGE -> {
                        drawCircle(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF1E3A8A), Color(0xFF172554), Color(0xFF0F172A)),
                                start = Offset(0f, 0f),
                                end = Offset(w, h)
                            ),
                            radius = w / 2f
                        )
                        drawCircle(
                            Color(0xFFFBBF24).copy(alpha = 0.15f),
                            radius = w * 0.36f,
                            center = Offset(w * 0.5f, h * 0.5f)
                        )
                        val band = Path().apply {
                            moveTo(w * 0.26f, h * 0.68f)
                            lineTo(w * 0.74f, h * 0.68f)
                            lineTo(w * 0.72f, h * 0.76f)
                            lineTo(w * 0.28f, h * 0.76f)
                            close()
                        }
                        drawPath(band, Color(0xFFF59E0B))
                        val crownPath = Path().apply {
                            moveTo(w * 0.26f, h * 0.66f)
                            lineTo(w * 0.74f, h * 0.66f)
                            lineTo(w * 0.78f, h * 0.38f)
                            lineTo(w * 0.62f, h * 0.50f)
                            lineTo(w * 0.50f, h * 0.28f)
                            lineTo(w * 0.38f, h * 0.50f)
                            lineTo(w * 0.22f, h * 0.38f)
                            close()
                        }
                        drawPath(
                            crownPath,
                            brush = Brush.linearGradient(listOf(Color(0xFFFDE68A), Color(0xFFF59E0B)))
                        )
                        drawCircle(Color(0xFFFEE2E2), radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.30f))
                        drawCircle(Color(0xFF93C5FD), radius = w * 0.03f, center = Offset(w * 0.34f, h * 0.48f))
                        drawCircle(Color(0xFF86EFAC), radius = w * 0.03f, center = Offset(w * 0.66f, h * 0.48f))
                    }

                    AvatarStyle.COFFEE_BREAK -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFFBBF24)),
                                center = Offset(w * 0.4f, h * 0.3f),
                                radius = w * 0.85f
                            ),
                            radius = w / 2f
                        )
                        val steamStroke = (w * 0.035f).coerceAtLeast(1.5f)
                        listOf(0.40f, 0.50f, 0.60f).forEach { x ->
                            val steam = Path().apply {
                                moveTo(w * x, h * 0.40f)
                                cubicTo(w * (x - 0.04f), h * 0.32f, w * (x + 0.04f), h * 0.26f, w * x, h * 0.18f)
                            }
                            drawPath(
                                steam,
                                Color(0xFFB45309).copy(alpha = 0.75f),
                                style = Stroke(width = steamStroke, cap = StrokeCap.Round)
                            )
                        }
                        drawRoundRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF92400E), Color(0xFF78350F))),
                            topLeft = Offset(w * 0.30f, h * 0.44f),
                            size = Size(w * 0.36f, h * 0.34f),
                            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                        )
                        drawOval(
                            Color(0xFF451A03),
                            topLeft = Offset(w * 0.33f, h * 0.46f),
                            size = Size(w * 0.30f, h * 0.08f)
                        )
                        drawArc(
                            color = Color(0xFF92400E),
                            startAngle = -70f,
                            sweepAngle = 140f,
                            useCenter = false,
                            topLeft = Offset(w * 0.58f, h * 0.50f),
                            size = Size(w * 0.18f, h * 0.20f),
                            style = Stroke(width = (w * 0.05f).coerceAtLeast(2f), cap = StrokeCap.Round)
                        )
                    }

                    AvatarStyle.FOCUS_CLOCK -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFE0F2FE), Color(0xFF38BDF8), Color(0xFF0369A1)),
                                center = Offset(w * 0.35f, h * 0.3f),
                                radius = w * 0.9f
                            ),
                            radius = w / 2f
                        )
                        drawCircle(Color.White.copy(alpha = 0.92f), radius = w * 0.32f, center = Offset(w * 0.5f, h * 0.52f))
                        drawCircle(
                            Color(0xFF0284C7),
                            radius = w * 0.32f,
                            center = Offset(w * 0.5f, h * 0.52f),
                            style = Stroke((w * 0.04f).coerceAtLeast(2f))
                        )
                        drawRoundRect(
                            Color(0xFF0369A1),
                            topLeft = Offset(w * 0.44f, h * 0.16f),
                            size = Size(w * 0.12f, h * 0.10f),
                            cornerRadius = CornerRadius(w * 0.03f)
                        )
                        val handStroke = (w * 0.045f).coerceAtLeast(2f)
                        drawLine(
                            Color(0xFF0F172A),
                            Offset(w * 0.5f, h * 0.52f),
                            Offset(w * 0.5f, h * 0.34f),
                            handStroke,
                            StrokeCap.Round
                        )
                        drawLine(
                            Color(0xFF0369A1),
                            Offset(w * 0.5f, h * 0.52f),
                            Offset(w * 0.66f, h * 0.58f),
                            handStroke * 0.85f,
                            StrokeCap.Round
                        )
                        drawCircle(Color(0xFF0EA5E9), radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.52f))
                    }

                    AvatarStyle.PEAK_MOUNTAIN -> {
                        drawCircle(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFBAE6FD), Color(0xFF7DD3FC), Color(0xFF0EA5E9))
                            ),
                            radius = w / 2f
                        )
                        drawCircle(Color.White.copy(alpha = 0.55f), radius = w * 0.10f, center = Offset(w * 0.72f, h * 0.28f))
                        val back = Path().apply {
                            moveTo(w * 0.08f, h * 0.78f)
                            lineTo(w * 0.38f, h * 0.38f)
                            lineTo(w * 0.62f, h * 0.78f)
                            close()
                        }
                        drawPath(back, Color(0xFF0369A1).copy(alpha = 0.55f))
                        val front = Path().apply {
                            moveTo(w * 0.28f, h * 0.82f)
                            lineTo(w * 0.58f, h * 0.30f)
                            lineTo(w * 0.92f, h * 0.82f)
                            close()
                        }
                        drawPath(
                            front,
                            brush = Brush.verticalGradient(listOf(Color(0xFF0EA5E9), Color(0xFF075985)))
                        )
                        val snow = Path().apply {
                            moveTo(w * 0.58f, h * 0.30f)
                            lineTo(w * 0.48f, h * 0.46f)
                            lineTo(w * 0.54f, h * 0.44f)
                            lineTo(w * 0.58f, h * 0.50f)
                            lineTo(w * 0.64f, h * 0.42f)
                            lineTo(w * 0.70f, h * 0.46f)
                            close()
                        }
                        drawPath(snow, Color.White.copy(alpha = 0.9f))
                    }

                    AvatarStyle.ENERGY_BOLT -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFEF08A), Color(0xFFFACC15), Color(0xFFCA8A04)),
                                center = Offset(w * 0.4f, h * 0.35f),
                                radius = w * 0.85f
                            ),
                            radius = w / 2f
                        )
                        val bolt = Path().apply {
                            moveTo(w * 0.58f, h * 0.14f)
                            lineTo(w * 0.34f, h * 0.50f)
                            lineTo(w * 0.50f, h * 0.50f)
                            lineTo(w * 0.40f, h * 0.86f)
                            lineTo(w * 0.70f, h * 0.42f)
                            lineTo(w * 0.52f, h * 0.42f)
                            close()
                        }
                        drawPath(bolt, Color(0xFF422006).copy(alpha = 0.2f))
                        drawPath(
                            bolt,
                            brush = Brush.linearGradient(listOf(Color(0xFFFFFBEB), Color(0xFFFEF08A)))
                        )
                        drawPath(
                            bolt,
                            Color(0xFF854D0E),
                            style = Stroke(width = (w * 0.025f).coerceAtLeast(1.2f), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    AvatarStyle.OCEAN_WAVE -> {
                        drawCircle(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF67E8F9), Color(0xFF0891B2), Color(0xFF164E63))
                            ),
                            radius = w / 2f
                        )
                        val wave1 = Path().apply {
                            moveTo(0f, h * 0.55f)
                            cubicTo(w * 0.25f, h * 0.42f, w * 0.45f, h * 0.68f, w * 0.7f, h * 0.52f)
                            cubicTo(w * 0.85f, h * 0.42f, w * 0.95f, h * 0.55f, w, h * 0.48f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        drawPath(wave1, Color(0xFF0E7490).copy(alpha = 0.55f))
                        val wave2 = Path().apply {
                            moveTo(0f, h * 0.68f)
                            cubicTo(w * 0.3f, h * 0.55f, w * 0.5f, h * 0.82f, w * 0.78f, h * 0.64f)
                            cubicTo(w * 0.9f, h * 0.56f, w * 0.96f, h * 0.70f, w, h * 0.66f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        drawPath(wave2, Color(0xFF155E75).copy(alpha = 0.75f))
                        drawCircle(Color.White.copy(alpha = 0.5f), radius = w * 0.06f, center = Offset(w * 0.28f, h * 0.30f))
                        drawCircle(Color.White.copy(alpha = 0.35f), radius = w * 0.04f, center = Offset(w * 0.70f, h * 0.24f))
                    }

                    AvatarStyle.ROSE_DAWN -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFFFF1F2), Color(0xFFFDA4AF), Color(0xFFE11D48)),
                                center = Offset(w * 0.38f, h * 0.28f),
                                radius = w * 0.9f
                            ),
                            radius = w / 2f
                        )
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFFFB7185), Color(0xFFBE123C))),
                            radius = w * 0.17f,
                            center = Offset(w * 0.5f, h * 0.35f)
                        )
                        val body = Path().apply {
                            moveTo(w * 0.16f, h * 0.92f)
                            cubicTo(w * 0.22f, h * 0.56f, w * 0.78f, h * 0.56f, w * 0.84f, h * 0.92f)
                            close()
                        }
                        drawPath(
                            body,
                            brush = Brush.verticalGradient(listOf(Color(0xFFFB7185), Color(0xFF9F1239)))
                        )
                        drawCircle(Color.White.copy(alpha = 0.35f), radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.58f))
                    }

                    AvatarStyle.AURORA_GLOW -> {
                        drawCircle(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81))
                            ),
                            radius = w / 2f
                        )
                        val aurora = Path().apply {
                            moveTo(0f, h * 0.45f)
                            cubicTo(w * 0.2f, h * 0.25f, w * 0.4f, h * 0.55f, w * 0.6f, h * 0.30f)
                            cubicTo(w * 0.78f, h * 0.12f, w * 0.9f, h * 0.40f, w, h * 0.28f)
                            lineTo(w, h * 0.58f)
                            cubicTo(w * 0.85f, h * 0.70f, w * 0.65f, h * 0.48f, w * 0.45f, h * 0.62f)
                            cubicTo(w * 0.25f, h * 0.76f, w * 0.1f, h * 0.55f, 0f, h * 0.65f)
                            close()
                        }
                        drawPath(
                            aurora,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF34D399).copy(alpha = 0.75f),
                                    Color(0xFF22D3EE).copy(alpha = 0.7f),
                                    Color(0xFFA78BFA).copy(alpha = 0.75f)
                                )
                            )
                        )
                        listOf(
                            Offset(w * 0.22f, h * 0.22f) to w * 0.025f,
                            Offset(w * 0.78f, h * 0.18f) to w * 0.02f,
                            Offset(w * 0.55f, h * 0.78f) to w * 0.018f
                        ).forEach { (c, r) ->
                            drawCircle(Color.White.copy(alpha = 0.85f), radius = r, center = c)
                        }
                    }

                    AvatarStyle.LEAF_FOCUS -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color(0xFFECFCCB), Color(0xFF86EFAC), Color(0xFF15803D)),
                                center = Offset(w * 0.35f, h * 0.28f),
                                radius = w * 0.9f
                            ),
                            radius = w / 2f
                        )
                        val leaf = Path().apply {
                            moveTo(w * 0.52f, h * 0.18f)
                            cubicTo(w * 0.78f, h * 0.28f, w * 0.82f, h * 0.58f, w * 0.52f, h * 0.78f)
                            cubicTo(w * 0.22f, h * 0.58f, w * 0.26f, h * 0.28f, w * 0.52f, h * 0.18f)
                            close()
                        }
                        drawPath(
                            leaf,
                            brush = Brush.linearGradient(
                                listOf(Color(0xFFBBF7D0), Color(0xFF22C55E), Color(0xFF166534)),
                                start = Offset(w * 0.3f, h * 0.2f),
                                end = Offset(w * 0.7f, h * 0.8f)
                            )
                        )
                        drawLine(
                            Color(0xFF14532D),
                            Offset(w * 0.52f, h * 0.22f),
                            Offset(w * 0.52f, h * 0.82f),
                            (w * 0.04f).coerceAtLeast(1.8f),
                            StrokeCap.Round
                        )
                        drawLine(
                            Color(0xFF166534).copy(alpha = 0.75f),
                            Offset(w * 0.52f, h * 0.40f),
                            Offset(w * 0.36f, h * 0.48f),
                            (w * 0.028f).coerceAtLeast(1.4f),
                            StrokeCap.Round
                        )
                        drawLine(
                            Color(0xFF166534).copy(alpha = 0.75f),
                            Offset(w * 0.52f, h * 0.52f),
                            Offset(w * 0.68f, h * 0.58f),
                            (w * 0.028f).coerceAtLeast(1.4f),
                            StrokeCap.Round
                        )
                        drawCircle(
                            Color.White.copy(alpha = 0.28f),
                            radius = w * 0.08f,
                            center = Offset(w * 0.40f, h * 0.32f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarSelectionBottomSheet(
    selectedAvatarId: String,
    onAvatarSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Image Picker Launcher for choosing a photo from the device
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    // Clean up previous avatar files to save disk space
                    context.filesDir.listFiles { file -> file.name.startsWith("user_avatar_") }?.forEach { it.delete() }

                    val avatarFile = File(context.filesDir, "user_avatar_${System.currentTimeMillis()}.jpg")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        avatarFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    val permanentUri = Uri.fromFile(avatarFile).toString()
                    withContext(Dispatchers.Main) {
                        onAvatarSelected(permanentUri)
                        onDismiss()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onAvatarSelected(uri.toString())
                        onDismiss()
                    }
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 38.dp, height = 4.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.outlineVariant
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("avatar_selection_sheet")
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = stringResource(R.string.avatar_choose_picture_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.avatar_choose_picture_desc),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Choose from Phone / Gallery Action Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        photoPickerLauncher.launch("image/*")
                    }
                    .testTag("btn_upload_from_phone")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = stringResource(R.string.avatar_cd_upload_photo),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.avatar_upload_from_phone),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.avatar_upload_from_phone_desc),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = stringResource(R.string.avatar_gallery_badge),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subheading for Built-in Avatars
            Text(
                text = stringResource(R.string.avatar_default_collection),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Grid of Built-in Avatars
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                items(AvatarStyle.entries) { avatarStyle ->
                    val isSelected = avatarStyle.id == selectedAvatarId

                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onAvatarSelected(avatarStyle.id)
                                onDismiss()
                            }
                            .testTag("avatar_option_${avatarStyle.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                CustomAvatarDisplay(
                                    avatarId = avatarStyle.id,
                                    sizeDp = 56.dp,
                                    showBorder = true
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = stringResource(R.string.cd_selected),
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = stringResource(avatarStyle.titleRes),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
