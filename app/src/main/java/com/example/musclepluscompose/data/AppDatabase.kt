package com.example.musclepluscompose.data


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

import com.google.gson.Gson


@Database(
    entities = [Workout::class, Exercise::class, Workout_Done::class, Exercise_Done::class],
    version = 1

)
@TypeConverters(DateConverter::class, Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun Exercise_DoneDao(): Exercise_DoneDao
    abstract fun Workout_DoneDao(): Workout_DoneDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val exercises = listOf<Exercise>(
            Exercise(name = "Bench Press", desc = "Lie on a bench and lower a barbell towards your chest, then push it back up.", id = 1),
            Exercise(name = "Squats", desc = "Stand with feet hip-width apart, lower down into a sitting position while keeping your back straight, and return to standing while holding weights.", id = 2),
            Exercise(name = "Deadlifts", desc = "Stand with feet hip-width apart, bend down to pick up a barbell, then straighten up while holding the weights.", id = 3),
            Exercise(name = "Barbell Rows", desc = "Bend forward with knees slightly bent and lift a barbell towards your chest, then lower it back down.", id = 4),
            Exercise(name = "Bicep Curls", desc = "Stand with feet hip-width apart, hold weights in each hand, and curl weights towards your shoulders.", id = 5),
            Exercise(name = "Tricep Extensions", desc = "Stand with feet hip-width apart, hold weights above your head, and lower weights behind your head by bending elbows.", id = 6),
            Exercise(name = "Shoulder Press", desc = "Stand with feet hip-width apart, hold weights at shoulder height, and push weights overhead.", id = 7),
            Exercise(name = "Lateral Raises", desc = "Stand with feet hip-width apart, hold weights at your sides, and lift weights to shoulder height.", id = 8),
            Exercise(name = "Front Raises", desc = "Stand with feet hip-width apart, hold weights in front of you, and lift weights to shoulder height.", id = 9),
            Exercise(name = "Hammer Curls", desc = "Stand with feet hip-width apart, hold weights with palms facing each other, and curl weights towards your shoulders.", id = 10),
            Exercise(name = "Dumbbell Lunges", desc = "Take a big step forward with one foot, bending both knees to lower the back knee towards the ground while holding weights, and return to standing.", id = 11),
            Exercise(name = "Calf Raises", desc = "Stand with feet hip-width apart on a raised platform, hold weights, and raise your heels up and down.", id = 12),
            Exercise(name = "Leg Press", desc = "Sit on a leg press machine and push a weight up and down with your legs.", id = 13),
            Exercise(name = "Incline Bench Press", desc = "Lie on an incline bench and lower a barbell towards your chest, then push it back up.", id = 14),
            Exercise(name = "Decline Bench Press", desc = "Lie on a decline bench and lower a barbell towards your chest, then push it back up.", id = 15)
        )


        val workout1 = listOf<Exercise>(
            exercises[0], // Bench Press
            exercises[3], // Barbell Rows
            exercises[4], // Bicep Curls
            exercises[5], // Tricep Extensions
            exercises[6], // Shoulder Press
        )

        val workout2 = listOf<Exercise>(
            exercises[1], // Squats
            exercises[2], // Deadlifts
            exercises[10], // Dumbell Lunges
            exercises[11], // Calf Raises
            exercises[12], // Leg Press
        )

        val workout3 = listOf<Exercise>(
            exercises[0], // Bench Press
            exercises[1], // Squats
            exercises[2], // Deadlifts
            exercises[4], // Bicep Curls
            exercises[5], // Tricep Extensions
            exercises[6], // Shoulder Press
            exercises[7], // Lateral Raises
            exercises[8], // Front Raises
            exercises[9], // Hammer Curls
            exercises[11], // Calf Raises
        )

        //val gson = Gson()
        //val workout1Json = gson.toJson(workout1)
        val workout1MutableList = workout1.toMutableList()
        val workout2MutableList = workout2.toMutableList()
        val workout3MutableList = workout3.toMutableList()

        val workouts = listOf<Workout>(
            Workout(name = "Upper Body Focus", desc = "This workout is designed to build a strong and powerful chest, arms, and back.", exercise = workout1MutableList, id = 1),
            Workout(name = "Lower Body Focus", desc = "This workout is designed to build strong and toned legs and calves.", exercise = workout2MutableList, id = 2),
            Workout(name = "Full Body Workout", desc = "This workout is designed to target all major muscle groups and provide a full body challenge.", exercise = workout3MutableList, id = 3)
        )

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "App.db"
                ).allowMainThreadQueries().addCallback(DataBaseCallback(exercises, workouts)).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class DataBaseCallback(private val exercises: List<Exercise>, private val workouts: List<Workout>) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        exercises.forEach()
        {
            db.execSQL("INSERT INTO exercises (name, desc) VALUES ('${it.name}', '${it.desc}')")
        }

        workouts.forEach()
        {
            val gson = Gson()
            val workoutJson = gson.toJson(it.exercise)
            db.execSQL("INSERT INTO workouts (name, desc, exercises) VALUES ('${it.name}', '${it.desc}', '$workoutJson')")
        }

    }
}

@Dao
interface WorkoutDao{

    @Query("SELECT * FROM workouts")
    fun getAll(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getById(id: Int): Workout

    @Query("SELECT * FROM workouts WHERE id =:id")
    fun getWorkoutById(id : Int) : Workout

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(workout: Workout)

    @Upsert()
    suspend fun upsert(workout: Workout)

}

@Dao
interface ExerciseDao{

    @Query("SELECT * FROM exercises")
    fun getAll(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(exercise: Exercise)

    @Upsert()
    suspend fun upsert(exercise: Exercise)

}

@Dao
interface Workout_DoneDao{

    @Query("SELECT * FROM workouts_done")
    fun getAll(): Flow<List<Workout_Done>>

    @Query("SELECT * FROM workouts_done ORDER BY id DESC LIMIT 1")
    fun getLatestWorkoutDone(): Workout_Done?

    @Query("SELECT * FROM workouts_done WHERE id = :id")
    fun getWorkoutDoneById(id: Int): Workout_Done?

    @Query("SELECT * FROM workouts_done WHERE id =:id")
    fun getWorkoutDoneByIdTom(id : Int) : Workout_Done

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout_done: Workout_Done)

    @Delete
    suspend fun delete(workout_done: Workout_Done)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(workout_done: Workout_Done)

    @Upsert()
    suspend fun upsert(workout_done: Workout_Done)

}

@Dao
interface Exercise_DoneDao{

    @Query("SELECT * FROM exercises_done")
    fun getAll(): Flow<List<Exercise_Done>>

    @Query("SELECT * FROM exercises_done WHERE exercise_id =:exercise_id")
    fun getAllExerciseDoneByExerciseId(exercise_id : Int) : List<Exercise_Done>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise_done: Exercise_Done)

    @Delete
    suspend fun delete(exercise_done: Exercise_Done)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(exercise_done: Exercise_Done)

    @Upsert()
    suspend fun upsert(exercise_done: Exercise_Done)

}