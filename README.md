# Unsleepify
An alarm app without alarms. Just cool beats and nice melodies to rock you out of your slumber.

Uses Spotify, who may frown upon such use of their API. As such this is not for Play store distribution.

## TODO
- don't use set text - replace resource text with placeholders
- skip song, because that one Sigur Ros song is just too much
- Notification before and during alarm for easy access, no surprises, good experience
- Show art for song being played - either in notification like Spotify, or whole background
- Configure a playlist in GUI rather than a hardcoded list

## Accomplished TODOs
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
