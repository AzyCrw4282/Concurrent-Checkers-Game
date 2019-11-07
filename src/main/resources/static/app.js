
/*
I have use this as reference. https://codepen.io/calincojo/pen/wBQqYm, though most of the code are changed.
 */
var user;
var room_value;;
var room_action;
var difficulty;
var players= [];
var chat = false;

//first game
var square_class = document.getElementsByClassName("square");
var white_checker_class = document.getElementsByClassName("white_checker");
var black_checker_class = document.getElementsByClassName("black_checker");
var table = document.getElementById("table");
var score = document.getElementById("score");

//second game


var moveSound = document.getElementById("moveSound");
var winSound = document.getElementById("winSound");
var windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
var windowWidth =  window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
var moveLength = 50 ;
var moveDeviation = 6;
var Dimension = 1;
var selectedPiece,selectedPieceindex;
var upRight,upLeft,downLeft,downRight;  // toate variantele posibile de mers pt o  dama
var contor = 0 , gameOver = 0;
var cur_big_screen = 1;//takes in the curr pos of the screen

var block = [];
var w_checker = [];
var b_checker = [];
var the_checker ;
var oneMove;
var anotherMove;
var mustAttack = false;
var multiplier = 1; // 2 daca face saritura 1 in caz contrat
var user_action;

var tableLimit,reverse_tableLimit ,  moveUpLeft, moveUpRight, moveDownLeft, moveDownRight , tableLimitLeft, tableLimitRight;

$(document).ready(function(){

    document.getElementsByTagName("BODY")[0].onresize = function(){

        getDimension();//vars here will also need to eb updated on f/e
        var screen_check = cur_big_screen ;

        if(windowWidth < 2550){
            moveLength = 50;
            moveDeviation = 6;
            if(cur_big_screen === 1){
                cur_big_screen = -1;
            }
        }
        if(windowWidth > 2550){
            moveLength = 80;
            moveDeviation = 10;
            if(cur_big_screen === -1) {
                cur_big_screen = 1;
            }
        }

        if(cur_big_screen !== screen_check){
            for(var i = 1; i <= 12; i++){
                b_checker[i].set_coords(0,0);
                w_checker[i].set_coords(0,0);
            }
            game.adjust_screen_size(moveLength,moveDeviation);
        }

    };
});

function enterName(){

    user = $("#id_name_value").val();
    room_value = $("#rm_nm_value").val();
    room_action = "create_room";
    start_game();
    /*we show the buttons to create room, join room and chat*/
    // document.getElementById('div_id_menu').style.display = "block";
    document.getElementById('div_id_name').style.display = "none";
    document.getElementById('table').style.display = "block";
    document.getElementById('game_status').style.display = "block";
    // document.getElementById('cur_player_id').innerHTML = "White";

}

/*When pressing create room we are asked to enter room name and room type*/
function create_room(){
    /*we show the elements to create room and hide what we don't need*/
    document.getElementById('div_id_menu').style.display = "none";
    document.getElementById('div_id_create_room').style.display = "block";
    document.getElementById('div_id_chat').style.display = "none";
    document.getElementById('console').style.height = "90%";

}

/*When we join the room we are asked for the name of the room*/
function join_a_room(){
    // document.getElementById('btnPrinc').style.display = "none";
    // // document.getElementById('div_id_create_room').style.display = "block";
    // document.getElementById('divChat').style.display = "none";
    // document.getElementById('console').style.height = "90%";

    //to use the above for the real design
    room_value = $("#rm_nm_value").val();
    room_action = "join_room";
    /*we show the buttons to create room, join room and chat*/
    // document.getElementById('div_id_menu').style.display = "block";
    document.getElementById('div_id_name').style.display = "none";
    document.getElementById('table').style.display = "block";
    document.getElementById('game_status').style.display = "block";
    start_game();

}

function getDimension (){
    windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
    windowWidth =  window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
}


