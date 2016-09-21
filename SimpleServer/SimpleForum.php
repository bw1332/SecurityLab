<?php
require('SecurityUtil.php');
require('db.php');
session_start();

$FILE_MAX_SIZE = 1024; // KB
$FILE_NAME_MAX_SIZE = 20;
$TITLE_MAX_SIZE = 200;
$CONTENT_MAX_SIZE = 2000;
$FILE_DIR = '/uploads/';

//variable
$title = "";
$content = "";
$filename = "";

/*check session.if user not login will redirect to login page */
checkLogin();

/* check token */
if(isset($_POST['token'])){
    if($_POST['token'] != $_SESSION['token']){
       require('Logout.php');
    }
}

$db = new mydb();
/* current all content in database*/
$current = mydb::getAllContent($db);

/* error messages to return */
$filErrorMessage = null;
$titleErrorMessage = null;
$contentErrorMeassge = null;

if(isset($_POST['title'], $_POST['content'])){

    if(empty($_POST['title'])){
        $titleErrorMessage = " title cannot be empty";
    }
    if(empty($_POST['content'])){
        $contentErrorMeassge = " content cannot be empty";
    }
    /* check input size*/
    if(strlen($_POST['title'])>$TITLE_MAX_SIZE){
        $titleErrorMessage = "title too long";
    }
    if(strlen($_POST['title'])>$TITLE_MAX_SIZE){
        $contentErrorMeassge = "content too long";
    }

    /* convert html special chars and set the limited size of input string */
    $title = htmlspecialchars(substr($_POST['title'],0,$TITLE_MAX_SIZE),ENT_QUOTES);
    $content = htmlspecialchars(substr($_POST['content'],0,$CONTENT_MAX_SIZE),ENT_QUOTES);
    $title = $db->escapeString($title);
    $content = $db->escapeString($content);
}

if(isset($_FILES['file']['error'],$_FILES['file']['name'],$_FILES['file']['size'],$_FILES['file']['type'],$_FILES['file']['tmp_name'])) {
    /* check if file uploaded successfully. error == 4 means no file uploaded,which is allow */
    if ($_FILES['file']['error'] > 0 && $_FILES['file']['error'] != 4) {
        /* set error message, no need to tell error detail */
        switch ($_FILES['file']['error']) {
            case 1:
            case 2:
                $filErrorMessage = "Please check the size of the file. The upload file is too large";
                break;
            case 3:
                $filErrorMessage = "Please upload again. The uploaded file was only partially uploaded";
                break;
            default:
                $filErrorMessage = "Please upload again";
                break;
        }
    } else if($_FILES['file']['error'] == 0){
        /*
         * convert html special chars and set the limited size of file name.
         * reset file name by file + user name + time to avoid conflict.
         */
        $filename = $_SESSION['usrname'].time().htmlspecialchars(substr($_FILES['file']['name'], 0, $FILE_NAME_MAX_SIZE),ENT_QUOTES);
        /* check file size */
        if ($_FILES['file']['size'] / 1024 > $FILE_MAX_SIZE) {
            $filErrorMessage = "Please check the size of the file. The upload file is too large";
        }
        /* check type, wrong type should be regarded as more serious problem, it will overwrite fileErrorMessage */
        if ($_FILES['file']['type'] != "application/zip") {
            $filErrorMessage = "Please upload again, The uploaed file is not a zip file";
        }

    }

    /* if have any error, the post will be denied */
    if (empty($filErrorMessage) && empty($contentErrorMeassge) && empty($titleErrorMessage)) {
        /* no error message store post */
        /* move file to right place */
        if($_FILES['file']['error']!= 4 && move_uploaded_file($_FILES['file']['tmp_name'], $_SERVER['DOCUMENT_ROOT'].$FILE_DIR.$filename)){
            mydb::insertPost($db, $title, $content, $_SERVER['DOCUMENT_ROOT'].$FILE_DIR.$filename);
        }else{
            mydb::insertPost($db, $title, $content, null);
        }

    }

}

?>



<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <style> 
        #postDIV{
            margin-bottom: 5%;
        }
        #showDiv{
            min-height: 50%;
        }
        #headDiv{
            float: right;
            margin-right: 5%;
            margin-top: 1%;
        }
    </style>
    <meta charset="UTF-8">
    <title>SimpleForum</title>
</head>
<body>
<div id="headDiv"><?php include('Head.php');?></div>
<h3>All Topics:</h3>
<div id="showDiv">
    <ul>
    <?php
    if($current!=null){
        while($row = $current->fetchArray(SQLITE3_ASSOC)){
            $title = $row['title'];
            $contentId = $row['_id'];
            echo "<li><a href = "."Detail.php?id=".$contentId.">{$title}</a></li>";
        }
    }
    $db->close();
    ?>
</ul>
</div>
<div id="postDIV">
    <hr/>
    <h4>New Post</h4>
    <form name="post"method="post" enctype="multipart/form-data" action="SimpleForum.php">
    <lable>Title:(no more than 200)</lable><br/><input name ="title" type="text"><label style="color: red"><?php echo $titleErrorMessage;?></label><br/><br/>
        <label>Content:(no more than 2000)</label><br/>
    <textarea name="content" rows="10" cols="100"></textarea><label style="color: red"><?php echo $contentErrorMeassge;?></label>
        <br/>
        <label>Attachment :(zip only, no more than 1MB) </label><input name="file" type="file" accept="application/zip"><label style="color: red"><?php echo $filErrorMessage;?></label>
        <br/>
        <input type="hidden" name="token" value="<?php echo $_SESSION['token'];?>">
        <input type="submit" value="Post" />
    </form>
</div>

</body>
</html>