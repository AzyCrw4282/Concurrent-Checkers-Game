/**JS code handles all front-end functionality and communicating with server via web-sockets.
 * @author Azky & calincojo (Used this as reference for the front-end implementation https://codepen.io/calincojo/pen/wBQqYm)
 */
/*
I have use this as reference, though most of the code are changed.
 */
var user_name;
var room_value;
var room_action;
var number_of_games;
var players= [];
var num_games;
var chat = false;
var game_started = false;
var game_started2 = false;
var get_room_players =false;
var user_permit_val = 0;//add 1 to get the value held by the user
var player_game;//1/2
var permit_obtained = false;
var already_opened = false;
var player_id = undefined;
var room_row_counter =0;
var leaderbd_row_counter = 0;
//first game
var square_class = document.getElementsByClassName("square");
var white_checker_class = document.getElementsByClassName("white_checker");
var black_checker_class = document.getElementsByClassName("black_checker");

//second game
var square_class2 = document.getElementsByClassName("square2");
var white_checker_class2 = document.getElementsByClassName("white_checker2");
var black_checker_class2 = document.getElementsByClassName("black_checker2");
//3 game
var square_class_g2_1 = document.getElementsByClassName("square3");
var white_checker_class_g2_1 = document.getElementsByClassName("white_checker_g2_1");
var black_checker_class_g2_1 = document.getElementsByClassName("black_checker_g2_1");
//4 game
var square_class_g2_2 = document.getElementsByClassName("square4");
var white_checker_class_g2_2 = document.getElementsByClassName("white_checker_g2_2");
var black_checker_class_g2_2 = document.getElementsByClassName("black_checker_g2_2");

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
//4 game representation
var block = [];
var w_checker = [];
var b_checker = [];
var the_checker = undefined;

var block2 = [];
var w_checker2 = [];
var b_checker2 = [];
var the_checker2 = undefined ;

var block3 = [];
var w_checker3 = [];
var b_checker3 = [];
var the_checker3 = undefined ;

var block4 = [];
var w_checker4 = [];
var b_checker4 = [];
var the_checker4 = undefined ;
var user_action;

//maps player to a game
var dict_game_rm = {
    "1" : 1, "2" : 1, "3" : 2, "4" : 2, "5" : 3, "6" : 3, "7" : 4, "8" : 4,
};

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
    };

    document.getElementById("msg_id").addEventListener("keydown", function(e) {
        if (!e) { var e = window.event; }
        // Enter is pressed
        if (e.key === "Enter") { action_chat_msg();}
    }, false);
});

function enterName(){

    user_name = $("#id_name_value").val();//gets users name
    document.getElementById('div_id_name').style.display = "none";
    document.getElementById('div_id_menu').style.display = "block";

}

function action_chat_msg(){

    if (user_permit_val == 0){//global chat broadcast
        var msg = user_name + " : " +$("#msg_id").val();
        $("#msg_id").val("");
        let msg_data = {"type": "global_chat", "msg": msg};
        game.send_data(msg_data);//send it to b-e
    }
    else{
        var msg = user_name + " : " +$("#msg_id").val();
        $("#msg_id").val("");
        let msg_data = {"type": "game_chat", "msg": msg};
        game.send_data(msg_data);//send it to b-e
    }
}

/*When pressing create room we are asked to enter room name and room type*/
function create_room(){
    lb_div = document.getElementById("leaderboard_div_id");
    chat_div = document.getElementById('chat_div_id');
    if (lb_div.style.display === "block" || chat_div.style.display === "block" ){
        lb_div.style.display = "none";
        chat_div.style.display = "none";
    }
    room_action = "create_room";
    /*we show the elements to create room and hide what we don't need*/
    document.getElementById('div_id_menu').style.display = "none";
    document.getElementById('div_id_room_settings').style.display = "block";
    document.getElementById('chat_div_id').style.display = "none";
    get_room_players = true;
    let msg_data = {"type" : "get_room_players","msg" : "N/A"};
    start_game(msg_data);
}

