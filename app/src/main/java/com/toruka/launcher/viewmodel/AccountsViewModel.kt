package com.toruka.launcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toruka.launcher.data.model.Account
import com.toruka.launcher.data.model.AccountType
import com.toruka.launcher.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getAllAccounts().collect { accounts ->
                val selected = accounts.find { it.isSelected }
                _uiState.update { state ->
                    state.copy(
                        accounts = accounts,
                        selectedAccount = selected,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addOfflineAccount(username: String) {
        viewModelScope.launch {
            val uuid = UUID.nameUUIDFromBytes("OfflinePlayer:$username".toByteArray()).toString()
            val account = Account(
                id = UUID.randomUUID().toString(),
                type = AccountType.OFFLINE,
                username = username,
                displayName = username,
                uuid = uuid.replace("-", "")
            )
            accountRepository.insertAccount(account)
            if (_uiState.value.accounts.isEmpty()) {
                accountRepository.selectAccount(account.id)
            }
        }
    }

    fun selectAccount(accountId: String) {
        viewModelScope.launch {
            accountRepository.selectAccount(accountId)
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.deleteAccount(account)
        }
    }

    fun startMicrosoftAuth() {
        // TODO: Implement Microsoft OAuth flow
        _uiState.update { it.copy(authPending = true) }
    }
}

data class AccountsUiState(
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val authPending: Boolean = false,
    val error: String? = null
)
