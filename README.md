# BarAudio

### Overview

[BarAudio](https://baraud.io/) is currently released on the [Play Store](https://play.google.com/store/apps/details?id=com.sommerengineering.baraudio) as a paid subscription, written in Java.

BarAudio is a mobile application that provides audible notifications to stock day traders. While the market is active, stock price and associated parameters are released from the various exchanges once per minute in realtime. To effectively trade on this time scale an investor must continuously monitor updating digital charts throughout the day. Audio alerts from a mobile device provide an alternative, allowing traders to step away from their screens with confidence.

### Intended User

The intended user of this app is a securities day trader. This person is buying and selling stocks, forex, commodities, or other financial instruments over short time intervals encapsulated within a single day. This can be a casual investor with sporadic investments, or a professional day trader whose primary income source derives from the market cycle.

### Primary Features

- NYSE, NASDAQ, and IEX stock exchange data in real-time
- Customizable audio notifications for indicator sets, or continuous updates
- UX adhering the Material Design specification
- Google, Facebook, and GitHub login authentication using Firebase

### Monetization

One of the guiding principles of this app is to avoid the use of ads. A one week free trial is offered, followed by a monthly subscription of $6.99 USD per month. 10 symbols and 10 indicators are allowed as a reasonable bound for edge cases. The primary utility of the app as an audio notification tool loses efficacy in excess of 100 parameters.

### Technical Notes

- User authentication and profile customization using Firebase from Google Cloud Platform.
- Google and Facebook sign-in providers
- Firestore database ORM.
- Text-to-speech processing of realtime stock data from RESTful API’s.
- A foreground service complements the modern and Material Design UX/UI, for API > 21.

### User Experience
<br/><br/>
[![](demo/UX.gif)](https://www.youtube.com/watch?v=fMMpsd_z_Cg)
<br/><br/>
![](demo/port_login.png)
<br/><br/>
![](demo/port_master.png)
<br/><br/>
![](demo/port_configure.png)
<br/><br/>
![](demo/land_exchanges.png)