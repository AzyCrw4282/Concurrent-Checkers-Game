package com.rhul.springboot;

public class CheckersSquare {


    //creats 64 squares objects to identify each and all
    public CheckersSquare(int square, int index){
        this.id = square;
        this.index  = index;
        this.occupied = false;
        this.pieceId = null;

    }
    //event handler will trigger this call
    public void onClick(){
        //when sqaure selected and valid, make a move
        Checkers checkers_obj = new Checkers();
        checkers_obj.makeMove(index);
    }


}
