package com.clj.blesample.operation;

import android.os.Environment;

/**
 * User: XIAOYong
 * Date: 2014-12-23
 * Time: 16:06
 * Description:常量
 */
public class XConstants {

    /**sharePreferences   key */
    public static String PRE_KEY_USER="USERINFO";
    public static String PRE_KEY_USER_TOKEN="muser_token";
    public static String PRE_KEY_USER_LOGIN="muser_Login";

//    public static String IMAGE_PATH=XEnvironment.getContext().getFilesDir().getPath()+"/";//--->>>>  /data/data/com.glufine.miogroup/files/






    public static  final String BROADCAST_LOGIN_ACTION="com.glufine.miogroup.broadcast.login";
    public static  final String BROADCAST_REGISTER_ACTION ="com.glufine.miogroup.broadcast.register";
    public static  final String BROADCAST_LOGINOUT_ACTION ="com.glufine.miogroup.broadcast.loginout";

    public static  final String BROADCAST_RECEIVE_DEVICEDATA_ACTION ="com.glufine.miogroup.broadcast.reveiceDeviceData";

    public static final String GET_BLOOD_SUGAR="/records/getblood";//获取血糖数据
    public static final String GET_BLOOD_PRESSURE="/records/getbp";//获取血压数据
    public static final String GET_MEDICINAL="/records/getdrup";//获取用药数据
    public static final String GET_FOOD="/records/getfood";//获取饮食数据
    /** specification */
    public final static float MAX_WEIGHT=200.0f;
    public final static float MIN_WEIGHT=0;

    public final static float MAX_AGE=100;
    public final static float MIN_AGE=0;

    public final static float MAX_ILLYEAR=100;
    public final static float MIN_ILLYEAR=0;

    public final static float MAX_HIGH=2.5F;
    public final static float MIN_HIGH=0.5F;

    public final static int MAX_PRESSURE=300;
    public final static int MIN_PRESSURE=30;

    public final static float MAX_BLOOD=33.3f;//33.3f
    public final static float MIN_BLOOD=0.5f;

    public final static int MAX_HEART_RATE=300;
    public final static int MIN_HEART_RATE=30;

    public final static float MAX_INSULIN=70.F;
    public final static float MIN_INSULIN=0.1F;



    //blood tag
//    0、空腹 1、早餐后 2、午餐前 3、午餐后 4、晚餐前 5、晚餐后 6、睡前 7、凌晨 8、血糖仪
    public static final int BLOOD_TAG_0 = 0;
    public static final  int BLOOD_TAG_1 = 1;
    public static final  int BLOOD_TAG_2 = 2;
    public static final  int BLOOD_TAG_3 = 3;
    public static final  int BLOOD_TAG_4 = 4;
    public static final  int BLOOD_TAG_5 = 5;
    public static final  int BLOOD_TAG_6 = 6;
    public static final  int BLOOD_TAG_7 = 7;
    public static final  int BLOOD_TAG_8 = 8;
    public static final  int BLOOD_TAG_9 = 9;

    //胰岛素类型
//    0早餐 1午餐 2晚餐 3睡前 4随机
    public static final String INSULIN_TAG_0 = "0";
    public static final String INSULIN_TAG_1 = "1";
    public static final String INSULIN_TAG_2 = "2";
    public static final String INSULIN_TAG_3 = "3";
    public static final String INSULIN_TAG_4 = "4";

    //status
    /**
     * 未同步删除
     */
    public static final int SYNC_STATUS_NO_DEL = 3;
    /**
     * 未同步添加
     */
    public static final int SYNC_STATUS_NO_ADD = 1;
    /**
     * 未同步修改
     */
    public static final int SYNC_STATUS_NO_EDIT = 2;
    /**
     * 已同步
     */
    public static final int SYNC_STATUS_SYNCED = 0;

    public static final int SOURCE_INPUT = 0;
    public static final int SOURCE_BOX_DEVICE= 1;


    public static final String UPlOAD_FILE_URL="上传文件地址";
//    public static String BASE_FILE_PATH=XEnvironment.getContext().getFilesDir().getPath();
    public static String BASE_FILE_PATH_PHOTO= Environment
            .getExternalStorageDirectory()+"/com.novonordisk/photo";
    public static String BASE_FILE_PATH_SAVE_IMAGE = Environment
            .getExternalStorageDirectory()+"/com.novonordisk/image";

}
