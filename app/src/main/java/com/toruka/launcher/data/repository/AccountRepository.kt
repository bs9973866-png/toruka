package com.toruka.launcher.data.repository

import com.toruka.launcher.data.local.AccountDao
import com.toruka.launcher.data.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) {
    fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()
    
    suspend fun getSelectedAccount(): Account? = accountDao.getSelectedAccount()
    
    suspend fun getAccountById(id: String): Account? = accountDao.getAccountById(id)
    
    suspend fun insertAccount(account: Account) = accountDao.insertAccount(account)
    
    suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)
    
    suspend fun deleteAccount(account: Account) = accountDao.deleteAccount(account)
    
    suspend fun selectAccount(id: String) {
        accountDao.clearSelectedAccount()
        accountDao.selectAccount(id)
    }
    
    suspend fun getAccountCount(): Int = accountDao.getAccountCount()
}
