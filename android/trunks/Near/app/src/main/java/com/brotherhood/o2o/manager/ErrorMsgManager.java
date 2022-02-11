package com.brotherhood.o2o.manager;

/**
 * Created by billy.shi on 2016/1/22.
 */
public class ErrorMsgManager {
    /*


     */

    /*------email注册接口返回错误码------ */
    private static final int ERROR_NETWORK_TIME_OUT = -1; //连网超时 请检查网络 Please check your network

    private static final int ERROR_EMAIL_PARAMETER_MISS = 3227; // 'msg' => 'Parameter email is missed'
    private static final int ERROR_PASSWORD_PARAMETER_MISS = 3228; // ['c' => ERROR_PASSWORD_PARAMETER_MISS, 'msg' => 'Parameter password is missed']		3228
    private static final int ERROR_PSAAWORD_INVALID = 3117; // ['c' => ERROR_PSAAWORD_INVALID, 'msg' => 'Parameter password is invalid']		3117
    private static final int ERROR_NICKNAME_PARAMETER_MISS = 3213; // ['c' => ERROR_NICKNAME_PARAMETER_MISS, 'msg' => 'Parameter nickname is missed']		3213


    private static final int ERROR_NICKNAME_PARAMETER_INVALID = 3110; //['c' => ERROR_NICKNAME_PARAMETER_INVALID, 'msg' => 'Parameter nickname is invalid    3110

    private static final int ERROR_COMMON_THIRD_PARTY_SERVICE = 3002;  //['c' => ERROR_COMMON_THIRD_PARTY_SERVICE, 'msg' => 'DFS is out of server']		3002

    private static final int ERROR_AVATAR_PARAMETER_INVALID = 3112; //['c' => ERROR_AVATAR_PARAMETER_INVALID, 'msg' => 'Parameter avatar is not available']		3112

    private static final int ERROR_AVATAR_PARAMETER_MISS = 3214;   //['c' => ERROR_AVATAR_PARAMETER_MISS, 'msg' => 'Parameter avatar is missed']		3214

    private static final int ERROR_GENDER_PARAMETER_MISS = 3215; //['c' => ERROR_GENDER_PARAMETER_MISS, 'msg' => 'Miss parameter gender']		3215

    private static final int ERROR_GENDER_PARAMETER_INVALID = 3109; //['c' => ERROR_GENDER_PARAMETER_INVALID, 'msg' => 'Parameter gender  3109

    private static final int ERROR_EMAIL_EXIST_ALREADY = 3116; //['c' => ERROR_EMAIL_EXIST_ALREADY, 'msg' => 'Email has existed already']		3116

    private static final int ERROR_COMMON_PARAMETER_INVALID = 3001; //['c' => ERROR_COMMON_PARAMETER_INVALID, 'msg' => 'Email has wrong format']		3001

    //两个 3002验证码
    //private static final int ERROR_COMMON_THIRD_PARTY_SERVICE = 3002; //['c' => ERROR_COMMON_THIRD_PARTY_SERVICE, 'msg' => 'Register from usercenter failed']		3002


}
