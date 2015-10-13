package com.dji.getstart;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dji.log.DJILogHelper;
import dji.sdk.api.Camera.DJICameraSettingsTypeDef;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.api.media.DJIMedia;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGerneralListener;
import dji.sdk.interfaces.DJIMainControllerExternalDeviceRecvDataCallBack;
import dji.sdk.interfaces.DJIMediaFetchCallBack;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ViewHolder") public class SelectDroneTypeActivity extends DemoBaseActivity implements View.OnClickListener {
    private static final String TAG = "SelectDroneTypeActivity";

    private final int SHOWDIALOG = 2;
    private DjiGLSurfaceView mDjiGLSurfaceView;
    private DJIReceivedVideoDataCallBack mReceivedVideoDataCallBack = null;
    private final int SHOWTOAST = 1;
    private TextView mConnectStateTextView;
    private Timer mTimer;
    private Context m_context;
    private ScrollView mMCOnBoardRecvScrollView;
    private TextView mRecvTextView;
    private Button mSendOnBoardBtn;
    private EditText mSendOnBoardEdit;
    private DJIMainControllerExternalDeviceRecvDataCallBack mExtDevReceiveDataCallBack = null;
    private String McRecvOnBoard = "";

    class Task extends TimerTask {
        //int times = 1;

        @Override
        public void run()
        {
            //Log.d(TAG ,"==========>Task Run In!");
            checkConnectState();
        }

    };

    private void checkConnectState(){

        SelectDroneTypeActivity.this.runOnUiThread(new Runnable(){

            @Override
            public void run()
            {
                boolean bConnectState = DJIDrone.getDjiCamera().getCameraConnectIsOk();
                if(bConnectState){
                    mConnectStateTextView.setText(R.string.camera_connection_ok);
                }
                else{
                    mConnectStateTextView.setText(R.string.camera_connection_break);
                }
            }
        });

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOWDIALOG:
                    showMessage(getString(R.string.demo_activation_message_title),(String)msg.obj);
                    break;

                default:
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_drone_type);
        DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_M100);
        DJIDrone.connectToDrone();

        new Thread(){
            public void run() {
                try {
                    DJIDrone.checkPermission(getApplicationContext(), new DJIGerneralListener() {

                        @Override
                        public void onGetPermissionResult(int result) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "onGetPermissionResult = "+result);
                            Log.e(TAG, "onGetPermissionResultDescription = "+DJIError.getCheckPermissionErrorDescription(result));
                            if (result == 0) {
                                handler.sendMessage(handler.obtainMessage(SHOWDIALOG, DJIError.getCheckPermissionErrorDescription(result)));
                            } else {
                                handler.sendMessage(handler.obtainMessage(SHOWDIALOG, getString(R.string.demo_activation_error)+DJIError.getCheckPermissionErrorDescription(result)+"\n"+getString(R.string.demo_activation_error_code)+result));

                            }
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();

        mDjiGLSurfaceView = (DjiGLSurfaceView)findViewById(R.id.DjiSurfaceView_02);

        mDjiGLSurfaceView.start();

        mReceivedVideoDataCallBack = new DJIReceivedVideoDataCallBack(){

            @Override
            public void onResult(byte[] videoBuffer, int size)
            {
                // TODO Auto-generated method stub
                mDjiGLSurfaceView.setDataToDecoder(videoBuffer, size);
            }


        };

        DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(mReceivedVideoDataCallBack);


        m_context = this.getApplicationContext();
        mConnectStateTextView = (TextView)findViewById(R.id.ConnectStateMCTextView);
        mMCOnBoardRecvScrollView = (ScrollView)findViewById(R.id.MCOnBoardRecvScrollView);
        mRecvTextView = (TextView)findViewById(R.id.MCOnBoardRecv);
        mSendOnBoardBtn = (Button)findViewById(R.id.MCOnBoardSendBtn);
        mSendOnBoardEdit = (EditText)findViewById(R.id.MCOnBoardSendEdit);
        mSendOnBoardBtn.setOnClickListener(this);

        if(DJIDrone.getDroneType() == DJIDroneType.DJIDrone_Inspire1){
            DJIDrone.getDjiCamera().setCameraMode(DJICameraSettingsTypeDef.CameraMode.Camera_Capture_Mode, new DJIExecuteResultCallback(){

                @Override
                public void onResult(DJIError mErr)
                {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "Set Camera Mode errorCode = "+ mErr.errorCode);
                    Log.d(TAG, "Set Camera Mode errorDescription = "+ mErr.errorDescription);
                    String result = "errorCode =" + mErr.errorCode + "\n"+"errorDescription =" + DJIError.getErrorDescriptionByErrcode(mErr.errorCode);
                    handler.sendMessage(handler.obtainMessage(SHOWTOAST, result));
                }

            });
        }

        mExtDevReceiveDataCallBack = new DJIMainControllerExternalDeviceRecvDataCallBack() {

            @Override
            public void onResult(final byte[] data)
            {
                // TODO Auto-generated method stub
                StringBuffer sb = new StringBuffer();
                sb.append(getString(R.string.external_device_recv_data)).append("\n");
                sb.append(new String(data)).append("\n");

                McRecvOnBoard = sb.toString();

                SelectDroneTypeActivity.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run()
                    {
                        mRecvTextView.setText(McRecvOnBoard);
                    }
                });
            }

        };
        //设置回调接口
        DJIDrone.getDjiMC().setExternalDeviceRecvDataCallBack(mExtDevReceiveDataCallBack);

    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        mTimer = new Timer();
        Task task = new Task();
        mTimer.schedule(task, 0, 500);
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if(mTimer!=null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        super.onPause();
    }
       
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        mDjiGLSurfaceView.destroy();
        DJIDrone.getDjiCamera().setReceivedVideoDataCallBack(null);
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }


    @Override
    public void onClick(View v)
    {
        List<String> strlist = null;
        List<String> strlist2 = null;
        int TotalStringCnt = 0;
        String[] mSettingStrs = null;

        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case  R.id.MCOnBoardSendBtn:
                // TODO Auto-generated method stub
                if ("" != mSendOnBoardEdit.getText().toString()){
                    DJIDrone.getDjiMC().sendDataToExternalDevice(mSendOnBoardEdit.getText().toString().getBytes(),new DJIExecuteResultCallback(){
                        @Override
                        public void onResult(DJIError result)
                        {
                            // TODO Auto-generated method stub
                        }
                    });
                    Log.d(TAG, "Submit1");
                }
                break;
            default:
                break;
        }
    }

    public void showMessage(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
   
}
