package com.forwardex.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.forwardex.database.HistoryEntity
import com.forwardex.database.RuleEntity

@Composable
fun ForwardexRoot() {
    MaterialTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomBar(navController) }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(padding)
            ) {
                composable("dashboard") { DashboardScreen() }
                composable("rules") { RulesScreen() }
                composable("builder") { RuleBuilderScreen() }
                composable("history") { HistoryScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val items = listOf("dashboard", "rules", "builder", "history", "settings")
    NavigationBar {
        items.forEach { route ->
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(route) },
                icon = {},
                label = { Text(route.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
fun DashboardScreen(viewModel: ForwardexViewModel = hiltViewModel()) {
    val rules by viewModel.rules.collectAsState()
    val history by viewModel.history.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Active Rules: ${rules.count { it.enabled }}")
        Text("Execution Count: ${rules.sumOf { it.executionCount }}")
        Text("Recent Logs: ${history.size}")
        Text("Battery Strategy: Event-driven, no polling")
    }
}

@Composable
fun RulesScreen(viewModel: ForwardexViewModel = hiltViewModel()) {
    val rules by viewModel.rules.collectAsState()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(rules) { rule -> RuleCard(rule, onToggle = viewModel::toggleRule) }
    }
}

@Composable
private fun RuleCard(rule: RuleEntity, onToggle: (RuleEntity, Boolean) -> Unit) {
    val enabled = remember(rule.enabled) { mutableStateOf(rule.enabled) }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(rule.name)
            Text("Trigger: ${rule.triggerType}")
            Text("Priority: ${rule.priority} | Executions: ${rule.executionCount}")
            Switch(checked = enabled.value, onCheckedChange = {
                enabled.value = it
                onToggle(rule, it)
            })
        }
    }
}

@Composable
fun RuleBuilderScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Visual Rule Builder")
        Text("- Trigger block")
        Text("- Nested AND/OR conditions")
        Text("- Ordered action chain")
        Text("- Live preview")
        Text("Drag/drop block interactions are scaffolded for iterative implementation.")
    }
}

@Composable
fun HistoryScreen(viewModel: ForwardexViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(history) { item -> HistoryCard(item) }
    }
}

@Composable
private fun HistoryCard(item: HistoryEntity) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("Rule #${item.ruleId} - ${item.status}")
            Text("Duration: ${item.executionTimeMs}ms")
            Text("Time: ${item.timestamp}")
            item.failureReason?.let { Text("Failure: $it") }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: ForwardexViewModel = hiltViewModel()) {
    val localOnly by viewModel.localOnly.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Permissions, backup, startup, notifications, OEM optimization")
        Text("Local-only mode")
        Switch(checked = localOnly, onCheckedChange = viewModel::setLocalOnly)
        Text("No telemetry, no remote analytics, user-controlled forwarding.")
    }
}
