package com.example.engpu.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = StudyWithYellow,
    secondary = StudyWithOrange,
    tertiary = StudyWithPurple,
    background = StudyWithBlack,
    surface = StudyWithDarkGray,
    onPrimary = StudyWithBlack,
    onSecondary = StudyWithBlack,
    onTertiary = StudyWithBlack,
    onBackground = StudyWithYellow,
    onSurface = StudyWithYellow
)

private val LightColorScheme = lightColorScheme(
    primary = StudyWithYellow,
    secondary = StudyWithOrange,
    tertiary = StudyWithPurple,
    background = StudyWithYellow,
    surface = BackgroundWhite,
    onPrimary = StudyWithBlack,
    onSecondary = StudyWithBlack,
    onTertiary = StudyWithBlack,
    onBackground = StudyWithBlack,
    onSurface = StudyWithBlack
)

@Composable
fun StudyWithTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
