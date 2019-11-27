/**JS code handles all front-end functionality and communicating with server via web-sockets.
 * @author Azky & calincojo (Used this as reference for the front-end implementation https://codepen.io/calincojo/pen/wBQqYm)
 */
/*
I have use this as reference. , though most of the code are changed.
 */
var user;
var room_value;;
var room_action;
var difficulty;
var players= [];
var chat = false;
var game_started = false;
var game_started2 = false;
var user_permit_val = 0;//add 1 to get the value held by the user
var player_game;//1/2
var permit_obtained = false;
var already_opened = false;
var player_id = undefined;
//first game
var square_class = document.getElementsByClassName("square");
var square_class2 = document.getElementsByClassName("square2");
var white_checker_class = document.getElementsByClassName("white_checker");
var black_checker_class = document.getElementsByClassName("black_checker");
var white_checker_class2 = document.getElementsByClassName("white_checker2");
var black_checker_class2 = document.getElementsByClassName("black_checker2");

//second game


var moveSound = document.getElementById("moveSound");
var winSound = document.getElementById("winSound");
var windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
var windowWidth =  window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
var moveLength = 50 ;
var moveDeviation = 6;
var moveLength2 = 150 ;
var moveDeviation2 = 7;

var cur_big_screen = 1;//takes in the curr pos of the screen

//appended 2 represents the 2nd game, whilst without it is tfor the first game
var block = [];
var w_checker = [];
var b_checker = [];
var block2 = [];
var w_checker2 = [];
var b_checker2 = [];
var the_checker = undefined;
var the_checker2 = undefined ;
var user_action;
var user_action2;


$(document).ready(function(){
    //error with this function. may need to remove this
    // document.getElementById("body_id").src = "bkground.png";
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

        // game.adjust_screen_size(moveLength,moveDeviation);

        // if(cur_big_screen !== screen_check){
        //     for(var i = 1; i <= 12; i++){
        //         b_checker[i].set_coords(0,0);
        //         w_checker[i].set_coords(0,0);
        //     }
        //
        // }

    };
});

function enterName(){

    user = $("#id_name_value").val();//gets users name
    // room_value = $("#rm_nm_value").val();
    // room_action = "create_room";

    // start_game();
    /* we show the buttons to create room, join room and chat*/
    // document.getElementById('div_id_menu').style.display = "block";
    document.getElementById('div_id_name').style.display = "none";
    document.getElementById('div_id_menu').style.display = "block";
    // document.getElementById('table2').style.display = "block";
    // document.getElementById('game_status').style.display = "block";
    // document.getElementById('game_status2').style.display = "block";
    // document.getElementById('div_id_menu').innerHTML = "White";


}


/*When pressing create room we are asked to enter room name and room type*/
function create_room(){

    room_action = "create_room";
    /*we show the elements to create room and hide what we don't need*/
    document.getElementById('div_id_menu').style.display = "none";
    document.getElementById('div_id_room_settings').style.display = "block";
    document.getElementById('div_id_chat').style.display = "none";
    document.getElementById('console').style.height = "90%";

}


/*When we join the room we are asked for the name of the room*/
function join_a_room(){
    room_action = "join_room";
    //to use the above for the real design
    // room_value = $("#rm_nm_value").val();
    // room_action = "join_room";
    /*we show the buttons to create room, join room and chat*/
    // document.getElementById('div_id_menu').style.display = "block";
    // start_game();

    document.getElementById('div_id_menu').style.display = "none";
    document.getElementById('div_id_room_settings').style.display = "block";
    // game_2.initialize_snd_game();
``
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


    players.push(new Player(user));
    updatePlayerBox();
    chat =  true;

}

function enter_game_room(){
    /*we show the canvas and we worship the rest of the elements*/

    document.getElementsByTagName("body").style.backgroundColor = "#ffffff";
    document.getElementById('div_id_room_settings').style.display = "none";
    document.getElementById('table').style.display = "block";

    document.getElementById('table').style.display = "block";
    document.getElementById('table2').style.display = "block";
    document.getElementById('game_status').style.display = "block";
    document.getElementById('game_status2').style.display = "block";

    /*weGetTheValues​​toCreateTheRoom*/
    room_value = $("#rm_nm_value").val();

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
    var msg = {"type" : "start_game", "room_value": room_value,"player_id" : player_id};//main user triggers this btn
    game_started = true;
    document.getElementById('start_div').style.display = "none";
    game.send_data(msg);
}

function start_game_btn2(){
    console.log(" 2 game has been started");
    var msg = {"type" : "start_game2", "room_value": room_value,"player_id" : player_id};//main user triggers this btn
    game_started2 = true;
    document.getElementById('start_div2').style.display = "none";
    game.send_data(msg);
}

