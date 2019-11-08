package com.rhul.springboot;


public class CheckersSquare {

    public  CheckersSquare[] block = new CheckersSquare[65];

    private int id;
    private boolean occupied;
    private Checkers pieceId;//the id of the piece on the square

    //creats 64 squares objects to identify each and all
    public CheckersSquare(){

    }

    public CheckersSquare(int square_id){
        this.id = square_id;
        this.occupied = false;
        this.pieceId = null;


    }

    //event handler will trigger this call
    public void onClick() throws Exception{
        //when square selected and valid, make a move
        Checkers checkers_obj = new Checkers();
//        checkers_obj.make_move(1,"game+_1");
    }


    //these can be removed if lombok is working
    public void setOccupied(){
        this.occupied = true;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setPieceId(Checkers piece){
        this.pieceId = piece;
    }


    public Checkers getPieceId() {
        return pieceId;
    }


    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
