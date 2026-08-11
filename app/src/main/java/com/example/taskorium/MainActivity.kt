package com.example.taskorium

import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.taskorium.route.TaskoriumNavGraph
import com.example.taskorium.ui.features.splashView.SplashViewModel
import com.example.taskorium.ui.features.splashView.StartDestination
import com.example.taskorium.ui.theme.TaskoriumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { splashViewModel.state.value.isLoading }
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            window.statusBarColor = android.graphics.Color.TRANSPARENT            // الحصول على الـ View الخاص بالـ Splash
            val splashView = splashScreenViewProvider.view

            // إنشاء تأثير تلاشي (Fade Out) وتكبير للخلف (Scale Up)
            splashView.animate()
                .alpha(0f) // الشفافية تصبح 0 (اختفاء كامل)
                .scaleX(1.1f) // تكبير العرض قليلاً
                .scaleY(1.1f) // تكبير الطول قليلاً
                .setDuration(500L) // مدة الحركة 500 مللي ثانية
                .setInterpolator(AnticipateInterpolator()) // مؤثر حركي يعطي "دفع" للخلف قبل الاختفاء
                .withEndAction {
                    // هام جداً! 🛑 يجب إزالة الـ Splash View بعد انتهاء الحركة تماماً
                    splashScreenViewProvider.remove()
                }
                .start() // بدء الحركة
        }
        enableEdgeToEdge()
        setContent {
            TaskoriumTheme {
                val state by splashViewModel.state.collectAsStateWithLifecycle()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskoriumApp(state.startDestination, innerPadding)
                }
            }
        }
    }
}

@Composable
private fun TaskoriumApp(startDestination: StartDestination, innerPadding: PaddingValues){
    val navController = rememberNavController()
    TaskoriumNavGraph(
        navController = navController,
        startDestination = startDestination,
        innerPadding = innerPadding
    )
}