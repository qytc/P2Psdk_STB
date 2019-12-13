package io.qytc.vc;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;

import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;

import cn.jiguang.api.JCoreManager;
import cn.jpush.android.api.JPushInterface;
import io.qytc.vc.constant.SpConstant;
import io.qytc.vc.utils.SpUtil;

public class TRTCApplication extends MultiDexApplication {

    public static TRTCApplication mInstance;
    private static TRTCCloud mTrtcCloud;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //重置部分数据
        resetData();

        //极光初始化
        initJpush();
    }

    public static TRTCCloud getTrtcCloud() {
        if (mTrtcCloud == null) {
            mTrtcCloud = TRTCCloud.sharedInstance(mInstance);

            // 预览前配置默认参数
            TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();     // 大画面的编码器参数设置
            encParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_960_540;//分辨率
            encParam.videoFps = 15;//帧率
            encParam.videoBitrate = 1500;//码率
            encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE;
            mTrtcCloud.setVideoEncoderParam(encParam);

            TRTCCloudDef.TRTCNetworkQosParam qosParam = new TRTCCloudDef.TRTCNetworkQosParam();
            qosParam.controlMode = TRTCCloudDef.VIDEO_QOS_CONTROL_SERVER;//服务端控制
            qosParam.preference = TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_SMOOTH;//流畅 清晰
            mTrtcCloud.setNetworkQosParam(qosParam);

            mTrtcCloud.setPriorRemoteVideoStreamType(TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);//设置大画面
            mTrtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT);//适应模式
            mTrtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);//音频线路为：扬声器
            mTrtcCloud.setGSensorMode(TRTCCloudDef.TRTC_GSENSOR_MODE_DISABLE);//UI 不适应布局 （无重力感应）

        }
        return mTrtcCloud;
    }

    private void initJpush() {
        Bundle bundle = new Bundle();
        // 设置心跳3s
        bundle.putInt("heartbeat_interval", 3);
        JCoreManager.setSDKConfigs(this, bundle);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
    }

    private void resetData() {

        SpUtil.saveString(mInstance, SpConstant.INROOM, "0");
        SpUtil.saveString(mInstance, SpConstant.BROADCAST, "0");
        SpUtil.saveString(mInstance, SpConstant.CAMERA1, "0");
        SpUtil.saveString(mInstance, SpConstant.CAMERA2, "-1");
        SpUtil.saveString(mInstance, SpConstant.CAMERA3, "-1");
        SpUtil.saveString(mInstance, SpConstant.MIC, "0");
        SpUtil.saveString(mInstance, SpConstant.SPEAK, "0");

        SpUtil.saveString(mInstance, SpConstant.SENDBYTES, "0");
        SpUtil.saveString(mInstance, SpConstant.RECEIVEBYTES, "0");

        SpUtil.saveString(mInstance, SpConstant.JOIN_PMI, "0");
        SpUtil.saveString(mInstance, SpConstant.INROOM, "0");

    }
}
