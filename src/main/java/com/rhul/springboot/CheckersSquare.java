package com.rhul.springboot;

import com.sun.tools.javac.comp.Check;

public class CheckersSquare {


    //creats 64 squares objects to identify each and all
    public CheckersSquare(int square_id){
        this.id = square_id;
//        this.index  = index;
        this.occupied = false;
        this.pieceId = null;

    }
    //event handler will trigger this call
    public void onClick(){
        //when sqaure selected and valid, make a move
        Checkers checkers_obj = new Checkers();
        checkers_obj.makeMove(index);
    }

    public void setOccupied(){
        this.occupied = true;
    }


    public void setPieceId(Checkers id){
        this.pieceID = id;
    }


}