class checkers_squares {
    constructor(square, index) {
        this.id = square;
        this.occupied = false;
        this.piece_id = undefined;
        this.id.onclick = function () {
            if (game_started && player_game == 1) {
                game.make_move(index);
            }
            else if (game_started2 && player_game == 2){
                game2.make_move(index);
            }
            else{
                alert("Game not started. Please wait for the other user to join.")
            }

        }
    }
}

class checkers{
        constructor (piece,colour,square,index){
            this.id = piece;
            this.color = colour;
            this.index = index;

            if(square%8){
                this.coordX= square%8;
                this.coordY = Math.floor(square/8) + 1 ;
            }
            else{
                this.coordX = 8;
                this.coordY = square/8 ;
            }
            this.id.onclick = function () {
                if (game_started && player_game == 1) {
                    game.show_moves(index, colour);
                }
                else if(game_started2 && player_game == 2){
                    game2.show_moves(index, colour);
                }
                else{
                    alert("Game not started. Please wait for the other user to join.")
                }
            }

        }
        set_coords(X,Y){
            var x = (this.coordX - 1  ) * moveLength + moveDeviation;
            var y = (this.coordY - 1 ) * moveLength  + moveDeviation;
            this.id.style.top = y + 'px';
            this.id.style.left = x + 'px';
        }
        set_second_game_coords(X,Y){
            var x = (this.coordX - 1  ) * moveLength + moveDeviation;
            var y = (this.coordY - 1 ) * moveLength  + moveDeviation;
            this.id.style.top = y + 'px';
            this.id.style.left = x+17 + 'px';
         }
        move_coords(X,Y,game_no){
            // var x = (this.coordX - 1  ) * moveLength + moveDeviation;
            // var y = (this.coordY - 1 ) * moveLength  + moveDeviation;
            // console.log("Here we go", X,Y,x,y,this.id);
            if (game_no == 1){
                console.log("game no 1 move ");
                this.id.style.top = Y + 'px';
                this.id.style.left = X + 'px';
            }
            else if (game_no == 2){
                console.log("Game no 2 move")
                this.id.style.top = Y + 'px';
                let new_x = parseInt(X) + 17;
                this.id.style.left = new_x + 'px';
            }


        }


}
//used to check the player of the game to use the right game obj.
class Player {

    constructor(name) {
        this.name = name;
    }

    get_plyr_name(){
        return this.name;

    }

}

