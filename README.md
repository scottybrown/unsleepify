# Unsleepify
An alarm app without alarms. Just cool beats and nice melodies to rock you out of your slumber.

Uses Spotify, who may frown upon such use of their API. As such this is not for Play store distribution.

## TODO
- don't use set text - replace resource text with placeholders
- Notification before and during alarm for easy access, no surprises, good experience
- Configure a playlist in GUI rather than a hardcoded list - easy way is a textbox that a playlist id can be pasted into.
	- Hard way is to use https://api.spotify.com/v1/search to search for playlists, display them, and allow the user to choose one
	- this needs auth, which would be taken from the old songplayingactivity code - it was successfully doing auth, it just couldn't do it while the phone was locked. Not an issue here.
	- also ideally it would display the album art for each playlist, or some identifying factor to differentiate those that share the same name

## Accomplished TODOs
- Show art for song being played - either in notification like Spotify, or whole background - i get this for free by yielding control to the spotify app
- skip song, because that one Sigur Ros song is just too much - i get this for free by yielding control to the spotify app
- Properly format time of currently set alarm
- Improve UI for text and buttons
- more tests
- Error handling - hitting Spotify API can bring all sorts of issues - I'd like those to at least be debuggable
- replace all use of calendar/date with Java8 classes
- Alarm uses alarm volume not main volume
- icon
- A button to disable alarm
- When an alarm is set, report how long until it occurs. So that user is aware if they accidentally set it for 2hr from now
- Show time of currently set alarm
- Make sure it loops, if it gets to the end of the playlist
- Shows current alarm volume when set
- Runs if phone locked, but stops when minimized/back button is pressed
- Displays song name
- Configure alarm time using gui, not code lol
- Make default time be 8h from "now"
- Retrieve a playlist, play the songs
