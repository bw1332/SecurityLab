<?php
if(session_status()!=PHP_SESSION_ACTIVE){
    session_start();
}
if(isset($_SESSION['usrid'],$_SESSION['usrname'])){
    echo "<label>hello, {$_SESSION['usrname']}</label><br><a href='Logout.php'>Logout</a>";
    echo "<br/><a href='ChangePassword.php'>change password</a>";
}else{
    echo "<label><br/><a href='Login . php'>Login</a></lable>";
}

?>

