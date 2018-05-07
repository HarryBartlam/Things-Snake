# Things Snake  
An **Android Things** project to play snake on a custom display, and allow control by another Android device using **Google Nearby**.  
 
Control App		|	Snake Things App
:-------------------------:|:-------------------------:
<img src="https://i.imgur.com/qpKEHTc.png" height="400" alt="Gif Showing demo of the app"/> | <img src="https://i.imgur.com/Q0IyC0Q.gif" height="400" alt="Gif Showing demo of the app"/>
The Snake game can either be controlled by a USB keyboard or the control app, Once the control app has started simple connect to the Things device from the available devices


This project is structured with a modular design so that the Android Things app and Controller App share core components.

<img src="https://i.imgur.com/obO6W6S.jpg" height="400" alt="Raspberry Pi And custom Display "/> 


## Display  
  
The display for this project is made up of 16 MAX7219 displays, which came in segments of 4.  
<img src="https://i.imgur.com/jnbUYUe.jpg" height="250" alt="4 segment MAX7219 display"/>  
  
  
I loosely fitted these together   
<img src="https://i.imgur.com/3cbGhnF.gif" height="250" alt="MAX7219 display together"/>  
  
  
Then I soldered the displays together  
<img src="https://i.imgur.com/RHMeuys.jpg" height="250" alt="MAX7219 display soldered together"/>  

And finally I designed (in AutoCad inventor) and 3d printed the case to hold all the displays together  
<img src="https://i.imgur.com/pbDncAX.gif" height="250" alt="3d printing display case"/>  
 
The finished display
<img src="https://i.imgur.com/jatydKh.jpg" height="300" alt="MAX7219 display finished"/>  

Control of the display has been done by modifying a library found [here](https://github.com/Nilhcem/ledcontrol-androidthings).  This was needed due to the display limit of this library, see implemented code [here](https://github.com/offbow/Things-Snake/blob/master/max72xx/src/main/java/com/simplyapp/max72xx/driver/MAX72xxLEDController.java)

Before I was able to get any displays, I created custom views to implement the game and connection logic of the project ["data/src/main/java/com/simplyapp/data/ui"](https://github.com/offbow/Things-Snake/tree/master/data/src/main/java/com/simplyapp/data/ui)  
  
## Architecture  
The code created for this project uses the MVP design architecture and a module approach 
### Modules
This project is made of 2 core modules **base > data**
- **base** 			(Kotlin extension and Architecture for app context)
- **data** 				(Project shared models, custom views and small network Util)
- **app** 					(Activities, layouts and logic for control app)
- **max72xx** 					(Driver class for the MAX7219)
- **slave** 					(Activities, layouts and logic for Snake/Things app)

Control app **base > data > app**

Snake/Things app **(base > data) && (max72xx) > slave**

### Libraries  
- Kotlin  
- Android Things 0.7  
- RXjava 2  
- Room  
- Timber