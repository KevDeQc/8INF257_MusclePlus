package com.example.musclepluscompose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musclepluscompose.data.AppViewModel

@Composable
fun SetupNavGraph(navController: NavHostController, viewModel: AppViewModel)
 {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){
        composable(
            route = Screen.Home.route
        ){
            HomeScreen(viewModel)
        }
        composable(
            route = Screen.Exercise.route
        ){
            ExerciseScreen(viewModel)
        }
        composable(
            route = Screen.Workout.route
        ){
            WorkoutScreen(viewModel)
        }
        composable(
            route = Screen.Stats.route
        ){
            StatsScreen(viewModel)
        }
    }
}
