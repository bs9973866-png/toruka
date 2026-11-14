package com.toruka.launcher.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toruka.launcher.data.model.DownloadState
import com.toruka.launcher.data.model.VersionType
import com.toruka.launcher.ui.components.*
import com.toruka.launcher.viewmodel.VersionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionsScreen(
    viewModel: VersionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()
    var showDownloadDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TorukaTopBar(title = "Версии Minecraft")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VersionTypeChip(
                    type = VersionType.RELEASE,
                    selected = uiState.filterType == VersionType.RELEASE,
                    onClick = { 
                        viewModel.filterVersions(
                            if (uiState.filterType == VersionType.RELEASE) null else VersionType.RELEASE
                        )
                    }
                )
                VersionTypeChip(
                    type = VersionType.SNAPSHOT,
                    selected = uiState.filterType == VersionType.SNAPSHOT,
                    onClick = { 
                        viewModel.filterVersions(
                            if (uiState.filterType == VersionType.SNAPSHOT) null else VersionType.SNAPSHOT
                        )
                    }
                )
                VersionTypeChip(
                    type = VersionType.BETA,
                    selected = uiState.filterType == VersionType.BETA,
                    onClick = { 
                        viewModel.filterVersions(
                            if (uiState.filterType == VersionType.BETA) null else VersionType.BETA
                        )
                    }
                )
                VersionTypeChip(
                    type = VersionType.ALPHA,
                    selected = uiState.filterType == VersionType.ALPHA,
                    onClick = { 
                        viewModel.filterVersions(
                            if (uiState.filterType == VersionType.ALPHA) null else VersionType.ALPHA
                        )
                    }
                )
            }

            when {
                uiState.isLoading -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadVersions() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.filteredVersions.isEmpty() -> {
                    EmptyState(
                        message = "Нет доступных версий",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.filteredVersions) { version ->
                            VersionCard(
                                version = version,
                                isInstalled = uiState.installedVersions.contains(version.id),
                                isLatestRelease = version.id == uiState.latestRelease,
                                isLatestSnapshot = version.id == uiState.latestSnapshot,
                                onInstall = {
                                    viewModel.installVersion(version)
                                    showDownloadDialog = true
                                },
                                onDelete = { viewModel.deleteVersion(version.id) },
                                onSelect = { /* Select version for profile */ }
                            )
                        }
                    }
                }
            }
        }

        // Download Progress Dialog
        if (showDownloadDialog && downloadState is DownloadState.Downloading) {
            DownloadProgressDialog(
                state = downloadState as DownloadState.Downloading,
                onDismiss = {
                    showDownloadDialog = false
                    viewModel.resetDownloadState()
                }
            )
        }

        if (showDownloadDialog && downloadState is DownloadState.Installing) {
            InstallingDialog(
                progress = (downloadState as DownloadState.Installing).progress,
                onDismiss = {
                    showDownloadDialog = false
                    viewModel.resetDownloadState()
                }
            )
        }

        if (showDownloadDialog && downloadState is DownloadState.Completed) {
            LaunchedEffect(Unit) {
                showDownloadDialog = false
                viewModel.resetDownloadState()
            }
        }

        if (showDownloadDialog && downloadState is DownloadState.Error) {
            AlertDialog(
                onDismissRequest = {
                    showDownloadDialog = false
                    viewModel.resetDownloadState()
                },
                title = { Text("Ошибка") },
                text = { Text((downloadState as DownloadState.Error).message) },
                confirmButton = {
                    TextButton(onClick = {
                        showDownloadDialog = false
                        viewModel.resetDownloadState()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun DownloadProgressDialog(
    state: DownloadState.Downloading,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Загрузка версии") },
        text = {
            Column {
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatBytes(state.bytesDownloaded)} / ${formatBytes(state.totalBytes)}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (state.speed > 0) {
                    Text(
                        text = "${formatBytes(state.speed)}/с",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun InstallingDialog(
    progress: Float,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Установка") },
        text = {
            Column {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {}
    )
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
        bytes >= 1024 * 1024 -> "%.2f MB".format(bytes / (1024.0 * 1024.0))
        bytes >= 1024 -> "%.2f KB".format(bytes / 1024.0)
        else -> "$bytes B"
    }
}
