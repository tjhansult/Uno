# UNO game (P_MOD2)
This project concludes 3 months of learning Java combining the knowledge and skills students gained during **MODULE 2**.

## Description
UNO is a classic card game for 2-10 players, where the goal is to get rid of all your cards before the other players. This project provides a platform for players to play UNO with friends on different devices, by utilizing Java's networking capabilities.

Our digital implementation of the game promises to bring the excitement and fun of the physical game. Gather your friends, pick up your devices and get ready for a thrilling game of UNO!

## Project requirements
1. Multiplayer gameplay over a network connection with a client-server architecture.
2. Client application must have a user interface, either graphical or text-based.
3. Server must be capable of hosting at least one game of UNO and determining the winner.
4. Client must be able to connect to the server, participate in the game, and announce the winner.
5. Adherence to a Model-View-Controller (MVC) architecture for modularity and maintainability.
6. Robust handling of common exceptions and errors, such as connection loss or invalid input.
7. Incorporation of a computer player for availability of an opponent even in absence of human players.
8. Computer player programmed to perform only valid actions within the game of UNO.

## Approach
The Java UNO project underwent a comprehensive requirements analysis prior to its development. To align the visions of the involved developers, visual aids such as illustrations were produced. A class diagram was then formulated to provide a clear understanding of the project's structure.

The implementation phase of the project commenced immediately after the class diagram was devised. A local version of the game was implemented, followed by the creation of key classes such as "card," "deck," "table," and "game." The development of the first version of the computer player was also initiated.

Once the local version of the game was thoroughly tested and debugged, work began on the network version. A protocol was established by a team of 20 individuals, which the developers were obligated to adhere to throughout the project. The implementation of the methods from the specified interface proved to be a challenge initially, prompting the team to conduct extensive research.

After gaining a comprehensive understanding of the topic, the developers began work on the network version of the game. Classes such as "server," "client," "clienthandler," and "serverhandler" were created, followed by the necessary modifications to the local version of the game. JUnit tests were also created to validate the functionality of the code.

Finally, a professional report documenting the work completed was compiled, showcasing the team's achievements and the successful implementation of the Java UNO project.

## Installation

1. Install an app on your local device (type below code onto your terminal) :
    1. ``` $ git clone https://gitlab.utwente.nl/s3077489/gameuno.git ```
2. Open a project
   **NOTE: USE JAVA11 FOR THIS PROJECT**

## Setup (locally)
To start a game you can either decide to play on local version or networking version!
If you wish to play local version, below is the complete tutorial how to do it!
1. Run UNO class
2. You will be asked to enter number of players. Count all the players and input the number. Click enter afterwards! **REMINDER: IT HAS TO BE NUMBER 2-10**
3. Now you will be asked to name your players. Input all the names one at a time and press enter!
4. Then you will be asked to input desired game mode. We offer three of them: normal, progressive and sevenZero. Choose one of them, type it onto console and press enter!
5. First player will be asked to move. At this point you can use one of the valid command below:
    1. Normal gamemode
        * 0 - [size of your hand] -> it can be any number in range of your hand. Every card is indexed at the beginning.
            * **EXAMPLE**: p2 make your move: `2`
        * draw -> it can be used when you don't have any valid card to play or you simply wish to draw. **NOTE! IF THE CARD YOU DREW IS VALID TO PLAY YOU WILL BE ASKED TO DECIDE WHETHER OR NOT YOU WANT TO PLAY IT. TYPE yes OR no RESPECTIVELY!**
            * **EXAMPLE**: p2 make your move: `draw`
        * challenge -> if you suspect that previous player played DRAW_FOUR card on you illegally, type challenge to see!
            * **EXAMPLE**: p2 make your move: `challenge`
        * 0 - [size of your hand] uno -> it can be used when you have two cards left and you want to shout UNO. We suggest doing that, otherwise you will be punished with two cards!
            * **EXAMPLE**: p2 make your move: `6 uno`
        * red -> it can be used when you are asked to pick a color
            * **EXAMPLE**: Please pick a color:  `red`
        * green -> it can be used when you are asked to pick a color
            * **EXAMPLE**: Please pick a color:  `green`
        * yellow -> it can be used when you are asked to pick a color
            * **EXAMPLE**: Please pick a color:  `yellow`
        * blue -> it can be used when you are asked to pick a color
            * **EXAMPLE**: Please pick a color:  `blue`
    2. Progressive gamemode (includes every normal game mode)
        * If you see following message: "You can forward drawing two cards, by placing your draw two card" you are allowed to either choose a card (index input) or type "skip"
            * **EXAMPLE**: p2 make your move: `skip`
            * **EXAMPLE**: p2 make your move: `2`
            * **NOTE**! the only accepted card in this scenario is DRAW_TWO
    3. SevenZero mode
        * If you see following message: ">> Please pick a player to switch hands with." you have tot ype a nickname of a player you wish to switch hands with
            * **EXAMPLE**: Please pick a player to switch hands with: `player1`

