package com.rhul.springboot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor

//handles the board related funcs

public class Checkers {


    //keeps track of the board and counters
    public Checkers(int square_piece, String colour, int square){
        this.id = square_piece;
        this.colour = colour;
        this.king = false;
        this.occupiedSquare = square; //n number of occupied, ! eliminated
        this.alive = true;
        this.attack = false;
        this.coordX = getXcoordinates(square);
        this.coordY = getYcoordinates(square);

    }

    public int getXcoordinates(int square){
        if (square % 8 == 1){//if is square 9
            this.coordX = square % 8;
            return(coordX);
        }
        else{
            this.coordX = 8;
            return(coordX);
        }

    }

    public int getYcoordinates(int square){
        if (square % 8 == 1){//if is square 9
            this.coordy = Math.floor(square/8) + 1;
            return(coordy);
        }
        else{
            this.coordY = square/8 ;
            return(coordy);
        }
    }

    public void onClick(){
        showMoves(id);
    }
//    this would only be needed in f/e since display not done here
//    public void setXCoordinates(X,Y){
//
//
//
//
//    }


    public void changeCoordinates(int X, int Y){
        this.coordX = X;
        this.coordY = Y;
    }


    public void checkIfKing(){
        //if red is gone from btm to top then its a king
        if(this.coordY == 8 && !this.king && this.color == "white"){
            this.king = true;
        }
        if(this.coordY == 1 && !this.king &&this.color == "black"){
            this.king = true;
        }
    }
}
