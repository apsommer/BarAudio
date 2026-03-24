# BarAudio

## Overview

BarAudio is a production Android application that delivers real-time financial market signals through a voice-first interface. The system is designed for high-frequency intraday traders who need to stay informed without continuously monitoring charts.

The app ingests external trading signals via webhook endpoints, persists them locally, and delivers them as spoken alerts using a lifecycle-aware architecture. The core design prioritizes reliability, low latency, and predictable behavior across foreground, background, and cold-start states.

---

## Problem

Active traders must constantly monitor charts across multiple instruments and timeframes. This leads to:

* Cognitive overload from continuous visual scanning
* Missed opportunities during attention lapses
* Poor mobility (tethered to screens)

Existing alerting systems are typically:

* Visual-first (notifications, charts)
* Noisy or inconsistent
* Not lifecycle-aware (duplicate alerts, missed delivery)

---

## Solution

BarAudio introduces a **voice-first delivery system**:

* Signals are spoken in real time using Text-to-Speech
* Users can step away from screens while maintaining awareness
* Delivery is deterministic and tied to app lifecycle state

The system ensures:

* No duplicate alerts
* No missed alerts during reconnect
* Clear separation between live delivery and historical replay

---

## Architecture

### Client (Android)

* **Language:** Kotlin
* **UI:** Jetpack Compose (Material 3)
* **Architecture:** MVVM + Repository pattern
* **Persistence:** Room (single source of truth)
* **Dependency Injection:** Dagger Hilt

### Backend

* **Webhook ingestion:** Google Cloud Functions
* **Realtime sync:** Firebase Realtime Database
* **Push delivery:** Firebase Cloud Messaging

---

## Data Flow

1. External platform sends signal → webhook endpoint
2. Cloud Function validates and writes to Firebase
3. Client receives update (FCM or sync)
4. Repository writes message to Room
5. UI observes Room via Flow
6. TTS is triggered after successful persistence

**Key invariant:**
Room is always the source of truth. No UI or TTS logic reads directly from Firebase.

---

## Voice Delivery System

The app uses a **process-aware delivery model**:

* **App alive (foreground/background):**

    * Signals are spoken immediately
    * Background speech uses a temporary foreground service
* **App killed:**

    * No speech or notifications (by design)
    * Messages are stored and delivered passively on next launch

Additional guarantees:

* Speech occurs exactly once per message
* No speech during cold-start hydration
* Asset name is always prepended at the TTS layer

---

## Key Engineering Decisions

### 1. Voice-first architecture

Speech is treated as the primary output, not an enhancement to UI.
This simplifies user interaction and reduces cognitive load.

### 2. Repository-owned data flow

All reads/writes go through the repository layer:

* Prevents duplication bugs
* Centralizes business logic
* Improves testability

### 3. Local-first consistency (Room)

Room acts as the single source of truth:

* Enables deterministic UI rendering
* Decouples backend from presentation
* Supports offline and replay behavior

### 4. Lifecycle-aware delivery

Speech behavior is explicitly tied to process state:

* Avoids background inconsistencies
* Eliminates duplicate notifications
* Ensures predictable UX

---

## Features

* Real-time signal ingestion via secure webhooks
* Voice alerts with configurable language, pitch, and speed
* Multi-asset support (stocks, futures, crypto, forex)
* Grouped and linear feed modes
* Federated authentication (Google, GitHub)
* Unlimited message retention

---

## Monetization

* 1-week free trial
* Subscription: $5.99/month
* No advertisements

---

## Engineering Focus

This project emphasizes:

* Real-time data pipelines
* Event-driven architecture
* Mobile lifecycle correctness
* Low-latency user feedback systems
* Clean separation of concerns

---

## Links

* Website: https://baraud.io/
* Play Store: https://play.google.com/store/apps/details?id=com.sommerengineering.baraudio

---

## Summary

BarAudio is not just a mobile app—it is a real-time signal delivery system designed around a voice-first interaction model. The architecture prioritizes correctness, determinism, and user attention efficiency in a domain where timing and clarity are critical.
