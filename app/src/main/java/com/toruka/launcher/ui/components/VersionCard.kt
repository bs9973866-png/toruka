package com.toruka.launcher.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.toruka.launcher.data.model.VersionInfo
import com.toruka.launcher.data.model.VersionType

@Composable
fun VersionCard(
    version: VersionInfo,
    isInstalled: Boolean,
    isLatestRelease: Boolean,
    isLatestSnapshot: Boolean,
    onInstall: () -> Unit,
    onDelete: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val versionType = VersionType.fromString(version.type)
    val typeColor = when (versionType) {
        VersionType.RELEASE -> MaterialTheme.colorScheme.primary
        VersionType.SNAPSHOT -> MaterialTheme.colorScheme.tertiary
        VersionType.BETA -> MaterialTheme.colorScheme.secondary
        VersionType.ALPHA -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        onClick = onSelect,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = version.id,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (isLatestRelease) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text("RELEASE", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (isLatestSnapshot) {
                        Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                            Text("SNAPSHOT", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = versionType.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = typeColor
                )
                
                Text(
                    text = version.releaseTime.substring(0, 10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isInstalled) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Установлено",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Button(onClick = onInstall) {
                    Text("Установить")
                }
            }
        }
    }
}

@Composable
fun VersionTypeChip(
    type: VersionType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(type.displayName) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
