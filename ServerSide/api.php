<?php

// This is the server API for the PushChat iPhone app. To use the API, the app
// sends an HTTP POST request to our URL. The POST data contains a field "cmd"
// that indicates what API command should be executed.

try
{
	require_once 'api_config.php';

	// To keep the code clean, I put the API into its own class. Create an
	// instance of that class and let it handle the request.
	
    //start the connection
    $api = new API($config);
    
    $command = $_POST['command'];
    
    // IF COMMAND IS TO READ DATA
    if($command == "read")
    {
        // IF READ THE BALL POSITION
        if(isset($_POST["ball_id"]))
        {
            $ball_id = $_POST["ball_id"];
            $position = $api->getBallPositionAndSpeed($ball_id);
            echo json_encode($position);
        }
        
        // IF READ THE SCORE OF TWO PLAYER
        if(isset($_POST["player1_name"]) && isset($_POST["player2_name"]))
        {
            $player1_name = $_POST["player1_name"];
            $player2_name = $_POST["player2_name"];
            $score = $api->getPlayerScore($player1_name, $player2_name);
            echo json_encode($score);
        }
        
        // IF READ THE PLAYER BAR POSITION
        if(isset($_POST["player_name"]))
        {
            $player_name = $_POST["player_name"];
            $bar_position = $api->getBarPosition($player_name);
            echo json_encode($bar_position);
        }
        
        // IF READ THE LEVEL
        if(isset($_POST["level"]))
        {
            $level = $api->downloadLevel();
            echo json_encode($level);
        }
    }
    // IF COMMAND IS TO UPDATE DATA
    else if($command == "write")
    {
        // IF WRITE POSITION TO THE BALL
        if(isset($_POST["ball_id"]) && isset($_POST["ball_position_x"]) && isset($_POST["ball_position_y"]))
        {
            $ball_id = $_POST["ball_id"];
            $ball_position_x = $_POST["ball_position_x"];
            $ball_position_y = $_POST["ball_position_y"];
            $api->updateBallPosition($ball_id,$ball_position_x,$ball_position_y);
            
        }
        
        // IF WRITE SPPED TO THE BALL
        if(isset($_POST["ball_id"]) && isset($_POST["ball_speed_x"]) && isset($_POST["ball_speed_y"]))
        {
            $ball_id = $_POST["ball_id"];
            $ball_speed_x = $_POST["ball_speed_x"];
            $ball_speed_y = $_POST["ball_speed_y"];
            $api->updateBallSpeed($ball_id,$ball_speed_x,$ball_speed_y);
            
        }
        
        // IF HITTING A BRICK AND WANT TO WRITE VALUE TO PLAYER SCORE
        if(isset($_POST["brick_id"]) && isset($_POST["player_name"]))
        {
            $brick_id = $_POST["brick_id"];
            $player_name = $_POST["player_name"];
            $info = $api->updateScore($brick_id,$player_name);
            echo json_encode($info);
        }
        
        // IF WANT TO UPDATE MY BAR POSITION
        if(isset($_POST["player_name"]) && isset($_POST["bar_position_x"]) && isset($_POST["bar_position_y"]))
        {
           $player_name = $_POST["player_name"];
           $bar_position_x = $_POST["bar_position_x"];
           $bar_position_y = $_POST["bar_position_y"];
           $api->updateBarPosition($player_name,$bar_position_x,$bar_position_y);
        }
    }
    
}
catch (Exception $e)
{
		var_dump($e);
}

////////////////////////////////////////////////////////////////////////////////
    
////////////////////////////////////////////////////////////////////////////////

class API
{


	private $pdo;

	function __construct($config)
	{
		// Create a connection to the database.
		$this->pdo = new PDO(
			'mysql:host=' . $config['host'] . ';dbname=' . $config['dbname'],
			$config['username'],
			$config['password'],
			array());

		$this->pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

		// We want the database to handle all strings as UTF-8.
		$this->pdo->query('SET NAMES utf8');
	}

    function getBallPositionAndSpeed($ball_id)
    {
        $stmt = $this->pdo->prepare('SELECT ball_position_x, ball_position_y,ball_speed_x,ball_speed_y FROM Ball WHERE ball_id = ?;');
        $stmt->execute(array($ball_id));
        $ball_detail = $stmt->fetch(PDO::FETCH_OBJ);
        
        $ball_detail = array(
                               'ball_id' => $ball_id,
                               'position' => array(
                                                   'ball_position_x' => $ball_detail->ball_position_x,
                                                   'ball_position_y' => $ball_detail->ball_position_y,
                                                   ),
                               'speed' => array(
                                                'ball_speed_x' => $ball_detail->ball_speed_x,
                                                'ball_speed_y' => $ball_detail->ball_speed_y,
                                                )
                               );
        return $ball_detail;
    }

    function getPlayerScore($player1_name, $player2_name)
    {
        $stmt = $this->pdo->prepare('SELECT player_score  as score FROM Player WHERE player_name = ?;');
        $stmt->execute(array($player1_name));
        $score1 = $stmt->fetch(PDO::FETCH_OBJ);
        
        $stmt = $this->pdo->prepare('SELECT player_score  as score FROM Player WHERE player_name = ?;');
        $stmt->execute(array($player2_name));
        $score2 = $stmt->fetch(PDO::FETCH_OBJ);
        
        $score = array('player1Score' => $score1->score,'player2Score' => $score2->score);
        return $score;
    }
    
