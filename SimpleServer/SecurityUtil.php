<?php

/*check session.if user not login will redirect to login page */
function checkLogin()
{
    if (!isset($_SESSION['usrid'], $_COOKIE[session_name()]) || session_id() != $_COOKIE[session_name()]) {
        setcookie(session_name(), null, time() - 36000, '/');
        $_SESSION = array();
        header("Content-type: text/html; charset=utf-8");
        header('location:Login.php');
        session_destroy();
        exit();
    }
}

/* return a random token, helping to prevent CSRF */
function getRandomToken()
{
    return md5(mt_rand());
}
?>