<?php
require 'db.php';
require 'SecurityUtil.php';
$MAX_SIZE = 20;

session_start();

/* set token if having session */
$token = "";
if(isset($_SESSION['token'])){
    $token = $_SESSION['token'];
}

if(isset($_POST['name'],$_POST['password'],$_POST['token'])){

    /* check token */
    if($token!=$_POST['token']){
        require('Logout.php');
    }

    /* convert html special chars for user name which will show on the page */
    $name = htmlspecialchars(substr($_POST['name'],0,$MAX_SIZE),ENT_QUOTES);
    $password = substr($_POST['password'],0,$MAX_SIZE);
    /* get escaped strings */
    $db = new mydb();
    $name = $db->escapeString($name);
    $password = $db->escapeString($password);

    $res = mydb::checkUser($db,$name,$password);
    if(!$res){
        header("Content-type: text/html; charset=utf-8");
        header("location:Login.php");
    }
    $row = $res->fetchArray();
    /* check if already login */
    if(isset($_SESSION['usrid']) && $_SESSION['usrid']!=$row['_id']){
        /* already login but try to login in another account */
        if(isset($_COOKIE[session_name()])){
            /* if having cookie, expire it */
            setcookie(session_name(),null,time()-36000,'/');
        }
        /* reset $_SESSION */
        $_SESSION = array();
        /* reset session and delete old id */
        session_regenerate_id(true);
        /* reset cookie */
        setcookie(session_name(),session_id(),0,'/');
    }
    /* if it is a new session, set token */
    if(!isset($_SESSION['token'])){
        $_SESSION['token'] = getRandomToken();
    }
    $_SESSION['usrid'] = $row['_id'];
    $_SESSION['usrname'] = $row['name'];

    header("Content-type: text/html; charset=utf-8");
    header("location:SimpleForum.php");
    $db->close();
    exit;
}
?>

<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>#head{
            float: right;
            margin-right: 5%;
            margin-top: 1%;
        }</style>
</head>
<body>
<div id="head"><?php include('Head.php') ?></div>
<h1>Login</h1>
<hr/>
<form action="Login.php" method="post">
    <label>UserName :</label><br/><input type="text" name="name"/><br/>
    <label>PassWord :</label><br/><input type="password" name="password"/><br/>
    <input type="hidden" name="token" value="<?php echo $token ?>"/>
    <input type="submit" name="submit" value="Login">
</form>
<a href="Register.php">register</a>
</body>
</html>
