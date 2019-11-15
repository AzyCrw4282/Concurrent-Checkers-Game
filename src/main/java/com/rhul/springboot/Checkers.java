package com.rhul.springboot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Array;

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
    private boolean one_move = false;
    private Room rm;
    public Checkers[] w_checkers = new Checkers[13];//array obj initized from 0-12 so works :)
    public Checkers[] b_checkers = new Checkers[13];
    private int coordX;
    private int coordY;
    private boolean alive;
    private int occupiedSquare;
    private int selected_piece;
    private boolean attack_possible = false;
    private Checkers the_checker[];
    private Player plyr;
    private CheckersSquare cs;

    //adjust screen sizes fix to coord x and y
    public static int move_length = 50;
    public static int move_deviation = 6;

    int reverse_tableLimit;
    int tableLimit;
    int tableLimitRight;
    int tableLimitLeft;
    int moveUpRight;
    int moveUpLeft;
    int moveDownRight;
    int moveDownLeft;
    private int move_factor;
    private int up_left =0;
    private int up_right=0;
    private int down_left=0;
    private int down_right=0;

    boolean another_move;

    public Checkers(Player plyr,CheckersSquare cS){
        this.plyr = plyr;
        this.cs = cS;
    }

    //keeps track of the board and counters. need this int game_id,
    public Checkers(int square_piece, String colour, int square){//add  player, String game_type ltr vrsions
        this.id = square_piece;
        this.colour = colour;
        this.game_type = game_type;
        this.king = false;
        this.occupiedSquare = square; //n number of occupied, ! eliminated
        this.alive = true;
        this.coordX = getXcoordinates(square);//both updated when initiazled
        this.coordY = getYcoordinates(square);
    }

    public int getXcoordinates(int square){
        if (square % 8 != 0){//if is square 9
            this.coordX = square % 8;
            return(coordX);
        }
        else{
            this.coordX = 8;
            return(coordX);
        }

    }

    public int getYcoordinates(int square){
        if (square % 8 != 0){//if is square 9
            this.coordY = (int) Math.floor(square/8) + 1;
            return(coordY);
        }
        else{
            this.coordY = (square/8);
            return(coordY);
        }
    }

    // used for f/e updating and hence nt required here :)
    public void setCoordinates(int X ,int Y){

        X = (this.coordX-1) * move_length + move_deviation;
        Y = (this.coordY-1) * move_length + move_deviation;

    }

    public void changeCoordinates(int X, int Y){
        this.coordX += X;
        this.coordY += Y;
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    // For clicking on square after selecting a counter - >order of checking ops: makemove once square clicked, then erase road( rests colour of the moved pieces to the same colour

    // clicking on counter -> show moves for the cur piece, and if no possible attackmoves and then changeTurns. set colour edges and so on. and perform attack move -> check attack. if last prev 2 ops not done then simple move checked without attack else nothing possible.


    //starting with coutner selections first
    //For now will havv method as static and synced so only one possible move at a time.
    public synchronized boolean show_moves(Checkers piece,Room rm){//clciked piece shoudl be passed in here
        System.out.println("prev selected piece: " + selected_piece);

        boolean match =false;
        attack_possible = false;


//        apply_front_changes(rm,0,"remove_road",null);//nt primitive type so can pass in null
        //Before setting selected piece remove roads

        if(selected_piece > 0){ //refers to id
            apply_front_changes(rm,0,"remove_road",null);//0 value ignored, passed as its the primitive
        }else if (piece.id > 0){
            selected_piece = piece.id;
        }


        int i =0,j = 0;

        for (j=1;j<=12;j++){
            if (w_checkers[j].equals(piece)){
                selected_piece = w_checkers[j].id;
                match = true; //game is on
                i = j;
                the_checker = w_checkers;
            }
            else if (b_checkers[j].equals(piece)){
                System.out.println("150");
                selected_piece = b_checkers[j].id;
                the_checker = b_checkers;
                i = j;
                match = true;
            }
        }

        if ( one_move & !attack_move(piece,rm)){//another move allowed after checking and its not attack->return false
            change_turns(the_checker);
            return false;
        }
        if ( one_move & !match){
            return (false);
        }

        if (piece.colour.equals("white")){
            tableLimit = 8;
            tableLimitRight = 1;
            tableLimitLeft = 8;
            moveUpRight = 7;
            moveUpLeft = 9;
            moveDownRight = -9;
            moveDownLeft = -7;
        }
        else if (piece.colour.equals("black")){// if black
            tableLimit = 1;
            tableLimitRight = 8;
            tableLimitLeft = 1;
            moveUpRight = -7;
            moveUpLeft = -9;
            moveDownRight = 9;
            moveDownLeft = 7;
        }

        attack_move(the_checker[i],rm);

        if (!attack_possible){//if attack not possible then make just a move

            down_left = check_move(the_checker[i], tableLimit , tableLimitRight , moveUpRight , down_left,rm);
            down_right = check_move( the_checker[i] , tableLimit , tableLimitLeft , moveUpLeft , down_right,rm);


            if (the_checker[i].isKing()){
                up_left = check_move( the_checker[i] , reverse_tableLimit , tableLimitRight , moveDownRight , up_left,rm);
                up_right = check_move( the_checker[i], reverse_tableLimit , tableLimitLeft , moveDownLeft, up_right,rm);
            }
        }

        if (up_left != 0 || up_right != 0  || down_left != 0 || down_right != 0){//any possible moves, 0's are default
            return true;
        }
        else{
            return false;
        }

    }

    //passes in the index
    public synchronized boolean make_move(int index,String colour,Room rm){

        boolean isMove = false;
        boolean must_attack = false;

        if (!game_started & selected_piece == 0){
            return false; //if no counter selected but a square was selected
        }else if (up_left != index && up_right != index && down_left != index && down_right != index){//1 means move possible else no
            apply_front_changes(rm,0,"remove_road",null);//0 value ignored, passed as its the primitive//in any case removed highlighted roads even if no move possible
            return false; // no move possible ..may need to reapply value moves for differnet users since they are different
        }


        if (colour.equals("white")){

            moveUpRight = down_right;
            moveUpLeft = down_left;
            moveDownRight = up_right;
            moveDownLeft = up_left;
        }
        else if (colour.equals("black")){// if black

            moveUpRight = down_left;
            moveUpLeft = down_right;
            moveDownRight = up_left;
            moveDownLeft = up_right;
        }


        if (attack_possible){
            move_factor = 2;//used to multiply to move the counter
        }
        else{
            move_factor =  1; //black player
        }
        if (moveUpRight == index){
            isMove = true;
            if (the_checker == w_checkers){
                execute_move("white",index,move_factor * 1, move_factor * 1, move_factor * 9,rm,the_checker[selected_piece]);

                if (attack_possible){eliminate_check(index-9,rm);}
            }
            else{
                execute_move("black",index,move_factor * 1, move_factor * - 1, move_factor * -7,rm,the_checker[selected_piece]);
                if (attack_possible){eliminate_check(index+7,rm);}
            }

        }
        if (moveUpLeft == index){
            isMove = true;
            if (the_checker == w_checkers){
                execute_move("white",index,move_factor * -1, move_factor * 1, move_factor * 7,rm,the_checker[selected_piece]);
                if (attack_possible){eliminate_check(index-7,rm);}
            }
            else{
                execute_move("black",index,move_factor * -1, move_factor * - 1, move_factor * -9,rm,the_checker[selected_piece]);
                if (attack_possible){eliminate_check(index+9,rm);}
            }
        }

        if (the_checker[selected_piece].isKing()){
            if (moveDownRight == index){
                isMove = true;
                if (the_checker == w_checkers){
                    execute_move("white",index,move_factor * 1, move_factor * -1, move_factor * -9,rm,the_checker[selected_piece]);
                    if (attack_possible){eliminate_check(index+7,rm);}
                }
                else{
                    execute_move("black",index,move_factor * 1, move_factor * 1, move_factor * 9,rm,the_checker[selected_piece]);
                    if (attack_possible){eliminate_check(index-9,rm);}
                }
            }
            if (moveDownLeft == index){
                isMove = true;
                if (the_checker == w_checkers){
                    execute_move("white",index,move_factor * -1, move_factor * -1, move_factor * -9,rm,the_checker[selected_piece]);
                    if (attack_possible){eliminate_check(index+9,rm);}
                }
                else{
                    execute_move("black",index,move_factor * -1, move_factor * 1, move_factor * 7,rm,the_checker[selected_piece]);
                    if (attack_possible){eliminate_check(index-7,rm);}
                }

            }
        }
        apply_front_changes(rm,0,"remove_road",null);
        the_checker[selected_piece].checkIfKing();//to apply f/e colour for kings

        if (isMove){
            if (must_attack){
                another_move = attack_move(the_checker[selected_piece],rm);
            }
            if (another_move){
                show_moves(the_checker[selected_piece],null);
            }
            else{

                change_turns(the_checker);// change turns nd check if the other has lost
                boolean game_over = check_if_lost(the_checker);
                if (game_over){
                    declare_winner();
                }
                return false;
            }
        }
        return true;
    }


    public int check_move(Checkers piece, int top_limit, int LimitSide, int moveDirection, int theDirection,Room rm){
        if (piece.coordY != top_limit){
            if (piece.coordX != LimitSide && !cs.block[piece.occupiedSquare + moveDirection].isOccupied()){//isOccupied is the root
                int value = piece.occupiedSquare + moveDirection;
                java.lang.String sdfg = "";
                // method call to apply ->send colour change to f/e
                apply_front_changes(rm,value,"apply_road",null);
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

    public int check_attack(Checkers piece, int X, int Y, int negX, int negY,int squareMove, int direction,Room rm ) {

        if (piece.coordX * negX >= negX * X && piece.coordY * negY <= Y * negY && cs.block[piece.occupiedSquare + squareMove].isOccupied() && cs.block[piece.occupiedSquare + squareMove].getPieceId().colour != piece.colour && !cs.block[piece.occupiedSquare + squareMove * 2].isOccupied()){
            attack_possible = true;
            direction = (piece.occupiedSquare + squareMove * 2);
            apply_front_changes(rm,direction,"move_attack",null);
            return direction;//when returned, goes through handler and colour val applied to new pos
        }
        else{
            direction = 0;
            return direction;
        }

    }


    public boolean attack_move(Checkers piece, Room rm){

        //types of moves possible
        up_left=0;
        up_right=0;
        down_left=0;
        down_right=0;

        if (piece.king){//all pos moves
            if (piece.colour.equals("white")){
                up_right = check_attack(piece, 6,3,-1,-1,-1,up_right,rm);
                up_left = check_attack(piece,3,3,1,-1,-9,up_left,rm);
            }
            else{//checked for black king
                down_left = check_attack(piece,3,6,1,1,7,down_left,rm);
                down_right = check_attack(piece,6,6,-1,1,9,down_right,rm);
            }
        }
        if(piece.colour.equals("white")){
            down_left = check_attack( piece , 3, 6, 1 , 1 , 7 , down_left,rm );
            down_right = check_attack( piece , 6 , 6 , -1, 1 ,9 , down_right,rm );

        }
        else{//normal black check
            up_right = check_attack( piece , 6, 3 , -1 , -1 , -7, up_right,rm );
            up_left = check_attack( piece, 3 , 3 , 1 , -1 , -9 , up_left,rm );
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

    public  void execute_move(String cur_player,int index, int X, int Y, int nSquare,Room rm,Checkers piece){//index is the board pos and nt counter id
        if (cur_player.equals("white")){
            w_checkers[selected_piece].changeCoordinates(X,Y);
            apply_front_changes(rm,0,"non_attack_move",w_checkers[selected_piece]);
            w_checkers[selected_piece].setCoordinates(0,0);
            cs.block[w_checkers[selected_piece].occupiedSquare].setOccupied(false);
            cs.block[w_checkers[selected_piece].occupiedSquare+nSquare].setOccupied(true);// id and piece id in sqaure class different. piece id are assigned in the initialization and set it to the value of it
            cs.block[w_checkers[selected_piece].occupiedSquare+nSquare].setPieceId(cs.block[w_checkers[selected_piece].occupiedSquare].getPieceId());
            cs.block[w_checkers[selected_piece].occupiedSquare].setPieceId(null);
            w_checkers[selected_piece].occupiedSquare += nSquare;//so this index has moves this many places on the board
        }
        else if (cur_player.equals("black")){
            System.out.println("sdfsd "+ X + " "+ Y);
            b_checkers[selected_piece].changeCoordinates(X,Y);
            apply_front_changes(rm,0,"non_attack_move",b_checkers[selected_piece]);
            b_checkers[selected_piece].setCoordinates(0,0);
            cs.block[b_checkers[selected_piece].occupiedSquare].setOccupied(false);
            cs.block[b_checkers[selected_piece].occupiedSquare+nSquare].setOccupied(true);// id and piece id in sqaure class different. piece id are assigned in the initialization and set it to the value of it
            cs.block[b_checkers[selected_piece].occupiedSquare+nSquare].setPieceId(cs.block[b_checkers[selected_piece].occupiedSquare].getPieceId());
            cs.block[b_checkers[selected_piece].occupiedSquare].setPieceId(null);
            b_checkers[selected_piece].occupiedSquare += nSquare;//so this index has moves this many places on the board

        }

    }


    public  void eliminate_check(int index,Room rm) {
        if (index > 0 && index < 65){
            Checkers piece = cs.block[index].getPieceId();
            System.out.println("Eliminate Id " + piece + " index: " +index);
            piece.setAlive(false);//that piece sets it self to false
            cs.block[index].setOccupied(false);
//            display then need to be set in the f/e
            apply_front_changes(rm,piece.getId(),"eliminate_piece",null);

        }
    }

    public void change_turns(Checkers[] player){
        if (player == w_checkers ){
            the_checker = b_checkers;
        }
        else{
            the_checker = w_checkers;
        }


    }

    public boolean check_if_lost(Checkers[] player){//pass in the array of current players

        for (int i=1;i <= player.length;i++){
            if (player[i].isAlive()){
                return false;
            }
        }
        return true;
    }

    public void declare_winner(){
//        msg to f/e that game has been won.
    }
    //Needs to be modifed to show moves to all users.
    public void apply_front_changes(Room rm,int square, String type,Checkers piece) {
        System.out.println("f/e change requested");
        String msg = "";
        switch (type) {
            case "apply_road":
                msg = String.format("{\"type\": \"apply_road\",\"data\":\"%s\"", square);
                break;
            case "remove_road":
                msg = String.format("{\"type\": \"remove_road\",\"up_left\":\"%d\",\"up_right\":\"%d\",\"down_left\":\"%d\",\"down_right\":\"%d\"", up_left,up_right,down_left,down_right);
                break;
            case "eliminate_piece":
                msg = String.format("{\"type\": \"eliminate_piece\",\"data\":\"%d\"", square);
                break;
            case "move_attack":
                msg = String.format("{\"type\": \"move_attack\",\"data\":\"%d\"", square);
                break;
            case "non_attack_move":
                msg = String.format("{\"type\": \"non_attack_move\",\"id\":\"%d\",\"X\":\"%d\",\"Y\":\"%d\"",piece.getId(), (piece.getCoordX()-1 ) * move_length + move_deviation,(piece.getCoordY()-1) * move_length + move_deviation);
                System.out.println(msg + " " + piece.getCoordX() + " " + piece.getCoordY());
                break;
        }
        rm.apply_to_room_users(msg,rm,plyr);
    }


}
