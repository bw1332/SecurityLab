<?php
class mydb extends SQLite3{

    function __construct()
    {
        $this->open('test.db');
    }

    /**
     * check a user name and password
     * password is encoded by md5
     *
     * @param SQLite3 $db
     * @param $usr
     * @param $password
     * @return SQLite3Result
     */
    static  function checkUser(SQLite3 $db, $usr, $password){
        $password = md5($password);
        $sql = "select * from user where name = :name and  password = :password";
        $stmt = $db->prepare($sql);
        $stmt -> bindValue(':name',$usr,SQLITE3_TEXT);
        $stmt->bindValue(':password',$password,SQLITE3_TEXT);
        return $stmt->execute();
    }

    /**
     * get all posts
     *
     * @param SQLite3 $db
     * @return SQLite3Result
     */
    static function getAllContent(SQLite3 $db){
        $sql = "select * from content";
        return $db->query($sql);
    }

    /**
     * get certain post, associating with poster info
     *
     * @param SQLite3 $db
     * @param $contentID
     * @return SQLite3Result
     */
    static function getContentDetail(SQLite3 $db, $contentID){
        $sql = "select * from content, user  where content._id = :id and user._id = content.userid";
        $stmt = $db->prepare($sql);
        $stmt ->bindValue(':id',$contentID,SQLITE3_INTEGER);
        return $stmt->execute();
    }

    /**
     * insert the post
     *
     * @param SQLite3 $db
     * @param $title
     * @param $content
     * @param $filePath
     * @return SQLite3Result
     */
    static function insertPost(SQLite3 $db, $title, $content, $filePath){
        $sql = "insert into content(title, content, filePath, userid,time) VALUES (:title,:content,:filePath,:userid, datetime())";
        $stmt = $db->prepare($sql);
        $stmt -> bindValue(':title',$title,SQLITE3_TEXT);
        $stmt -> bindValue(':content',$content,SQLITE3_TEXT);
        $stmt -> bindValue(':filePath',$filePath,SQLITE3_TEXT);
        $stmt -> bindValue(':userid',$_SESSION['usrid'],SQLITE3_TEXT);
        return $stmt->execute();
    }

    /**
     * delete post by given id
     *
     * @param SQLite3 $db
     * @param $postID
     * @return SQLite3Result
     */
    static function deletePost(SQLite3 $db, $postID){
        $sql = "delete from content where _id = :id";
        $stmt = $db->prepare($sql);
        $stmt -> bindValue(':id',$postID,SQLITE3_INTEGER);
        return $stmt->execute();
    }

    /**
     * check whether a user name exist
     *
     * @param SQLite3 $db
     * @param $name
     * @return SQLite3Result
     */
    static function checkName(SQLite3 $db, $name){
        $sql = "select * from user where name = :name";
        $stmt = $db->prepare($sql);
        $stmt->bindValue(':name',$name,SQLITE3_TEXT);
        return $stmt->execute();
    }

    /**
     * insert a new register user into table
     * the password is encoded by md5
     *
     * @param SQLite3 $db
     * @param $name
     * @param $password
     * @return SQLite3Result
     */
    static function insertUser(SQLite3 $db, $name,$password){
        $password = md5($password);
        $sql = "insert into user(name,password) VALUES (:name, :password)";
        $stmt = $db->prepare($sql);
        $stmt->bindValue(':name',$name,SQLITE3_TEXT);
        $stmt->bindValue(':password',$password,SQLITE3_TEXT);
        return $stmt->execute();
    }

    /**
     * update password
     *
     * update user password
     * @param SQLite3 $db
     * @param $id
     * @param $new
     */
    static function updatePassword(SQLite3 $db, $id, $new){
        $new = md5($new);
        $sql = "update user set password = :password where _id = :id";
        $stmt = $db->prepare($sql);
        $stmt->bindValue(':password',$new,SQLITE3_TEXT);
        $stmt->bindValue(':id',$id,SQLITE3_TEXT);
        return $stmt->execute();
    }
}

?>