# build.md

# Objective

Build a fully offline-first Android application that replicates and surpasses the functionality shown in the provided screenshots.

The app is an advanced no-code automation engine for:

* SMS forwarding
* OTP forwarding
* Call forwarding events
* Missed call triggers
* SMS auto-replies
* HTTP webhook triggers
* Offline rule execution
* Multi-condition rule engine
* Dual SIM support
* History logs
* Local-only execution
* No cloud dependency
* Unlimited rules
* Unlimited execution
* Zero subscriptions
* Works without internet

The application must be production-grade, battery-efficient, resilient against Android background restrictions, and designed with modular clean architecture.

---

# Product Name

Suggested:

* RelayX
* Forwardy Pro
* SignalRelay
* AutoForward Engine
* Offline SMS Automator

Use configurable branding.

---

# Core Philosophy

This is NOT a simple SMS forwarding app.

This is:

* A local automation engine
* An event-driven communication router
* A rule execution platform
* A fully offline automation system
* A telecom workflow orchestrator

The app must continue functioning:

* Without internet
* Without Google services
* Without Firebase
* Without proprietary backend
* Without cloud sync
* Without vendor APIs

Everything must execute locally on-device.

---

# Mandatory Requirements

## Must Work Offline

All core features must work:

* without internet
* without server
* without cloud
* without login

Internet is optional only for:

* webhook actions
* update checking
* optional backups

---

# No Usage Limits

The system must support:

* unlimited rules
* unlimited conditions
* unlimited actions
* unlimited logs
* unlimited keywords
* unlimited forwarding targets

No artificial limits.

---

# Android Support

Support:

* Android 8+
* Android 9
* Android 10
* Android 11
* Android 12
* Android 13
* Android 14
* Android 15

Must handle:

* background restrictions
* Doze mode
* battery optimization
* OEM killing
* dual SIM APIs
* notification permission
* SMS permission changes

---

# Tech Stack

## Language

Use:

* Kotlin only

Do NOT use Java.

---

# UI

Use:

* Jetpack Compose
* Material 3
* Adaptive layouts
* Dark mode
* AMOLED mode

---

# Architecture

Use:

* Clean Architecture
* MVVM
* Repository pattern
* UseCases
* StateFlow
* Coroutines
* Dependency Injection

---

# Dependency Injection

Use:

* Hilt

---

# Local Database

Use:

* Room Database

Tables:

* Rules
* Conditions
* Actions
* ExecutionHistory
* SMSLogs
* CallLogs
* Settings
* SIMProfiles
* Analytics

---

# Background Processing

Use:

* Foreground Service
* WorkManager
* BroadcastReceivers
* AlarmManager fallback

The system must survive:

* app swipe kill
* reboot
* memory pressure
* OEM battery savers

---

# Required Permissions

Handle dynamically.

Required:

* RECEIVE_SMS
* READ_SMS
* SEND_SMS
* RECEIVE_MMS
* RECEIVE_WAP_PUSH
* READ_PHONE_STATE
* READ_CALL_LOG
* PROCESS_OUTGOING_CALLS
* RECEIVE_BOOT_COMPLETED
* FOREGROUND_SERVICE
* POST_NOTIFICATIONS
* READ_CONTACTS
* CALL_PHONE (optional)

Explain each permission clearly.

---

# Core Features

# 1. Rule Engine

Users can create rules.

Each rule contains:

* Trigger
* Conditions
* Actions
* Advanced options

Rules execute locally.

Rules must support:

* enable/disable
* priority
* execution counters
* cooldowns
* scheduling
* recursion prevention
* failure retry

---

# 2. Trigger Types

Implement:

## SMS Received

Trigger when:

* SMS received
* MMS received
* RCS fallback received

---

## Call Received

Trigger when:

* incoming call
* answered call
* rejected call

---

## Missed Call

Trigger on missed call.

---

## Outgoing SMS

Optional advanced feature.

---

## Device Events

Optional:

* battery low
* charging
* WiFi connected
* SIM changed
* reboot

---

# 3. Conditions Engine

The conditions system must support:

## Fields

* Sender Number
* Sender Name
* Message Content
* Receive Time
* Day of Week
* SIM Slot
* Contact Group
* Regex Match
* OTP Detected
* Contains URL
* Language
* Length

---

## Operators

Support:

