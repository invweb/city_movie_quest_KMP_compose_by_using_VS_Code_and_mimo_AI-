package com.questcity.ui.screens.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.questcity.ui.i18n.LocalStrings

enum class MenuItem(val icon: ImageVector) {
    QUESTS(Icons.Default.List),
    SETTINGS(Icons.Default.Settings),
    ABOUT(Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onMenuItemClick: (MenuItem) -> Unit
) {
    val strings = LocalStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.appName) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = strings.appDescription,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            MenuItemCard(
                title = strings.quests,
                icon = MenuItem.QUESTS.icon,
                onClick = { onMenuItemClick(MenuItem.QUESTS) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuItemCard(
                title = strings.settings,
                icon = MenuItem.SETTINGS.icon,
                onClick = { onMenuItemClick(MenuItem.SETTINGS) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuItemCard(
                title = strings.about,
                icon = MenuItem.ABOUT.icon,
                onClick = { onMenuItemClick(MenuItem.ABOUT) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