/*weJoinARandomRoom*/
function action_matchmaking(){
    document.getElementById('btnPrinc').style.display = "none";
    document.getElementById('canvas').style.display = "block";
    document.getElementById('divChat').style.display = "none";
    document.getElementById('console').style.height = "90%";

    menu_option="MatchMaking";

    /*If we are in the chat we remove the user from the chat*/
    if (!chat){
        start_game()
    }else{
        let aux = {"type": "delete", "name": user};
        game.enviar(aux);
        $("#player-box").text("");
        chat = false;
        // game.open(); fault here
    }
}

/*weSelectTheChat*/
function enter_chat(){
    document.getElementById('divChat').style.display = "block";
    document.getElementById('chat').style.display = "none";
    document.getElementById('console').style.height = "400px";

    /*thePlayerIsAddedToTheChat*/
    sala = "-1";

    players.push(new Player(user));
    updatePlayerBox();
    chat =  true;
    juego();
}

function enter_game_room(){
    /*we show the canvas and we worship the rest of the elements*/

    document.getElementById('div_id_create_room').style.display = "none";
    document.getElementById('table').style.display = "block";

    /*weGetTheValues​​toCreateTheRoom*/
    room_value = $("#rm_name").val();

    difficulty = 0;

    if ($('#radio2').prop('checked')){
        difficulty = 1;
    }else if ($('#radio3').prop('checked')){
        difficulty = 2;
    }
    start_game();
}
//displayed when 2 users exists for nw
function start_game_btn(){
    console.log("game has been started");
    var msg = {"type" : "start_game"};
    document.getElementById('start_div').style.display = "none";
    game.send_data(msg);
}


class checkers_squares {
    constructor(square, index) {
        this.id = square;
        this.occupied = false;
        this.piece_id = undefined;
        this.id.onclick = function () {
            game.make_move(index);
        }
    }
}
class checkers{
        constructor (piece,colour,square,index){
            this.id = piece;
            this.color = colour;
            this.index = index;
            this.king = false;
            this.ocupied_square = square;
            if(square%8){
                this.coordX= square%8;
                this.coordY = Math.floor(square/8) + 1 ;
            }
            else{
                this.coordX = 8;
                this.coordY = square/8 ;
            }
            this.id.onclick = function () {
                game.show_moves(index,colour);
            }

        }
        set_coords(X,Y){
            var x = (this.coordX - 1  ) * moveLength + moveDeviation;
            var y = (this.coordY - 1 ) * moveLength  + moveDeviation;
            this.id.style.top = y + 'px';
            this.id.style.left = x + 'px';
        }
        move_coords(X,Y){
            // var x = (this.coordX - 1  ) * moveLength + moveDeviation;
            // var y = (this.coordY - 1 ) * moveLength  + moveDeviation;
            // console.log("Here we go", X,Y,x,y,this.id);
            this.id.style.top = Y + 'px';
            this.id.style.left = X + 'px';

        }

        change_coords(X,Y){
            this.coordY += Y;
            this.coordX += X;
        }

}

//these will have to be changed for new game method
// var square_p = function(square,index){
//     //this.gameId = game_id; //identifies the game of which there can be many
//     this.id = square;
//     this.occupied = false;
//     this.pieceId = undefined;
//     this.id.onclick = function(){
//         // b/e process to send to check for moves
//         game.make_move(index);
//
//     }
// };
//
// //these when implemented needs to be uniquely idenfitied for each game
// var checker = function(piece,color,square,index) {//unique idenfitification for each counter
//     this.id = piece;
//     this.color = color;
//     this.index = index;
//     this.king = false;
//     this.ocupied_square = square;
//     this.alive = true;
//     this.attack = false;
//     if(square%8){
//         this.coordX= square%8;
//         this.coordY = Math.floor(square/8) + 1 ;
//     }
//     else{
//         this.coordX = 8;
//         this.coordY = square/8 ;
//     }
//     //clickable function
//     this.id.onclick = function  () {
//         game.show_moves(index,color);//index is nt unique as they can be same for black/white
//     }
// };//identifies each checker

class Player {

    constructor(name) {
        this.name = name;
    }
}

//A game class
class Game {
    send_data(data){
        var data = JSON.stringify(data);
        this.socket.send(data)
    }

    constructor(){
        this.socket = null;
        this.fps = 30;

    }

