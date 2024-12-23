package com.example.myapplication;

public class Db_Contract {

        public static String ip = "192.168.56.1";

    public static final String UrlRegister = "http://" + ip + "/my_api_android/register.php";
    public static final String UrlLogin = "http://" + ip + "/my_api_android/login.php";
    public static final String UrlGetCourses = "http://" + ip + "/my_api_android/get_courses.php";
    public static final String UrlUpdateAccount = "http://" + ip + "/my_api_android/update_account.php";
    public static final String UrlDeleteAccount = "http://" + ip + "/my_api_android/delete_account.php";
    public static final String UrlGetLikedCourses = "http://" + ip + "/my_api_android/get_liked_courses.php";
    public static final String UrlLikeUnlike = "http://" + ip + "/my_api_android/like_unlike.php";
    public static final String UrlGetCourseDetails = "http://" + ip + "/my_api_android/get_course_details.php";
    public static final String UrlGetUserId = "http://" + ip + "/my_api_android/get_user_id.php";
    public static final String UrlAddLike = "http://" + ip + "/my_api_android/add_like.php";

}