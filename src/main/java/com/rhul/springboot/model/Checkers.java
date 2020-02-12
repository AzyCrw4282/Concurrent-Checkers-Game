package com.rhul.springboot.model;

import com.rhul.springboot.utils.BugsnagConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.SQLException;

/** This class handles all board related functionalities. An instance is user for each game, so 2 players use 1 game.
 * Some of the algorithms that check and performs checker move has been taken and adpted from the repo of the mentioned author.
 * @author Azky & Elise(From github)
 */
@Getter @Setter @NoArgsConstructor
public class Checkers {

    private int id;
    private String colour;
    private String game_type;
    private boolean king = false;
    private boolean game_started = false;
    private boolean one_move = false;
    private Room rm;
    public Checkers[] w_checkers = new Checkers[13];
    public Checkers[] b_checkers = new Checkers[13];
    private int coordX;
    private int coordY;
    private boolean alive;
    private int occupiedSquare;
    private int selected_piece;
    private boolean attack_possible = false;
    private Checkers the_checker[];
    private Player plyr;
    private String plyr_name;
    private CheckersSquare cs;


    public static int move_length = 50;
    public static int move_deviation = 6;

    int reverse_table_limit;
    int table_limit;
    int table_limit_right;
    int table_limit_left;
    int move_up_right;
    int move_up_left;
    int move_down_right;
    int move_down_left;
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

    public Checkers(int square_piece, String colour, int square){
        this.id = square_piece;
        this.colour = colour;
        this.game_type = game_type;
        this.king = false;
        this.occupiedSquare = square;
        this.alive = true;
        this.coordX = getXcoordinates(square);
        this.coordY = getYcoordinates(square);
    }

    public int getXcoordinates(int square){
        if (square % 8 != 0){
            this.coordX = square % 8;
            return(coordX);
        }
        else{
            this.coordX = 8;
            return(coordX);
        }

    }

    public int getYcoordinates(int square){
        if (square % 8 != 0){
            this.coordY = (int) Math.floor(square/8) + 1;
            return(coordY);
        }
        else{
            this.coordY = (square/8);
            return(coordY);
        }
    }


    public void setCoordinates(int X ,int Y){

        X = (this.coordX-1) * move_length + move_deviation;
        Y = (this.coordY-1) * move_length + move_deviation;

    }

    public void changeCoordinates(int X, int Y){
        this.coordX += X;
        this.coordY += Y;
    }

