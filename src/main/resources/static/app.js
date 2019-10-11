
var user;
var room;
var difficulty;
var players= [];
var chat = false;



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

    menu_option="MatchMaking";

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
        let data = {"type": "delete", "name": user};
        game.process_data(data);
        $("#player-box").text("");
        chat = false;
        game.open();
    }
}


class Player {

    constructor(name) {
        this.name = name;
    }
}








//A game class
class Game {

    process_data(data){
        var data = JSON.stringify(data);
        this.socket.send(data)
    }


    constructor(){
        this.socket = null;
        this.fps = 30;
        this.nextFrame = null;
        this.interval = null;
        this.gridSize = 30;

        this.skipTicks = 1000 / this.fps;
        this.nextGameTick = (new Date).getTime();
    }

    /*initializeTheGame*/
    //!checkers can be loaded below.
    initialize() {

        this.snakes = [];
        let canvas = document.getElementById('playground');
        if (!canvas.getContext) {
            Console.log('Error: 2d canvas is not supported on this browser.');
            return;
        }
        else{
            this.connect();
        }

    }

    /*initializeTheGame*/
    startGameLoop() {
        this.nextFrame = () => {
            requestAnimationFrame(() => this.run());
        }

        this.nextFrame();
    }

    /*forTheGame*/
    stopGameLoop() {
        this.nextFrame = null;
        if (this.interval != null) {
            clearInterval(this.interval);
        }
    }

    /*updateTheGame*/
    run() {

        while ((new Date).getTime() > this.nextGameTick) {
            this.nextGameTick += this.skipTicks;
        }
        this.draw();
        this.comida.draw(this.context);
        if (this.nextFrame != null) {
            this.nextFrame();
        }

    }

    /*send the first message of each client to the server*/
    open() {
        var aux = {"type": "user", "user": user, "ComandoSala":menu_option,"Sala":room, "difficulty":difficulty};
        var mens=JSON.stringify(aux);
        this.socket.send(mens);

        if (chat == false){
            this.startGameLoop();

            var aux = {"type": "ping"};
            var mens=JSON.stringify(aux);
            setInterval(() => this.socket.send(mens), 5000);
        }
    }

