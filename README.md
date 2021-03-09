NativeScreen
============
A useless project developed as a result of having too much free time.
The main idea was playing real AAA games and streaming videos in Minecraft.

The plugin has two main features:
* high-performance rendering on [maps](https://minecraft.gamepedia.com/Map)  
  _Manages to generate and compress more than 3600 packets per second (30FPS, 15x8 maps, equivalent of 1920x1080)_
* catching a player's input using "vehicle steer" packet

Renderers
=========
A _renderer_ is responsible for generating an image that is shown for the players.
NativeScreen has several built-in renderers.
Each renderer implements interface `IMapRenderer`.

RandomColorRenderer
-------------------
A simple renderer, mainly useful for performance testing, fills a whole screen with a solid random color.

FullScreenRenderer
------------------
This renderer uses Java's `Robot` API to take a screenshot of a whole screen.

NativeWindowRenderer
--------------------
![IntelliJ IDEA in Minecraft](/Resources/intellij_idea.png?raw=true "IntelliJ IDEA in Minecraft")
Calls `winapi` through `java native access` to take the content of the specified window.

VideoRenderer
-------------
![YouTube in Minecraft](/Resources/video_renderer.png?raw=true "YouTube in Minecraft")
Uses java binding to the `FFmpeg` to decode a video file and play it in the game.