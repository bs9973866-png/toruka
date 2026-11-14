package com.toruka.launcher.data.local

import androidx.room.*
import com.toruka.launcher.data.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    
    @Query("SELECT * FROM accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): Flow<List<Account>>
    
    @Query("SELECT * FROM accounts WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedAccount(): Account?
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: String): Account?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)
    
    @Update
    suspend fun updateAccount(account: Account)
    
    @Delete
    suspend fun deleteAccount(account: Account)
    
    @Query("UPDATE accounts SET isSelected = 0")
    suspend fun clearSelectedAccount()
    
    @Query("UPDATE accounts SET isSelected = 1 WHERE id = :id")
    suspend fun selectAccount(id: String)
    
    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getAccountCount(): Int
}
