package com.toruka.launcher.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toruka.launcher.data.model.RendererType
import com.toruka.launcher.ui.components.TorukaTopBar
import com.toruka.launcher.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { TorukaTopBar(title = "Настройки") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Settings
            SettingsSection(title = "Общие") {
                SettingsItem(
                    icon = Icons.Default.Folder,
                    title = "Директория игры",
                    subtitle = "/sdcard/Android/data/.../.minecraft",
                    onClick = { }
                )
            }

            // Video Settings
            SettingsSection(title = "Видео") {
                var selectedRenderer by remember { mutableStateOf(RendererType.GL4ES) }
                
                RendererSelector(
                    selected = selectedRenderer,
                    onSelect = { selectedRenderer = it }
                )

                SettingsSliderItem(
                    icon = Icons.Default.AspectRatio,
                    title = "Выделение памяти",
                    value = 2048f,
                    valueRange = 512f..8192f,
                    steps = 15,
                    valueText = "2048 МБ",
                    onValueChange = { }
                )
            }

            // Java Settings
            SettingsSection(title = "Java") {
                SettingsItem(
                    icon = Icons.Default.Memory,
                    title = "Java Runtime",
                    subtitle = "Java 17 (встроенная)",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.Code,
                    title = "JVM аргументы",
                    subtitle = "Дополнительные флаги JVM",
                    onClick = { }
                )
            }

            // Controls
            SettingsSection(title = "Управление") {
                SettingsItem(
                    icon = Icons.Default.TouchApp,
                    title = "Сенсорное управление",
                    subtitle = "Настройка виртуальных кнопок",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.Mouse,
                    title = "Управление мышью",
                    subtitle = "Чувствительность и жесты",
                    onClick = { }
                )
            }

            // About
            SettingsSection(title = "О приложении") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Toruka Launcher",
                    subtitle = "Версия 1.0.0-alpha",
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
private fun SettingsSliderItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueText: String,
    onValueChange: (Float) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.fillMaxWidth()
            )
        },
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
private fun RendererSelector(
    selected: RendererType,
    onSelect: (RendererType) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Рендерер",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        RendererType.entries.forEach { renderer ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(renderer) }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = renderer.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = renderer.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RadioButton(
                    selected = selected == renderer,
                    onClick = { onSelect(renderer) }
                )
            }
        }
    }
}