/*When we join the room we are asked for the name of the room*/
function join_a_room(){
    lb_div = document.getElementById("leaderboard_div_id");
    chat_div = document.getElementById('chat_div_id');
    if (lb_div.style.display === "block" || chat_div.style.display === "block" ){
        lb_div.style.display = "none";
        chat_div.style.display = "none";
    }
    room_action = "join_room";
    document.getElementById('div_id_menu').style.display = "none";
    document.getElementById('div_id_room_settings').style.display = "block";
    document.getElementById('n_game_selector').style.display = "none";
    // game_2.initialize_snd_game();
    let msg_data = {"type" : "get_room_players","msg" : "N/A"};
    start_game(msg_data);
}

function update_leaderboard(user_id,games_competed,win_per,win_streak,rank){
    //apply changes  below for correct table
    var table =document.getElementById("leaderboard_data");
    var row = table.insertRow(leaderbd_row_counter);
    var cell_id = row.insertCell(0);
    cell_id.innerHTML = user_id;
    var cell_i = row.insertCell(1);
    cell_i.innerHTML = games_competed;
    cell_i = row.insertCell(2);
    cell_i.innerHTML = win_per;
    cell_i = row.insertCell(3);
    cell_i.innerHTML = win_streak;
    cell_i = row.insertCell(4);
    cell_i.innerHTML = rank;
    // cell_i.innerHTML = '<button class="btn btn-primary" type="button" value = "Join Room" onClick=join_a_room('"+ cell_id+'") </button>';
    // cell_i.innerHTML  = '<button class="btn btn-primary" style="width:120px;height: 50px" type="button" onclick="join_selected_room(\''+game_room+'\')">Join</button>';
    leaderbd_row_counter +=1;
}

function update_room_players(id,name,num_of_players,max_permits){
    //here should update all the rows for the fetched records
    var game_room = ""+name;
    var table =document.getElementById("room_players_data");
    var row = table.insertRow(room_row_counter);
    var cell_id = row.insertCell(0);
    cell_id.innerHTML = id;
    var cell_i = row.insertCell(1);
    cell_i.innerHTML = name;
    cell_i = row.insertCell(2);
    cell_i.innerHTML = num_of_players + "/"+(parseInt(max_permits)*2);
    cell_i = row.insertCell(3);
    // cell_i.innerHTML = '<button class="btn btn-primary" type="button" value = "Join Room" onClick=join_a_room('"+ cell_id+'") </button>';
    cell_i.innerHTML  = '<button class="btn btn-primary" style="width:120px;height: 50px" type="button" onclick="join_selected_room(\''+game_room+'\',\''+max_permits+'\')">Join</button>';
    room_row_counter +=1;
}

function getDimension (){
    windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
    windowWidth =  window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
}
//Allocate user to a random room without picking any specific room. The algorithm would simple check for rooms and find the best one
function join_matchmaking(){
    lb_div = document.getElementById("leaderboard_div_id");
    chat_div = document.getElementById('chat_div_id');
    if (lb_div.style.display === "block" || chat_div.style.display === "block" ){
        lb_div.style.display = "none";
        chat_div.style.display = "none";
    }
    room_action="MatchMaking";
    let msg_data = {"type" : "join_matchmaking","msg" : "N/A"};
    start_game(msg_data);

}

function show_leaderboard(){
    lb_div = document.getElementById("leaderboard_div_id");
    if (lb_div.style.display === "block"){
        lb_div.style.display = "none";
        return
    }

    lb_div.style.display = "block";
    lb_div.style.left = "-200px";
    // document.getElementById('leaderboard_div_id').style.top = "-565px";
    lb_div.style.marginTop = "10%";

    var enter_chat = {"type": "req_leaderboard"};
    start_game(enter_chat);

}

/*weSelectTheChat*/
function enter_chat(){
    chat_div = document.getElementById("chat_div_id");
    lb_div = document.getElementById("leaderboard_div_id");
    if (lb_div.style.display === "block"){
        lb_div.style.display = "none";
    }

    if (chat_div.style.display === "block"){
        chat_div.style.display = "none";
        return
    }

    chat_div.style.display = "block";
    chat_div.style.left = "-200px";
    chat_div.style.top = "-565px";
    chat_div.style.marginLeft = "100px";
    chat_div.style.height = "400px";

    chat =  true;
    var enter_chat = {"type": "enter_chat_lobby"};
    start_game(enter_chat);
}

