package com.ridvanosma.yazboz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ridvanosma.yazboz.ui.YazbozApp
import com.ridvanosma.yazboz.ui.theme.YazbozTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            YazbozTheme {
                YazbozApp()
            }
        }
    }
}
