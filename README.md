# COMP 361 Project - Splendor

## Table of Contents

 * [Project Description](#project-description)
 * [Project Structure](#project-structure)
 * [Useful Links](#useful-links)
    * [Code Style and Tools](#code-style-and-tools)
    * [Requirements](#requirements)
 * [Authors](#authors)

## Project Description
Splendor is a board game for 2-4 players. More information about the game can be found [here]
(https://boardgamegeek.com/boardgame/148228/splendor). <br>
In addition, we added expansions to the game. The expansions are listed below:
* (Cities expansion)[https://www.ultraboardgames.com/splendor/cites-of-splendor.php]
* (Trader expansion)[https://boardgamegeek.com/thread/1889165/trade-routes-expansion-trigger-sell-goods]

## Project Structure

### Server

Contains the server side of the game. The server is a Spring Boot application that has multiple 
REST API endpoints that can be used to interact with the game.
See the [API documentation](docs/rest_interface_description.pdf).

### Client
The client is a pygame application that can be used to play the game. The client can be run in 
two ways:
* Running the main.py file in the client directory
* Running the docker container

More information can be found in the [setup section](##Setup).

### Lobby Service
This project is dependent on the [Lobby Service](https://github.com/m5c/BoardGamePlatform).
The Lobby Service manages the sessions and user accounts.


## Code Style and Tools

This project follows the best practices of the [Google's Checkstyle Configuration](https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml).


## Requirements

* [Docker](https://docs.docker.com/install/)
* [Python 3.7](https://www.python.org/downloads/release/python-370/)
* Install maven


## Setup

1. Clone the repository
2. In the client directory, run the following command:
    ```bash
    pip install -r requirements.txt
    ```
3. In the root directory of the project, run the following command based on your operating system:
    ```bash
    ./updatesubmodules.ps1 # Windows
    ./updatesubmodules.sh # Linux
    ```
4. Run the following command in the root directory of the project.
   It will take a while for the images to download the first time.
    ```bash
    docker-compose up
    ```
   **Note:**
    The first time you run this command, the Splendor-Server container will not be able to start 
   up. This is because the game service needs to be registered with the lobby service. To fix 
   this, go to localhost:4242. login with username "maex" and password "abc123_ABC123". Then, 
   add the game service by clicking on the "Admin Zone", and name the service splendor, and 
   password also "abc123_ABC123". Then, restart the server container by running the following
    command:
     ```bash
     docker-compose up
     ```
   **Note 2:**
   LS database container does not have a volume mounted to it, so running docker-compose down 
   will remove all data in the database (including the registered game service).
5. Edit client/config.py to set the IP address of the server to the IP address of the machine
   running the docker container.
6. Run the client by running the main.py file in the client directory.
7. Play the game and have fun!
8. To stop the server, run the following command in the root directory of the project:
    ```bash
    docker-compose stop
    ```

## Authors

 * [Youssef Samaan](https://github.com/YoussefSamaan2)
 * [Kevin Yu](https://github.com/iveykun)
 * [Wassim Wazzi](https://github.com/wassimwazzi)
 * [Rui Cong Su](https://github.com/a-lil-birb)
 * [Felicia Sun](https://github.com/Felicia-Sun)
 * [Jessie Xu](https://github.com/XiaoyuJessieXu1) 