    /*connect to the server and define the socket methods*/
    connect() {

        this.socket = new WebSocket("ws://127.0.0.1:8080/snake");

        /*startTheConnection*/
        this.socket.onopen = () => {

            // Socket open.. start the game loop.
            Console.log('Info: WebSocket connection opened.');
            Console.log('Info: Press an arrow key to begin.');

            // weSendTheUserToTheServer
            this.open();
        };

        /*closeTheConnection*/
        this.socket.onclose = () => {
            let aux = {"type": "delete", "name": user};
            game.process_data(aux);
            Console.log('Info: WebSocket closed.');

            this.stopGameLoop();
        };

        /*define the actions when receiving the different messages*/
        this.socket.onmessage = (message) => {

            var packet = JSON.parse(message.data);
            //handles message received from b/e. need work on this
            switch (packet.type) {
                /*updateTheGameSnakes*/
                case 'update':
                    for (var i = 0; i < packet.data.length; i++) {
                        this.updateSnake(packet.data[i].id, packet.data[i].body);
                    }
                    break;
                /*addANewPlayerToTheRoom*/
                case 'join':
                    Console.log("SALA: "+packet.data[0].nombre+" heHasEnteredTheRoom");
                    for (var j = 0; j < packet.data.length; j++) {
                        this.addSnake(packet.data[j].id, packet.data[j].color,packet.data[j].nombre);
                    }
                    break;
                /*removeAPlayerFromTheRoom*/
                case 'leave':
                    Console.log("SALA"+packet.nombre+'hasLeftTheGame');
                    this.removeSnake(packet.id);
                    break;
                /*indicatesThatTheSnakeHasDied*/
                case 'dead':
                    Console.log('roomYourSnakeIsDead');
                    this.direction = 'none';
                    break;
                /*indicatesThatYouHaveTakenTheFood*/
                case 'kill':
                    Console.log('SALA:foodObtained!');
                    break;
                /*indicates if the room has been created successfully or if it already exists*/
                case 'Okcrear':
                    if(packet.data==='Ok'){
                        Console.log('Info: Sala '+sala+" createdSuccessfully");
                        document.getElementById('crearSala').style.display = "none";
                    }else{
                        Console.log('alertTheRoom '+sala+" itAlreadyExists");
                    }
                    break;
                /*indicates if it could be added to the room or if it could not be added*/
                case 'Okunir':
                    document.getElementById('cancel').style.display = "none";
                    if(packet.data==='Ok'){

                        document.getElementById('unirSala').style.display = "none";

                    }else if (packet.data==='NotOk'){
                        Console.log("alertWaitCanceledTimeOut");
                        $("#unir").val("");
                        document.getElementById('cancel').style.display = "none";
                        document.getElementById('canvas').style.display = "none";
                        document.getElementById('btnPrinc').style.display = "block";
                    }else if (packet.data==='NoSalas') {
                        Console.log("alertThereAreNoRoomsAvailable");
                        document.getElementById('cancel').style.display = "none";
                        document.getElementById('canvas').style.display = "none";
                        document.getElementById('btnPrinc').style.display = "block";
                    }else{
                        Console.log("alertTheRoomDoesNotExist");
                        $("#unir").val("");
                        document.getElementById('cancel').style.display = "none";
                        document.getElementById('canvas').style.display = "none";
                        document.getElementById('btnPrinc').style.display = "block";
                    }
                    break;
                /*write the received message*/
                case "chat" :
                    Console.log(packet.mensaje);
                    break;
                /*add a player to the players in the chat*/
                case "player" :
                    players.push(new Player(packet.name));
                    updatePlayerBox();
                    break;
                /*update all the players that must be in the chat*/
                case "players" :
                    var aux = [];
                    var aux2 = [];
                    if (packet.list != ""){
                        aux = packet.list.split(",");

                        for (i = 0;i<aux.length-1 ; i++){
                            aux2.push(new Player(aux[i]));
                        }

                        players = aux2;
                    }else{
                        players = [];
                    }
                    updatePlayerBox();
                    break;
                /*Waiting to enter a room is canceled*/
                case "cancelar" :
                    Console.log(packet.info);
                    document.getElementById('cancel').style.display = "none";
                    break;
                /*It indicates that the room is full and shows the button to cancel the wait*/
                case "espera":
                    Console.log("alertFullRoomWaiting5Seconds");
                    document.getElementById('cancel').style.display = "block";
                    break;
                /*Indicates to the creator of the room that there are already two players and gives him the possibility to start the game*/
                case 'iniciar':
                    Console.log("roomYouCanStart");
                    document.getElementById('modal3').style.display = "block";
                    break;
                /*Add food to the game*/
                case 'comida':
                    this.añadirComida(packet.x,packet.y,'#FF0000');
                    break;
                /*end of game*/
                case 'fin':
                    Console.log("roomEndOfGame");
                    document.getElementById('modal4').style.display = "block";
                    break;

                case 'empezar':
                    document.getElementById('modal3').style.display = "none";
                    break;

                case 'partidasEnJuego':
                    Console.log("Alert: thereAreGamesAtStakePleaseWait");
                    document.getElementById('modal4').style.display = "none";
                    document.getElementById('button6').style.display = "none";

                /*showTheWallOfFame*/
                case 'muro':
                    Console.clean();
                    Console.log("------------------------------");
                    Console.log("wallOFFAME");
                    Console.log("------------------------------");
                    for (var m = 0; m < packet.data.length; m++) {
                        Console.log("Nombre: "+packet.data[m].Nombre+"\nPuntuacion: "+packet.data[m].Puntuacion);
                    }
                    var aux = {"type":"finPartida"};
                    game.enviar(aux);



            }
        }
    }




}







let game = new Game();


function start_game(){
    game.initialize();
}