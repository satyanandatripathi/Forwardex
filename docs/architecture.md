# Architecture Overview

Forwardex follows Clean Architecture + MVVM with these layers:

- `presentation`: Compose UI + ViewModels
- `domain`: use cases and repository contracts
- `data`: repository implementations
- `database`: Room entities and DAOs
- `ruleengine`: condition evaluator + action executor + orchestrator
- `receivers/services/workers`: Android platform integration
- `parsers/security/backup/analytics/notifications`: support modules

Event flow:
`BroadcastReceiver -> RuleEngine -> ConditionEvaluator -> ActionExecutor -> HistoryRepository`
