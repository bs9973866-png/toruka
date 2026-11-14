package com.toruka.launcher.data.repository

import com.toruka.launcher.data.local.ProfileDao
import com.toruka.launcher.data.model.Profile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao
) {
    fun getAllProfiles(): Flow<List<Profile>> = profileDao.getAllProfiles()
    
    fun getFavoriteProfiles(): Flow<List<Profile>> = profileDao.getFavoriteProfiles()
    
    suspend fun getProfileById(id: String): Profile? = profileDao.getProfileById(id)
    
    suspend fun createProfile(profile: Profile) = profileDao.insertProfile(profile)
    
    suspend fun updateProfile(profile: Profile) = profileDao.updateProfile(profile)
    
    suspend fun deleteProfile(profile: Profile) = profileDao.deleteProfile(profile)
    
    suspend fun deleteProfileById(id: String) = profileDao.deleteProfileById(id)
    
    suspend fun updateLastPlayed(id: String) = profileDao.updateLastPlayed(id)
    
    suspend fun incrementPlayCount(id: String) = profileDao.incrementPlayCount(id)
    
    suspend fun getProfileCount(): Int = profileDao.getProfileCount()
}
