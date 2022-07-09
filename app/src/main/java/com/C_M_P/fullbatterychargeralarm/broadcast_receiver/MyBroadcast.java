package com.C_M_P.fullbatterychargeralarm.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import com.C_M_P.fullbatterychargeralarm.MainActivity;
import com.C_M_P.fullbatterychargeralarm.service.MyService;

// Training:
// https://developer.android.com/training/monitoring-device-state/battery-monitoring#java
public class MyBroadcast extends BroadcastReceiver {

    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Guides from developer.android.com
        // How are we charging?
        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        // Power source is an USB Port
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        // Power source is an AC charger
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;


        // ========================================
        // Xác định phần trăm pin
        // Guides from developer.android.com
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPercent = level * 100 / (float)scale;

        sharedPreferences = context.getSharedPreferences(MainActivity.CHARGER_ALERT, Context.MODE_PRIVATE);
        boolean isStartAlarm = sharedPreferences.getBoolean(MainActivity.KEY_isServiceRunning, false);

        if(isStartAlarm && MyService.isServiceRunning) {
            if (acCharge || usbCharge) {
                if (batteryPercent == 100) {
                    MyService.startAlarm(context);
                }
            }else{
                MyService.stopAlarm(context);
            }
        }

        if(acCharge || usbCharge) {
            MainActivity.showChargingAnimation(true);
        }else{
            MainActivity.showChargingAnimation(false);
        }
        MainActivity.setBatteryLevel(batteryPercent);

        // ==================================================
        float voltage = 0;
        int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        long capacity = getBatteryCapacity(context);
        if(acCharge || usbCharge){
            voltage = (float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        }
        MainActivity.showBatteryInfo(voltage, capacity, temperature);
    }

    public long getBatteryCapacity(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);
            Long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter != null && capacity != null) {
                long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
                return value;
            }
        }

        return 0;
    }

}