function join_selected_room(room_nm,max_permits){
    room_value = room_nm;
    number_of_games = parseInt(max_permits).toString();
    enter_game_room();
}

function enter_game_room(){
    get_document_element('chat_div_id');
    get_document_element('chat_div_id').style.left = "0px";
    get_document_element('chat_div_id').style.top = "0px";
    get_document_element('chat_div_id').style.marginLeft = "0px";
    document.body.style.backgroundImage = "none";
    get_document_element("body_id").style.backgroundColor = "#ffffff";
    get_document_element('div_id_room_settings').style.display = "none";
    get_document_element('div_id_menu').style.display = "none";
    get_document_element('chat_div_id').style.display = "none";

    /*weGetTheValues​​toCreateTheRoom*/
    if (room_value === undefined) {
        room_value = $("#rm_nm_value").val();
    }

    if ($('#radio1').prop('checked')){
        number_of_games ="1";
    }else if ($('#radio2').prop('checked')){
        number_of_games = "2";
    }else if ($('#radio3').prop('checked')){
        number_of_games = "3";
    }else if ($('#radio4').prop('checked')){
        number_of_games = "4";
    }
    show_number_of_games(number_of_games);
    start_game();
}

function show_number_of_games(n_of_games) {
    switch (n_of_games) {
        case "4":
            get_document_element('g2_table2').style.display = "block";
            get_document_element('g2_game_status2').style.display = "block";
        case "3":
            get_document_element('g2_table').style.display = "block";
            get_document_element('g2_game_status').style.display = "block";
        case "2":
            get_document_element('table2').style.display = "block";
            get_document_element('game_status2').style.display = "block";
        case "1":
            get_document_element('table').style.display = "block";
            get_document_element('game_status').style.display = "block";

    get_document_element('chat_div_id').style.display = "block";
    get_document_element('game_status_id').style.display = "block";

    }

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

function get_document_element(element_id){
    return (document.getElementById(element_id))
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

        set_coords(game_no){
            var x = (this.coordX - 1  ) * moveLength + moveDeviation;
            var y = (this.coordY - 1 ) * moveLength  + moveDeviation;

            if (game_no == 1){
                this.id.style.top = y + 'px';
                this.id.style.left = x + 'px';
            }
            else if (game_no == 2){
                this.id.style.top = y + 'px';
                this.id.style.left = x+17 + 'px';
            }
            else if (game_no == 3){
                this.id.style.top = y + 'px';
                this.id.style.left = x + 'px';
            }
            else if (game_no == 4){
                this.id.style.top = y + 'px';
                this.id.style.left = x +17 + 'px';
            }
        }


        // set_coords(X,Y){
        //     var x = (this.coordX - 1  ) * moveLength + moveDeviation;
        //     var y = (this.coordY - 1 ) * moveLength  + moveDeviation;
        //     this.id.style.top = y + 'px';
        //     this.id.style.left = x + 'px';
        // }
        // set_second_game_coords(X,Y){
        //     var x = (this.coordX - 1  ) * moveLength + moveDeviation;
        //     var y = (this.coordY - 1 ) * moveLength  + moveDeviation;
        //     this.id.style.top = y + 'px';
        //     this.id.style.left = x+17 + 'px';
        //  }
        move_coords(X,Y,game_no){
            // var x = (this.coordX - 1  ) * moveLength + moveDeviation;
            // var y = (this.coordY - 1 ) * moveLength  + moveDeviation;
            // console.log("Here we go", X,Y,x,y,this.id);
            if (game_no == 1){
                console.log("game no 1 move ");
                this.id.style.top = Y + 'px';
                this.id.styl2e.left = X + 'px';
            }
            else if (game_no == 2){
                console.log("Game no 2 move")
                this.id.style.top = Y + 'px';
                let new_x = parseInt(X) + 17;
                this.id.style.left = new_x + 'px';
            }
        }
}

var chatbox_logs = {};
var gameStatus_logs = {};

chatbox_logs.log = (function (msg) {
    var chatbox = document.getElementById("console_id");
    var p_msg = document.createElement('h4');
    p_msg.style.wordWrap = 'break-word';
    p_msg.innerHTML = msg;
    chatbox.appendChild(p_msg);

    //optimise the chat box
    while(chatbox.childNodes.length > 20){
        //remove to keep chat in place
        chatbox.removeChild(chatbox.firstChild);//removes the top element
    }
    chatbox.scrollTop = chatbox.scrollHeight;//scrolls it to height measurement to adjust chatbox
});

gameStatus_logs.log = (function (msg) {
    if (msg === undefined) msg = "Game Status will appear below";
    var gameStatus = document.getElementById("console_status_id");
    var p_msg = document.createElement('h4');
    p_msg.style.wordWrap = 'break-word';
    p_msg.innerHTML = msg;
    gameStatus.appendChild(p_msg);

    //optimise the chat box
    while(gameStatus.childNodes.length > 20){
        //remove to keep chat in place
        gameStatus.removeChild(gameStatus.firstChild);//removes the top element
    }
    gameStatus.scrollTop = gameStatus.scrollHeight;//scrolls it to height measurement to adjust chatbox
});


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

    set_game_data(game_num){

        if (game_num == 1){
            square_class =square_class;
            block = block;
            w_checker = w_checker;
            b_checker = b_checker;
            white_checker_class = white_checker_class;
            black_checker_class =black_checker_class;
        }

        else if (game_num == 2){
            square_class =square_class2;
            block = block2;
            w_checker = w_checker2;
            b_checker = b_checker2;
            white_checker_class = white_checker_class2;
            black_checker_class =black_checker_class2;
        }
        else if (game_num == 3){
            square_class =square_class_g2_1;
            block = block3;
            w_checker = w_checker3;
            b_checker = b_checker3;
            white_checker_class = white_checker_class_g2_1;
            black_checker_class =black_checker_class_g2_1;
        }
        else if (game_num == 4){
            square_class =square_class_g2_2;
            block = block4;
            w_checker = w_checker4;
            b_checker = b_checker4;
            white_checker_class = white_checker_class_g2_2;
            black_checker_class =black_checker_class_g2_2;
        }
    }

    /*initializeAllGame testing*/
    initialize_game(game_no) {

        //Multiple return values doesnt work in js as expected :)
        // console.log(square_class,block,w_checker,b_checker,white_checker_class,black_checker_class,game_no);
        // square_class,block,w_checker,b_checker,white_checker_class,black_checker_class = game_dict[game_no];

        this.set_game_data(game_no);
        console.log(square_class,block,w_checker,b_checker,white_checker_class,black_checker_class,game_no);
        /*===============initializingThePlayingFields =================================*/
        for (var i = 1; i <= 64; i++)
        {
            block[i] = new checkers_squares(square_class[i], i);
        }
        /*================Initializing white black counters =================================*/
        // white piece
        for (var i = 1; i <= 4; i++) {
            w_checker[i] = new checkers(white_checker_class[i], "white", 2 * i - 1,i);
            w_checker[i].set_coords(game_no);
            block[2 * i - 1].occupied = true;
            block[2 * i - 1].pieceId = w_checker[i];
        }

        for (var i = 5; i <= 8; i++) {
            w_checker[i] = new checkers(white_checker_class[i], "white", 2 * i,i);
            w_checker[i].set_coords(game_no);
            block[2 * i].occupied = true;
            block[2 * i].pieceId = w_checker[i];
        }

        for (var i = 9; i <= 12; i++) {
            w_checker[i] = new checkers(white_checker_class[i], "white", 2 * i - 1,i);
            w_checker[i].set_coords(game_no);
            block[2 * i - 1].occupied = true;
            block[2 * i - 1].pieceId = w_checker[i];
        }

        //black piece
        for (var i = 1; i <= 4; i++) {
            b_checker[i] = new checkers(black_checker_class[i], "black", 56 + 2 * i,i);
            b_checker[i].set_coords(game_no);
            block[56 + 2 * i].occupied = true;
            block[56 + 2 * i].pieceId = b_checker[i];
        }

        for (var i = 5; i <= 8; i++) {
            b_checker[i] = new checkers(black_checker_class[i], "black", 40 + 2 * i - 1,i);
            b_checker[i].set_coords(game_no);
            block[40 + 2 * i - 1].occupied = true;
            block[40 + 2 * i - 1].pieceId = b_checker[i];
        }

        for (var i = 9; i <= 12; i++) {
            b_checker[i] = new checkers(black_checker_class[i], "black", 24 + 2 * i,i);
            b_checker[i].set_coords(game_no);
            block[24 + 2 * i].occupied = true;
            block[24 + 2 * i].pieceId = b_checker[i];
        }
        user_action = "initialize";
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
    connect(msg_data) {
        user_action = "initialize";
        // this.socket = new WebSocket("wss://springboot21.herokuapp.com/springboot");
           this.socket = new WebSocket("ws://127.0.0.1:8080/springboot");//https:springboot21.herokuapp.com/

        /*startTheConnection*/
        this.socket.onopen = () => {
            if (!already_opened) {
                console.log('Info: WebSocket connection opened.');// weSendTheUserToTheServer
                // already_opened = true;
                if (room_value !== undefined ){ //before entering game room this would stay undefined
                    console.log("580", room_value);
                    already_opened = true;
                    this.open();
                }
                else{//used for all other cases in which game room is not defined
                    game.send_data(msg_data);
                }
            }
        };

        /*define the actions when receiving the different messages*/
        this.socket.onmessage = (message) => {
            var packet = JSON.parse(message.data);
            console.log("packet received from B/E :", packet);
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

                case "room_players_data":
                    for (var i=0;i<packet.data.length;i++){
                        update_room_players(packet.data[i].game_id,packet.data[i].game_name,packet.data[i].players_active,packet.data[i].max_permits);
                    }
                    break;

                case "leaderboard_resp":
                    //method call to update the fetched rows
                    for (var i=0;i<packet.data.length;i++){
                        update_leaderboard(packet.data[i].user_id,packet.data[i].games_competed,packet.data[i].win_percent,packet.data[i].long_win_streak,packet.data[i].game_ranking);
                    }
                    console.log("leaderboard updated");
                    break;

                case 'remove_road':
                    var up_left = packet.up_left;
                    var up_right = packet.up_right;
                    var down_left = packet.down_left;
                    var down_right = packet.down_right;
                    var game_move = packet.game_no;
                    game.remove_road(up_left,up_right,down_left,down_right,game_move);
                    break;

                case "chat":
                    chatbox_logs.log(packet.msg);
                    break;
                case "game_status_logs":
                    gameStatus_logs.log(packet.data);
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
                case "join_matchmaking_resp":
                    //modal update to user
                    if (packet.data !== null){
                        //join room call
                        room_action = "join_room";
                        room_value = packet.data;
                        enter_game_room();//join room process
                        document.getElementById("modalBtnTrigger").click();
                        document.getElementById("modal_message").innerHTML = "You have been allocated to a room. Please wait while the joining process takes place.";

                    }else{
                        document.getElementById("modal_message").innerHTML =  "All rooms are full. Please create a room or manually join a room!";
                        document.getElementById("modalBtnTrigger").click();
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
                case "room_permits":
                    user_permit_val  = packet.data;
                    num_games = packet.num_games;
                    if (user_permit_val >0){
                        //use a method to intialize the required games
                        permit_obtained = true;
                        player_game = dict_game_rm[String(user_permit_val)];
                        for(var game_no=1;game_no<=num_games;game_no++){
                            game.initialize_game(game_no);
                        }
                    }
                    break;
            }
        };

        /*closeTheConnection*/
        this.socket.onclose = () => {

            // this.stopGameLoop();
        };
    }

    /*only runs once and communicates the needed msg at first and does all needed once in the case statements*/
    //fix error on this
    open() { //
        var msg = {"type": "user", "user_action":user_action, "room_action" : room_action,"room_value" : room_value, "plyr_name" : user_name, "n_games" : number_of_games};
        var json_str = JSON.stringify(msg);
        this.socket.send(json_str);

        setTimeout(function () {
            if (!permit_obtained){
                var check_permits = {"type": "get_room_permits", "room_value" : room_value};
                game.send_data(check_permits);
            }
        },3000);
    }

    enter_chat_lobby() {
        this.socket.onopen = () => {
            console.log('Info: WebSocket connection opened.');
            var enter_chat = {"type": "enter_chat_lobby"};
            game.send_data(enter_chat);
        }
    }
}

// multiple objects for multiple games
let game = new Game();
let game2 = new Game();

function start_game(msg){
    game.connect(msg);

}
