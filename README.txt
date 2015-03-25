-------------------------------------------------------------------------------
| COMP90018 Mobile Computing System Programming, Project Breakout Game		  |
| Semester 2, 2014															  |
| Group 25 																	  |
| Students: (Name, StudentNumber, Email) 									  |
|   		Chenchao Ye,    633657, chenchaoy@student.unimelb.edu.au		  |
|           Fengmin Deng,   659332, dengf@student.unimelb.edu.au			  |
|           Jiajie Li,      631482, jiajiel@student.unimelb.edu.au			  |
|           Shuangchao Yin, 612511, shuangchaoy@student.unimelb.edu.au		  |
-------------------------------------------------------------------------------

>> Project Description
We implemented the Breakout game which is the standard project of the subject.
All the tasks required in the project specification are implemented except for
the bonus one. The game has two main activities which are the menu and the game
itself. The menu is the portal of all other activities including player name
register, game, local level entry, level download, high score list and the game 
rule. Meanwhile, these activities interact with each other, e.g. a player just
wins the game with top ten high score will see directly the updated high score 
list if the player confirm an entry to the list. Besides the required function
of the game, an additional function "Pause" was implemented for better user 
experience. The operations connected to the server are all done in the 
background with appropriate processing indicator if it takes for a while.



>> There are four parts of the codes.

1. Menu:
MenuActivity.java, activity_menu.xml

The functionalities of the game are all outlined in this part. The codes here
defined functions to start a task or another activity such as starting the game
activity, to invoke a dialogue for feedbacks or an improper operations and to 
retrieve the stored data from last game state.


2. Game:
Ball.java
Bar.java
Brick.java
Bricks.java
LoadFilesTask.java
MainActivity.java, activity_main.xml (portrait and landscape versions)
OnPlayData.java
RuntimeData.java
Utils.java
WorldView.java

LoadFilesTask loads the data for a game view, the data consists of information
of the game state (a new game or an uncompleted one) and the downloaded 
(locally stored) level file. MainActivity is started if the task executed
successfully.
MainActivity holds the whole logistics of the game. The game view is 
initialized here. It starts a runnable WorldView which keeps drawing the 
motional graphic components Ball, Bar(the paddle) and Bricks. At the meantime, 
the WorldView deals with the actions of screen touch, updates and passes the 
scores, lives e.t.c. to MainActivity, and saves all data of the game state such
as ball x position, speeds e.t.c. temporarily on each iteration and 
persistently on surface destroyed. Functions defined in MainActivity does jobs
like displaying the timely score (calculated via OnPlayData) e.t.c, prompt
the dialogue for high score list entry or game over.
RuntimeData stores all the information needed to restore a game state for the
game that is interrupted, these information are also used excessively during 
game play and the interactions of the activities. Utils provides some helper
functions to deal with the ranking and bricks building.


3. Server connection tasks:
DownloadLevelListener.java
RetrieveHighScoreTask.java
UpdateAllLevelTask.java
UpdateLevelTask.java
UpdateScoreTask.java

DownloadLevelListener listens to the need for downloading an unavailable file 
in local storage and assigns the UpdateLevelTask. UpdateAllLevelTask and 
UpdateLevelTask download the server for the available level files, one does
a comprehensive download and the other does one level only. UpdateScoreTask
sends the requested entry of high score to server and RetrieveHighScoreTask
downloads the real time top ten high scores from the server.


4. Various optional / compulsory activities:
HelpDisplayActivity.java, help.xml
HighScoreListActivity.java, record.xml, list.xml
PlayerActivity.java, player.xml
SelectLevelActivity.java, level.xml, list.xml

HelpDisplayActivity shows the game rules while HighScoreListActivity shows
the list on leader board. PlayerActivity lets the player enter the name which
is compulsory for the game while it is optional to change it. 
SelectLevelActivity finds and shows game level located in local storage, if the
level is clicked, it calls the LoadFilesTask and starts MainActivity from that
chosen level.


>> Team member contribution

Chenchao Ye:
 - The server and the database that stores the entries of high scores.
 - All the networking related components, mainly part 3 above.
 - Download latest level data.

Jiajie Li:
 - The control of movement of the paddle.
 - The movement of the ball.
 - The detection of ball hitting the bricks including elimination of the bricks
	after hitting.
 - The detection of ball hitting the paddle including adding paddle speed to 
	the ball.
 - The generator of Levels.
 - Prototype of menu activity switching to main activity.
 - Prototype of drawing a fluid game in WorldView.
 - Part of interaction among tasks and activities.

Shuangchao Yin:
 - Prototype of the menu.
 - Endeavor on additional entertainment settings.
 
Fengmin Deng:
 - The construction of the runtime data and integrated it into the whole game 
	logic for consistency of the game.
 - Refine the layout of the menu and the game, added in layout for landscape.
 - The statistics (lives, next, rank) during game run.
 - The components of name entry, level selection, leader board and help.
 - Part of interaction among tasks and activities.