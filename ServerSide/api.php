<?php
    
    // This is the server API for the PushChat iPhone app. To use the API, the app
    // sends an HTTP POST request to our URL. The POST data contains a field "cmd"
    // that indicates what API command should be executed.
    
    try
    {
        //require_once 'remoteClient.php';
        //$client = new RemoteAddress();
        //$ip = $client->getIpAddress();
        
        require_once 'api_config.php';
        
        // To keep the code clean, I put the API into its own class. Create an
        // instance of that class and let it handle the request.
        
        //start the connection
        $api = new API($config);
        
        $command = $_GET['command'];
        
        // IF COMMAND IS TO READ DATA
        if($command == "read")
        {
            // IF READ THE BALL POSITION
            if(isset($_GET["ball_id"]))
            {
                $ball_id = $_GET["ball_id"];
                
                $position = $api->getBallPositionAndSpeed($ball_id);
                echo json_encode($position);
            }
            
            // IF READ THE SCORE OF TWO PLAYER
            if(isset($_GET["player1_name"]) && isset($_GET["player2_name"]))
            {
                $player1_name = $_GET["player1_name"];
                $player2_name = $_GET["player2_name"];
                $score = $api->getPlayerScore($player1_name, $player2_name);
                echo json_encode($score);
            }
            
            // IF READ THE PLAYER BAR POSITION
            if(isset($_GET["player_name"]))
            {
                $player_name = $_GET["player_name"];
                $bar_position = $api->getBarPosition($player_name);
                echo json_encode($bar_position);
            }
            
            // IF READ THE LEVEL
            if(isset($_GET["level"]))
            {
                $level = $api->downloadLevel();
                echo json_encode($level);
            }
        }
        // IF COMMAND IS TO UPDATE DATA
        else if($command == "write")
        {
            // IF WRITE POSITION TO THE BALL
            if(isset($_GET["ball_id"]) && isset($_GET["ball_position_x"]) && isset($_GET["ball_position_y"]))
            {
                $ball_id = $_GET["ball_id"];
                $ball_position_x = $_GET["ball_position_x"];
                $ball_position_y = $_GET["ball_position_y"];
                $api->updateBallPosition($ball_id,$ball_position_x,$ball_position_y);
                
            }
            
            // IF WRITE SPPED TO THE BALL
            if(isset($_GET["ball_id"]) && isset($_GET["ball_speed_x"]) && isset($_GET["ball_speed_y"]))
            {
                $ball_id = $_GET["ball_id"];
                $ball_speed_x = $_GET["ball_speed_x"];
                $ball_speed_y = $_GET["ball_speed_y"];
                $api->updateBallSpeed($ball_id,$ball_speed_x,$ball_speed_y);
                
            }
            
            // IF CHANGE THE OWNERSHIP OF THE BALL
            if(isset($_GET["ball_id"]) && isset($_GET["owner_name"]))
            {
                $ball_id = $_GET["ball_id"];
                $new_owner_name = $_GET["owner_name"];
                $api->changeOwnerShip($ball_id,$new_owner_name);
                
            }
            // IF HITTING A BRICK AND WANT TO WRITE VALUE TO PLAYER SCORE
            if(isset($_GET["brick_id"]) && isset($_GET["player_name"]))
            {
                $brick_id = $_GET["brick_id"];
                $player_name = $_GET["player_name"];
                $info = $api->updateScore($brick_id,$player_name);
                echo json_encode($info);
            }
            
            // IF WANT TO UPDATE MY BAR POSITION
            if(isset($_GET["player_name"]) && isset($_GET["bar_position_x"]))
            {
                $player_name = $_GET["player_name"];
                $bar_position_x = $_GET["bar_position_x"];
                $api->updateBarPosition($player_name,$bar_position_x);
            }
        }
        else if($command == "start")
        {
            if(isset($_GET["player_name"]))
            {
                $player_name = $_GET["player_name"];
                $response = $api->startGame($player_name);
                echo json_encode($response);
            }
        }
        else if($command == "stop")
        {
            if(isset($_GET["player_name"]))
            {
                $player_name = $_GET["player_name"];
                $api->stopGame($player_name);
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
        
        function startGame($player_name)
        {
            // Get a spot for this player
            $stmt = $this->pdo->prepare('SELECT map_side, COUNT(*) AS num FROM Player WHERE player_status = ?;');
            $stmt->execute(array('INACTIVE'));
            $result = $stmt->fetch(PDO::FETCH_OBJ);
            
            if($result->num == 0)
            {
                return array('response' => 'server is fully loaded');
            }
            
            // ACTIVE THE PLAYER ITSELF
            $stmt = $this->pdo->prepare('UPDATE Player SET player_name = ?, player_score = ?,bar_position_x = ?,  player_status = ?, latest_eliminated_brick_id = ?, lock_type = ?, serving_number = 0, ticket = 0 WHERE map_side = ?;');
            $stmt->execute(array($player_name,'0','0.3','ACTIVE','null','no',$result->map_side));
            
            for ($i = 0; $i < 5; $i++)
            {
                $stmt = $this->pdo->prepare('SELECT COUNT(*) AS num FROM Player WHERE player_status = ?;');
                $stmt->execute(array('ACTIVE'));
                $active_num = $stmt->fetch(PDO::FETCH_OBJ);
                // IF TWO GUYS ARE ACTIVE
                if($active_num->num > 1)
                {
                    
                    // INITIALISE THE PLAYER BALL
                    $this->initialiseTheBall($player_name,$result->map_side);
                    
                    $stmt = $this->pdo->prepare('SELECT player_name FROM Player WHERE player_name <> ?;');
                    $stmt->execute(array($player_name));
                    $rival = $stmt->fetch(PDO::FETCH_OBJ);
                    return array('response' => 'start','map_side' => $result->map_side, 'rival_name' => $rival->player_name);
                    
                }
                sleep(1);
            }
            
            // IF 5 seconds cannot wait for another player
            $stmt = $this->pdo->prepare('UPDATE Player SET player_name = ?, player_status = ? WHERE player_name = ?;');
            $stmt->execute(array('_'.$player_name,'INACTIVE',$player_name));
            
            return array('response' => 'No rival');
            
        }
        
        function getOwnerOfBall($ball_id)
        {
            $stmt = $this->pdo->prepare('SELECT owner_name FROM Ball WHERE ball_id = ?;');
            $stmt->execute(array($ball_id));
            $owner_name = $stmt->fetch(PDO::FETCH_OBJ)->owner_name;
            return $owner_name;
        }
        
        function getBallPositionAndSpeed($ball_id)
        {
            $instance = array("field" =>  "ball_id","value" =>  $ball_id);
            $operation = "read";
            $table_name = "Ball";
            $runningInfo = $this->startLampartAlgorithm($operation,$table_name,$instance);
            
            $stmt = $this->pdo->prepare('SELECT ball_position_x, ball_position_y,ball_speed_x,ball_speed_y,owner_name FROM Ball WHERE ball_id = ?;');
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
                                                  ),
                                 'owner_name' => $ball_detail->owner_name
                                 );
            $this->exitCriticalSection($instance,$table_name,$runningInfo['ticket_num']);
            return $ball_detail;
            
        }
        
        function getPlayerScore($player1_name, $player2_name)
        {
            
            $instance1 = array('field' =>  'player_name','value' =>  "'".$player1_name."'");
            $instance2 = array('field' =>  'player_name','value' =>  "'".$player2_name."'");
            $operation = "read";
            $table_name = "Player";
            $runningInfo1 = $this->startLampartAlgorithm($operation,$table_name,$instance1);
            $runningInfo2 = $this->startLampartAlgorithm($operation,$table_name,$instance2);
            
            $stmt = $this->pdo->prepare('SELECT player_score  as score FROM Player WHERE player_name = ?;');
            $stmt->execute(array($player1_name));
            $score1 = $stmt->fetch(PDO::FETCH_OBJ);
            
            $stmt = $this->pdo->prepare('SELECT player_score  as score FROM Player WHERE player_name = ?;');
            $stmt->execute(array($player2_name));
            $score2 = $stmt->fetch(PDO::FETCH_OBJ);
            
            $score = array('player1Score' => $score1->score,'player2Score' => $score2->score);
            
            $this->exitCriticalSection($instance1,$table_name,$runningInfo1['ticket_num']);
            $this->exitCriticalSection($instance2,$table_name,$runningInfo2['ticket_num']);
            return $score;
        }
        
        function getBarPosition($player_name)
        {
            
            // CHECK IF THE RIVAL IS LOST CONNECTION
            if($this->getNumberOfActivePlayer() < 2)
            {
                // IF RIVAL IS LOST CONNECTION THEN I RETURN THIS AFTER MOBILE RECEIVE THIS HE WILL REQUEST TO CALL THE STOP AGAIN.
                return array('response' => 'player_lost');
            }
            
            // IF NO BODY IS LOST CONNECTION RUN THE LAMPORT ALGRRITHM
            $instance = array('field' =>  'player_name','value' =>  "'".$player_name."'");
            $operation = "read";
            $table_name = "Player";
            $runningInfo = $this->startLampartAlgorithm($operation,$table_name,$instance);
            
            $stmt = $this->pdo->prepare('SELECT bar_position_x, latest_eliminated_brick_id FROM Player WHERE player_name = ?;');
            $stmt->execute(array($player_name));
            $result = $stmt->fetch(PDO::FETCH_OBJ);
            $bar_position = $result->bar_position_x;
            $latest_eliminated_brick_id = $result->latest_eliminated_brick_id;
            
            // IF THE RIVAL NOT ELIMINATE A BRICK DURING THIS FRAME
            if($latest_eliminated_brick_id == '0')
            {
                $this->exitCriticalSection($instance,$table_name,$runningInfo['ticket_num']);
                // IF THE THE BALL CHANGED OWNERSHIP
                if($this->checkOwnerShipChange($player_name)){
                    $owner1 = $this->getOwnerOfBall(1);
                    $owner2 = $this->getOwnerOfBall(2);
                    return array('response' => 'bar_ownership_changed','bar_position_x' => $bar_position,'ball1' => $owner1,'ball2' => $owner2);
                }else{
                    return array('response' => 'bar','bar_position_x' => $bar_position);
                }
            }
            // IF THE RIVAL DID ELIMINATE A BRICK DURING THIS FRAME
            else
            {
                $stmt = $this->pdo->prepare('UPDATE Player SET latest_eliminated_brick_id = ? WHERE player_name = ?;');
                $stmt->execute(array('null',$player_name));
                $this->exitCriticalSection($instance,$table_name,$runningInfo['ticket_num']);
                
                // IF THE THE BALL CHANGED OWNERSHIP
                if($this->checkOwnerShipChange($player_name)){
                    $owner1 = $this->getOwnerOfBall(1);
                    $owner2 = $this->getOwnerOfBall(2);
                    return array('response' => 'more_ownership_changed','bar_position_x' => $bar_position, 'brick_id' => $latest_eliminated_brick_id,'ball1' => $owner1,'ball2' => $owner2);
                }else{
                    return array('response' => 'more','bar_position_x' => $bar_position, 'brick_id' => $latest_eliminated_brick_id);
                }
            }
            
            
            
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
        
        function checkOwnerShipChange($player_name)
        {
            // SEE IF RIVAL MARK THEIR NAME ON THE BALL CHANG_MARK, IF SO, THAT MEANS HE JUST CHANGED THE OWNERSHIP OF THAT BALL
            $stmt = $this->pdo->prepare('SELECT COUNT(*) AS num FROM Ball WHERE owner_name = ? AND change_mark = ?;');
            $stmt->execute(array($player_name, '1'));
            $num = $stmt->fetch(PDO::FETCH_OBJ)->num;
            if($num > 0)
            {
                $stmt = $this->pdo->prepare('UPDATE Ball SET change_mark = ? WHERE owner_name = ?;');
                $stmt->execute(array('0',$player_name));
                return true;
            }
            else
            {
                return false;
            }
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
        
        function getNumberOfActivePlayer()
        {
            $stmt = $this->pdo->prepare("SELECT count(*) as num FROM Player WHERE player_status = 'ACTIVE';");
            $stmt->execute();
            $result = $stmt->fetch(PDO::FETCH_OBJ)->num;
            return $result;
        }
        
        function updateBallPosition($ball_id,$ball_position_x,$ball_position_y)
        {
            $instance = array('field' =>  'ball_id','value' =>  $ball_id);
            $operation = "write";
            $table_name = "Ball";
            $runningInfo = $this->startLampartAlgorithm($operation,$table_name,$instance);
            
            $stmt = $this->pdo->prepare('UPDATE Ball SET ball_position_x = ?, ball_position_y = ? WHERE ball_id = ?;');
            $stmt->execute(array($ball_position_x,$ball_position_y,$ball_id));
            
            $this->exitCriticalSection($instance,$table_name,$runningInfo['ticket_num']);
        }
        
        
        function initialiseTheBall($player_name,$map_side)
        {
            //$stmt = $this->pdo->prepare('SELECT COUNT(*) AS available FROM BALL WHERE taken = ?;');
            //$stmt->execute(array('0'));
            //$num = $stmt->fetch(PDO::FETCH_OBJ)->available;
            if($map_side == 'A')
            {
                // INITIALISE THE BALL POSITION
                $stmt = $this->pdo->prepare('UPDATE Ball SET owner_name = ?, ball_position_x = ?, ball_position_y = ?, ball_speed_x = ?, ball_speed_y =?, lock_type = ?,	serving_number = ? ,ticket = ?, change_mark = ? WHERE ball_id = ?;');
                $stmt->execute(array($player_name, '0.5', '0.6', '0.005', '0.005','no','0','0','0', '1'));
            }
            if($map_side == 'B')
            {
                $stmt = $this->pdo->prepare('UPDATE Ball SET owner_name = ?, ball_position_x = ?, ball_position_y = ?, ball_speed_x = ?, ball_speed_y =?, lock_type = ?,	serving_number = ? ,ticket = ?, change_mark = ?  WHERE ball_id = ?;');
                $stmt->execute(array($player_name, '0.5', '0.4', '-0.005', '-0.005','no','0','0', '0', '2'));
            }
            
        }
        
        
        function updateBallSpeed($ball_id,$ball_speed_x,$ball_speed_y)
        {
            $instance = array('field' =>  'ball_id','value' =>  $ball_id);
            $operation = "write";
            $table_name = "Ball";
            $runningInfo = $this->startLampartAlgorithm($operation,$table_name,$instance);
            
            $stmt = $this->pdo->prepare('UPDATE Ball SET ball_speed_x = ?, ball_speed_y = ? WHERE ball_id = ?;');
            $stmt->execute(array($ball_speed_x,$ball_speed_y,$ball_id));
            
            $this->exitCriticalSection($instance,$table_name,$runningInfo['ticket_num']);
        }
        
        function changeOwnerShip($ball_id,$new_owner_name)
        {
            $instance = array('field' =>  'ball_id','value' =>  $ball_id);
            $operation = "write";
            $table_name = "Ball";
            $runningInfo = $this->startLampartAlgorithm($operation,$table_name,$instance);
            
            $stmt = $this->pdo->prepare('UPDATE Ball SET owner_name = ?, change_mark = ? WHERE ball_id = ?;');
            $stmt->execute(array($new_owner_name,'1',$ball_id));
            
            $this->exitCriticalSection($instance,$table_name,$runningInfo['ticket_num']);
        }
        
        function eliminateBrick($brick_id)
        {
            $instance = array('field' =>  'brick_id','value' =>  $brick_id);
            $operation = "write";
            $table_name = "Brick";
            $runningInfo = $this->startLampartAlgorithm($operation,$table_name,$instance);
            
            $stmt = $this->pdo->prepare('UPDATE Brick SET brick_status = ? WHERE brick_id = ?;');
            $stmt->execute(array('INACTIVE',$brick_id));
            
            $this->exitCriticalSection($instance,$table_name,$runningInfo['ticket_num']);
        }
        
        function updateScore($brick_id,$player_name)
        {
            $instance = array('field' =>  'player_name','value' => "'".$player_name."'");
            $operation = "write";
            $table_name = "Player";
            $runningInfo = $this->startLampartAlgorithm($operation,$table_name,$instance);
            
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
                
                
                $instance2 = array('field' =>  'player_name','value' => "'".$opponent_name."'");
                $runningInfo2 = $this->startLampartAlgorithm($operation,$table_name,$instance2);
                
                
                // ADD THE BRICK VALUE TO THIS PLAYER
                $stmt = $this->pdo->prepare('UPDATE Player SET player_score = player_score + ? WHERE player_name = ?;');
                $stmt->execute(array($value,$player_name));
                
                // ADD THE BRICK VALUE TO OPPONENT PLAYER
                $stmt = $this->pdo->prepare('UPDATE Player SET player_score = player_score - ? WHERE player_name = ?;');
                $stmt->execute(array($value,$opponent_name));
                $this->exitCriticalSection($instance2,$table_name,$runningInfo['ticket_num']);
                
            }
            // MARK THE LATEST ELIMINATED BRICK
            $stmt = $this->pdo->prepare('UPDATE Player SET latest_eliminated_brick_id = ? WHERE player_name = ?;');
            $stmt->execute(array($brick_id,$player_name));
            
            // DESTROY THE BRICK
            $this->eliminateBrick($brick_id);
            
            $this->exitCriticalSection($instance,$table_name,$runningInfo['ticket_num']);
            return array('Info' => 'Brick Eliminate Success');
        }
        
        function updateBarPosition($player_name,$bar_position_x)
        {
            $stmt = $this->pdo->prepare('UPDATE Player SET bar_position_x = ? WHERE player_name = ?;');
            $stmt->execute(array($bar_position_x,$player_name));
        }
        
        function stopGame($player_name)
        {
            $rand = rand(1, 100);
            $stmt = $this->pdo->prepare("UPDATE Player SET player_name = ?,player_score = ?, bar_position_x = ?, player_status = ?, latest_eliminated_brick_id = ?, lock_type = 'no', serving_number = ?, ticket = ? WHERE player_name = ?;");
            $stmt->execute(array($rand.$player_name,'0','0.300','INACTIVE','0','0','0',$player_name));
            
            $stmt = $this->pdo->prepare('SELECT COUNT(*) AS num FROM Player WHERE player_status = ?;');
            $stmt->execute(array('INACTIVE'));
            $inactive_num = $stmt->fetch(PDO::FETCH_OBJ);
            
            // IF ALL PLAYERS STOP
            if($inactive_num->num > 1)
            {
                // REACTIVE THE BRICKS PREPARE FOR THE NEXT GAME
                $stmt = $this->pdo->prepare("UPDATE Brick SET brick_status = ?, lock_type = 'no', serving_number = ?, ticket = ?;");
                $stmt->execute(array('ACTIVE','0','0'));
            }
        }
        
        /*
         function allStop()
         {
         // INACTIVE ALL PLAYERS
         $stmt = $this->pdo->prepare('UPDATE Player SET player_name = CONCAT('_', player_name) , player_status = ?;');
         $stmt->execute(array('INACTIVE'));
         
         // REACTIVE ALL THE BRICKS
         $stmt = $this->pdo->prepare('UPDATE Brick SET brick_status = ?;');
         $stmt->execute(array('ACTIVE'));
         }
         
         */
        
        function startLampartAlgorithm($operationType,$table_name,$instance)
        {
            
            
            $ticket_num = $this->requestEnterCriticalSection($table_name,$instance);
            
            $ready = $this->tryEnterCriticalSection($operationType,$instance,$table_name,$ticket_num);
            
            return array('ticket_num' => $ticket_num, 'ready' => $ready);
            //$this->exitCriticalSection($instance,$table_name,$ticket_num);
            
        }
        
        
        // INSTANCE IS A DCTIONARY LIKE (player_name:jack) or (ball_id: 1)or (brick_id: abrick_id)
        function requestEnterCriticalSection($table_name,$instance)
        {
            
            
            // DRAW A TICKET
            $stmt = $this->pdo->prepare("SELECT ticket FROM " .$table_name. " WHERE " .$instance['field']. " = " .$instance['value']);
            
            $stmt->execute();
            $ticket = $stmt->fetch(PDO::FETCH_OBJ);
            // INCREASE THE TICKET NUMBER FOR NEXT CLIENT
            $stmt = $this->pdo->prepare("UPDATE " .$table_name.  " SET ticket = ticket + 1 WHERE " .$instance['field']. " = " .$instance['value']);
            $stmt->execute();
            return $ticket->ticket;
            
            
        }
        
        
        function tryEnterCriticalSection($operationType,$instance,$table_name,$ticket_num)
        {
            for ($i = 0; $i < 5; $i++)
            {
                if($this->tryLockCriticalSection($operationType,$instance,$table_name,$ticket_num))
                {
                    return true; //ready to execute the code
                }
                usleep(1000);
            }
            
            return false; //fail to enter the critical section
        }
        
        
        function tryLockCriticalSection($operationType,$instance,$table_name,$ticket_num)
        {
            
            $stmt = $this->pdo->prepare("SELECT lock_type FROM " .$table_name. " WHERE " .$instance['field']. " = " .$instance['value']);
            $stmt->execute();
            $current_lock_type = $stmt->fetch(PDO::FETCH_OBJ)->lock_type;
            
            // BASE ON DIFFERENT TYPE OF OPERATION NEEDS DIFFERENT LOCKS
            
            if($current_lock_type == 'no')
            {
                if($operationType == 'read')
                {
                    $lock_type = 'shared';
                }
                else
                {
                    $lock_type = 'exclusive';
                }
                
                // Check the service counter is calling who
                $stmt = $this->pdo->prepare("SELECT serving_number FROM " .$table_name. " WHERE " .$instance['field']. " = " .$instance['value']);
                $stmt->execute();
                $serving_number = $stmt->fetch(PDO::FETCH_OBJ)->serving_number;
                
                // If service counter is is calling you
                if($serving_number == $ticket_num)
                {
                    
                    $stmt = $this->pdo->prepare("UPDATE " .$table_name. " SET lock_type = '" .$lock_type. "' WHERE " .$instance['field']. " = " .$instance['value']);
                    $stmt->execute();
                    return true;
                }
                // It's not your turn yet
                else
                {
                    return false;
                }
            }
            
            // An exception is :
            // If it's a shared lock and you want to read you don't need to wait till you get the lock, instead, you directly read
            if($current_lock_type == 'shared' && $operationType == 'read')
            {
                
                //Set the service_number is your ticket to extend the duration of the lock
                $stmt = $this->pdo->prepare("UPDATE " .$table_name. " SET serving_number = " .$ticket_num. " WHERE " .$instance['field']. " = " .$instance['value']);
                $stmt->execute();
                return true;
            }
            
            return false;
        }
        
        
        function exitCriticalSection($instance,$table_name,$ticket_num)
        {
            
            $stmt = $this->pdo->prepare("SELECT serving_number FROM " .$table_name. " WHERE " .$instance['field']. " = " .$instance['value']);
            $stmt->execute();
            $serving_number = $stmt->fetch(PDO::FETCH_OBJ)->serving_number;
            
            /* Why I have to test this but not just directly plus serving_number and free the lock?
             Because if more than one guys in the shared read lock, if the early thread entered this zone finishes earlier and it add the serving number then free the lock
             then might make the next trancation like write to access this zone while the other reading guy is not finish. So I have to check the $ticket_num is whether equal
             to sercing number to determine if this guy is the last guy in parallel reading
             */
            
            if($ticket_num == $serving_number)
            {
                
                $stmt = $this->pdo->prepare("UPDATE " .$table_name. " SET serving_number = serving_number + 1, lock_type = 'no' WHERE " .$instance['field']. " = " .$instance['value']);
                $stmt->execute();
            }
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
    }