    function getBarPosition($player_name)
    {
        $stmt = $this->pdo->prepare('SELECT bar_position_x, bar_position_y FROM Player WHERE player_name = ?;');
        $stmt->execute(array($player_name));
        $bar_position = $stmt->fetch(PDO::FETCH_OBJ);
        return $bar_position;
    }
    
    function downloadLevel()
    {
        $stmt = $this->pdo->prepare('SELECT * FROM Brick;');
        $stmt->execute();
        $level = $stmt->fetchAll(PDO::FETCH_OBJ);
        return $level;
    }
    
    function getBrickInfo($brick_id)
    {
        $stmt = $this->pdo->prepare('SELECT * FROM Brick Where brick_id = ?;');
        $stmt->execute(array($brick_id));
        $brick = $stmt->fetch(PDO::FETCH_OBJ);
        return $brick;
    }
    
    function checkBrickSpecial($brick_id)
    {
        $stmt = $this->pdo->prepare('SELECT brick_special AS special FROM Brick WHERE brick_id = ?;');
        $stmt->execute(array($brick_id));
        $brick_special = $stmt->fetch(PDO::FETCH_OBJ);
        return $brick_special->special;
    }
    
    function getValueOfBrick($brick_id)
    {
        $stmt = $this->pdo->prepare('SELECT brick_value AS value FROM Brick WHERE brick_id = ?;');
        $stmt->execute(array($brick_id));
        $brick_value = $stmt->fetch(PDO::FETCH_OBJ);
        return $brick_value->value;
    }
    
    function getBrickStatue($brick_id)
    {
        $stmt = $this->pdo->prepare('SELECT brick_stauts AS status FROM Brick WHERE brick_id = ?;');
        $stmt->execute(array($brick_id));
        $brick_status = $stmt->fetch(PDO::FETCH_OBJ);
        return $brick_status->status;
    }
    
    function getOpponentName($player_name)
    {
        $stmt = $this->pdo->prepare('SELECT player_name FROM Player WHERE player_status = ? AND player_name <> ?;');
        $stmt->execute(array('ACTIVE',$player_name));
        $opponent_name = $stmt->fetch(PDO::FETCH_OBJ);
        return $opponent_name->player_name;
    }
    
    function updateBallPosition($ball_id,$ball_position_x,$ball_position_y)
    {
        $stmt = $this->pdo->prepare('UPDATE Ball SET ball_position_x = ?, ball_position_y = ? WHERE ball_id = ?;');
        $stmt->execute(array($ball_position_x,$ball_position_y,$ball_id));
    }
    
    function updateBallSpeed($ball_id,$ball_speed_x,$ball_speed_y)
    {
        $stmt = $this->pdo->prepare('UPDATE Ball SET ball_speed_x = ?, ball_speed_y = ? WHERE ball_id = ?;');
        $stmt->execute(array($ball_speed_x,$ball_speed_y,$ball_id));
    }
    
    function eliminateBrick($brick_id)
    {
        $stmt = $this->pdo->prepare('UPDATE Brick SET brick_status = ? WHERE brick_id = ?;');
        $stmt->execute(array('INACTIVE',$brick_id));
    }
    
    function updateScore($brick_id,$player_name)
    {
        $brick = $this->getBrickInfo($brick_id);
        $status = $brick->brick_status;
        // IF THE BRICK IS ALREADY ELIMINATED
        if($status == 'INACTIVE')
        {
            return array('Info' => 'Brick Eliminate Not Success');
        }
        
        // ELSE CONTINUE
        $special = $brick->brick_special;
        $value = $brick->brick_value;
        
        // CHECK IF THE BRICK IS A SPECIAL BRICK
        if($special == 'N')
        {
            $stmt = $this->pdo->prepare('UPDATE Player SET player_score = player_score + ? WHERE player_name = ?;');
            $stmt->execute(array($value,$player_name));
        }
        else if($special == 'Y')
        {
            // GET THE OTHER PLAYER NAME FIRST
            $opponent_name = $this->getOpponentName($player_name);
            
            // ADD THE BRICK VALUE TO THIS PLAYER
            $stmt = $this->pdo->prepare('UPDATE Player SET player_score = player_score + ? WHERE player_name = ?;');
            $stmt->execute(array($value,$player_name));
            
            // ADD THE BRICK VALUE TO OPPONENT PLAYER
            $stmt = $this->pdo->prepare('UPDATE Player SET player_score = player_score - ? WHERE player_name = ?;');
            $stmt->execute(array($value,$opponent_name));
            
        }
      
		// DESTROY THE BRICK
        $this->eliminateBrick($brick_id);
		
        return array('Info' => 'Brick Eliminate Success');
    }
                                                 
   function updateBarPosition($player_id,$bar_position_x,$bar_position_y)
    {
        $stmt = $this->pdo->prepare('UPDATE Player SET bar_position_x = ?, bar_position_y = ? WHERE player_name = ?;');
        $stmt->execute(array($bar_position_x,$bar_position_y,$player_id));
    }
    
}
