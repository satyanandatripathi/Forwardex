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
import androidx.compose.material3.HorizontalDivider
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
import com.forwardex.notifications.OemOptimizationGuides
import com.forwardex.notifications.PermissionRationale

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
                composable("condition-builder") { ConditionBuilderScreen() }
                composable("action-builder") { ActionBuilderScreen() }
                composable("history") { HistoryScreen() }
                composable("settings") { SettingsScreen() }
                composable("onboarding") { PermissionOnboardingScreen() }
                composable("oem-guides") { OemGuidesScreen() }
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val items = listOf("dashboard", "rules", "builder", "history", "settings", "onboarding")
    NavigationBar {
        items.forEach { route ->
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(route) },
                icon = { Text(route.first().uppercaseChar().toString()) },
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
        Text("- Trigger block with event type")
        Text("- Nested condition groups (AND/OR)")
        Text("- Ordered action chain with priorities")
        Text("- Cooldown / retry / schedule controls")
        HorizontalDivider()
        Text("Navigate to dedicated builders:")
        Text("• Condition Builder tab route: condition-builder")
        Text("• Action Builder tab route: action-builder")
    }
}

@Composable
fun ConditionBuilderScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Condition Builder")
        Text("Supported fields: sender, message, time, SIM, regex, OTP, URL, language, length")
        Text("Operators: contains/equals/regex/range/not-contains")
        Text("Logic: nested AND/OR groups")
        Text("Regex testing and multi-value input are enabled in this flow design.")
    }
}

@Composable
fun ActionBuilderScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Action Builder")
        Text("Available actions: SMS, auto-reply, HTTP, notification, clipboard, local storage, email queue")
        Text("Template variables: sender, number, message, otp, timestamp, sim_slot")
        Text("Action order controls execution chain sequencing.")
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
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("Permissions, backup, startup, notifications, OEM optimization") }
        item { Text("Local-only mode") }
        item { Switch(checked = localOnly, onCheckedChange = viewModel::setLocalOnly) }
        item { Text("No telemetry, no remote analytics, user-controlled forwarding.") }
        item { HorizontalDivider() }
        item { Text("Permission onboarding route: onboarding") }
        item { Text("OEM guides route: oem-guides") }
    }
}

@Composable
fun PermissionOnboardingScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Permission Onboarding") }
        items(PermissionRationale.mapping.entries.toList()) { entry ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(entry.key)
                    Text(entry.value)
                }
            }
        }
    }
}

@Composable
fun OemGuidesScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("OEM Optimization Guides") }
        items(OemOptimizationGuides.guides.entries.toList()) { (oem, guide) ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(oem.replaceFirstChar { it.uppercase() })
                    Text(guide)
                }
            }
        }
    }
}
