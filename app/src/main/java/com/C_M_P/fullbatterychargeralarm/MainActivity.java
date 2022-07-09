package com.C_M_P.fullbatterychargeralarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.C_M_P.fullbatterychargeralarm.broadcast_receiver.MyBroadcast;
import com.C_M_P.fullbatterychargeralarm.service.MyService;
import com.airbnb.lottie.LottieAnimationView;

import java.text.DecimalFormat;

/** TODO<br>
 * [x] Save status of Switch button<br>
 * [x] Touch Notification to open app<br>
 * [x] Xóa ActionBar<br>
 * [x] Thêm ngôn ngữ Tiếng Việt<br>
 * [x] Đổi icon
 */
public class MainActivity extends AppCompatActivity {
    public static final String KEY_1 = "KEY_1";
    public static final String CHARGER_ALERT = "CHARGER_ALERT";
    public static final String KEY_isServiceRunning = "KEY_isServiceRunning";

    private MyBroadcast myBroadcast;

    private static TextView tv_battery_level;
    private static TextView tv_voltage;
    private static TextView tv_capacity;
    private static TextView tv_temperature;
    private static ImageView iv_info_app;
    private static ImageView iv_battery_level;
    private static LottieAnimationView image_animation;

    private SwitchCompat sw_on_off;
    private boolean isServiceRunning = false;
    private SharedPreferences sharedPreferences;

//    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initial();

        sharedPreferences = getSharedPreferences(CHARGER_ALERT, MODE_PRIVATE);
        isServiceRunning = sharedPreferences.getBoolean(KEY_isServiceRunning, false);

        sw_on_off.setChecked(isServiceRunning && MyService.isServiceRunning);

        myBroadcast = new MyBroadcast();


        sw_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
//                intent.putExtra(KEY_1, "Charging...");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_isServiceRunning, isChecked);
                editor.apply();

                if(isChecked) { // ON
                    startService(intent);
                }else{ // OFF
                    stopService(intent);
                }
            }
        });


        iv_info_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog();
            }
        });
    }

    private void showMyDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(R.string.instruction);
        alert.setMessage(R.string.instruction_description);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.create();
        alert.show();

    }


    @Override
    protected void onStart() {
        super.onStart();
//        Logd("onStart()");
        registerReceiver(myBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Logd("onStop()");
        if(!sharedPreferences.getBoolean(KEY_isServiceRunning, false)) {
            unregisterReceiver(myBroadcast);
        }
    }

    public static void showBatteryInfo(float voltage, long capacity, int temperature){
        DecimalFormat dm = new DecimalFormat(voltage == 0 ? "0" : "0.0");
        tv_voltage.setText(dm.format(voltage/1000) + "V");
        tv_capacity.setText(capacity + " mAh");
        tv_temperature.setText((temperature/10) +"" +(char) 0x00B0 +"C");
    }

    public static void showChargingAnimation(boolean isShow){
        if(isShow){
            image_animation.setVisibility(View.VISIBLE);
            iv_battery_level.setVisibility(View.GONE);
        }else{
            image_animation.setVisibility(View.GONE);
            iv_battery_level.setVisibility(View.VISIBLE);
        }
    }

    public static void setBatteryLevel(float n){
        tv_battery_level.setText((int) n +"%");

        if(n <= 20){
            iv_battery_level.setImageResource(R.drawable.level_1);
            tv_battery_level.setTextColor(Color.parseColor("#de6409"));
        }
        else if(n > 20 && n <= 55){
            iv_battery_level.setImageResource(R.drawable.level_2);
            tv_battery_level.setTextColor(Color.parseColor("#deb908"));
        }
        if(n > 55 && n < 100){
            iv_battery_level.setImageResource(R.drawable.level_3);
            tv_battery_level.setTextColor(Color.parseColor("#35c500"));
        }
        if(n == 100){
            iv_battery_level.setImageResource(R.drawable.level_4);
            tv_battery_level.setTextColor(Color.parseColor("#35c500"));
        }
    }

    private void initial() {
        tv_battery_level = findViewById(R.id.tv_battery_level);
        tv_voltage = findViewById(R.id.tv_voltage);
        tv_capacity = findViewById(R.id.tv_capacity);
        tv_temperature = findViewById(R.id.tv_temperature);
        sw_on_off = findViewById(R.id.sw_on_off);
        image_animation = findViewById(R.id.lottie_animation);
        iv_battery_level = findViewById(R.id.iv_battery_level);
        iv_info_app = findViewById(R.id.iv_info_app);
    }





    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== MainActivity.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== MainActivity.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== MainActivity.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== MainActivity.java - line: " + n + " ==============================\n" + str);
    }

    public void showToast( String str ){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }



}