# Vertically Paged Music Player App 
This is a vertically paged music player for Android that fetches from Suno AI's test endpoint. Made by Richard Massimilla. 

## Core features 
* **Music player**: Media3 Exoplayer will play music fetched from the endpoint. 
* **Infinite scroll**: The user can scroll back and forth among all songs available at the endpoint, fetched in batches of 10-song pages. 
* **Media controls**: Custom play/pause button, replay button, and seekbar were implemented with Material 3 components in favor of ExoPlayer PlayerView. 
* **Background playback**: Player works in background with default media notification. 

## Bells and whistles 
* **Autoplay**: When songs end, the player will automatically play the next song and scroll the pager with a dampened spring animation. 
* **Share**: The share button launches an Android sharesheet to share the video URL. 
* **Like/dislike**: Functional like/dislike buttons that could easily be hooked to a POST endpoint, if one existed.
* **Light/dark theming**: Compose dynamic theming was used to easily implement view components in both light and dark mode. 
* **Orientable**: Rotating between portrait and landscape is seamless.
* **Compatible with Overview Screen**: Ending the task from the overview screen will stop the media service. It will be restarted when the app is reopened.
* **Room for growth**: Architected to be scalable. View layer is wrapped in a navigation layer. API is consumed through Hilt/Dagger dependency injection. Player is never re-initialized unless the user leaves the screen. 
