package com.toruka.launcher.data.local

import androidx.room.*
import com.toruka.launcher.data.model.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    
    @Query("SELECT * FROM profiles ORDER BY lastPlayed DESC")
    fun getAllProfiles(): Flow<List<Profile>>
    
    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): Profile?
    
    @Query("SELECT * FROM profiles WHERE isFavorite = 1 ORDER BY lastPlayed DESC")
    fun getFavoriteProfiles(): Flow<List<Profile>>
    
    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getProfileCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: Profile)
    
    @Update
    suspend fun updateProfile(profile: Profile)
    
    @Delete
    suspend fun deleteProfile(profile: Profile)
    
    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteProfileById(id: String)
    
    @Query("UPDATE profiles SET lastPlayed = :timestamp WHERE id = :id")
    suspend fun updateLastPlayed(id: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE profiles SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: String)
}
