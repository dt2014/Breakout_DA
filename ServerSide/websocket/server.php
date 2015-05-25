<?php
$host = '192.168.0.5'; //host
$port = '9000'; //port
$null = NULL; //null var

//Create TCP/IP sream socket
$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
//reuseable port
socket_set_option($socket, SOL_SOCKET, SO_REUSEADDR, 1);

//bind socket to specified host
socket_bind($socket, 0, $port);

//listen to port
socket_listen($socket);

//create & add listning socket to the list
$clients = array($socket);

require_once 'api_config.php';
require_once 'apiV7.php';
$api = new API($config);
$reply = array();

$number_of_client = 0;
$notify = false;
$player_names = array();
//start endless loop, so that our script doesn't stop
while (true) {
	//manage multipal connections
	$changed = $clients;
	//returns the socket resources in $changed array
	socket_select($changed, $null, $null, 0, 10);
	
	//check for new socket
	if (in_array($socket, $changed)) {

		$socket_new = socket_accept($socket); //accpet new socket
		$clients[] = $socket_new; //add socket to client array
		
		$header = socket_read($socket_new, 1024); //read data sent by the socket
		perform_handshaking($header, $socket_new, $host, $port); //perform websocket handshake
		
		socket_getpeername($socket_new, $ip); //get ip address of connected socket
		//$response = mask(json_encode(array('type'=>'system', 'message'=>$ip.' connected'))); //prepare json data
		//send_message($response); //notify all users about new connection
		//make room for new socket
		$found_socket = array_search($socket, $changed);
		unset($changed[$found_socket]);

	}
	
	//loop through all connected sockets
	foreach ($changed as $changed_socket) {	
		
		//check for any incomming data
		while(socket_recv($changed_socket, $buf, 1024, 0) >= 1)
		{
			$received_text = unmask($buf); //unmask data
			$tst_msg = json_decode($received_text); //json decode 
			//$user_name = $tst_msg->name; //sender name
			//$user_message = $tst_msg->message; //message text
			//$user_color = $tst_msg->color; //color
			gamestate($tst_msg,$api,$changed_socket);

			//prepare data to be sent to client
			//$response_text = mask(json_encode(array('type'=>'usermsg', 'name'=>$user_name, 'message'=>$user_message, 'color'=>$user_color)));
			//send_message($response_text); //send data
			break 2; //exist this loop
		}
		
		$buf = @socket_read($changed_socket, 1024, PHP_NORMAL_READ);
		if ($buf === false) { // check disconnected client
			// remove client for $clients array
			$found_socket = array_search($changed_socket, $clients);
			socket_getpeername($changed_socket, $ip);
			unset($clients[$found_socket]);
			
			//notify all users about disconnected connection
			$response = mask(json_encode(array('type'=>'system', 'message'=>$ip.' disconnected')));
			send_message($response);

			global $number_of_client;
    		$number_of_client = 0;
    		unset($GLOBALS['player_names']);
		}
	}
}
// close the listening socket
socket_close($sock);

function send_message($msg)
{
	global $clients;
	foreach($clients as $changed_socket)
	{
		@socket_write($changed_socket,$msg,strlen($msg));
	}
	return true;
}

function send_message_to_socket($msg,$changed_socket)
{

	@socket_write($changed_socket,$msg,strlen($msg));
	
	return true;
}


//Unmask incoming framed message
function unmask($text) {
	$length = ord($text[1]) & 127;
	if($length == 126) {
		$masks = substr($text, 4, 4);
		$data = substr($text, 8);
	}
	elseif($length == 127) {
		$masks = substr($text, 10, 4);
		$data = substr($text, 14);
	}
	else {
		$masks = substr($text, 2, 4);
		$data = substr($text, 6);
	}
	$text = "";
	for ($i = 0; $i < strlen($data); ++$i) {
		$text .= $data[$i] ^ $masks[$i%4];
	}
	return $text;
}

//Encode message for transfer to client.
function mask($text)
{
	$b1 = 0x80 | (0x1 & 0x0f);
	$length = strlen($text);
	
	if($length <= 125)
		$header = pack('CC', $b1, $length);
	elseif($length > 125 && $length < 65536)
		$header = pack('CCn', $b1, 126, $length);
	elseif($length >= 65536)
		$header = pack('CCNN', $b1, 127, $length);
	return $header.$text;
}

//handshake new client.
function perform_handshaking($receved_header,$client_conn, $host, $port)
{
	$headers = array();
	$lines = preg_split("/\r\n/", $receved_header);
	foreach($lines as $line)
	{
		$line = chop($line);
		if(preg_match('/\A(\S+): (.*)\z/', $line, $matches))
		{
			$headers[$matches[1]] = $matches[2];
		}
	}

	$secKey = $headers['Sec-WebSocket-Key'];
	$secAccept = base64_encode(pack('H*', sha1($secKey . '258EAFA5-E914-47DA-95CA-C5AB0DC85B11')));
	//hand shaking header
	$upgrade  = "HTTP/1.1 101 Web Socket Protocol Handshake\r\n" .
	"Upgrade: websocket\r\n" .
	"Connection: Upgrade\r\n" .
	"WebSocket-Origin: $host\r\n" .
	"WebSocket-Location: ws://$host:$port/demo/shout.php\r\n".
	"Sec-WebSocket-Accept:$secAccept\r\n\r\n";
	socket_write($client_conn,$upgrade,strlen($upgrade));
}



