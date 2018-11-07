package com.example.yangliang.zxingdemo.utils;

import android.hardware.Camera;

/**
 * 创建日期：2018/11/7 on 上午9:54
 * 描述:判断相机是否能使用工具类
 * 作者:yangliang
 */
public class CameraUtils {
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            if (mCamera != null)
                mCamera.release();
            mCamera = null;
        }
        return canUse;
    }
}
