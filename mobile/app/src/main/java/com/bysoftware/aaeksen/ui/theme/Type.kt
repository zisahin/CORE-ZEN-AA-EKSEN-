package com.bysoftware.aaeksen.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bysoftware.aaeksen.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val ParDefaultLonie = FontFamily(
    Font(R.font.par_default_lonie_black, weight = FontWeight.Black, style = FontStyle.Normal),
    Font(R.font.par_default_lonie_black_italic, weight = FontWeight.Black, style = FontStyle.Italic),
    Font(R.font.par_default_lonie_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.par_default_lonie_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(R.font.par_default_lonie_light, weight = FontWeight.Light, style = FontStyle.Normal),
    Font(R.font.par_default_lonie_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.par_default_lonie_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.par_default_lonie_regular_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.par_default_lonie_thin, weight = FontWeight.Thin, style = FontStyle.Normal),
    Font(R.font.par_default_lonie_thin_italic, weight = FontWeight.Thin, style = FontStyle.Italic)
)