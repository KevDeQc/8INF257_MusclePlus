package com.example.musclepluscompose.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises_done")
data class Exercise_Done(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workout_done_id: Int,
    val exercise_id: Int,
    val rep: Int,
    val weight: Int
)
