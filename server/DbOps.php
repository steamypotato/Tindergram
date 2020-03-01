<?php
    include_once "DbConnect.php";
    
    class DbOps{
        public $conn;

        function __construct(){
            $db = new DbConnect();

            $this->conn = $db->connect();
        }

        public function createUser($username, $password, $email){
            if($this->isUserExist($username, $email)) {
                return 0;
            }else{
                $encrPassword = md5($password);
                $stmt = $this->conn->prepare("INSERT INTO `users`(`username`, `email`, `password`, `following`) VALUES (?,?,?,NULL);");
                $stmt->bind_param("sss", $username, $email, $encrPassword);

                if($stmt->execute()){
                    return 1;
                }else{
                    echo $this->conn->error;    
                    return 2;
                }
            }
        }

        public function changeUsername($username, $id){
            if($this->isUsernameExist($username)) {
                return 0;
            }else{
                $stmt = $this->conn->prepare("UPDATE `users` SET `username` = ? WHERE `users`.`id` = ?");
                $stmt->bind_param("si", $username, $id);

                if($stmt->execute()){
                    return 1;
                }else{
                    echo $this->conn->error;    
                    return 2;
                }
            }
        }

        public function changePassword($password, $id){
            $encrPassword = md5($password);
            $stmt = $this->conn->prepare("UPDATE `users` SET `password` = ? WHERE `users`.`id` = ?");
            $stmt->bind_param("si", $encrPassword, $id);

            if($stmt->execute()){
                return true;
            }else{
                echo $this->conn->error;    
                return false;
            }
        }

        public function changeEmail($email, $id){
            if($this->isEmailExist($email)) {
                return 0;
            }else{
                $stmt = $this->conn->prepare("UPDATE `users` SET `email` = ? WHERE `users`.`id` = ?");
                $stmt->bind_param("si", $email, $id);

                if($stmt->execute()){
                    return 1;
                }else{
                    echo $this->conn->error;    
                    return 2;
                }
            }
        }

        private function isUserExist($username, $email) {
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
            $stmt->bind_param("ss", $username, $email);
            $stmt->execute(); 
            $stmt->store_result(); 
            return $stmt->num_rows > 0; 
        }

        private function isUsernameExist($username) {
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute(); 
            $stmt->store_result(); 
            return $stmt->num_rows > 0; 
        }

        private function isEmailExist($email) {
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
            $stmt->store_result(); 
            return $stmt->num_rows > 0; 
        }

        public function loginUser($username, $password) {
            $encrPassword = md5($password);
            $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ? AND password = ?");
            $stmt->bind_param("ss", $username, $encrPassword);
            $stmt->execute();
            $stmt->store_result(); 
            return $stmt->num_rows > 0; 
        }

        public function getUserByUsername($username) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");
            $stmt->bind_param("s", $username);
            $stmt->execute();
            return $stmt->get_result()->fetch_assoc();
	}

	public function saveImage($image,$id,$likes,$comments,$name,$type){
		$imageData = file_get_contents($image['image']["tmp_name"]);
		$stmt = $this->conn->prepare("INSERT INTO `pictures` (`image`,`name`,`type`, `id`, `likes`, `comments`) VALUES (?,?,?,?,?,?);");
		$stmt->bind_param("bsssss",$imageData,$name,$type,$id,$likes,$comments);
		if($stmt->execute()){
                    return true;
                }else{
                    echo $this->conn->error;
                    return false;
                }

	}
    }
?>
