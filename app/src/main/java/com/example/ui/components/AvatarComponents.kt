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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AvatarStyle(val id: String, val title: String, val category: String) {
    // Minimal & People (Default)
    MINIMAL_AVATAR("minimal_avatar", "Calm Emerald", "People"),

    // Creative & Artistic
    SUNNY_CREATIVE("sunny_creative", "Creative Sun", "Artistic"),
    ZEN_CIRCLE("zen_circle", "Zen Flow", "Artistic"),
    GRADIENT_BLAZE("gradient_blaze", "Sunset Blaze", "Artistic"),

    // Tech & Modern
    GEOMETRIC_CODE("geometric_code", "Dev Matrix", "Tech"),
    NIGHT_OWL("night_owl", "Night Shift", "Tech"),
    CYBER_PULSE("cyber_pulse", "Cyber Pulse", "Tech"),
    TERMINAL_RUN("terminal_run", "Terminal", "Tech"),

    // Minimal & People
    PASTEL_PORTRAIT("pastel_portrait", "Pastel Bloom", "People"),
    ROYAL_BADGE("royal_badge", "Royal Crown", "People"),
    COFFEE_BREAK("coffee_break", "Coffee Cup", "People")
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
                contentDescription = "User Avatar",
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
                            brush = Brush.linearGradient(listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0))),
                            radius = w / 2f
                        )
                        drawCircle(color = Color(0xFF065F46), radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.38f))
                        val bodyPath = Path().apply {
                            moveTo(w * 0.2f, h * 0.85f)
                            cubicTo(w * 0.28f, h * 0.62f, w * 0.72f, h * 0.62f, w * 0.8f, h * 0.85f)
                        }
                        drawPath(
                            path = bodyPath,
                            color = Color(0xFF047857),
                            style = Stroke(width = (w * 0.08f).coerceAtLeast(3f), cap = StrokeCap.Round)
                        )
                    }

                    AvatarStyle.GEOMETRIC_CODE -> {
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81))),
                            radius = w / 2f
                        )
                        val stroke = (w * 0.035f).coerceAtLeast(1.5f)
                        drawLine(Color(0xFF818CF8), Offset(w * 0.25f, h * 0.35f), Offset(w * 0.75f, h * 0.35f), stroke, StrokeCap.Round)
                        drawLine(Color(0xFF38BDF8), Offset(w * 0.2f, h * 0.55f), Offset(w * 0.8f, h * 0.55f), stroke, StrokeCap.Round)
                        drawLine(Color(0xFFC084FC), Offset(w * 0.35f, h * 0.75f), Offset(w * 0.65f, h * 0.75f), stroke, StrokeCap.Round)
                        drawCircle(Color(0xFF38BDF8), radius = w * 0.09f, center = Offset(w * 0.5f, h * 0.55f))
                    }

                    AvatarStyle.SUNNY_CREATIVE -> {
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFFFDE68A), Color(0xFFFCA5A5))),
                            radius = w / 2f
                        )
                        drawCircle(color = Color(0xFFF59E0B), radius = w * 0.24f, center = Offset(w * 0.5f, h * 0.5f))
                        val rayLength = w * 0.12f
                        val stroke = (w * 0.04f).coerceAtLeast(1.8f)
                        val angles = listOf(0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0)
                        angles.forEach { deg ->
                            val rad = Math.toRadians(deg)
                            val startX = (w * 0.5f + Math.cos(rad) * (w * 0.28f)).toFloat()
                            val startY = (h * 0.5f + Math.sin(rad) * (h * 0.28f)).toFloat()
                            val endX = (w * 0.5f + Math.cos(rad) * (w * 0.28f + rayLength)).toFloat()
                            val endY = (h * 0.5f + Math.sin(rad) * (h * 0.28f + rayLength)).toFloat()
                            drawLine(Color(0xFFD97706), Offset(startX, startY), Offset(endX, endY), stroke, StrokeCap.Round)
                        }
                    }

                    AvatarStyle.NIGHT_OWL -> {
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))),
                            radius = w / 2f
                        )
                        drawCircle(color = Color(0xFFFDE047), radius = w * 0.25f, center = Offset(w * 0.48f, h * 0.5f))
                        drawCircle(color = Color(0xFF0F172A), radius = w * 0.22f, center = Offset(w * 0.58f, h * 0.46f))
                        drawCircle(Color(0xFF93C5FD), radius = w * 0.04f, center = Offset(w * 0.78f, h * 0.32f))
                        drawCircle(Color(0xFF93C5FD), radius = w * 0.03f, center = Offset(w * 0.28f, h * 0.68f))
                    }

                    AvatarStyle.ZEN_CIRCLE -> {
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFFFFEDD5), Color(0xFFFED7AA))),
                            radius = w / 2f
                        )
                        val ringStroke = (w * 0.05f).coerceAtLeast(2f)
                        drawCircle(color = Color(0xFFEA580C), radius = w * 0.25f, center = Offset(w * 0.42f, h * 0.45f), style = Stroke(width = ringStroke))
                        drawCircle(color = Color(0xFF0284C7), radius = w * 0.25f, center = Offset(w * 0.58f, h * 0.55f), style = Stroke(width = ringStroke))
                    }

                    AvatarStyle.GRADIENT_BLAZE -> {
                        // Radiant fiery gradient with clean dynamic rings
                        drawCircle(
                            brush = Brush.sweepGradient(listOf(Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFE91E63), Color(0xFFFF5722))),
                            radius = w / 2f
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.25f),
                            radius = w * 0.34f,
                            center = Offset(w * 0.5f, h * 0.5f),
                            style = Stroke(width = (w * 0.06f).coerceAtLeast(2f))
                        )
                        drawCircle(
                            color = Color.White,
                            radius = w * 0.16f,
                            center = Offset(w * 0.5f, h * 0.5f)
                        )
                    }

                    AvatarStyle.CYBER_PULSE -> {
                        // Deep Cyan / Teal Neon Matrix
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFF042F2E), Color(0xFF134E4A))),
                            radius = w / 2f
                        )
                        val pulseStroke = (w * 0.045f).coerceAtLeast(2f)
                        val pulsePath = Path().apply {
                            moveTo(w * 0.15f, h * 0.5f)
                            lineTo(w * 0.35f, h * 0.5f)
                            lineTo(w * 0.45f, h * 0.25f)
                            lineTo(w * 0.55f, h * 0.75f)
                            lineTo(w * 0.65f, h * 0.5f)
                            lineTo(w * 0.85f, h * 0.5f)
                        }
                        drawPath(
                            path = pulsePath,
                            color = Color(0xFF2DD4BF),
                            style = Stroke(width = pulseStroke, cap = StrokeCap.Round)
                        )
                    }

                    AvatarStyle.TERMINAL_RUN -> {
                        // Hacker Terminal Emerald on Dark
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFF0A0A0A), Color(0xFF171717))),
                            radius = w / 2f
                        )
                        val codeStroke = (w * 0.05f).coerceAtLeast(2f)
                        val promptPath = Path().apply {
                            moveTo(w * 0.3f, h * 0.35f)
                            lineTo(w * 0.48f, h * 0.5f)
                            lineTo(w * 0.3f, h * 0.65f)
                        }
                        drawPath(path = promptPath, color = Color(0xFF22C55E), style = Stroke(width = codeStroke, cap = StrokeCap.Round))
                        drawLine(Color(0xFF22C55E), Offset(w * 0.55f, h * 0.65f), Offset(w * 0.72f, h * 0.65f), codeStroke, StrokeCap.Round)
                    }

                    AvatarStyle.PASTEL_PORTRAIT -> {
                        // Gentle Lavender & Violet Sky
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))),
                            radius = w / 2f
                        )
                        drawCircle(color = Color(0xFF7C3AED), radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.36f))
                        val coatPath = Path().apply {
                            moveTo(w * 0.22f, h * 0.85f)
                            cubicTo(w * 0.3f, h * 0.62f, w * 0.7f, h * 0.62f, w * 0.78f, h * 0.85f)
                        }
                        drawPath(path = coatPath, color = Color(0xFF6D28D9), style = Stroke(width = (w * 0.07f).coerceAtLeast(2.5f), cap = StrokeCap.Round))
                        drawCircle(Color(0xFFA78BFA), radius = w * 0.06f, center = Offset(w * 0.5f, h * 0.62f))
                    }

                    AvatarStyle.ROYAL_BADGE -> {
                        // Gold & Navy Prestige
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF172554))),
                            radius = w / 2f
                        )
                        val crownPath = Path().apply {
                            moveTo(w * 0.28f, h * 0.65f)
                            lineTo(w * 0.72f, h * 0.65f)
                            lineTo(w * 0.75f, h * 0.40f)
                            lineTo(w * 0.60f, h * 0.50f)
                            lineTo(w * 0.50f, h * 0.34f)
                            lineTo(w * 0.40f, h * 0.50f)
                            lineTo(w * 0.25f, h * 0.40f)
                            close()
                        }
                        drawPath(path = crownPath, color = Color(0xFFFBBF24))
                        drawCircle(Color(0xFFF59E0B), radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.32f))
                    }

                    AvatarStyle.COFFEE_BREAK -> {
                        // Warm Mocha / Caramel Roast
                        drawCircle(
                            brush = Brush.linearGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))),
                            radius = w / 2f
                        )
                        // Cup
                        drawRoundRect(
                            color = Color(0xFF78350F),
                            topLeft = Offset(w * 0.32f, h * 0.45f),
                            size = Size(w * 0.36f, h * 0.32f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.08f)
                        )
                        // Steam
                        val steamStroke = (w * 0.035f).coerceAtLeast(1.5f)
                        val steam1 = Path().apply {
                            moveTo(w * 0.42f, h * 0.38f)
                            cubicTo(w * 0.40f, h * 0.32f, w * 0.45f, h * 0.28f, w * 0.43f, h * 0.22f)
                        }
                        val steam2 = Path().apply {
                            moveTo(w * 0.58f, h * 0.38f)
                            cubicTo(w * 0.56f, h * 0.32f, w * 0.61f, h * 0.28f, w * 0.59f, h * 0.22f)
                        }
                        drawPath(steam1, color = Color(0xFFB45309), style = Stroke(width = steamStroke, cap = StrokeCap.Round))
                        drawPath(steam2, color = Color(0xFFB45309), style = Stroke(width = steamStroke, cap = StrokeCap.Round))
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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            text = "Choose Profile Picture",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pick a custom illustration or upload your photo",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                            contentDescription = "Upload Photo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Upload from Your Phone",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select any picture from your gallery or camera",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Gallery",
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
                text = "Default Avatar Collection",
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
                    .height(320.dp)
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
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = avatarStyle.title,
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
