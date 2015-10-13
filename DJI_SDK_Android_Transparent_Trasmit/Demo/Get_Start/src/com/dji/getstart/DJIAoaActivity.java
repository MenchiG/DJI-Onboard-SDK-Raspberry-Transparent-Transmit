/**
 * @filename		: DJIAoaActivity.java
 * @package			: com.dji.getstart
 * @date			: 2015年4月21日 下午3:46:43
 * 
 * Copyright (c) 2015, DJI All Rights Reserved.
 * This activity is used to support the aoa connection with the remote controller. Developers can directly use this activity
 * as the main activity.
 */

package com.dji.getstart;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import dji.log.DJILogHelper;
import dji.midware.data.manager.P3.ServiceManager;
import dji.midware.usb.P3.DJIUsbAccessoryReceiver;
import dji.midware.usb.P3.UsbAccessoryService;

public class DJIAoaActivity extends Activity {
    private static boolean isStarted = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        
        if (isStarted) {
            //finish();
        }else {
            
            isStarted = true;
            ServiceManager.getInstance();
            UsbAccessoryService.registerAoaReceiver(this);
            Intent intent = new Intent(DJIAoaActivity.this, SelectDroneTypeActivity.class);
            startActivity(intent);
            
            //finish();
        }
        
        Intent aoaIntent = getIntent();
        if (aoaIntent!=null) {
            String action = aoaIntent.getAction();
            DJILogHelper.getInstance().LOGE("", "action="+action, false, true);
            if (action==UsbManager.ACTION_USB_ACCESSORY_ATTACHED ||
                    action==Intent.ACTION_MAIN) {
                Intent attachedIntent=new Intent();
                attachedIntent.setAction(DJIUsbAccessoryReceiver.ACTION_USB_ACCESSORY_ATTACHED);  
                sendBroadcast(attachedIntent);
//                DJILogHelper.getInstance().LOGE("", "action=send", false, true);
            }
        }
        
        finish();
    }
}