## Setup (networking)
To start a game you can either decide to play on local version or networking version!
If you wish to play networking version, below is the complete tutorial how to do it!
1. RUN Server.java
2. RUN Client.java
3. You will be asked to enter desired connection. Type in the IP address or "localhost" to connect with local server. Click enter afterwards! **REMINDER: IT HAS TO BE NUMBER 2-10**
    * **EXAMPLE**: Please type desired connection (IP or localhost): `149.89.228.20`
4. Now you will be asked to input desired port. Use the one that server provider uses!
    * **EXAMPLE**: Please type desired PORT (compatible with server): `5050`
5. Then you will be asked to name your player. Name him how you want!
    *  **EXAMPLE**: Please enter name:  `player1`
6. Now you are in starting panel! Below is the list of available commands:
    1. jl|[name of lobby] -> joins to the lobby with given name
        * **EXAMPLE**: Enter command: `jl|main`
    2. cl|[name of lobby] -> creates lobby with given name
        * **EXAMPLE**: Enter command: `cl|playground`
    3. lol -> shows you available lobbies and number fo players in them
        * **EXAMPLE**: Enter command: `lol`
   4. acp|[name] -> adds computerPlayer to your lobby **NOTE! IT ONLY APPLIES IF YOU ARE THE ADMIN AND YOU ARE ALREADY IN ONE OF LOBBIES!**
        * **EXAMPLE**: Enter command: `acp|monkey`
   5. start|[name of gamemode] -> starts the game in given mode! **NOTE! IT ONLY APPLIES IF YOU ARE THE ADMIN!**
        * **EXAMPLE**: Enter command: `start|progressive`
        * **Please make sure to type your first input twice! This will indicate that you are ready to start!**
7. Once the game is started, you can input one of the commands below: **NOTE: FROM NOW ON IF YOU ARE INACTIVE FOR MORE THAN 45 SECONDS YOU WILL LEAVE THE GAME!**
    1. Normal gamemode
        * 0 - [size of your hand] -> it can be any number in range of your hand. Every card is indexed at the beginning.
            * **EXAMPLE**: Make your move: `2`
        * draw -> it can be used when you don't have any valid card to play or you simply wish to draw. **NOTE! IF THE CARD YOU DREW IS VALID TO PLAY YOU WILL BE ASKED TO DECIDE WHETHER OR NOT YOU WANT TO PLAY IT. TYPE yes OR no RESPECTIVELY!**
            * **EXAMPLE**: Make your move: `draw`
        * 0 - [size of your hand] uno -> it can be used when you have two cards left and you want to shout UNO. We suggest doing that, otherwise you will be punished with two cards!
            * **EXAMPLE**: Make your move: `6 uno`
        * red -> it can be used when you are asked to pick a color
            * **EXAMPLE**: Please pick a color:  `red`
        * green -> it can be used when you are asked to pick a color
            * **EXAMPLE**: Please pick a color:  `green`
        * yellow -> it can be used when you are asked to pick a color
            * **EXAMPLE**: Please pick a color:  `yellow`
        * blue -> it can be used when you are asked to pick a color
            * **EXAMPLE**: Please pick a color:  `blue`
        * sm|[message] -> to send a message to your lobby
            * **EXAMPLE**: Make move:  `sm|hi all friends`
    2. Progressive gamemode (includes every normal game mode)
        * If you see following message: "You can forward drawing two cards, by placing your draw two card" you are allowed to either choose a card (index input) or type "skip"
            * **EXAMPLE**: Make your move: `skip`
            * **EXAMPLE**: Make your move: `2`
            * **NOTE**! the only accepted card in this scenario is DRAW_TWO
    3. SevenZero mode
        * If you see following message: "Please pick a player to switch hands with." you have tot ype a nickname of a player you wish to switch hands with
            * **EXAMPLE**: Please pick a player to switch hands with: `player1`

## Key features
The application facilitates the creation of a server, enabling the connection of multiple clients to it. Additionally, the server maintains a registry of available lobbies, allowing clients to opt for a preferred lobby. The game incorporates multithreading, enabling concurrent gameplay between clients in separate lobbies. For instance, Client A and Client B can engage in a game while Client C and Client D participate in another.

A technologically advanced computer player has also been incorporated, utilizing advanced algorithms to make informed decisions regarding card selection. The computer player considers various factors such as the cards played and held in hand, conducting a comprehensive analysis before determining the most suitable move.

The multithreading aspect of the project has been carefully managed by the students, utilizing methods such as wait, notifyall, and lock to control the threads effectively.


# Project status - finished

   
