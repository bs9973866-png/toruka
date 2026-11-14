package com.toruka.launcher.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toruka.launcher.data.model.LaunchState
import com.toruka.launcher.data.model.Profile
import com.toruka.launcher.ui.components.*
import com.toruka.launcher.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToVersions: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launchState by viewModel.launchState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TorukaTopBar(
                title = "Toruka Launcher",
                actions = {
                    IconButton(onClick = onNavigateToAccounts) {
                        Icon(Icons.Default.Person, contentDescription = "Аккаунты")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(padding))
            }
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadData() },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                HomeContent(
                    uiState = uiState,
                    launchState = launchState,
                    onPlayClick = { viewModel.launchGame() },
                    onProfileSelect = { viewModel.selectProfile(it) },
                    onNavigateToVersions = onNavigateToVersions,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: com.toruka.launcher.viewmodel.HomeUiState,
    launchState: LaunchState,
    onPlayClick: () -> Unit,
    onProfileSelect: (Profile) -> Unit,
    onNavigateToVersions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Account Info Card
        AccountCard(
            account = uiState.selectedAccount,
            onClick = { /* Navigate to accounts */ }
        )

        // Play Button
        PlayButton(
            isLaunching = launchState is LaunchState.Launching || launchState is LaunchState.Preparing,
            onClick = onPlayClick,
            modifier = Modifier.fillMaxWidth()
        )

        if (launchState is LaunchState.Preparing) {
            Text(
                text = (launchState as LaunchState.Preparing).message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (launchState is LaunchState.Error) {
            Text(
                text = (launchState as LaunchState.Error).message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Latest Versions
        Text(
            text = "Последние версии",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LatestVersionCard(
                label = "Релиз",
                version = uiState.latestRelease,
                onClick = onNavigateToVersions,
                modifier = Modifier.weight(1f)
            )
            LatestVersionCard(
                label = "Снапшот",
                version = uiState.latestSnapshot,
                onClick = onNavigateToVersions,
                modifier = Modifier.weight(1f)
            )
        }

        // Profiles
        Text(
            text = "Профили",
            style = MaterialTheme.typography.titleLarge
        )

        if (uiState.profiles.isEmpty()) {
            EmptyState("Нет профилей. Создайте профиль в настройках.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.profiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        isSelected = uiState.selectedProfile?.id == profile.id,
                        onClick = { onProfileSelect(profile) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountCard(
    account: com.toruka.launcher.data.model.Account?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account?.displayName ?: "Нет аккаунта",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = account?.type?.displayName ?: "Нажмите для входа",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlayButton(
    isLaunching: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        enabled = !isLaunching,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if (isLaunching) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Запуск...",
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "ИГРАТЬ",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun LatestVersionCard(
    label: String,
    version: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = version.ifEmpty { "—" },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileCard(
    profile: Profile,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Gamepad,
                contentDescription = null,
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${profile.versionId} | ${profile.renderer.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Выбрано",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
