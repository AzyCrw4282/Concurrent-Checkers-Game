package com.rhul.springboot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor

//handles the board related funcs. individual object shall also handle
//the  checking mechanisms and other too . may need to change should
//rules or something needs to be adapted

public class Checkers {//for indivial counters

    private int id;
    private String colour;
    private String game_type;
    private boolean king = false;
    public static Checkers[] w_checkers;
    public static Checkers[] b_checkers;
    private int coordX;
    private int coordy;
    private int occupiedSquare;

    //keeps track of the board and counters. need this int game_id,
    public Checkers(int square_piece, String colour, int square){//add , String game_type ltr vrsions
        this.id = square_piece;
        this.colour = colour;
        this.game_type = game_type;
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


    public void setCoordinates(int x ,int y){
        this.coordX = X;
        this.coordY = Y;
    }

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

    // For clicking on square after selecting a counter - >order of checking ops: makemove once square clicked, then erase road( rests colour of the moved pieces to the same colour

    // clicking on counter -> show moves for the cur piece, and if no possible attackmoves and then changeTurns. set colour edges and so on. and perform attack move -> check attack. if last prev 2 ops not done then simple move checked without attack else nothing possible.


    //starting with coutner selections first
    public boolean show_moves(Checkers piece){//clciked piece shoudl be passed in here
        boolean match =false;
        boolean must_attack = false;
        Checkers selected_piece;

//need to check on this
//        if(selected_piece == 1){
//            apply_road_colour(selected_piece);//done on f/e
//        }


        //check the colour of the selected piece and udpate it

        int i,j;

        for (j=1;j<=12;j++){
            if (Checkers.w_checkers[j].equals(piece)){
                selected_piece = Checkers.w_checkers[j];
            }
            else if (Checkers.b_checkers[j].equals(piece)){
                selected_piece = Checkers.b_checkers[j];
            }
        }

        if (!attack_move(selected_piece)){//not attack move then change turns based on colour
            change_turns(selected_piece);
            return false;
        }




    }








    public static void eliminate_check(int index){
        if (index > 0 && index <65){
            //need to set to false in the board so its not longer alive
            int piece_id = CheckersSquare.block[index].getPieceId();
            CheckersSquare.block[index].setAlive(false);
            CheckersSquare.block[index].setOccupied(false);
//            display then need to be set in the f/e
        }
        else{
            System.out.println("Unable to eleimiate");
        }

    }

    public void make_move(int index){
        boolean isMove = false;
        if











    }

    public boolean attack_move(Checkers piece){
        Checkers selected_piece = piece;

        //types of moves possible
        int up_left=0;
        int up_right=0;
        int down_left=0;
        int down_right=0;

        if (selected_piece.king){//all poss moves
            if (selected_piece.colour == "white"){
                up_right = check_attack(selected_piece, 6,3,-1,-1,-1,up_right);
                up_left = check_attack(selected_piece,3,3,-1,-1,-9,up_left);
            }
            else{//checked for black king
                down_left = check_attack(selected_piece,3,6,1,1,7,down_left);
                down_right = check_attack(selected_piece,6,6,-1,1,9,down_right);
            }
        }
        if(selected_piece.colour == "white"){
            down_left = check_attack( selected_piece , 3, 6, 1 , 1 , 7 , down_left );
            down_right = check_attack( selected_piece , 6 , 6 , -1, 1 ,9 , down_right );
        }
        else{//normal black check
            up_right = check_attack( selected_piece , 6, 3 , -1 , -1 , -7, up_right );
            up_left = check_attack( selected_piece, 3 , 3 , 1 , -1 , -9 , up_left );
        }

//        identifies how many places to move and the direction
        if (piece.colour == "black" && up_left != 0 || up_right !=0 && down_left !=0 && down_right != 0){
            //if move predicted then its the negating move thats performed

            int val = up_left;
            up_left = down_left;
            down_left = val;

            int val2 = up_right;
            up_right = down_right;
            down_right = val;

            int val3 = down_left;
            down_left = down_right;
            down_right = val;

            int val4 = up_right;
            up_right = up_left;
            up_left = val;

        }

        if (up_left != 0 || up_right != 0  || down_left != 0 || down_right != 0){//any possible moves
            return true;
        }
        else{
            return false;
        }


    }

    public static int check_attack(Checkers piece, int X, int Y, int negX, int negY,int squareMove, int direction ){
        boolean attack_possible = false;


        if (piece.coordX * negX >= negX * X && piece.coordy * negY <= Y * negY && CheckersSquare.block[piece.occupiedSquare + squareMove].isOccupied() && CheckersSquare.block[piece.occupiedSquare + squareMove].getPieceId().colour != piece.colour && !CheckersSquare.block[piece.occupiedSquare + squareMove * 2].isOccupied()){
            attack_possible = true;
            direction = (String) (piece.occupiedSquare + squareMove * 2).toString();
            return direction;//when returned, goes through handler and colour val applied to new pos
        }
        else{
            direction = 0;
            return direction;
        }

    }




}
