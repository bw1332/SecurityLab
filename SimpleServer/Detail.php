<?php
session_start();
require('db.php');
require('SecurityUtil.php');

$FILE_DIR = $_SERVER['DOCUMENT_ROOT']."/uploads/";
/*check session. If user not login will redirect to login page */
checkLogin();

$contentId = $_GET['id'];
$db = new mydb();
$res = mydb::getContentDetail($db,$contentId);
if(isset($res)){
    $row = $res->fetchArray();
    $title = $row['title'];
    $content = $row['content'];
    $filePath = $row['filePath'];
    $userid = $row['userid'];
    $username = $row['name'];
    $time = $row['time'];
    $fileName = substr_replace($filePath,null,0,strlen($FILE_DIR));
}


/* if receive "att" then download corresponding attachment */
if(!empty($_GET['att']) && !empty($filePath) && $_GET['att'] == $fileName && file_exists($filePath)){
    header("Content-Type: application/zip");
    header("Content-Disposition: attachment; filename=".$fileName);
    header("Content-Transfer-Encoding: binary");
    readfile($filePath);
    exit;
}

/* if receive "del" then delete the corresponding post. Only poster */
if(!empty($_GET['del']) && !empty($contentId) && $_GET['del'] === $contentId){
    /* check whether the current user */
    if($_SESSION['usrid'] == $userid){
        /* having attachment, remove file */
        if(!empty($filePath) && file_exists($filePath)){
            unlink($filePath);
        }
        mydb::deletePost($db,$contentId);
        header("Content-type: text/html; charset=utf-8");
        header("location:SimpleForum.php");
        $db->close();
        exit();
    }
}
$db->close();
?>

<html>
<head>
    <meta charset="UTF-8">
<title>detail</title>
    <style>
        #head{
            float: right;
            margin-right: 5%;
            margin-top: 1%;
        }
    </style>
</head>
<body>
<div id="head"><?php include('Head.php') ?></div>
<b style="font-size: x-large"><?php echo $title?></b><label style="margin-left: 1%">
    <?php
        /* only author can delete the post. legal user can get the access for deleting */
        if(isset($_SESSION['usrid'],$userid)&& $_SESSION['usrid']==$userid){
            echo "(<a href='Detail.php?id={$contentId}&del={$contentId}'>delete</a>)";
        }
    ?></label>
<p> author : <?php echo $username?><br>post time : <?php echo $time?>
</p>
<hr/>
<p>
<?php echo $content?>
</p>
<p>
    Attachment:<?php if(!empty($fileName)){ echo "<a href='Detail.php?id={$contentId}&att={$fileName}'>{$fileName}</a>";}?>

</body>

</html>
