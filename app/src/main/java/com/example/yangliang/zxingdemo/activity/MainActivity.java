package com.example.yangliang.zxingdemo.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangliang.zxingdemo.R;
import com.example.yangliang.zxingdemo.utils.CameraUtils;
import com.example.yangliang.zxingdemo.utils.CheckPermission;
import com.example.yangliang.zxingdemo.zxing.activity.CaptureActivity;
import com.example.yangliang.zxingdemo.zxing.encoding.EncodingHandler;
import com.google.zxing.WriterException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.openQrCodeScan)
    Button openQrCodeScan;
    @BindView(R.id.qrCodeText)
    TextView qrCodeText;
    @BindView(R.id.text)
    EditText text;
    @BindView(R.id.CreateQrCode)
    Button CreateQrCode;
    @BindView(R.id.QrCode)
    ImageView QrCode;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;

    /**
     * android 6.0 或以上权限申请
     */
    private static final int PERMISSION_REQUEST_CODE = 0;                        //请求码
    private CheckPermission mCheckPermission;                        //检测权限工具

    //配置需要取的权限
    static final String[] PERMISSION = new String[]{
            Manifest.permission.CAMERA,                                // 摄像头权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,                // SD卡写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,                // SD卡读取权限
    };

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //程序开启时就判断手机应用权限
        initPermission();
    }

    /**
     * 判断手机应用权限
     */
    private void initPermission() {
        //SDK版本小于23时候不做检测
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        mCheckPermission = new CheckPermission(this);
        //缺少权限时，进入权限设置页面
        if (mCheckPermission.permissionSet(PERMISSION)) {
            startPermissionActivity();
        }
    }

    /**
     * 进入权限设置页面
     */
    private void startPermissionActivity() {
        PermissionActivity.startActivityForResult(this, PERMISSION_REQUEST_CODE, PERMISSION);
    }

    @OnClick({R.id.openQrCodeScan, R.id.CreateQrCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.openQrCodeScan:
                //打开二维码扫描界面
                if (CameraUtils.isCameraCanUse()) {
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(this, "请打开此应用的摄像头权限！", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.CreateQrCode:
                //生成二维码图片
                //获取输入的文本信息
                String str = text.getText().toString().trim();
                if (str != null && !"".equals(str.trim())) {
                    //根据输入的文本生成对应的二维码并显示出来
                    try {
                        Bitmap bitmap = EncodingHandler.createQRCode(text.getText().toString(), 500);
                        if (bitmap!=null){
                            Toast.makeText(this, "二维码生成成功！", Toast.LENGTH_SHORT).show();
                            QrCode.setImageBitmap(bitmap);
                        }
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(this, "文本信息不能为空！", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE && resultCode == PermissionActivity.PERMISSION_DENIEG) {
            //拒绝时，没有获取到主要权限，无法运行，关闭页面
            Toast.makeText(this, "请打开此应用的摄像头权限！", Toast.LENGTH_SHORT).show();
        } else {
            //扫描结果回调
            if (resultCode == CaptureActivity.RESULT_CODE_QR_SCAN) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
                //将扫描出来的信息做相应的处理
                //第一次扫描按钮隐藏
                qrCodeText.setText(scanResult);
            }
        }
    }
}
