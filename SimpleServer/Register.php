<?php
require('db.php');
$MAX_SIZE = 20;
$nameError = null;
$passwordError = null;
if(isset($_POST['name'],$_POST['password'])) {
    //length check
    if(empty($_POST['name'])){
        $nameError = "can not be empty!";
    }
    if(empty($_POST['password'])){
        $passwordError = "can not be empty!";
    }
    if(strlen($_POST['name'])>$MAX_SIZE){
        $nameError = "too long!";
    }
    if(strlen($_POST['password']>$MAX_SIZE)){
        $passwordError = "too long!";
    }
    if(empty($nameError)&&empty($passwordError)) {
        $db = new mydb();
        /* get legal input */
        $name = htmlspecialchars(substr($_POST['name'], 0, $MAX_SIZE));
        $password = htmlspecialchars(substr($_POST['password'], 0, $MAX_SIZE));
        $name = $db->escapeString($name);
        $password = $db->escapeString($password);

        $res = mydb::checkName($db, $name);
        $row = $res->fetchArray();
        if (empty($row)) {
            mydb::insertUser($db, $name, $password);
            header("Content-type: text/html; charset=utf-8");
            header('location:Login.php');
        } else {
            $nameError = "the name already exit";
        }
        $db->close();
    }
}


?>

<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <style>#head{
            float: right;
            margin-right: 5%;
            margin-top: 1%;
        }</style>
</head>
<body>
<div id="head"><a href="Login.php">Login</a></div>
<h1>Register</h1>
<hr/>
<form action="Register.php" method="post">
    <label>UserName (no more than 20):</label><br/><input type="text" name="name"/><label style="color: red"><?php echo $nameError;?></label><br/>
    <label>PassWord (no more than 20):</label><br/><input type="password" name="password"/><label style="color: red"><?php echo $passwordError;?></label><br/>
    <input type="submit" name="submit" value="Register">
</form>
</body>
</html>