* Contains
* Contains Any
* Contains All
* Equals
* Starts With
* Ends With
* Regex
* Greater Than
* Less Than
* Between
* Not Contains

---

## Logic

Support:

* AND
* OR
* Nested groups
* Complex boolean trees

---

# 4. OTP Detection Engine

Build a real OTP detector.

Not keyword-only.

Implement:

* Regex extraction
* NLP heuristics
* Bank message patterns
* Numeric sequence detection
* Time-sensitive code detection

Detect:

* OTP
* Verification code
* Login code
* Authentication code
* Banking code

Extract:

* code
* sender
* expiry
* app name

---

# 5. Action Engine

Implement actions.

## Send SMS

Forward messages.

Support:

* multiple recipients
* recipient groups
* templates
* variables
* dual SIM selection

Variables:

* sender name
* sender number
* message
* timestamp
* OTP code
* SIM slot

---

## Send Email

Offline queue + SMTP support.

Support:

* attachments
* HTML
* templates

---

## Auto Reply SMS

Support:

* templates
* delayed replies
* smart replies

---

## HTTP Request

Support:

* GET
* POST
* PUT
* PATCH
* DELETE

Support:

* JSON
* form data
* headers
* auth
* retry

---

## Notification Action

Create local notifications.

---

## Clipboard Action

Copy OTP automatically.

---

## Local Storage Action

Save messages locally.

---

# 6. History System

Build complete history logging.

Store:

* execution result
* timestamps
* trigger data
* execution duration
* failure reason
* SIM used
* forwarded targets

Support:

* search
* filters
* export
* delete
* pagination

---

# 7. Dashboard

Build advanced dashboard.

Show:

* total forwarded
* OTP count
* top senders
* most active rules
* execution graphs
* success/failure charts
* SIM usage
* daily trends

Use:

* Compose Charts
* smooth animations
* realtime updates

---

# 8. SIM Management

Support:

* dual SIM
* eSIM
* SIM labels
* SIM selection
* per-rule SIM routing

Handle vendor differences.

---

# 9. Rule Scheduler

Allow rule scheduling.

Examples:

* active only weekdays
* active during work hours
* disabled at night
* temporary rules

---

# 10. Export / Backup

Allow:

* JSON export
* encrypted backup
* import/export rules
* migration

No cloud required.

---

# 11. Security

Implement:

* encrypted database
* biometric lock
* app lock
* hidden notifications
* local-only mode
* anti-loop protection

Never upload SMS automatically.

User must control forwarding.

---

# 12. Battery Optimization

Must be extremely battery efficient.

Requirements:

* no polling
* event-driven architecture
* efficient receivers
* lazy loading
* optimized DB queries
* bounded workers

---

# 13. Reliability

Must handle:

* reboot
* process death
* low memory
* airplane mode
* SIM changes
* SMS delays

Must self-recover.

---

# UI Requirements

Replicate and improve the provided screenshots.

---

# Screens Required

## Dashboard

Show:

* active rules
* execution count
* recent logs
* quick actions
* battery status

---

## Rules List

Features:

* enable/disable toggle
* execution stats
* colored status
* search
* grouping

---

## Rule Builder

Must be visual no-code.

Support:

* drag and drop blocks
* nested conditions
* AND/OR builder
* action chains
* live previews

---

## Condition Builder

Allow:

* field selection
* operator selection
* multi-values
* regex testing

---

## Action Builder

Allow:

* SMS templates
* variable insertion
* recipient management
* SIM selection

---

## History Screen

Show:

* success/failure
* timestamps
* expandable logs
* filters

---

## Settings

Support:

* permissions
* backup
* theme
* startup behavior
* notification config
* optimization guides

---

# Internal System Design

# Event Flow

SMS_RECEIVED Broadcast
→ Receiver
→ Rule Engine
→ Condition Evaluator
→ Action Executor
→ History Logger
→ Notification Manager

---

# Rule Execution Engine

Implement:

* async execution
* coroutine pipeline
* retry policies
* execution queue
* rate limiting
* deduplication

---

# Parsing Engine

Implement robust parsers:

* OTP extractor
* URL extractor
* bank detection
* sender normalization
* Unicode-safe parsing

---

# Recommended Libraries

## Android

* Hilt
* Room
* WorkManager
* Navigation Compose
* Accompanist
* Kotlin Serialization
* Coil

---

## Charts

* Vico
  or
