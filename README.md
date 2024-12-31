# BarAudio

### Overview

[BarAudio](https://baraud.io/) is on the [Play Store](https://play.google.com/store/apps/details?id=com.sommerengineering.baraudio) as a paid subscription, written in Kotlin. It leverages Compose with Material Design 3, updated for API 35, Android 15.

BarAudio provides audible market notifications to financial day traders. Product price, volume, and associated parameters are emitted from various exchanges once per minute in realtime. To effectively trade on this time scale, the investor must visually monitor digital charts continuously throughout the day. Audio alerts from a mobile device provide an alternative, allowing traders to step away from their screens with confidence.

### Intended User

The intended user of this app is a securities day trader. This person is buying and selling stocks, forex, commodities, or other financial instruments over short time intervals within a single day. This can be a casual investor with sporadic investments, or a professional trader with primary income source derived from the market cycle.

### Primary Features

- All stock exchange data in reatime
- Customizable audio notifications for indicators, or continuous updates
- UX using Compose and Material Design 3
- Google and GitHub federated authentication providers
- Webhook endpoint using Google Cloud Functions
- Firebase realtime database persists user alert messages

### Monetization

This app does use advertisements. Instead, a one week free trial is offered, followed by a rolling subscription of $5.99 USD per month. The subscription unlocks unlimited webhook requests and unlimited storage.

### Technical Notes

- ...

### User Experience
<br/><br/>
![](readme/1r.png)
<br/><br/>
![](readme/2r.png)
<br/><br/>
![](readme/3r.png)
<br/><br/>
![](readme/4r.png)