<?php
session_start();
if(isset($_COOKIE[session_name()])){
    /*if having cookie, expire it*/
    setcookie(session_name(),null,time()-36000,'/');
}
/*reset $_SESSION*/
$_SESSION = array();
/*destroy session*/
session_destroy();
/*redirect*/
header("Content-type: text/html; charset=utf-8");
header("location:Login.php");
exit();
?>