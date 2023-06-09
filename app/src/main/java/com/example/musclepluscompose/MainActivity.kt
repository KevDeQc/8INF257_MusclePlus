package com.example.musclepluscompose

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.musclepluscompose.data.AppViewModel
import com.example.musclepluscompose.data.Workout
import com.example.musclepluscompose.ui.theme.MuscleBlue
import com.example.musclepluscompose.ui.theme.MusclePlusComposeTheme
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController

    private val viewModel by viewModels<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusclePlusComposeTheme {

                navController = rememberNavController()


                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()

                var showDialog = remember { mutableStateOf(false) }
                val allWorkouts by viewModel.allWorkout.collectAsState(emptyList())
                var selectedItem by remember { mutableStateOf<Workout?>(null) }
                var expanded by remember { mutableStateOf(false) }


                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                             AppBar(
                                 onNavigationIconClick = {
                                     scope.launch { scaffoldState.drawerState.open() }
                                 }
                             )
                    },
                    drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                    drawerContent = {
                        DrawerHeader()
                        DrawerBody(
                            items = listOf(
                                MenuItem(
                                    id = "home",
                                    title = "Home",
                                    contentDescription = "Go to home screen",
                                    icon = Icons.Default.Home
                                ),
                                MenuItem(
                                    id = "exercise",
                                    title = "Exercise",
                                    contentDescription = "Go to exercise screen",
                                    icon = Icons.Default.Star
                                ),
                                MenuItem(
                                    id = "workout",
                                    title = "Workout",
                                    contentDescription = "Go to workout screen",
                                    icon = Icons.Default.Favorite
                                ),
                                MenuItem(
                                    id = "stats",
                                    title = "Stats",
                                    contentDescription = "Go to stats screen",
                                    icon = Icons.Default.Info
                                ),
                                MenuItem(
                                    id = "startWorkout",
                                    title = "Start Workout",
                                    contentDescription = "Go to start workout activity",
                                    icon = Icons.Default.PlayArrow
                                ),
                                MenuItem(
                                id = "share",
                                title = "Share latest workout",
                                contentDescription = "Share your last workout stats where you want",
                                icon = Icons.Default.Email
                            )
                            ) ,
                            onItemClick = {
                                scope.launch { scaffoldState.drawerState.close() }
                                when(it.id){
                                    "home" -> navController.navigate(route = Screen.Home.route)
                                    "exercise" -> navController.navigate(route = Screen.Exercise.route)
                                    "workout" -> navController.navigate(route = Screen.Workout.route)
                                    "stats" -> navController.navigate(route = Screen.Stats.route)
                                    "startWorkout" -> showDialog.value = true
                                    "share" -> share()
                                }
                            })
                    }
                ) {

                    contentPadding ->
                    Box(modifier = Modifier.fillMaxSize()){
                        SetupNavGraph(navController = navController, viewModel)

                        if(showDialog.value){
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    color = Color.Transparent,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    // This makes the rest of the screen transparent
                                }
                                Dialog(
                                    onDismissRequest = { showDialog.value = false },
                                    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colors.surface.copy())
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                    ) {
                                        Column {
                                            Text(
                                                text = "Select the workout:",
                                                modifier = Modifier.padding(bottom = 8.dp),
                                                style = MaterialTheme.typography.h6
                                            )

                                            OutlinedTextField(
                                                value = selectedItem?.name ?: "No workout chosen",
                                                onValueChange = {},
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable(onClick = { expanded = true }),
                                                enabled = false,
                                                readOnly = true,
                                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                                    disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
                                                    backgroundColor = Color.Transparent,
                                                    disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                                                    disabledLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium),
                                                ),
                                                trailingIcon = {
                                                    Icon(Icons.Default.ArrowDropDown, "contentDescription")
                                                        //Modifier.clickable { expanded = !expanded })
                                                }
                                            )

                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                // Create a dropdown item for each item in the list
                                                allWorkouts.forEach { item ->
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            selectedItem = item
                                                            expanded = false;
                                                        }
                                                    ) {
                                                        Text(text = "${item.name}" )
                                                    }
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    val getId: Int = selectedItem?.id ?:0

                                                    if(getId != 0){
                                                        startWorkoutActivity(getId)
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(
                                                    backgroundColor = MuscleBlue,
                                                    contentColor = MaterialTheme.colors.onPrimary // White
                                                )
                                            ) {
                                                Text(text = "Start")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startWorkoutActivity(id: Int) {
        Intent(this, WorkoutTracker::class.java).also {
            it.putExtra("WorkoutID", id)
            startActivity(it)
        }
    }

    fun getTwitterIntent(ctx: Context?, shareText: String):  Intent? {
        val shareIntent: Intent

            val tweetUrl = "https://twitter.com/intent/tweet?text=$shareText"
            val uri: Uri = Uri.parse(tweetUrl)
            shareIntent = Intent(Intent.ACTION_VIEW, uri)
            return shareIntent
    }

    fun share(){
        val shareIntent = Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        val lastWorkoutDone = viewModel.getWorkoutDoneById(viewModel.getLastWorkoutDone())

        val workoutId = lastWorkoutDone?.workout_id ?:-1
        if (workoutId !== -1)
        {
            val workout = viewModel.findWorkout(workoutId)

            val message = "I finished the ${workout.name} workout in ${formatElapsedTime(lastWorkoutDone!!.time)} minutes on the date of ${lastWorkoutDone.date}";
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(Intent.createChooser(shareIntent, "Share my workout"))
        }

    }

    private fun formatElapsedTime(timeMillis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
