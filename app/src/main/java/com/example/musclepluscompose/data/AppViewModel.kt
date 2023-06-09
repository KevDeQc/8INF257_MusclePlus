package com.example.musclepluscompose.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.*
import kotlinx.coroutines.runBlocking

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()
    private val workout_doneDao = AppDatabase.getDatabase(application).Workout_DoneDao()
    private val exercise_doneDao = AppDatabase.getDatabase(application).Exercise_DoneDao()

    val allWorkout: Flow<List<Workout>> = workoutDao.getAll()

    fun findWorkout(id: Int): Workout{
        return workoutDao.getById(id)
    }

    fun insertWorkout(workout: Workout){
        viewModelScope.launch {
            workoutDao.insert(workout)
        }
    }

    fun deleteWorkout(workout: Workout){
        viewModelScope.launch {
            workoutDao.delete(workout)
        }
    }

    fun updateWorkout(workout: Workout){
        viewModelScope.launch {
            workoutDao.update(workout)
        }
    }

    fun upsertWorkout(workout: Workout){
        viewModelScope.launch {
            workoutDao.upsert(workout)
        }
    }

    // should use coroutine
    fun getWorkoutById(id : Int) : Workout{
        return workoutDao.getWorkoutById(id)
    }

//-------- Exercise -------

    val allExercise: Flow<List<Exercise>> = exerciseDao.getAll()

    fun insertExercise(exercise: Exercise){
        viewModelScope.launch {
            exerciseDao.insert(exercise)
        }
    }

    fun deleteExercise(exercise: Exercise){
        viewModelScope.launch {
            exerciseDao.delete(exercise)
        }
    }

    fun updateExercise(exercise: Exercise){
        viewModelScope.launch{
            exerciseDao.update(exercise)
        }
    }

    fun upsertExercise(exercise: Exercise){
        viewModelScope.launch {
            exerciseDao.upsert(exercise)
        }
    }

//-------- Workout Done -------

    val allWorkout_Done: Flow<List<Workout_Done>> = workout_doneDao.getAll()


    fun getLastWorkoutDone(): Int{
        return workout_doneDao.getLatestWorkoutDone()?.id ?: 0
    }

    fun getWorkoutDoneById(id: Int) : Workout_Done?
    {
        return workout_doneDao.getWorkoutDoneById(id)
    }

    fun getWorkoutDoneByIdTom(id : Int) : Workout_Done{
        return workout_doneDao.getWorkoutDoneByIdTom(id)
    }

    fun getAllWorkoutInTime(timeScope: Int) : List<Workout_Done>{
        val now = ZonedDateTime.now()
        val maxTime = now.plusDays(-timeScope.toLong())


        val result = mutableListOf<Workout_Done>()
        val copy = workout_doneDao.getAllNoFlow()

        copy.forEach { item ->
            val date = item.date
            if (date.time > maxTime.toInstant().toEpochMilli()){
                result.add(item)
            }
        }
        return result
    }


        fun insertWorkout_Done(workout_done: Workout_Done){
        viewModelScope.launch {
            workout_doneDao.insert(workout_done)
        }
    }

    fun deleteWorkout_Done(workout_done: Workout_Done){
        viewModelScope.launch {
            workout_doneDao.delete(workout_done)
        }
    }

    fun updateWorkout_Done(workout_done: Workout_Done){
        viewModelScope.launch{
            workout_doneDao.update(workout_done)
        }
    }

    fun upsertWorkout_Done(workout_done: Workout_Done): Int{
        viewModelScope.launch {
            workout_doneDao.upsert(workout_done)
        }
        val id: Int = getLastWorkoutDone()

        return id + 1
    }

//-------- Exercise Done -------

    val allExercise_Done: Flow<List<Exercise_Done>> = exercise_doneDao.getAll()

    fun getAllExerciseDoneByExerciseId(exercise_id : Int) : List<Exercise_Done>{
        return exercise_doneDao.getAllExerciseDoneByExerciseId(exercise_id)
    }

    fun getAllExerciseDoneInTimeById(exercise_id: Int, timeScope : Int) : List<Pair<Exercise_Done, Date>>{


        val now = ZonedDateTime.now()
        val maxTime = now.plusDays(-timeScope.toLong())


        val result = mutableListOf<Pair<Exercise_Done, Date>>()
        val temp = getAllExerciseDoneByExerciseId(exercise_id)
        temp.forEach { item ->
            val date = getWorkoutDoneByIdTom(item.workout_done_id).date
            if (date.time > maxTime.toInstant().toEpochMilli()){
                result.add(Pair(item, date))
            }
        }

        return result
    }

    fun getAllExerciseInTime(timeScope: Int) : List<Exercise_Done>{
        val now = ZonedDateTime.now()
        val maxTime = now.plusDays(-timeScope.toLong())


        val result = mutableListOf<Exercise_Done>()
        val copy = exercise_doneDao.getAllNoFlow()

        copy.forEach { item ->
            val date = getWorkoutDoneByIdTom(item.workout_done_id).date
            if (date.time > maxTime.toInstant().toEpochMilli()){
                result.add(item)
            }

        }

        return result
    }

    fun insertExercise_Done(exercise_done: Exercise_Done){
        viewModelScope.launch {
            exercise_doneDao.insert(exercise_done)
        }
    }

    fun deleteExercise_Done(exercise_done: Exercise_Done){
        viewModelScope.launch {
            exercise_doneDao.delete(exercise_done)
        }
    }

    fun updateExercise_Done(exercise_done: Exercise_Done){
        viewModelScope.launch{
            exercise_doneDao.update(exercise_done)
        }
    }

    fun upsertExercise_Done(exercise_done: Exercise_Done){
        viewModelScope.launch {
            exercise_doneDao.upsert(exercise_done)
        }
    }



}