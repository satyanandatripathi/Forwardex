package com.forwardex.domain

import com.forwardex.ruleengine.RuleEngine
import javax.inject.Inject

class ProcessTriggerUseCase @Inject constructor(
    private val ruleEngine: RuleEngine
) {
    suspend operator fun invoke(event: TriggerEvent) = ruleEngine.processEvent(event)
}
