# Turtle Challenge

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/873c18adc38b48ca819515172c97f998)](https://app.codacy.com/app/barambani/turtle?utm_source=github.com&utm_medium=referral&utm_content=barambani/turtle&utm_campaign=Badge_Grade_Dashboard)

A turtle must walk through a minefield. Write a program that will read the initial game settings from one file and one or more sequences of moves from a different file, then for each move sequence, the program will output if the sequence leads to the success or failure of the little turtle.
The program should also handle the scenario where the turtle doesn’t reach the exit point or doesn’t hit a mine.   
   
   
#### Inputs
- The board is a square of n by m number of tiles
- The starting position is a tile (x,y) and the initial direction the turtle is facing (that is: north, east, south, west)
- The exit point is a tile (x,y)
- The mines are defined as a list of tiles (x,y)   
   
   
#### Actions
Turtle actions can be either a move (m) one tile forward or rotate (r) 90 degrees to the right   
   
   
#### Notes
There are no restrictions or requirements on how to model the game settings and the sequences of moves   
   
   
#### Board Example

|        |        |        |        |   E    |
| ------ | ------ | ------ | ------ | ------ |
|   M    |        |        |        |        |
|   T    |        |        |   M    |        |
|        |    M   |        |        |        |
|        |        |   M    |        |        |


T : turtle (orientation up)  
M : mine  
E : exit  
   
   
#### Output Example
Given a file containing the board size, starting point and direction, exit point and mines called “game-settings” and a file containing one or more move sequences called “moves”
When I run the program passing the filenames as a parameters, the program will print out the result for each sequence in the “moves” file
```
Sequence 1: Success
Sequence 2: Mine hit
Sequence 3: Success
Sequence 4: Still in danger
Sequence 5: Outside the limits
```
