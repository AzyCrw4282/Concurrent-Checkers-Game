package com.rhul.springboot;

/**This class is responsible for each square on the board. This allows to identify if it's occupied or not.
 * For each board 64 instance of this class is used.
 * @author Azky
 */
public class CheckersSquare {

    public  CheckersSquare[] block = new CheckersSquare[65];

    private int id;
    private boolean occupied;
    private Checkers pieceId;


    public CheckersSquare(){

    }

    public CheckersSquare(int square_id){
        this.id = square_id;
        this.occupied = false;
        this.pieceId = null;


    }


    public void onClick() throws Exception{

        Checkers checkers_obj = new Checkers();

    }



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
