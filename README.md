# Tugas Besar IF2211 Strategi Algoritma Team AFK

## Table of Contents

- [Tugas Besar IF2211 Strategi Algoritma Team AFK](#tugas-besar-if2211-strategi-algoritma-team-afk)
  - [Table of Contents](#table-of-contents)
  - [Project Description](#project-description)
  - [Algorithm Description](#algorithm-description)
  - [Program Requirements](#program-requirements)
  - [Running the Program](#running-the-program)
  - [Authors](#authors)

## Project Description

This project is an Algorithm Strategies Project that implements a bot/Artificial Intelligence using the Greedy Algorithm as the backbone of the decision making algorithm. The basic for this project is an understanding in Object-Oriented Programming, The Greedy Algorithm, Algorithm Design and Analysis, and with a bonus of basic understanding of threading. The bot to be implemented is a bot from The Entellect 2021 Challenge game, Galaxio, where it an be classified as a battle-royale, agar.io-like game with a twist.

The full game description can be read [here](github.com/EntelectChallenge/2021-Galaxio/blob/develop/game-engine/game-rules.md)
Starter pack can be downloaded [here](https://github.com/EntelectChallenge/2021-Galaxio/releases/tag/2021.3.2)

## Algorithm Description

The Base Algorithm of this project is the greedy algorithm, where there can be many alternatives to the greedy approach, such as greedy by nearest distance, safest position, etc. The approach that our team used is the greedy by weight, where we can combine all of the greedy approaches based on the features we want to process, and convert it into a weight that can be weighted by the main greedy algorithm to choose.

In short, the greedy algorithm is an algorithm that always takes the best-possible solution at that instance, or in other words, the local maximum at an instance. This way, in some cases, the greedy algorithm can approximate the global maximum with the local maximum.

## Program Requirements

* Java version 11
* .Net Core 3.1
* Maven 

## Running the Program

1. use `mvn package` to compile the JavaBot at the root directory (JavaBot Directory)
2. use `sh run.sh` to run the shell command to execute the game
3. open the visualiser `Galaxio.exe` to visualize the game
4. open the settings and change the logger directory to the folder that the log is saved to
5. start and enjoy the game!

## Authors

| NIM      | Name                       |
| -------- | -------------------------- |
| 13521045 | Fakhri Muhammad Mahendra   |
| 13521089 | Kenneth Ezekiel Suprantoni |
| 13521101 | Arsa Izdihar Islam         |