    /*initializeTheGame*/
    initialize_game() {

        /*===============initializingThePlayingFields =================================*/
        for (var i = 1; i <= 64; i++)
        {
            block[i] = new checkers_squares(square_class[i], i);

        }
        /*================initializarea white black counters =================================*/
        // white Ladies
        for (var i = 1; i <= 4; i++) {
            w_checker[i] = new checkers(white_checker_class[i], "white", 2 * i - 1,i);
            w_checker[i].set_coords(0, 0);
            block[2 * i - 1].occupied = true;
            block[2 * i - 1].pieceId = w_checker[i];
        }

        for (var i = 5; i <= 8; i++) {
            w_checker[i] = new checkers(white_checker_class[i], "white", 2 * i,i);
            w_checker[i].set_coords(0, 0);
            block[2 * i].occupied = true;
            block[2 * i].pieceId = w_checker[i];
        }

        for (var i = 9; i <= 12; i++) {
            w_checker[i] = new checkers(white_checker_class[i], "white", 2 * i - 1,i);
            w_checker[i].set_coords(0, 0);
            block[2 * i - 1].occupied = true;
            block[2 * i - 1].pieceId = w_checker[i];
        }

        //black Ladies
        for (var i = 1; i <= 4; i++) {
            b_checker[i] = new checkers(black_checker_class[i], "black", 56 + 2 * i,i);
            b_checker[i].set_coords(0, 0);
            block[56 + 2 * i].occupied = true;
            block[56 + 2 * i].pieceId = b_checker[i];
        }

        for (var i = 5; i <= 8; i++) {
            b_checker[i] = new checkers(black_checker_class[i], "black", 40 + 2 * i - 1,i);
            b_checker[i].set_coords(0, 0);
            block[40 + 2 * i - 1].occupied = true;
            block[40 + 2 * i - 1].pieceId = b_checker[i];
        }

        for (var i = 9; i <= 12; i++) {
            b_checker[i] = new checkers(black_checker_class[i], "black", 24 + 2 * i,i);
            b_checker[i].set_coords(0, 0);
            block[24 + 2 * i].occupied = true;
            block[24 + 2 * i].pieceId = b_checker[i];
        }
        user_action = "initialize";
        this.connect();
    }
    //var str = {"type" : "user","user_action" : user_action,"room_action" : "N/A","room_value" : room_value,"index" : index ,"player_colour" : colour};
    //send msg to b/e when these methods are triggered. so wont need the game loop, i.e. no bad performance
    make_move(index){
        console.log("sqaure clicked");
        // var str = {"type" : "make_move","index" : index};
        user_action = "make_move";
        var str = {"type" : "user","user_action" : user_action,"room_action" : "N/A","room_value" : room_value,"index" : index};
        var json_str = JSON.stringify(str);
        this.socket.send(json_str);
    }

    //No corrds changes in b/e only display changes in f/e
    adjust_screen_size(move_length,move_dev){
        var str = {"type" : "adjust_screen_size","move_length" : move_length ,"move_dev" : move_dev};
        var json_str = JSON.stringify(str);
        this.socket.send(json_str);
        //once notifified, f/e changes applied


    }

    change_turns(){
        if (the_checker === w_checker){
            the_checker = b_checker;
            document.getElementById("cur_player_img_id").src = "black_checker.jpg"
        }
        else if (the_checker ===  b_checker){
            the_checker = w_checker;
            document.getElementById("cur_player_img_id").src = "white_checker.png"
        }
        else{
            the_checker = w_checker
        }
    }


    show_moves(index,colour)
    {
        //Allows for user click validations
        if (the_checker === b_checker && colour === "white") {
            alert("It's the black player's turn");
            return false;
        } else if (the_checker === w_checker && colour === "black") {
            alert("It's the white player's turn");
            return false;
        }

        //set colour here.
        if (colour === "white"){
            the_checker = w_checker;
        }
        else if (colour === "black"){
            the_checker = b_checker;
        }
        console.log("square selected index below");
        console.log(index);
        user_action = "show_moves";
        var str = {"type" : "user","user_action" : user_action,"room_action" : "N/A","room_value" : room_value,"index" : index ,"player_colour" : colour};
        var json_str = JSON.stringify(str);
        this.socket.send(json_str);
    }