// API THINGS
function gamestate($tst_msg,$api,$changed_socket){
	try
{
	global $reply;
	//bool array_key_exists ( mixed $key , $tst_msg)
    $command = $tst_msg->command;
    
    if($command == "play")
    {
        
        $ball_indicate = 1;
        
        // WRITE THE BAR
        $player_name = $tst_msg->n;
        $bar_position_x = $tst_msg->x;
        $api->updateBarPosition($player_name,$bar_position_x);
        
        
        // Because at a time a player can have two balls, one ball, no ball
        // IF WRITE THE BALL NUMBER ONE
        if(array_key_exists('x1' , $tst_msg))
        {
            $ball_id = 1;
            $x1 = $tst_msg->x1;
            $y1 = $tst_msg->y1;
            $w1 = $tst_msg->w1;
            $z1 = $tst_msg->z1; 
            $api->updateBallPositionAndSpeed($ball_id,$x1,$y1,$w1,$z1);
            $ball_indicate  = $ball_indicate + 1;
        }
        
        // IF WRITE THE BALL NUMBER TWO
        if(array_key_exists('x2' , $tst_msg))
        {
            $ball_id = 2;
            $x2 = $tst_msg->x2;
            $y2 = $tst_msg->y2;
            $w2 = $tst_msg->w2;
            $z2 = $tst_msg->z2;
            $api->updateBallPositionAndSpeed($ball_id,$x2,$y2,$w2,$z2);
            $ball_indicate = $ball_indicate * -1;
        }
        
        // IF USER WANTS TO CHANGE OWNERSHIP
        if(array_key_exists('b' , $tst_msg))
        {
            $ball_id = $tst_msg->b;
            $new_owner_name = $player_name;
            $api->changeOwnerShip($ball_id,$new_owner_name);
        } 
        
        // IF HITTING A BRICK AND WANT TO WRITE VALUE TO PLAYER SCORE
        if(array_key_exists('k' , $tst_msg))
        {
            $brick_id = $tst_msg->k;
            $api->updateScore($brick_id,$player_name);
            //$reply['e'] = $info;

            // RETURN THE SCORE OF TWO PLAYER( Notice no if statement here)
            $names = $api->getPlayerNames();
            $player1_name = $names[0];
            $player2_name = $names[1];
            $score = $api->getPlayerScore($player1_name, $player2_name);
            $reply['s'] = $score;
        }
        
        // RETURN THE BAR OF RIVAL PLAYER
        $rival_name = $api->getOpponentName($player_name);
        $bar_position = $api->getBarPosition($rival_name);
        $reply['b'] = $bar_position;
        
        // Based on how many ball the requester have now, we just return the
        // ball that he doesn't know
        $about_ball = $api->getRivalBall($ball_indicate);
        // If it has at least one ball doesnt know
        if($about_ball)
        {
            $reply['l'] = $about_ball;
		}
        send_message_to_socket(mask(json_encode($reply)),$changed_socket);

        unset($GLOBALS['reply']);
        
    }
    else if($command == "start")
    {
        if(array_key_exists('n' , $tst_msg))
        {

            $player_name = $tst_msg->n;

            global $player_names;
            global $number_of_client;
            $number_of_client = $number_of_client + 1;
            if($number_of_client > 2){
            	return;
            }
            
            array_push($player_names, $player_name);
            
            
            $api->startGame($player_name);

            if($number_of_client == 2){
            	global $clients;
				for ($count = 0; $count < 2; $count++)
				{
					$changed_socket = $clients[$count+1];

					$response = $api->startInfo($player_names[$count]);

					$msg = mask(json_encode($response));
					@socket_write($changed_socket,$msg,strlen($msg));
				}
            }
        }
    }
    else if($command == "stop")
    {
        if(array_key_exists('n' , $tst_msg))
        {
            $player_name = $tst_msg->n;
            $api->stopGame($player_name);

            global $number_of_client;
    		$number_of_client = $number_of_client - 1;
    		if($number_of_client < 0){
    			$number_of_client = 0;
    		}

    		unset($GLOBALS['player_names']);
        }
    }
    else if($command == "lose")
    {
        if(array_key_exists('n' , $tst_msg))
        {
            $player_name = $tst_msg->n;
            $api->loseGame($player_name);
        }
    }
    
}
catch (Exception $e)
{
        var_dump($e);
}
}




