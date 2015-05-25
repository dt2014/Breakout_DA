<?php

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
                
        // ACTIVE THE PLAYER ITSELF
        $stmt = $this->pdo->prepare('UPDATE Player SET player_name = ?, player_score = ?,bar_position_x = ?,  player_status = ?, latest_eliminated_brick_id = ?, lock_type = ?, serving_number = 0, ticket = 0 WHERE map_side = ?;');
        $stmt->execute(array($player_name,'0','0.3','ACTIVE','null','no',$result->map_side));
        

                
        // INITIALISE THE PLAYER BALL
        $this->initialiseTheBall($player_name,$result->map_side);
                
    }
    
    function startInfo($player_name){

        $stmt = $this->pdo->prepare('SELECT player_name FROM Player WHERE player_name <> ?;');
        $stmt->execute(array($player_name));
        $rival = $stmt->fetch(PDO::FETCH_OBJ);

        $stmt = $this->pdo->prepare('SELECT map_side FROM Player WHERE player_name = ?;');
        $stmt->execute(array($player_name));
        $map_side = $stmt->fetch(PDO::FETCH_OBJ);

        return array('response' => 'start','map_side' => $map_side->map_side, 'rival_name' => $rival->player_name);
    }

    function getOwnerOfBall($ball_id)
    {
        $stmt = $this->pdo->prepare('SELECT owner_name FROM Ball WHERE ball_id = ?;');
        $stmt->execute(array($ball_id));
        $owner_name = $stmt->fetch(PDO::FETCH_OBJ)->owner_name;
        return $owner_name;
    }
    
    function getRivalBall($ball_indicate)
    {
        // Know all the ball
        if($ball_indicate == -2)
        {
            return null;
        }
        
        // Know no ball
        else if($ball_indicate == 1)
        {
            $ball_id1 = 1;
            $ball_id2 = 2;
            $pos1 = $this->getBallPosition($ball_id1);
            $pos2 = $this->getBallPosition($ball_id2);
            return array('1' => $pos1, '2' => $pos2);
        }
        // Know the ball number 1 then get ball 2
        else if($ball_indicate == 2)
        {
            $ball_id = 2;
            $pos = $this->getBallPosition($ball_id);
            return array('2' => $pos);
        }
        // Know the ball number 2 then get ball 1
        else if($ball_indicate == -1)
        {
            $ball_id = 1;
            $pos = $this->getBallPosition($ball_id);
            return array('1' => $pos);
        }
        
    }
    
    function getBallPosition($ball_id)
    {
        $stmt = $this->pdo->prepare('SELECT ball_position_x, ball_position_y, ball_speed_x,ball_speed_y FROM Ball WHERE ball_id = ?;');
        $stmt->execute(array($ball_id));
        $ball_detail = $stmt->fetch(PDO::FETCH_OBJ);
        
        $ball_position =array('x' => $ball_detail->ball_position_x,
                              'y' => $ball_detail->ball_position_y,
                              'w' => $ball_detail->ball_speed_x,
                              'z' => $ball_detail->ball_speed_y
                              );
        return $ball_position;
        
    }

    function getPlayerScore($player1_name, $player2_name)
    {
        $stmt = $this->pdo->prepare('SELECT player_score  as score FROM Player WHERE player_name = ?;');
        $stmt->execute(array($player1_name));
        $score1 = $stmt->fetch(PDO::FETCH_OBJ);
        
        $stmt = $this->pdo->prepare('SELECT player_score  as score FROM Player WHERE player_name = ?;');
        $stmt->execute(array($player2_name));
        $score2 = $stmt->fetch(PDO::FETCH_OBJ);
        
        $score = array('A' => $score1->score,'B' => $score2->score);
        
        return $score;
    }
    
    function getBarPosition($player_name)
    {
        
        // CHECK IF THE RIVAL IS LOST CONNECTION
        if($player_name == null)
        {
            // IF RIVAL IS LOST CONNECTION THEN I RETURN THIS AFTER MOBILE RECEIVE THIS HE WILL REQUEST TO CALL THE STOP AGAIN.
            return array('r' => 'l');
        }
        
        // CHECK IF THE RIVAL IS LOSE THE GAME
        if($this->checkLose($player_name))
        {
            
            $this->stopGame($player_name);
            return array('r' => 'w');
        }
        
        $stmt = $this->pdo->prepare('SELECT bar_position_x, latest_eliminated_brick_id FROM Player WHERE player_name = ?;');
        $stmt->execute(array($player_name));
        $result = $stmt->fetch(PDO::FETCH_OBJ);
        $bar_position = $result->bar_position_x; 
        $latest_eliminated_brick_id = $result->latest_eliminated_brick_id;
        
        // IF THE RIVAL NOT ELIMINATE A BRICK DURING THIS FRAME
        if($latest_eliminated_brick_id == '0')
        {
            // IF THE THE BALL CHANGED OWNERSHIP
            if($this->checkOwnerShipChange($player_name)){
                $owner1 = $this->getOwnerOfBall(1);
                $owner2 = $this->getOwnerOfBall(2);
                return array('r' => 'o','x' => $bar_position,'1' => $owner1,'2' => $owner2);
            }else{
                return array('r' => 'b','x' => $bar_position);
            }
        }
        // IF THE RIVAL DID ELIMINATE A BRICK DURING THIS FRAME
        else
        {
            $stmt = $this->pdo->prepare('UPDATE Player SET latest_eliminated_brick_id = ? WHERE player_name = ?;');
            $stmt->execute(array('null',$player_name));
            
            // RETURN THE SCORE OF TWO PLAYER( Notice no if statement here)
            $names = $this->getPlayerNames();
            $player1_name = $names[0];
            $player2_name = $names[1];
            $score = $this->getPlayerScore($player1_name, $player2_name);
            global $reply;
            $reply['s'] = $score;


            // IF THE THE BALL CHANGED OWNERSHIP
            if($this->checkOwnerShipChange($player_name)){
                $owner1 = $this->getOwnerOfBall(1);
                $owner2 = $this->getOwnerOfBall(2);
                return array('r' => 'k','x' => $bar_position, 'k' => $latest_eliminated_brick_id,'1' => $owner1,'2' => $owner2);
             }else{
                 return array('r' => 'm','x' => $bar_position, 'k' => $latest_eliminated_brick_id);
             }
        }

        
        
    }
    
    function getPlayerNames()
    {
        $stmt = $this->pdo->prepare("SELECT player_name FROM Player WHERE map_side = 'A'");
        $stmt->execute();
        $names1 = $stmt->fetch(PDO::FETCH_OBJ)->player_name;
        $stmt = $this->pdo->prepare("SELECT player_name FROM Player WHERE map_side = 'B'");
        $stmt->execute();
        $names2 = $stmt->fetch(PDO::FETCH_OBJ)->player_name;
        return array($names1,$names2);
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
        $stmt = $this->pdo->prepare('SELECT player_name FROM Player WHERE player_status <> ? AND player_name <> ?;');
        $stmt->execute(array('INACTIVE',$player_name));
        $opponent_name = $stmt->fetch(PDO::FETCH_OBJ);
        if($opponent_name == null)
            return null;
        return $opponent_name->player_name;
    }
    
    function getNumberOfActivePlayer()
    {
        $stmt = $this->pdo->prepare("SELECT count(*) as num FROM Player WHERE player_status = 'ACTIVE';");
        $stmt->execute();
        $result = $stmt->fetch(PDO::FETCH_OBJ)->num;
        return $result;
    }
    
    function updateBallPositionAndSpeed($ball_id,$ball_position_x,$ball_position_y,$ball_speed_x,$ball_speed_y)
    {
        
        $stmt = $this->pdo->prepare('UPDATE Ball SET ball_position_x = ?, ball_position_y = ?, ball_speed_x = ?, ball_speed_y = ? WHERE ball_id = ?;');
        $stmt->execute(array($ball_position_x,$ball_position_y,$ball_speed_x,$ball_speed_y,$ball_id));
    }
    
    
    function initialiseTheBall($player_name,$map_side)
    {
            //$stmt = $this->pdo->prepare('SELECT COUNT(*) AS available FROM BALL WHERE taken = ?;');
            //$stmt->execute(array('0'));
            //$num = $stmt->fetch(PDO::FETCH_OBJ)->available;
            if($map_side == 'A')
            {
                // INITIALISE THE BALL POSITION
                $stmt = $this->pdo->prepare('UPDATE Ball SET owner_name = ?, ball_position_x = ?, ball_position_y = ?, ball_speed_x = ?, ball_speed_y =?, lock_type = ?,    serving_number = ? ,ticket = ?, change_mark = ? WHERE ball_id = ?;');
                $stmt->execute(array($player_name, '0.5', '0.6', '0.009', '0.010','no','0','0','0', '1'));
            }
            if($map_side == 'B')
            {
                $stmt = $this->pdo->prepare('UPDATE Ball SET owner_name = ?, ball_position_x = ?, ball_position_y = ?, ball_speed_x = ?, ball_speed_y =?, lock_type = ?,    serving_number = ? ,ticket = ?, change_mark = ?  WHERE ball_id = ?;');
                $stmt->execute(array($player_name, '0.5', '0.4', '-0.009', '-0.010','no','0','0', '0', '2'));
            }
        
    }
    

    
    function changeOwnerShip($ball_id,$new_owner_name)
    {
        
        $stmt = $this->pdo->prepare('UPDATE Ball SET owner_name = ?, change_mark = ? WHERE ball_id = ?;');
        $stmt->execute(array($new_owner_name,'1',$ball_id));
        
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
            return 'n';
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
            
            
            // MINUS THE BRICK VALUE TO THIS PLAYER
            $stmt = $this->pdo->prepare('UPDATE Player SET player_score = player_score - ? WHERE player_name = ?;');
            $stmt->execute(array($value,$player_name));
            
            // ADD THE BRICK VALUE TO OPPONENT PLAYER
            $stmt = $this->pdo->prepare('UPDATE Player SET player_score = player_score + ? WHERE player_name = ?;');
            $stmt->execute(array($value,$opponent_name));
            
        }
        // MARK THE LATEST ELIMINATED BRICK
        $stmt = $this->pdo->prepare('UPDATE Player SET latest_eliminated_brick_id = ? WHERE player_name = ?;');
        $stmt->execute(array($brick_id,$player_name));
        
        // DESTROY THE BRICK
        $this->eliminateBrick($brick_id);
        return 'y';
    }
                                                 
   function updateBarPosition($player_name,$bar_position_x)
    {
        $stmt = $this->pdo->prepare('UPDATE Player SET bar_position_x = ? WHERE player_name = ?;');
        $stmt->execute(array($bar_position_x,$player_name));
    }
    
    function stopGame($player_name)
    {
        // Initialize the ball
        $stmt = $this->pdo->prepare("SELECT map_side FROM Player WHERE player_name = ?;");
        $stmt->execute(array($player_name));
        $result = $stmt->fetch(PDO::FETCH_OBJ);
        $map_side = $result->map_side;
        $this->initialiseTheBall($player_name,$map_side);


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
    
    function loseGame($player_name)
    {
        $stmt = $this->pdo->prepare("UPDATE Player SET player_status = ? WHERE player_name = ?;");
        $stmt->execute(array('LOSE',$player_name));
    }
    
    function checkLose($player_name)
    {
        $stmt = $this->pdo->prepare("SELECT player_status FROM Player WHERE player_name = ?;");
        $stmt->execute(array($player_name));
        $result = $stmt->fetch(PDO::FETCH_OBJ)->player_status;
        if($result == 'LOSE')
            return true;
        else
            return false;
    }
    
 
    
    
    
    
    
    
    
    
    
    
    
}