    declare_winner(){
        //called after gamelost state is passed in conditional statement
        black_background.style.display = "inline";
        score.style.display = "block";

    }

    remove_road(upLeft,upRight,downLeft,downRight){ //to check this
        console.log(downRight,downLeft,upRight,upLeft );
        if(downRight >0 ) block[downRight].id.style.background = "#BA7A3A";
        if(downLeft >0) block[downLeft].id.style.background = "#BA7A3A";
        if(upRight >0) block[upRight].id.style.background = "#BA7A3A";
        if(upLeft >0) block[upLeft].id.style.background = "#BA7A3A";
    }

    apply_road(index){
        if (index > 0) block[index].id.style.background = "#704923";
    }

    move_attack(index){
        if (index > 0) block[index].id.style.background = "#007010";
    }

    non_attack_move(id,x,y){
        console.log("-----",x,y,id);
        this.id.style.top = y + 'px';
        id.style.left = x + 'px';
    }

    eliminate_check(index){//index on the board; may need other data soon
        if (index < 1 || index > 64){
            return false
        }
        else{
            var x = block[index].pieceId;//gets the piece id that was set on the board of checkers instance
            x.alive = false;
            block[index].occupied = false;
            x.id.style.display = "none"; //hides the piece

        }
    }

    /*connect to the server and define the socket methods*/
    connect() {
        this.socket = new WebSocket("ws://127.0.0.1:8080/springboot");
        /*startTheConnection*/
        this.socket.onopen = () => {
            console.log('Info: WebSocket connection opened.');
            // weSendTheUserToTheServer
            this.open();
        };

        /*closeTheConnection*/
        this.socket.onclose = () => {
            // let dic = {"type": "delete", "name": user};
            // // game.process_data(dic);
            // console.log('Info: WebSocket closed.');
            // this.stopGameLoop();
        };

        /*define the actions when receiving the different messages*/
        this.socket.onmessage = (message) => {
            var packet = JSON.parse(message.data);
            //handles message received from b/e. need work on this
            switch (packet.type) {
                /*removeAPlayerFromTheRoom*/
                case 'result_move':
                    if (packet.data === "possible"){
                        console.log("move possible")
                    }
                    break;

                case 'apply_road':
                    console.log(packet.data);
                    var parsed_val = parseInt(packet.data);
                    game.apply_road(parsed_val);
                    break;

                case 'remove_road':
                    var up_left = packet.up_left;
                    var up_right = packet.up_right;
                    var down_left = packet.down_left;
                    var down_right = packet.down_right;
                    game.remove_road(up_left,up_right,down_left,down_right);
                    break;
                case 'move_attack':
                    console.log("make the move");
                    var index = packet.data;
                    game.move_attack(index);
                    //once move made, set the other players turn
                    game.change_turns();
                    break;
                case 'eliminate_piece':
                    console.log("Piece elimination");
                    let elim_piece_id = packet.data;
                    console.log(elim_piece_id);
                    the_checker[elim_piece_id].id.style.display = "none";
                    break;
                case 'non_attack_move':
                    console.log("non-attack move");
                    var piece_id = packet.id;
                    var x_coord = packet.X;
                    var y_coord = packet.Y;
                    the_checker[piece_id].move_coords(x_coord,y_coord);
                    console.log("Move_made");
                    // game.non_attack_move(piece_id,x_coord,y_coord);
                    game.change_turns();
                    break;
                case "join_room_resp":
                    if (packet.data === "rdy_to_join"){
                        document.getElementById("start_div").style.display = "block";
                    }
                    else{
                        console.log(packet.data);
                    }
                    break;

            }
        }
    }

    /*only runs once and communicates the needed msg at first and does all needed once in the case statements*/
    open() { // tba -> "Sala":room, "difficulty":difficulty "user": user,
        var msg = {"type": "user", "user_action":user_action, "room_action" : room_action,"room_value" : room_value, "difficulty_lvl" : difficulty};
        var json_str=JSON.stringify(msg);
        this.socket.send(json_str);

    }
}


let game = new Game();//may need multiple objects for multiple games

function start_game(){
    game.initialize_game();

}
