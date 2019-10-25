package com.rhul.springboot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor

//handles the board related funcs. individual object shall also handle
//the  checking mechanisms and other too . may need to change should
//rules or something needs to be adapted

public class Checkers {//for individual counters

    private int id;
    private String colour;
    private String game_type;
    private boolean king = false;
    private boolean game_started = false;
    public static Checkers[] w_checkers;
    public static Checkers[] b_checkers;
    private int coordX;
    private int coordY;
    private boolean alive;
    private int occupiedSquare;
    private int selected_piece;
    private  boolean attack_possible = false;
    private Checkers the_checker[];

    int reverse_tableLimit;
    int tableLimit;
    int tableLimitRight;
    int tableLimitLeft;
    int moveUpRight;
    int moveUpLeft;
    int moveDownRight;
    int moveDownLeft;
    int player_turn;

    boolean another_move;

    //keeps track of the board and counters. need this int game_id,
    public Checkers(int square_piece, String colour, int square){//add , String game_type ltr vrsions
        this.id = square_piece;
        this.colour = colour;
        this.game_type = game_type;
        this.king = false;
        this.occupiedSquare = square; //n number of occupied, ! eliminated
        this.alive = true;
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
            this.coordY = (int) Math.floor(square/8) + 1;
            return(coordY);
        }
        else{
            this.coordY = square/8 ;
            return(coordY);
        }
    }


    public void setCoordinates(int X ,int Y){
        this.coordX = X;
        this.coordY = Y;
    }

    public void changeCoordinates(int X, int Y){
        this.coordX = X;
        this.coordY = Y;
    }


    public void checkIfKing(){
        //if red is gone from btm to top then its a king
        if(this.coordY == 8 && !this.king && this.colour.equals("white")){
            this.king = true;
        }
        if(this.coordY == 1 && !this.king &&this.colour.equals("black")){
            this.king = true;
        }
    }

    // For clicking on square after selecting a counter - >order of checking ops: makemove once square clicked, then erase road( rests colour of the moved pieces to the same colour

    // clicking on counter -> show moves for the cur piece, and if no possible attackmoves and then changeTurns. set colour edges and so on. and perform attack move -> check attack. if last prev 2 ops not done then simple move checked without attack else nothing possible.


    //starting with coutner selections first
    public boolean show_moves(Checkers piece){//clciked piece shoudl be passed in here
        selected_piece = piece.id;
        boolean match =false;
        attack_possible = false;


        if (the_checker == w_checkers){
            the_checker = b_checkers;
        }
        else{
            the_checker = w_checkers;
        }

//        if(selected_piece > 0){ //refers to id
//            remove_road(selected_piece);//done on f/e
//        }else if (piece.id > 0){
//            selected_piece = piece.id;
//        }

        int i =0,j=0;

        for (j=1;j<=12;j++){
            if (Checkers.w_checkers[j].equals(piece)){
                selected_piece = Checkers.w_checkers[j].id;
                match = true; //game is on
                i = j;
                the_checker = w_checkers;
            }
            else if (Checkers.b_checkers[j].equals(piece)){
                selected_piece = Checkers.b_checkers[j].id;
                the_checker = b_checkers;
                match = true;
            }
        }

        if (!attack_move(piece)){//not attack move then change turns based on colour
            change_turns(the_checker);
            return false;
        }
        if (!match){
            return (false);
        }

        if (piece.colour.equals("white")){
            tableLimit = 8;
            tableLimitRight = 1;
            tableLimitLeft = 8;
            moveUpRight = 7;
            moveUpLeft = 9;
            moveDownRight = - 9;
            moveDownLeft = -7;
        }
        else{// if black
            tableLimit = 1;
            tableLimitRight = 8;
            tableLimitLeft = 1;
            moveUpRight = -7;
            moveUpLeft = -9;
            moveDownRight = 9;
            moveDownLeft = 7;
        }

        attack_move(the_checker[i]);

        int up_left=0;
        int up_right=0;
        int down_left=0;
        int down_right=0;

        if (!attack_possible){//if attack not possible then make just a move
            down_left = check_move(the_checker[i],tableLimit , tableLimitRight , moveUpRight , down_left);
            down_right = check_move( the_checker[i] , tableLimit , tableLimitLeft , moveUpLeft , down_right);
            if (the_checker[i].isKing()){
                up_left = check_move( the_checker[i] , reverse_tableLimit , tableLimitRight , moveDownRight , up_left);
                up_right = check_move( the_checker[i], reverse_tableLimit , tableLimitLeft , moveDownLeft, up_right);
            }
        }
        if (up_left != 0 || up_right != 0  || down_left != 0 || down_right != 0){//any possible moves
            return true;
        }
        else{
            return false;
        }
    }

    public int check_move(Checkers piece, int top_limit, int LimitSide, int moveDirection, int theDirection){
        if (piece.coordY != top_limit){
            if (piece.coordX != LimitSide && !CheckersSquare.block[piece.occupiedSquare+ moveDirection].isOccupied()){
                CheckersSquare.block[piece.occupiedSquare + moveDirection].getId();// send colour change to f/e
                theDirection = piece.occupiedSquare + moveDirection;
            }
            else{
                theDirection = 0;
            }
        }
        else{
            theDirection = 0;
        }
        return (theDirection);

    }


    public boolean attack_move(Checkers piece){


        //types of moves possible
        int up_left=0;
        int up_right=0;
        int down_left=0;
        int down_right=0;

        if (piece.king){//all pos moves
            if (piece.colour.equals("white")){
                up_right = check_attack(piece, 6,3,-1,-1,-1,up_right);
                up_left = check_attack(piece,3,3,-1,-1,-9,up_left);
            }
            else{//checked for black king
                down_left = check_attack(piece,3,6,1,1,7,down_left);
                down_right = check_attack(piece,6,6,-1,1,9,down_right);
            }
        }
        if(piece.colour.equals("white")){
            down_left = check_attack( piece , 3, 6, 1 , 1 , 7 , down_left );
            down_right = check_attack( piece , 6 , 6 , -1, 1 ,9 , down_right );
        }
        else{//normal black check
            up_right = check_attack( piece , 6, 3 , -1 , -1 , -7, up_right );
            up_left = check_attack( piece, 3 , 3 , 1 , -1 , -9 , up_left );
        }

//        identifies how many places to move and the direction
        if (piece.colour.equals("black") && up_left != 0 || up_right !=0 && down_left !=0 && down_right != 0){
            //if move predicted then its the negating move thats performed

            int val = up_left;
            up_left = down_left;
            down_left = val;

            int val2 = up_right;
            up_right = down_right;
            down_right = val2;

            int val3 = down_left;
            down_left = down_right;
            down_right = val3;

            int val4 = up_right;
            up_right = up_left;
            up_left = val4;

        }

        if (up_left != 0 || up_right != 0  || down_left != 0 || down_right != 0){//any possible moves
            return true;
        }
        else{
            return false;
        }
    }


    //passes in the index
    public boolean make_move(int index, String move_type){
        boolean isMove = false;
        boolean must_attack = false;

        if (!game_started){
            return false; //if no counter selected but a square was selected
        }else if (moveUpLeft != 1 || moveUpRight != 1 || moveDownLeft != 1 || moveDownRight != 1){//1 means move possible else no
            remove_road();//in any case removed highlighted roads even if no move possible
            return false; // no move possible
        }


        /*
        ..may need to reapply value moves for differnet users since they are different
         */


        if (attack_possible){
            player_turn = 2;//used to multiply the right values to set the counter

        }
        else{
            player_turn =  1;

            if (moveUpRight == 1){
                isMove = true;
                if (the_checker == w_checkers){
                    execute_move("white",index,player_turn * 1, player_turn * 1, player_turn * 9);
                    eliminate_check(index);
                }
                else{
                    execute_move("black",index,player_turn * 1, player_turn - 1, player_turn * -7);
                    eliminate_check(index + 7);
                }

            }
            if (moveUpLeft == 1){
                isMove = true;
                if (the_checker == w_checkers){
                    execute_move("white",index,player_turn * -1, player_turn * 1, player_turn * 7);
                    eliminate_check(index -7);
                }
                else{
                    execute_move("black",index,player_turn * -1, player_turn - 1, player_turn * -9);
                    eliminate_check(index + 9);
                }
            }

            if (the_checker[selected_piece].isKing()){
                if (moveDownRight == 1){
                    isMove = true;
                    if (the_checker == w_checkers){
                        execute_move("white",index,player_turn * 1, player_turn * -1, player_turn * -9);
                        eliminate_check(index);
                    }
                    else{
                        execute_move("black",index,player_turn * 1, player_turn * 1, player_turn * 9);
                        eliminate_check(index - 7);
                    }
                }
                if (moveDownLeft == 1){
                    isMove = true;
                    if (the_checker == w_checkers){
                        execute_move("white",index,player_turn * -1, player_turn * -1, player_turn * -9);
                        eliminate_check(index);
                    }
                    else{
                        execute_move("black",index,player_turn * -1, player_turn * 1, player_turn * 7);
                        eliminate_check(index - 7);
                    }

                }
            }
        }
        remove_road();
        the_checker[selected_piece].checkIfKing();

        if (isMove){
            if (must_attack){
                another_move = attack_move(the_checker[selected_piece]);
            }
            if (another_move){
                show_moves(the_checker[selected_piece]);
            }
            else{
                change_turns(the_checker);
                boolean game_over = check_if_lost(the_checker);
                if (game_over == true){
                    declare_winner();
                }
                return false;
            }
        }
    return false;
    }

    public  void eliminate_check(int index){
        if (index < 0 && index > 65){
            //need to set to false in the board so its not longer alive
            Checkers piece_id = CheckersSquare.block[index].getPieceId();
            piece_id.setAlive(false);//that piece sets it self to false
            CheckersSquare.block[index].setOccupied(false);
//            display then need to be set in the f/e
        }
        else{
            System.out.println("Unable to eleimiate");
        }

    }


    public void change_turns(Checkers[] player){
        if (player == w_checkers ){
            the_checker = b_checkers;
        }
        else{
            player = w_checkers;
        }


    }

    public boolean check_if_lost(Checkers[] player){//pass in the array of current players

        for (int i=0;i < player.length;i++){
            if (player[i].isAlive()){
                return false;
            }
        }
        return true;
    }

    public void declare_winner(){
//        msg to f/e that game has been won.
    }




    public void remove_road(){
        //msg to f/e to remove the applied colours.



    }

    public int check_attack(Checkers piece, int X, int Y, int negX, int negY,int squareMove, int direction ){

        if (piece.coordX * negX >= negX * X && piece.coordY * negY <= Y * negY && CheckersSquare.block[piece.occupiedSquare + squareMove].isOccupied() && CheckersSquare.block[piece.occupiedSquare + squareMove].getPieceId().colour != piece.colour && !CheckersSquare.block[piece.occupiedSquare + squareMove * 2].isOccupied()){
            attack_possible = true;
            direction = (piece.occupiedSquare + squareMove * 2);
            return direction;//when returned, goes through handler and colour val applied to new pos
        }
        else{
            direction = 0;
            return direction;
        }

    }


    public  void execute_move(String cur_player,int index, int X, int Y, int nSquare){
        if (cur_player == "White"){
            w_checkers[index].changeCoordinates(X,Y);
            w_checkers[index].setCoordinates(0,0);
            CheckersSquare.block[w_checkers[index].occupiedSquare].setOccupied(false);

            CheckersSquare.block[w_checkers[index].occupiedSquare+nSquare].setOccupied(true);// id and piece id in sqaure class different. piece id are assigned in the initialization and set it to the value of it
            CheckersSquare.block[w_checkers[index].occupiedSquare+nSquare].setPieceId(CheckersSquare.block[w_checkers[index].occupiedSquare].getPieceId());
            CheckersSquare.block[w_checkers[index].occupiedSquare].setPieceId(null);
            w_checkers[index].occupiedSquare += nSquare;//so this index has moves this many places on the board
        }
        else{
            b_checkers[index].changeCoordinates(X,Y);
            b_checkers[index].setCoordinates(0,0);
            CheckersSquare.block[b_checkers[index].occupiedSquare].setOccupied(false);

            CheckersSquare.block[b_checkers[index].occupiedSquare+nSquare].setOccupied(true);// id and piece id in sqaure class different. piece id are assigned in the initialization and set it to the value of it
            CheckersSquare.block[b_checkers[index].occupiedSquare+nSquare].setPieceId(CheckersSquare.block[b_checkers[index].occupiedSquare].getPieceId());
            CheckersSquare.block[b_checkers[index].occupiedSquare].setPieceId(null);
            b_checkers[index].occupiedSquare += nSquare;//so this index has moves this many places on the board

        }

    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

}
