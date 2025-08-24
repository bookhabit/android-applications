package com.example.android_applicatoins.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android_applicatoins.screens.*
import com.example.android_applicatoins.screens.animation.AnimationScreen
import com.example.android_applicatoins.screens.basic.BasicScreen
import com.example.android_applicatoins.screens.basic.EventTestScreen
import com.example.android_applicatoins.screens.basic.GraphicsDrawingScreen
import com.example.android_applicatoins.screens.basic.ImageDisplayScreen
import com.example.android_applicatoins.screens.game.GameScreen
import com.example.android_applicatoins.screens.native.NativeScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    
    // 카테고리 화면들
    object Basic : Screen("basic")
    object Game : Screen("game")
    object Animation : Screen("animation")
    object Native : Screen("native")
    
    // 개별 앱 화면들
    object Todo : Screen("todo")
    object Notes : Screen("notes")
    object Calculator : Screen("calculator")
    object TicTacToe : Screen("tictactoe")
    object Weather : Screen("weather")
    object Calendar : Screen("calendar")
    object Gallery : Screen("gallery")
    object Quiz : Screen("quiz")
    object Memory : Screen("memory")
    object Snake : Screen("snake")
    object Stopwatch : Screen("stopwatch")
    object Flashlight : Screen("flashlight")
    object Compass : Screen("compass")
    object EventTest : Screen("event-test")
    object GraphicsDrawing : Screen("graphics-drawing")
    object ImageDisplay : Screen("image-display")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onAppSelected = { categoryId ->
                    when (categoryId) {
                        "basic" -> navController.navigate(Screen.Basic.route)
                        "game" -> navController.navigate(Screen.Game.route)
                        "animation" -> navController.navigate(Screen.Animation.route)
                        "native" -> navController.navigate(Screen.Native.route)
                    }
                }
            )
        }
        
        // 카테고리 화면들
        composable(Screen.Basic.route) {
            BasicScreen(
                onBackPressed = { navController.popBackStack() },
                onAppSelected = { appId ->
                    when (appId) {
                        "todo" -> navController.navigate(Screen.Todo.route)
                        "notes" -> navController.navigate(Screen.Notes.route)
                        "weather" -> navController.navigate(Screen.Weather.route)
                        "calculator" -> navController.navigate(Screen.Calculator.route)
                        "calendar" -> navController.navigate(Screen.Calendar.route)
                        "gallery" -> navController.navigate(Screen.Gallery.route)
                        "event-test" -> navController.navigate(Screen.EventTest.route)
                        "graphics-drawing" -> navController.navigate(Screen.GraphicsDrawing.route)
                        "image-display" -> navController.navigate(Screen.ImageDisplay.route)
                        // TODO: 다른 기본 앱들도 추가
                    }
                }
            )
        }
        
        composable(Screen.Game.route) {
            GameScreen(
                onBackPressed = { navController.popBackStack() },
                onAppSelected = { appId ->
                    when (appId) {
                        "tictactoe" -> navController.navigate(Screen.TicTacToe.route)
                        "memory" -> navController.navigate(Screen.Memory.route)
                        "snake" -> navController.navigate(Screen.Snake.route)
                        "quiz" -> navController.navigate(Screen.Quiz.route)
                        // TODO: 다른 게임들도 추가
                    }
                }
            )
        }
        
        composable(Screen.Animation.route) {
            AnimationScreen(
                onBackPressed = { navController.popBackStack() },
                onAppSelected = { appId ->
                    // TODO: 애니메이션 앱들 구현 후 추가
                }
            )
        }
        
        composable(Screen.Native.route) {
            NativeScreen(
                onBackPressed = { navController.popBackStack() },
                onAppSelected = { appId ->
                    when (appId) {
                        "stopwatch" -> navController.navigate(Screen.Stopwatch.route)
                        "flashlight" -> navController.navigate(Screen.Flashlight.route)
                        "compass" -> navController.navigate(Screen.Compass.route)
                        // TODO: 다른 네이티브 앱들도 추가
                    }
                }
            )
        }
        
        // 개별 앱 화면들
        composable(Screen.Todo.route) {
            TodoScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Notes.route) {
            NotesScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Calculator.route) {
            CalculatorScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.TicTacToe.route) {
            TicTacToeScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Weather.route) {
            WeatherScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Stopwatch.route) {
            StopwatchScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        // 플레이스홀더 화면들
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Gallery.route) {
            GalleryScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Quiz.route) {
            QuizScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Memory.route) {
            MemoryScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Snake.route) {
            SnakeScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Flashlight.route) {
            FlashlightScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Compass.route) {
            CompassScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.EventTest.route) {
            EventTestScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.GraphicsDrawing.route) {
            GraphicsDrawingScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ImageDisplay.route) {
            ImageDisplayScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}
