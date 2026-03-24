# PROJECT CONTEXT

This is an Android application written in Kotlin.

Architecture:

* MVVM with ViewModel
* Repository layer owns data flow and coroutines
* Room is the single source of truth for messages
* Firebase Realtime Database provides remote data
* Voice delivery via TextToSpeech (TTS)

Core product:

* Real-time trading signals
* Voice-first delivery when app process is alive
* No notifications or speech when app is killed
* On restart, Room is hydrated from backend (no speech during backfill)

Key principle:

* Voice delivery is primary, UI is secondary

# DEVELOPMENT RULES

* Make minimal, surgical changes
* Do not introduce new architectural layers
* Respect existing MVVM + Repository pattern
* Do not move business logic into UI
* Prefer coroutine-based solutions over callbacks
* Do not block main thread

# VOICE DELIVERY RULES (CRITICAL)

* TTS only runs when app process is alive
* Use foreground service for background speech
* Foreground notification must be temporary and removed after speech
* Never trigger speech during cold-start hydration
* Always prepend asset spokenName before message text

# DATA FLOW RULES

* Room is the source of truth for UI
* Firebase is upstream only
* All writes go through repository
* UI observes Flow from Room, never Firebase directly

# MESSAGE HANDLING

* Messages from backend contain raw signal text only
* Do not include asset name in message payload
* UI may prepend asset displayName depending on feed mode
* TTS must always prepend asset spokenName

# FEED MODES

* Linear mode: UI prepends asset displayName
* Grouped mode: no prefix in message row
* TTS behavior is unchanged across modes

# WHEN MODIFYING CODE

1. Identify the smallest possible change
2. Implement change
3. Verify:
    * no duplicate messages
    * no speech triggered incorrectly
    * no lifecycle leaks
4. Prefer clarity over cleverness

# COMMON TASKS

## If working on message delivery:

* Check repository flow
* Ensure Room write occurs before UI update
* Ensure TTS trigger happens only after insert

## If working on TTS:

* Ensure app process is alive
* Ensure foreground service is used if backgrounded
* Ensure service stops immediately after speech

## If working on Firebase:

* Do not bind UI directly to Firebase
* Always map data into Room entities first

# DO NOT

* Do not trigger speech from UI layer
* Do not bypass repository
* Do not introduce global mutable state
* Do not add unnecessary abstractions
* Do not refactor unrelated code

# SUCCESS CRITERIA

A change is correct if:

* Signals appear exactly once in UI
* Speech plays exactly once when expected
* No speech occurs when app is killed
* App lifecycle remains stable
* No regression in existing behavior
