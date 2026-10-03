package com.ridvanosma.yazboz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class HandPalette(
    val hand: Color,
    val penalty: Color
)

private val LightHandPalettes = listOf(
    HandPalette(
        hand = Color(0xFFE4F3FB),
        penalty = Color(0xFFC6E6F7)
    ),
    HandPalette(
        hand = Color(0xFFEAF5EE),
        penalty = Color(0xFFCFE8D8)
    ),
    HandPalette(
        hand = Color(0xFFFFF4D9),
        penalty = Color(0xFFF6E2AF)
    ),
    HandPalette(
        hand = Color(0xFFF1ECFA),
        penalty = Color(0xFFDDD2F1)
    ),
    HandPalette(
        hand = Color(0xFFFCECEF),
        penalty = Color(0xFFF3D2D9)
    ),
    HandPalette(
        hand = Color(0xFFEAF0FB),
        penalty = Color(0xFFD2DDF2)
    )
)

private val DarkHandPalettes = listOf(
    HandPalette(
        hand = Color(0xFF173643),
        penalty = Color(0xFF205064)
    ),
    HandPalette(
        hand = Color(0xFF1D382A),
        penalty = Color(0xFF29513C)
    ),
    HandPalette(
        hand = Color(0xFF40361C),
        penalty = Color(0xFF5A4A25)
    ),
    HandPalette(
        hand = Color(0xFF342B45),
        penalty = Color(0xFF493B60)
    ),
    HandPalette(
        hand = Color(0xFF432A31),
        penalty = Color(0xFF5B3944)
    ),
    HandPalette(
        hand = Color(0xFF273246),
        penalty = Color(0xFF384866)
    )
)

@Composable
fun handPalette(index: Int): HandPalette {
    val palettes = if (isSystemInDarkTheme()) {
        DarkHandPalettes
    } else {
        LightHandPalettes
    }

    return palettes[index % palettes.size]
}
