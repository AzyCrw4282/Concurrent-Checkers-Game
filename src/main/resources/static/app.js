function enterName(){

    user = $("#id_name_value").val();
    console.log(user);
    /*we show the buttons to create room, join room and chat*/
    document.getElementById('div_id_menu').style.display = "block";
    document.getElementById('div_id_name').style.display = "none";
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
    document.getElementById('btnPrinc').style.display = "none";
    document.getElementById('div_id_create_room').style.display = "block";
    document.getElementById('divChat').style.display = "none";
    document.getElementById('console').style.height = "90%";

}

/*weJoinARandomRoom*/
function action_matchmaking(){
    document.getElementById('btnPrinc').style.display = "none";
    document.getElementById('canvas').style.display = "block";
    document.getElementById('divChat').style.display = "none";
    document.getElementById('console').style.height = "90%";

    comandoSala="MatchMaking";

    /*If we are in the chat we remove the user from the chat*/
    if (!chat){
        juego();
    }else{
        let aux = {"type": "delete", "name": user};
        game.enviar(aux);
        $("#player-box").text("");
        chat = false;
        game.open();
    }
}

/*weSelectTheChat*/
function enter_chat(){
    document.getElementById('divChat').style.display = "block";
    document.getElementById('chat').style.display = "none";

    document.getElementById('console').style.height = "400px";

    /*thePlayerIsAddedToTheChat*/
    sala = "-1";
    comandoSala="Chat";
    players.push(new Player(user));
    updatePlayerBox();
    chat =  true;
    juego();
}

function enter_game_room(){
    /*we show the canvas and we worship the rest of the elements*/
    document.getElementById('canvas').style.display = "block";
    document.getElementById('div_id_create_room').style.display = "none";

    /*weGetTheValues​​toCreateTheRoom*/
    room = $("#room_id").val();
    comandoSala="Crear";
    difficulty = 0;

    if ($('#radio2').prop('checked')){
        difficulty = 1;
    }else if ($('#radio3').prop('checked')){
        difficulty = 2;
    }

    /*If we are in the chat we remove the user from the chat*/
    if (!chat){
        juego();
    }else{
        let aux = {"type": "delete", "name": user};
        game.enviar(aux);
        $("#player-box").text("");
        chat = false;
        game.open();
    }
}