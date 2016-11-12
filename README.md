# InternetRadio
After taking the Coursera courses Programming Mobile Applications for Android Handheld Systems Part 1 and 2 I wanted to apply my Android knowledge by creating this simple internet radio app. It streams the music broadcasted by radio station [De Blauwe Tegel](http://www.radiodeblauwetegel.nl), which is a hobby project of one of my neighbours. This radio station does not reflect my taste of music, but is was fun creating the app.

#Functionality
The app connects to a Shoutcast server in a background service and is able to play music on the background while other activities are performed. When the MainActivity runs, it polls the shoutcast server for track information of the current and next track every 30 seconds. An icon in the notification bar gives access to the MainActivity when the music plays in the background. The activity has a link to the stations website
