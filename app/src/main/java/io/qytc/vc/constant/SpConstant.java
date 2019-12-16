package io.qytc.vc.constant;


/**
 * SharedPreference存储的常量
 */
public interface SpConstant {

    /**
     * 快速缓存文件名
     */
    String SP_FILENAME = "sp_filename";

    /**
     * 极光推送ID
     */
    String JPUSH_DEVICE_ID = "jpush_device_id";

    /**
     * APP的ID
     */
    String APP_ID = "1400210901";

    /**
     * 1-机顶盒
     */
    int type = 1;

    /**
     * accessToken
     */
    String ACCESS_TOKEN = "accessToken";

    /**
     * 终端序列号
     */
    String SERIAL = "serial";

    /**
     * 硬件版本号
     */
    String HARDWARE = "hardware";

    /**
     * sig
     */
    String USERSIG = "userSig";

    /**
     * 终端用户的租户房间号
     */
    String PMI = "pmi";

    /**
     * 进入房间后的房间号
     */
    String JOIN_PMI = "join_pmi";

    /**
     * 终端名称
     */
    String NAME = "name";

    /**
     * 部门ID
     */
    String DEPTID = "deptId";

    /**
     * 租户ID
     */
    String TENANTID = "tenantId";

    /**
     * 终端ID
     */
    String ID = "id";

    /**
     * 广播画面
     */
    String BROADCAST = "Broadcast";

    /**
     * 摄像头1.2.3
     */
    String CAMERA1 = "Camera1";
    String CAMERA2 = "Camera2";
    String CAMERA3 = "Camera3";

    /**
     * cpu状态
     */
    String CPUSTATS = "CpuStats";

    /**
     * 是否在房间
     */
    String INROOM = "InRoom";

    /**
     * 制造商
     */
    String MAKER = "Maker";

    /**
     * 内存状态
     */
    String MEMSTATS = "MemStats";

    /**
     * 麦克风开关
     */
    String MIC = "Mic";

    /**
     * socketID
     */
    String SOCKETID = "SocketId";

    /**
     * 发言状态
     */
    String SPEAK = "Speak";

    /**
     * 发送数据
     */
    String SENDBYTES = "SendBytes";

    /**
     * 接收数据
     */
    String RECEIVEBYTES = "ReceiveBytes";

    /**
     * 用户头像
     */
    String IMAGE = "image";

    /**
     * 绑定的终端ID
     */
    String BINDTERM = "bindTerm";
}
