# Unsleepify
An alarm app without alarms. Just cool beats and nice melodies to rock you out of your slumber.

Uses Spotify, who have a history of banning such use of their API. This is not for app stores - this is for me and mine.

## TODO
- Alarm uses alarm volume not main volume
- Properly format time of currently set alarm
- My spinners to select time are cool, but an input box would be nicer
- icon
- Notification before and during alarm for easy access, no surprises, good experience
- Show art for song being played - either in notification like Spotify, or whole background
- Configure a playlist in GUI rather than a hardcoded list
- Error handling - hitting Spotify API can bring all sorts of issues - I'd like those to at least be debuggable
- more tests

## Accomplished TODOs
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
