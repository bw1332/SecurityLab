<?php
require('db.php');
require('SecurityUtil.php');
session_start();

$MAX_SIZE = 20;
$oldPasswordErrorMessage = null;
$newPasswordErrorMessage = null;

/*check session.if user not login will redirect to login page */
checkLogin();

/* check token */
if(isset($_POST['token'])){
    if($_POST['token'] != $_SESSION['token']){
        require('Logout.php');
    }
}
$db = new mydb();

if(isset($_POST['old'],$_POST['new'])){
    /* check input length*/
    if(empty($_POST['new'])){
        $newPasswordErrorMessage = "can not be empty";
    }
    if(strlen($_POST['new'])>$MAX_SIZE){
        $newPasswordErrorMessage = "too long";
    }
    /* convert html special chars and set the limited size of input string */
    $old = htmlspecialchars(substr($_POST['old'],0,$MAX_SIZE),ENT_QUOTES);
    $new = htmlspecialchars(substr($_POST['new'],0,$MAX_SIZE),ENT_QUOTES);
    $old = $db->escapeString($old);
    $new = $db->escapeString($new);
    /* check old password */
    $res = mydb::checkUser($db,$_SESSION['usrname'],$old);
    $row = $res->fetchArray();
    if(!empty($row) && empty($newPasswordErrorMessage)){
        /* right old password, no problem with new password */
        mydb::updatePassword($db,$_SESSION['usrid'],$new);
        $db->close();
        require('Logout.php');
    }else{
        $oldPasswordErrorMessage = "wrong password";
    }
    $db->close();
}



?>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>Change Password</title>
    <style>#head{
            float: right;
            margin-right: 5%;
            margin-top: 1%;
        }</style>
</head>
<body>
<div id="head"><?php include('Head.php') ?></div>
<h1>Change Password</h1>
<hr/>
<form action="ChangePassword.php" method="post">
    <label>Old Password :</label><br/><input type="password" name="old"/><label style="color: red"><?php echo $oldPasswordErrorMessage;?></label><br/>
    <label>New PassWord :</label><br/><input type="password" name="new"/><label style="color: red"><?php echo $newPasswordErrorMessage;?></label><br/>
    <input type="hidden" name="token" value="<?php echo $_SESSION['token']; ?>"/>
    <input type="submit" name="submit" value="change">
</form>
</body>
</html>