//A game class
class Game {
    send_data(data){
        var data = JSON.stringify(data);
        console.log("send packet ",data);
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

    initialize_second_game(){

        /*===============initializingThePlayingFields =================================*/
        for (var i = 1; i <= 64; i++)
        {
            block2[i] = new checkers_squares(square_class2[i], i);

        }
        /*================initializarea white black counters =================================*/
        // white Ladies
        for (var i = 1; i <= 4; i++) {
            w_checker2[i] = new checkers(white_checker_class2[i], "white", 2 * i - 1,i);
            w_checker2[i].set_second_game_coords(0, 0);
            block2[2 * i - 1].occupied = true;
            block2[2 * i - 1].pieceId = w_checker2[i];
        }

        for (var i = 5; i <= 8; i++) {
            w_checker2[i] = new checkers(white_checker_class2[i], "white", 2 * i,i);
            w_checker2[i].set_second_game_coords(0, 0);
            block2[2 * i].occupied = true;
            block2[2 * i].pieceId = w_checker2[i];
        }

        for (var i = 9; i <= 12; i++) {
            w_checker2[i] = new checkers(white_checker_class2[i], "white", 2 * i - 1,i);
            w_checker2[i].set_second_game_coords(0, 0);
            block2[2 * i - 1].occupied = true;
            block2[2 * i - 1].pieceId = w_checker2[i];
        }

        //black Ladies
        for (var i = 1; i <= 4; i++) {
            b_checker2[i] = new checkers(black_checker_class2[i], "black", 56 + 2 * i,i);
            b_checker2[i].set_second_game_coords(0, 0);
            block2[56 + 2 * i].occupied = true;
            block2[56 + 2 * i].pieceId = b_checker2[i];
        }

        for (var i = 5; i <= 8; i++) {
            b_checker2[i] = new checkers(black_checker_class2[i], "black", 40 + 2 * i - 1,i);
            b_checker2[i].set_second_game_coords(0, 0);
            block2[40 + 2 * i - 1].occupied = true;
            block2[40 + 2 * i - 1].pieceId = b_checker2[i];
        }

        for (var i = 9; i <= 12; i++) {
            b_checker2[i] = new checkers(black_checker_class2[i], "black", 24 + 2 * i,i);
            b_checker2[i].set_second_game_coords(0, 0);
            block2[24 + 2 * i].occupied = true;
            block2[24 + 2 * i].pieceId = b_checker2[i];
        }
        user_action2 = "initialize";
        this.connect();

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

    change_turn_game_2(){
        if (the_checker2 === w_checker2){
            the_checker2 = b_checker2;
            document.getElementById("cur_player_img_id2").src = "black_checker.jpg"
        }
        else if (the_checker2 ===  b_checker2){
            the_checker2 = w_checker2;
            document.getElementById("cur_player_img_id2").src = "white_checker.png"
        }
        else{
            the_checker2 = w_checker2
        }
    }


    show_moves(index,colour)
    {

        //To be improved
        //Allows for user click validations
        // if (player_game === 1){
        //     if (the_checker === b_checker && colour === "white") {
        //         alert("It's the black player's turn");
        //         return false;
        //     } else if (the_checker === w_checker && colour === "black") {
        //         alert("It's the white player's turn");
        //         return false;
        //     }
        // }
        // else if (player_game === 2){
        //     if (the_checker2 === b_checker2 && colour === "white") {
        //         alert("It's the black player's turn for game 2");
        //         return false;
        //     } else if (the_checker2 === w_checker2 && colour === "black") {
        //         alert("It's the white player's turn for gamwe 2");
        //         return false;
        //     }
        // }

        console.log("square selected index, ", index );
        user_action = "show_moves";
        var str = {"type" : "show_moves","room_action" : "N/A","room_value" : room_value,"index" : index ,"player_id":player_id,"player_colour" : colour};
        var json_str = JSON.stringify(str);
        this.socket.send(json_str);
    }



    //var str = {"type" : "user","user_action" : user_action,"room_action" : "N/A","room_value" : room_value,"index" : index ,"player_colour" : colour};
    //send msg to b/e when these methods are triggered. so wont need the game loop, i.e. no bad performance
    make_move(index){
        console.log("square clicked");
        // var str = {"type" : "make_move","index" : index};
        user_action = "make_move";
        var str = {"type" : "make_move","room_action" : "N/A","room_value" : room_value,"sqr_index" : index,"player_id":player_id};
        var json_str = JSON.stringify(str);
        this.socket.send(json_str);
    }


    remove_road(upLeft,upRight,downLeft,downRight,cur_game){ //to check this
        console.log(downRight,downLeft,upRight,upLeft );
        if (cur_game == 1) {
            if (downRight > 0) block[downRight].id.style.background = "#BA7A3A";
            if (downLeft > 0) block[downLeft].id.style.background = "#BA7A3A";
            if (upRight > 0) block[upRight].id.style.background = "#BA7A3A";
            if (upLeft > 0) block[upLeft].id.style.background = "#BA7A3A";
        }
        else if (cur_game == 2){
            if (downRight > 0) block2[downRight].id.style.background = "#BA7A3A";
            if (downLeft > 0) block2[downLeft].id.style.background = "#BA7A3A";
            if (upRight > 0) block2[upRight].id.style.background = "#BA7A3A";
            if (upLeft > 0) block2[upLeft].id.style.background = "#BA7A3A";
        }
    }

    apply_road(index,cur_game){
        if (index > 0 && cur_game == 1 ){
            block[index].id.style.background = "#704923";
        }
        else if (index >0 && cur_game == 2){
            block2[index].id.style.background = "#704923";
        }
    }

    move_attack(index,game_no){
        if (index > 0 && game_no == 1 ){
            if (index > 0) block[index].id.style.background = "#007010";
        }
        else if (index >0 & game_no == 2){
            if (index > 0) block2[index].id.style.background = "#007010";
        }

    }

    non_attack_move(id,x,y){
        console.log("-----",x,y,id);
        this.id.style.top = y + 'px';
        id.style.left = x + 'px';
    }


    /*connect to the server and define the socket methods*/
    connect() {
        user_action = "initialize";
        this.socket = new WebSocket("ws://127.0.0.1:8080/springboot");

        /*startTheConnection*/
        this.socket.onopen = () => {
            if (!already_opened) {
                console.log('Info: WebSocket connection opened.');
                // weSendTheUserToTheServer
                already_opened = true;
                this.open();
            }
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
            console.log("packet received :", packet);
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
                    var game_move = packet.game_no; //so 1 /2 and elow upodate it as necesary
                    game.apply_road(parsed_val,game_move);
                    break;

                case 'remove_road':
                    var up_left = packet.up_left;
                    var up_right = packet.up_right;
                    var down_left = packet.down_left;
                    var down_right = packet.down_right;
                    var game_move = packet.game_no;
                    game.remove_road(up_left,up_right,down_left,down_right,game_move);
                    break;

                case 'move_attack':
                    console.log("make the move");
                    var index = packet.data;
                    var game_move = packet.game_no;
                    game.move_attack(index,game_move);
                    //once move made, set the other players turn
                    if (game_move == 1){
                        game.change_turns();
                    }
                    else if(game_move == 2){
                        game2.change_turn_game_2();
                    }
                    // game.change_turns();
                    break;

                case 'eliminate_piece':
                    console.log("Piece elimination");
                    let elim_piece_id = packet.data;
                    var game_move = packet.game_no;
                    console.log(elim_piece_id);

                    if (game_move == 1){
                        the_checker[elim_piece_id].id.style.display = "none";
                        //make the move on game player and then update it on the other players
                    }
                    else if (game_move == 2){
                        the_checker2[elim_piece_id].id.style.display = "none";
                    }
                    break;

                case 'non_attack_move':// p - the_checker enabled one f/e and nt the other and hence undefined
                    console.log("non-attack move");
                    var piece_id = packet.id;
                    var x_coord = packet.X;
                    var y_coord = packet.Y;
                    var game_move = packet.game_no; //so 1 /2 and elow upodate it as necesary

                    //wrong approach
                    if (game_move == 1){
                        the_checker[piece_id].move_coords(x_coord,y_coord,game_move);
                        game.change_turns();
                    }
                    else if(game_move == 2){
                        the_checker2[piece_id].move_coords(x_coord,y_coord,game_move);
                        game2.change_turn_game_2();
                    }

                    console.log("Move_made");
                    // game.non_attack_move(piece_id,x_coord,y_coord);
                    break;

                case "create_room_resp":
                    if (packet.data === "Ok") {
                        player_id = packet.player_id;
                        console.log("room created");
                    }
                    break;

                case "player_joined"://start btn displayed for owner of the game. x-> shown joined player
                    if (packet.data === "successful"){
                        player_id = packet.player_id;
                        document.getElementById("start_div").style.display = "block";
                        console.log("530");
                    }
                    else{
                        console.log(packet.data);
                    }
                    break;
                case "player_joined2"://start btn displayed for owner of the game. x-> shown joined player
                    if (packet.data === "successful"){
                        player_id = packet.player_id;
                        if (player_id == 4){
                            document.getElementById("start_div2").style.display = "block";
                        }
                    }
                    else{
                        console.log(packet.data);
                    }
                    break;
                case "start_game_1":
                    if (packet.data === "rdy"){
                        game_started = true;
                        console.log("Game 1 is rdy to start");
                        document.getElementById("start_div").style.display = "none";
                        the_checker = w_checker;//to begin with
                    }
                    break;
                case "start_game_2":
                    if (packet.data === "rdy_2"){
                        game_started2 = true;
                        console.log("Game 2 is rdy to start");
                        document.getElementById("start_div").style.display = "none";
                        the_checker2 = w_checker2;//to begin with
                    }
                    break;
                case "room_permits":// Iniz here?
                    user_permit_val  = packet.data;
                    // console.log(user_permit_val == 4);
                    if (user_permit_val == 4 || user_permit_val == 3 ){
                        //will play the first game
                        console.log("user for first game");
                        permit_obtained = true;
                        game.initialize_game();
                        game2.initialize_second_game();//used for testing only
                        player_game = 1;
                    }
                    else if (user_permit_val == 2 || user_permit_val == 1){
                        console.log("user for second game");
                        game.initialize_game();//to see game 1
                        game2.initialize_second_game();
                        player_game = 2;
                    }
                    //after this then i can perform the initialization, whether game obj, game2 obj
                    break;
            }
        }
    }

    /*only runs once and communicates the needed msg at first and does all needed once in the case statements*/
    open() { //
        var msg = {"type": "user", "user_action":user_action, "room_action" : room_action,"room_value" : room_value, "difficulty_lvl" : difficulty};
        var json_str=JSON.stringify(msg);
        this.socket.send(json_str);

        setTimeout(function () {
            if (!permit_obtained){
                var check_permits = {"type": "get_room_permits", "room_value" : room_value};
                game.send_data(check_permits);
            }
        },3000);

    }
}

// multiple objects for multiple games
let game = new Game();
let game2 = new Game();

function start_game(){
    game.connect();

}

// setInterval(()=> {
//     var msg = {"type" : "ping"};
//     game.send_data(msg);
// },15000);


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
