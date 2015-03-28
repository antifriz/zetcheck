# ZETcheck
Public transport Android app for trams and buses in Zagreb, Croatia

> *App no longer in function due to restricted access to required data*

Description
-----------

Considering **Zagreb** has slightly less than a million citizens it lacks an app which
would enable the user to acquire the exact location, schedule and calculated approximate arrival time of
public transport vehicles. 

That is how **ZETcheck**, came to life. In just a few days since it was
published, the app was downloaded by **20 000 Android users** (2% of Zagreb's entire population, and the
**iOS** version wasn't even released yet!). 

The app was covered by almost every Croatian media, but as one of them said, in our country bureaucracy destroys everything that's worth anything. They disliked the idea so the app was shut down in a week. Needless to say that until this day nothing similar has been made.

Client-server architecture
--------------------------

Based on client-server architecture, every Android device acts like a client requesting updates from cache server in specified time intervals.

Cache server fetches data from well-known server, caches it, optimizes and distributes it to clients.

Chunks of data (approx. 10 kB) from well-known server were requested at approx. 20 sec interval. Considering that at peak time there were around 1000 users per minute, caching was extremely efficient 

License
-------

This software is licensed under the Apache License, Version 2.0

Contact
-------

Feel free to contact me at any time

* [Facebook Page](https://www.facebook.com/ZETcheck)
* [Google play](https://play.google.com/store/apps/details?id=com.zetcheck)
* [Twitter](https://twitter.com/zetcheck)
* [Gmail](mailto:zetcheck@gmail.com)
