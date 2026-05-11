package com.forwardex.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forwardex.database.HistoryEntity
import com.forwardex.database.RuleEntity
import com.forwardex.domain.HistoryRepository
import com.forwardex.domain.RuleRepository
import com.forwardex.domain.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForwardexViewModel @Inject constructor(
    private val ruleRepository: RuleRepository,
    historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val rules: StateFlow<List<RuleEntity>> = ruleRepository.observeRules().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val history: StateFlow<List<HistoryEntity>> = historyRepository.observeRecent().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _localOnly = MutableStateFlow(true)
    val localOnly = _localOnly.asStateFlow()

    init {
        viewModelScope.launch {
            _localOnly.value = settingsRepository.get("local_only_mode")?.toBooleanStrictOrNull() ?: true
        }
    }

    fun toggleRule(rule: RuleEntity, enabled: Boolean) {
        viewModelScope.launch {
            ruleRepository.saveRule(rule.copy(enabled = enabled, updatedAt = System.currentTimeMillis()), emptyList(), emptyList())
        }
    }

    fun setLocalOnly(value: Boolean) {
        _localOnly.value = value
        viewModelScope.launch { settingsRepository.put("local_only_mode", value.toString()) }
    }
}