    public void checkIfKing(){

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

    public synchronized boolean show_moves(Checkers piece,Room rm,String cur_player){
        System.out.println("prev selected piece: " + selected_piece);
        this.plyr_name =cur_player;

        boolean match =false;
        attack_possible = false;


        if(selected_piece > 0){
            apply_front_changes(rm,0,"remove_road",null);
        }else if (piece.id > 0){
            selected_piece = piece.id;
        }


        int i =0,j = 0;

        for (j=1;j<=12;j++){
            if (w_checkers[j].equals(piece)){
                selected_piece = w_checkers[j].id;
                match = true;
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

        if ( one_move & !attack_move(piece,rm)){
            change_turns(the_checker);
            return false;
        }
        if ( one_move & !match){
            return (false);
        }

        if (piece.colour.equals("white")){
            table_limit = 8;
            table_limit_right = 1;
            table_limit_left = 8;
            move_up_right = 7;
            move_up_left = 9;
            move_down_right = -9;
            move_down_left = -7;
        }
        else if (piece.colour.equals("black")){
            table_limit = 1;
            table_limit_right = 8;
            table_limit_left = 1;
            move_up_right = -7;
            move_up_left = -9;
            move_down_right = 9;
            move_down_left = 7;
        }

        attack_move(the_checker[i],rm);

        if (!attack_possible){

            down_left = check_move(the_checker[i], table_limit , table_limit_right , move_up_right , down_left,rm);
            down_right = check_move( the_checker[i] , table_limit , table_limit_left , move_up_left , down_right,rm);


            if (the_checker[i].isKing()){
                up_left = check_move( the_checker[i] , reverse_table_limit , table_limit_right , move_down_right , up_left,rm);
                up_right = check_move( the_checker[i], reverse_table_limit , table_limit_left , move_down_left, up_right,rm);
            }
        }

        if (up_left != 0 || up_right != 0  || down_left != 0 || down_right != 0){
            return true;
        }
        else{
            return false;
        }

    }

    public synchronized boolean make_move(int index,String colour,Room rm,String cur_player){

        boolean isMove = false;
        boolean must_attack = false;
        this.plyr_name = cur_player;

        if (!game_started & selected_piece == 0){
            return false;
        }else if (up_left != index && up_right != index && down_left != index && down_right != index){
            apply_front_changes(rm,0,"remove_road",null);
            return false;
        }


        if (colour.equals("white")){

            move_up_right = down_right;
            move_up_left = down_left;
            move_down_right = up_right;
            move_down_left = up_left;
        }
        else if (colour.equals("black")){

            move_up_right = down_left;
            move_up_left = down_right;
            move_down_right = up_left;
            move_down_left = up_right;
        }


        if (attack_possible){
            move_factor = 2;
        }
        else{
            move_factor =  1;
        }
        if (move_up_right == index){
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
        if (move_up_left == index){
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
            if (move_down_right == index){
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
            if (move_down_left == index){
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
        the_checker[selected_piece].checkIfKing();

        if (isMove){
            if (must_attack){
                another_move = attack_move(the_checker[selected_piece],rm);
            }
            if (another_move){
                show_moves(the_checker[selected_piece],null,this.plyr_name);
            }
            else{
                change_turns(the_checker);
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
            if (piece.coordX != LimitSide && !cs.block[piece.occupiedSquare + moveDirection].isOccupied()){
                int value = piece.occupiedSquare + moveDirection;
                java.lang.String sdfg = "";

                apply_front_changes(rm,value,"Looking_For_Moves",null);
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
            apply_front_changes(rm,direction,"Move_Attack",null);
            return direction;
        }
        else{
            direction = 0;
            return direction;
        }
    }

    public boolean attack_move(Checkers piece, Room rm){


        up_left=0;
        up_right=0;
        down_left=0;
        down_right=0;

        if (piece.king){
            if (piece.colour.equals("white")){
                up_right = check_attack(piece, 6,3,-1,-1,-1,up_right,rm);
                up_left = check_attack(piece,3,3,1,-1,-9,up_left,rm);
            }
            else{
                down_left = check_attack(piece,3,6,1,1,7,down_left,rm);
                down_right = check_attack(piece,6,6,-1,1,9,down_right,rm);
            }
        }
        if(piece.colour.equals("white")){
            down_left = check_attack( piece , 3, 6, 1 , 1 , 7 , down_left,rm );
            down_right = check_attack( piece , 6 , 6 , -1, 1 ,9 , down_right,rm );

        }
        else{
            up_right = check_attack( piece , 6, 3 , -1 , -1 , -7, up_right,rm );
            up_left = check_attack( piece, 3 , 3 , 1 , -1 , -9 , up_left,rm );
        }


        if (piece.colour.equals("black") && up_left != 0 || up_right !=0 && down_left !=0 && down_right != 0){


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

        if (up_left != 0 || up_right != 0  || down_left != 0 || down_right != 0){
            return true;
        }
        else{
            return false;
        }
    }

    public  void execute_move(String cur_player,int index, int X, int Y, int nSquare,Room rm,Checkers piece){
        if (cur_player.equals("white")){
            w_checkers[selected_piece].changeCoordinates(X,Y);
            apply_front_changes(rm,0,"Non_Attack_Move",w_checkers[selected_piece]);
            w_checkers[selected_piece].setCoordinates(0,0);
            cs.block[w_checkers[selected_piece].occupiedSquare].setOccupied(false);
            cs.block[w_checkers[selected_piece].occupiedSquare+nSquare].setOccupied(true);
            cs.block[w_checkers[selected_piece].occupiedSquare+nSquare].setPieceId(cs.block[w_checkers[selected_piece].occupiedSquare].getPieceId());
            cs.block[w_checkers[selected_piece].occupiedSquare].setPieceId(null);
            w_checkers[selected_piece].occupiedSquare += nSquare;
        }
        else if (cur_player.equals("black")){
            System.out.println("sdfsd "+ X + " "+ Y);
            b_checkers[selected_piece].changeCoordinates(X,Y);
            apply_front_changes(rm,0,"Non_Attack_Move",b_checkers[selected_piece]);
            b_checkers[selected_piece].setCoordinates(0,0);
            cs.block[b_checkers[selected_piece].occupiedSquare].setOccupied(false);
            cs.block[b_checkers[selected_piece].occupiedSquare+nSquare].setOccupied(true);
            cs.block[b_checkers[selected_piece].occupiedSquare+nSquare].setPieceId(cs.block[b_checkers[selected_piece].occupiedSquare].getPieceId());
            cs.block[b_checkers[selected_piece].occupiedSquare].setPieceId(null);
            b_checkers[selected_piece].occupiedSquare += nSquare;

        }

    }

    public  void eliminate_check(int index,Room rm) {
        if (index > 0 && index < 65){
            Checkers piece = cs.block[index].getPieceId();
            System.out.println("Eliminate Id " + piece + " index: " +index);
            piece.setAlive(false);
            cs.block[index].setOccupied(false);
            apply_front_changes(rm,piece.getId(),"Eliminate_Piece",null);

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

    public boolean check_if_lost(Checkers[] player){

        for (int i=1;i <= player.length;i++){
            if (player[i].isAlive()){
                return false;
            }
        }
        return true;
    }

    public void declare_winner(){
        try{
            boolean existing_user = plyr.getLeaderbd().check_if_usr_exists();
            if (existing_user){
                update_leaderbd_cmds();
            }
            else{//new user
                if (plyr.getLeaderbd().create_new_user()){
                    update_leaderbd_cmds();
                }
            }
        }catch (SQLException e){
            BugsnagConfig.bugsnag().notify(new RuntimeException("Error in declaring a winner. Likely to do with database update."));
            e.printStackTrace();
        }
    }

    public void update_leaderbd_cmds() throws SQLException {
        plyr.getLeaderbd().update_rank();
        plyr.getLeaderbd().update_games_competed();
        plyr.getLeaderbd().update_win_percent();
        plyr.getLeaderbd().update_long_win_streak();

    }

    public void apply_front_changes(Room rm,int square, String type,Checkers piece) {
        System.out.println("f/e change requested. Type : "+ type);
        String msg = "";
        switch (type) {
            case "Looking_For_Moves":
                msg = String.format("{\"type\": \"Looking_For_Moves\",\"data\":\"%s\"", square);
                break;
            case "remove_road":
                msg = String.format("{\"type\": \"remove_road\",\"up_left\":\"%d\",\"up_right\":\"%d\",\"down_left\":\"%d\",\"down_right\":\"%d\"", up_left,up_right,down_left,down_right);
                break;
            case "Eliminate_Piece":
                msg = String.format("{\"type\": \"Eliminate_Piece\",\"data\":\"%d\"", square);
                break;
            case "Move_Attack":
                msg = String.format("{\"type\": \"Move_Attack\",\"data\":\"%d\"", square);
                break;
            case "Non_Attack_Move":
                msg = String.format("{\"type\": \"Non_Attack_Move\",\"id\":\"%d\",\"X\":\"%d\",\"Y\":\"%d\"",piece.getId(), (piece.getCoordX()-1 ) * move_length + move_deviation,(piece.getCoordY()-1) * move_length + move_deviation);
                System.out.println(msg + " " + piece.getCoordX() + " " + piece.getCoordY());
                break;
        }
        rm.apply_to_room_users(msg,rm,plyr);
        rm.apply_game_status(rm,this.plyr_name,0,true,type);
    }

}