* MPAndroidChart Compose wrapper

---

## Logging

* Timber

---

## Encryption

* AndroidX Security Crypto
* SQLCipher

---

# Folder Structure

app/
├── core/
├── data/
├── domain/
├── presentation/
├── receivers/
├── services/
├── workers/
├── database/
├── ruleengine/
├── parsers/
├── analytics/
├── notifications/
├── security/
├── backup/
└── utils/

---

# Database Schema

Define complete Room schema.

Entities:

## RuleEntity

Fields:

* id
* name
* enabled
* priority
* triggerType
* createdAt
* updatedAt
* cooldown
* schedule

---

## ConditionEntity

Fields:

* ruleId
* field
* operator
* values
* groupId
* logicalOperator

---

## ActionEntity

Fields:

* ruleId
* actionType
* configJson
* executionOrder

---

## HistoryEntity

Fields:

* ruleId
* status
* executionTime
* payload
* failureReason

---

# Performance Requirements

Cold start:

* under 2 seconds

Rule execution:

* under 200ms average

SMS forwarding:

* near real-time

Memory usage:

* optimized for low-end devices

---

# Advanced Features

# Smart OTP Classification

Classify:

* banking OTP
* shopping OTP
* social OTP
* government OTP

---

# AI Suggestions

Offline-only heuristics.

Suggest:

* common rules
* auto grouping
* spam filters

Do not use cloud AI.

---

# Spam Filtering

Detect:

* spam senders
* scam messages
* repeated spam

---

# Template Engine

Support:

* variables
* formatting
* conditionals
* timestamp formatting

Example:

[OTP Alert]
Bank: {{sender}}
OTP: {{otp}}
Time: {{time}}

---

# OEM Compatibility Layer

Implement guides and detection for:

* Xiaomi
* Samsung
* Oppo
* Vivo
* OnePlus
* Realme
* Motorola

Provide battery optimization instructions.

---

# Testing Requirements

Write:

* unit tests
* integration tests
* receiver tests
* database tests
* UI tests
* rule engine tests

Target:

* 80%+ coverage

---

# CI/CD

Use GitHub Actions.

Pipeline:

* lint
* build
* test
* static analysis
* release APK

---

# Documentation Required

Generate:

* architecture docs
* API docs
* setup docs
* permission rationale docs
* OEM troubleshooting docs
* backup format docs

---

# Build Targets

Generate:

* debug APK
* release APK
* signed AAB

---

# Non-Functional Requirements

The app must be:

* scalable
* modular
* maintainable
* extensible
* testable
* offline-first
* privacy-focused
* performant
* resilient

---

# Important Android Constraints

GitHub Copilot must carefully handle:

## SMS Restrictions

Android limits SMS APIs.

Implement compatible fallback behavior.

Must support:

* default SMS app mode
* non-default SMS app mode

Explain tradeoffs.

---

# Background Restrictions

Handle:

* Android 12 foreground service limits
* Android 13 notification permissions
* Android OEM task killing

---

# Privacy

No telemetry.

No analytics upload.

No user tracking.

Everything local-first.

---

# Deliverables

Copilot must generate:

1. Full Android Studio project
2. Complete source code
3. Room schema
4. Rule engine
5. UI screens
6. Broadcast receivers
7. Foreground services
8. Settings system
9. Export/import
10. Tests
11. GitHub Actions CI
12. README
13. Architecture diagrams
14. Production-ready Gradle setup
15. Signed release configuration

---

# Final Goal

Build a production-grade offline automation platform for Android communications.

The system should feel like:

* Tasker + SMS Forwarder + Automation Engine + OTP Manager

but:

* easier
* offline-first
* modern
* unlimited
* privacy-focused
* stable
* battery efficient

The app must be capable of running for months continuously with high reliability.
I created a comprehensive `build.md` specification that GitHub Copilot can use as a full-system blueprint to build the entire offline-first SMS automation and forwarding platform shown in your screenshots — including:

* Rule engine
* OTP detection
* Offline execution
* Dual SIM handling
* HTTP actions
* Background services
* History logs
* Compose UI
* Room DB
* Hilt DI
* Clean architecture
* Battery optimization
* Android restrictions handling
* Unlimited rules/actions/logs
* Production-grade structure

It is written as a detailed engineering specification rather than a simple feature list, so Copilot can generate a much larger percentage of the actual system correctly.
