-------------------------------------------------------------------------------
/**
 * COMP90020 Distributed Algorithms
 * Semester 1, 2015
 * Group 4
 * Students: (Name, StudentNumber, Email)
 *          Bumsik Ahn, 621389, bahn@student.unimelb.edu.au
 *          Jiajie Li, 631482, jiajiel@student.unimelb.edu.au
 *          Fengmin Deng, 659332, dengf@student.unimelb.edu.au
 */
-------------------------------------------------------------------------------

>> Project Description
This project implement a distributed algorithm for Concurrency Control in a multi-player (currently two) breakout game for Android devices.

>> Code Structure
There are two main components of the code:
- Server source code: resides in the folder 'ServerSide'
- Mobile source code: resides in the folders 'src' and 'res'

1. Server source code
Server is implemented with PHP API and a MySQL database. 

The only server code is in the apiV6WithAlgorithm.php file. The structure of the code is divided in to execution body and the API class. 

The API class provides connection to the database and functions of generate different SQL queries and passes the query to the database in order to get the result.The algorithm is implemented as functions of the class. In the execution body of the php API file, it mainly handle the communication with the client through HTTP. 

2. Mobile source code
There are three parts of the code.

2.1 Menu:
MenuActivity.java, activity_menu.xml

The functionalities of the game are all outlined in this part. The codes here defined functions to start a task or another activity such as starting the game activity.

2.2 Game:
Ball.java
Bar.java
Brick.java
Bricks.java
InitGameAtServerTask.java
LoadFilesTask.java
MainActivity.java, activity_main.xml (portrait and landscape versions)
OnPlayData.java
RuntimeData.java
Utils.java
WorldView.java
Constants.java

InitGameAtServerTask sends HTTP request to the server to initiate the game. That is to say, the player sends his/her name to the server and wait for the server's response. If there is another player available to start a game at the same time, the response from the server would include the information about the side of the game map and the name of the rival, and then the LoadFilesTask would be executed; otherwise the player would get information about no rival available.
LoadFilesTask loads the map of the bricks for the game view. There are two maps in the 'assets' folder for now. It would load a map indicated by the response from server in InitGameAtServerTask.
MainActivity holds the whole logistics of the game. The game view is initialized here. It starts a runnable WorldView which keeps drawing the motional graphic components two Ball, Bars(the paddle) and Bricks. At the meantime, the WorldView deals with the actions of screen touch, updates and passes the scores to MainActivity. Furthermore all the networking communication would be done here because the latest status is captured here and the relevant data would be exchanged with those in the server. Functions defined in MainActivity does jobs like displaying the timely score (retrived from server) e.t.c, prompt for game over.
RuntimeData stores all the information needed to restore a game state for the game. 
Utils provides some helper functions to deal with the ranking and bricks building.

2.3 Various optional / compulsory activities:
HelpDisplayActivity.java, help.xml
PlayerActivity.java, player.xml

PlayerActivity lets the player enter the name which is compulsory for the game while it is optional to change it. 

>> How to run the code
1. Deploy the server
1.1 set up your laptop as a local server by installing the XAMPP or you can have your own remote server but the server has to install Mysql, PHP, phpMyAdmin and Apache before running the program.
1.2 create a database in the server and Import the tables(breakout-4.sql)  we created to the database. 
1.3 put the apiV6WithAlgorithm.php file and the api_config.php file in the directory your apache public html directory. If you use XAMPP the directory should be  /XAMPP/htdocs/xampp/algorithm. If you don't have a folder named algorithm then create one under that directory. 
1.4 change the value "dbname", "username" and "password" in the api_config.php with your own database name your own user name and your user password. 
1.5 start apache service and mysql service of the server.
Then all things are set up, and the server is ready to use.

2. Install the game 
2.1 Import the source code in a Development Toolkit, like eclipse.
2.2 Open the source code 'Constants.java', change the value of variable <SEVER_URL> with URL to locate the file 'apiV6WithAlgorithm.php'; save the change.
2.3 Build the application and install it in two android phone.
