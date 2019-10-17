package com.rhul.springboot;

import com.sun.tools.javac.comp.Check;

public class CheckersSquare {

    public static CheckersSquare[] block = new CheckersSquare[64];

    private int id;
    private boolean occupied;
    private int pieceId;
    private boolean alive;

    //creats 64 squares objects to identify each and all
    public CheckersSquare(int square_id){
        this.id = square_id;
        this.occupied = false;
        this.pieceId = 0;
        this.alive = true;


    }
    //event handler will trigger this call
    public void onClick(){
        //when square selected and valid, make a move
        Checkers checkers_obj = new Checkers();
        checkers_obj.makeMove(index);
    }

    public void setOccupied(){
        this.occupied = true;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setPieceId(Checkers id){
        this.pieceId = id;
    }


    public int getPieceId() {
        return pieceId;
    }


    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
