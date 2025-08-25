package com.example.android_applicatoins.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android_applicatoins.screens.MainScreen
import com.example.android_applicatoins.screens.animation.AnimationScreen
import com.example.android_applicatoins.screens.basic.BasicScreen
import com.example.android_applicatoins.screens.basic.TodoScreen
import com.example.android_applicatoins.screens.basic.NotesScreen
import com.example.android_applicatoins.screens.basic.WeatherScreen
import com.example.android_applicatoins.screens.basic.CalculatorScreen
import com.example.android_applicatoins.screens.basic.EventTestScreen
import com.example.android_applicatoins.screens.basic.GraphicsDrawingScreen
import com.example.android_applicatoins.screens.basic.ImageDisplayScreen
import com.example.android_applicatoins.screens.basic.DialogPopupNotificationScreen
import com.example.android_applicatoins.screens.basic.ListViewScreen
import com.example.android_applicatoins.screens.basic.FormExampleScreen
import com.example.android_applicatoins.screens.basic.ActionSheetScreen
import com.example.android_applicatoins.screens.basic.ModalScreen
import com.example.android_applicatoins.screens.basic.MovieInfoScreen
import com.example.android_applicatoins.screens.basic.CalendarScreen
import com.example.android_applicatoins.screens.basic.StepCounterScreen
import com.example.android_applicatoins.screens.basic.GalleryScreen
import com.example.android_applicatoins.screens.basic.IntentTestScreen
import com.example.android_applicatoins.screens.basic.ImplicitIntentTestScreen
import com.example.android_applicatoins.screens.basic.listView.AdapterViewDemoScreen
import com.example.android_applicatoins.screens.basic.listView.RecyclerViewDemoScreen
import com.example.android_applicatoins.screens.basic.listView.SpinnerDemoScreen
import com.example.android_applicatoins.screens.basic.listView.FragmentDemoScreen
import com.example.android_applicatoins.screens.basic.listView.ViewPagerDemoScreen
import com.example.android_applicatoins.screens.basic.listView.ComposeLazyDemoScreen
import com.example.android_applicatoins.screens.game.GameScreen
import com.example.android_applicatoins.screens.native.NativeScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    
    // 카테고리 화면들
    object Basic : Screen("basic")
    object Game : Screen("game")
    object Animation : Screen("animation")
    object Native : Screen("native")
    
    // 개별 앱 화면들 (독립적으로 접근 가능한 것들만)
    object Todo : Screen("todo")
    object Notes : Screen("notes")
    object Weather : Screen("weather")
    object Calculator : Screen("calculator")
    object EventTest : Screen("event-test")
    object GraphicsDrawing : Screen("graphics-drawing")
    object ImageDisplay : Screen("image-display")
    object DialogPopupNotification : Screen("dialog-popup-notification")
    object FormExample : Screen("form-example")
    object ActionSheet : Screen("action-sheet")
    object Modal : Screen("modal")
    object MovieInfo : Screen("movie-info")
    object Calendar : Screen("calendar")
    object StepCounter : Screen("step-counter")
    object Gallery : Screen("gallery")
    object IntentTest : Screen("intent-test")
    object ImplicitIntentTest : Screen("implicit-intent-test")
    
    // 리스트 뷰 관련 스크린들
    object ListView : Screen("list-view")
    object AdapterViewDemo : Screen("adapter-view-demo")
    object RecyclerViewDemo : Screen("recycler-view-demo")
    object SpinnerDemo : Screen("spinner-demo")
    object FragmentDemo : Screen("fragment-demo")
    object ViewPagerDemo : Screen("view-pager-demo")
    object ComposeLazyDemo : Screen("compose-lazy-demo")
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
                        "event-test" -> navController.navigate(Screen.EventTest.route)
                        "graphics-drawing" -> navController.navigate(Screen.GraphicsDrawing.route)
                        "image-display" -> navController.navigate(Screen.ImageDisplay.route)
                        "dialog-popup-notification" -> navController.navigate(Screen.DialogPopupNotification.route)
                        "form-example" -> navController.navigate(Screen.FormExample.route)
                        "action-sheet" -> navController.navigate(Screen.ActionSheet.route)
                        "modal" -> navController.navigate(Screen.Modal.route)
                        "movie-info" -> navController.navigate(Screen.MovieInfo.route)
                        "calendar" -> navController.navigate(Screen.Calendar.route)
                        "step-counter" -> navController.navigate(Screen.StepCounter.route)
                        "gallery" -> navController.navigate(Screen.Gallery.route)
                        "list-view" -> navController.navigate(Screen.ListView.route)
                        "intent-test" -> navController.navigate(Screen.IntentTest.route)
                        "implicit-intent-test" -> navController.navigate(Screen.ImplicitIntentTest.route)
                    }
                }
            )
        }
        
        composable(Screen.Game.route) {
            GameScreen(
                onBackPressed = { navController.popBackStack() },
                onAppSelected = { appId ->
                    when (appId) {
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
                    // TODO: 네이티브 앱들 구현 후 추가
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
        
        composable(Screen.Weather.route) {
            WeatherScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Calculator.route) {
            CalculatorScreen(
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
        
        composable(Screen.DialogPopupNotification.route) {
            DialogPopupNotificationScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.FormExample.route) {
            FormExampleScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ActionSheet.route) {
            ActionSheetScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Modal.route) {
            ModalScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.MovieInfo.route) {
            MovieInfoScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.StepCounter.route) {
            StepCounterScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Gallery.route) {
            GalleryScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.IntentTest.route) {
            IntentTestScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ImplicitIntentTest.route) {
            ImplicitIntentTestScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        // 리스트 뷰 관련 스크린들
        composable(Screen.ListView.route) {
            ListViewScreen(
                onBackPressed = { navController.popBackStack() },
                onAppSelected = { appId ->
                    when (appId) {
                        "adapter-view-demo" -> navController.navigate(Screen.AdapterViewDemo.route)
                        "recycler-view-demo" -> navController.navigate(Screen.RecyclerViewDemo.route)
                        "spinner-demo" -> navController.navigate(Screen.SpinnerDemo.route)
                        "fragment-demo" -> navController.navigate(Screen.FragmentDemo.route)
                        "view-pager-demo" -> navController.navigate(Screen.ViewPagerDemo.route)
                        "compose-lazy-demo" -> navController.navigate(Screen.ComposeLazyDemo.route)
                    }
                }
            )
        }
        
        composable(Screen.AdapterViewDemo.route) {
            AdapterViewDemoScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RecyclerViewDemo.route) {
            RecyclerViewDemoScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SpinnerDemo.route) {
            SpinnerDemoScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.FragmentDemo.route) {
            FragmentDemoScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ViewPagerDemo.route) {
            ViewPagerDemoScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ComposeLazyDemo.route) {
            ComposeLazyDemoScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}
