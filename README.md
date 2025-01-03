# BarAudio

### Overview

[BarAudio](https://baraud.io/) is an app for Android available on the [Play Store](https://play.google.com/store/apps/details?id=com.sommerengineering.baraudio) as a monthly subscription. It's written primarily in Kotlin, using Compose declarative UI, Firebase products, and a serverless backend with Google Cloud.

The app provides audible notifications to day traders in the financial sector. Price, volume, and other top-level metrics are emitted from exchanges in realtime. To effectively trade on this timeframe, an investor must continously monitor digital charts throughout the day. Listening to audio alerts from a mobile device is an alternative method of obtaining information, allowing traders to step away from their screens with confidence.

### Intended User

The intended user of this app is a securities day trader. This person is buying and selling stocks, forex, commodities, or other financial instruments over a short time interval within a single day. This can be a casual trader with sporadic participation, or a professional investor whose primary income derives from market cycles.

### Primary Features

- All exchange data available in reatime
- Audio notifications customized by language, voice, speed, and pitch
- Modern UI designed for Android 15
- Sign-in with Google or GitHub
- Unique webhook urls secured by Google Cloud
- Unlimited webhook requests
- Unlimited message storage

### Monetization

A free trial is offered for one week, then converts to a rolling subscription of $5.99 per month. This app does not contain advertisements.

### Technical Notes

- Compose using Material Design 3
- MVVM architecture
- Koin dependency injection
- Webhook endpoint deployed on Google Cloud Functions
- Firebase Cloud Messaging for notifications
- Firebase Realtime Database for local and cloud storage
- Google and GitHub federated authenication providers
- Text-to-speech engines for audible alerts
- Compatible with API 35, Android 15

### User Experience
<br/><br/>
![](readme/1r.png)
<br/><br/>
![](readme/2r.png)
<br/><br/>
![](readme/3r.png)
<br/><br/>
![](readme/4r.png)