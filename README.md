# BarAudio

### Overview

[BarAudio](https://baraud.io/) is an Android app available on the [Play Store](https://play.google.com/store/apps/details?id=com.sommerengineering.baraudio) as a monthly subscription. It's written in Kotlin using MVVM architecture with Jetpack Compose, Firebase, and Google Cloud Functions as the backend endpoint. The app provides audible realtime alerts for financial securities including stocks, futures, options, and crypto.

Financial market exchanges, brokers, and third party platforms emit data in realtime. To be an effective trader, digital charts must be constantly monitored throughout the trading day. This causes eye strain and mental fatigue. Audio alerts from a mobile device provide an alternative source of communication, allowing investors to step away from their screens with confidence.

### Intended User

The intended user of this app is a financial day trader. This person buys and sells stocks, forex, commodities, or other instruments multiple times in a day. This includes a casual trader with sporadic participation, or a professional investor whose derives their income from market cycles.

### Primary Features

- Worldwide exchange data in realtime
- Spoken audio notifications customized by language, voice, speed, and pitch
- Modern UI designed for Android 16
- Sign-in with Google or GitHub
- Secure customized webhook urls hosted on Google Cloud
- Unlimited webhook requests
- Unlimited message storage

### Monetization

A free trial is offered for one week, then converts to a rolling subscription of $5.99 per month. BarAudio does not contain advertisements.

### Technical Notes

- Jetpack Compose with Material Design 3
- MVVM architecture
- Dagger Hilt dependency injection
- Google Cloud Functions for webhook endpoint
- Firebase Cloud Messaging for notifications
- Firebase Realtime Database for local and cloud storage
- Google, GitHub federated authentication providers
- Text-to-speech engines for audible alerts
- Compatible with API 36, Android 16

### User Experience
<br/><br/>
![](readme/1r.png)
<br/><br/>
![](readme/2r.png)
<br/><br/>
![](readme/3r.png)
<br/><br/>
![](readme/4r.png)