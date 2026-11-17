package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE profileId = :profileId LIMIT 1")
    fun observeUserProfile(profileId: String = "primary_user"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles ORDER BY updatedAtMillis DESC")
    fun getAllUserProfiles(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM user_profiles ORDER BY updatedAtMillis DESC")
    suspend fun getAllUserProfilesOnce(): List<UserProfileEntity>

    @Query("SELECT * FROM user_profiles WHERE profileId = :profileId LIMIT 1")
    suspend fun getUserProfileOnce(profileId: String = "primary_user"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(entity: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(entity: UserProfileEntity)

    @Query(
        """
        UPDATE user_profiles
        SET displayName = :displayName,
            handle = :handle,
            roleBadge = :roleBadge,
            bio = :bio,
            statusText = :statusText,
            personaName = :personaName,
            updatedAtMillis = :updatedAtMillis
        WHERE profileId = :profileId
        """
    )
    suspend fun updateProfileIdentity(
        profileId: String = "primary_user",
        displayName: String,
        handle: String,
        roleBadge: String,
        bio: String,
        statusText: String,
        personaName: String,
        updatedAtMillis: Long = System.currentTimeMillis()
    )

    @Query(
        """
        UPDATE user_profiles
        SET isDarkMode = :isDarkMode,
            useDynamicColor = :useDynamicColor,
            highContrast = :highContrast,
            largeText = :largeText,
            dyslexiaSpacing = :dyslexiaSpacing,
            screenReaderHints = :screenReaderHints,
            offlineModeManual = :offlineModeManual,
            preferredLanguage = :preferredLanguage,
            notificationsEnabled = :notificationsEnabled,
            autoSyncWhenOnline = :autoSyncWhenOnline,
            updatedAtMillis = :updatedAtMillis
        WHERE profileId = :profileId
        """
    )
    suspend fun updateProfilePreferences(
        profileId: String = "primary_user",
        isDarkMode: Boolean,
        useDynamicColor: Boolean,
        highContrast: Boolean,
        largeText: Boolean,
        dyslexiaSpacing: Boolean,
        screenReaderHints: Boolean,
        offlineModeManual: Boolean,
        preferredLanguage: String,
        notificationsEnabled: Boolean,
        autoSyncWhenOnline: Boolean,
        updatedAtMillis: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM user_profiles")
    suspend fun clearAllUserProfiles()
}

@Dao
interface PlanetDao {
    @Query("SELECT * FROM financial_tracker_entries ORDER BY createdAtMillis DESC")
    fun observeFinancialEntries(): Flow<List<FinancialTrackerEntryEntity>>

    @Query("SELECT * FROM financial_tracker_entries ORDER BY createdAtMillis DESC")
    suspend fun getAllFinancialEntriesOnce(): List<FinancialTrackerEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFinancialEntry(entry: FinancialTrackerEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFinancialEntries(entries: List<FinancialTrackerEntryEntity>)

    @Query("DELETE FROM financial_tracker_entries WHERE entryId = :entryId")
    suspend fun deleteFinancialEntry(entryId: String)

    @Query("DELETE FROM financial_tracker_entries")
    suspend fun clearAllFinancialEntries()

    @Query("SELECT * FROM persona_stage_data ORDER BY stageScoreOrXp DESC")
    fun observePersonaStages(): Flow<List<PersonaStageEntity>>

    @Query("SELECT * FROM persona_stage_data ORDER BY stageScoreOrXp DESC")
    suspend fun getAllPersonaStagesOnce(): List<PersonaStageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPersonaStage(stage: PersonaStageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPersonaStages(stages: List<PersonaStageEntity>)

    @Query("SELECT * FROM profile_3d_configs ORDER BY updatedAtMillis DESC LIMIT 1")
    fun observeActive3DConfig(): Flow<Profile3DConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert3DConfig(config: Profile3DConfigEntity)

    @Query("SELECT * FROM career_milestones ORDER BY stepOrder ASC")
    fun observeCareerMilestones(): Flow<List<CareerMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCareerMilestones(milestones: List<CareerMilestoneEntity>)
}

@Database(
    entities = [
        UserProfileEntity::class,
        FinancialTrackerEntryEntity::class,
        PersonaStageEntity::class,
        Profile3DConfigEntity::class,
        CareerMilestoneEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class PlanetDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun planetDao(): PlanetDao

    companion object {
        @Volatile
        private var INSTANCE: PlanetDatabase? = null

        fun getInstance(context: Context): PlanetDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlanetDatabase::class.java,
                    "creation_planet_wondering_world.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
