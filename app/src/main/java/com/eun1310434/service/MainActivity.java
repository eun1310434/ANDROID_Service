/*==================================================================================================
□ INFORMATION
  ○ Data : Dienstag - 05/06/18
  ○ Mail : eun1310434@naver.com
  ○ WebPage : https://eun1310434.github.io/
  ○ Reference
     - Do it android app Programming

□ FUNCTION
   ○ Process
      01) MainActivity : startService() -> SendToActivity : onStartCommand()
          - Message to Service
      02) SendToActivity : onStartCommand() -> ServiceThread : run()
          - Message to Thread
      03) ServiceThread : listener.SetContent() -> SendToActivity : listener.SetContent()
          - Message to Thread
      04) SendToActivity : startActivity() -> MainActivity : onNewIntent()
          - Message to UIActivity
   ○ Unit
      - public class MainActivity extends AppCompatActivity {
        01) protected void onCreate(Bundle savedInstanceState)
        02) protected void onNewIntent(Intent intent)
        03) private void SenToService(Intent intent, String tag, String request)
        04) protected void onDestroy()

      - public class SendToService extends Service {
        01) public void onCreate()
        02) public int onStartCommand(Intent intent, int flags, int startId)
        03) private void SendToActivity(Intent intent, String tag, String data)
        04) public IBinder onBind(Intent intent)
        05) public boolean onUnbind(Intent intent)
        06) public void onRebind(Intent intent)
        07) public void onDestroy()

      - public class ServiceThread extends Thread {
        01) public interface OnThreadListener
        02) public ServiceThread(String _tag, OnThreadListener _listener)
        03) public void run()
        04) public void quit()
        05) public Boolean IsServiceThread(String _tag)

      - AndroidMainfest.xml
        <service
            android:name=".SendToService"
            android:enabled="true"
            android:exported="true">
        </service>

   ○ onNewIntent
      - Intent를 통해 오는 모든 것을 처리
      - 어떤 액티비티에서 자기 자신을 호출때 중복되어 호출하는 경우를 방지
      - onNewIntent가 만들어진 상태에서는 onCreate 호출 안됨
      - 일정 조건을 갖추었을 때, 자동 호출
         01) 인텐트에 Activity Flag값 설정 : intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP)
         02) Androidmanifest 에서 속성을 설정

  ○ ServiceThread.OnThreadListener()
      - Service 에서 UI에 직접 접근이 어려움
      - 이를 위하여 UI에 데이터를 전송할 수 있는 Listener를 선언
      - Service에 데이터가 변경이 발생시  Listener를 활용하여 UI에 메시지
      - Inner Class로 선언하여 언제서든 유용하게 해당 클래스를 활용하게 만든다.
      - 생성자 생성 시 Listener의 객체를 받아 올 수 있도록 인자를 대입한다.


□ Study
  ○ Service
     - Some processing could take a while.
       Also, by default, all processing occurs in the UI thread.
       Executing lengthy operations freezes the display and can result in an ANR
       (Application Not Responding) error, where the user forces the app to quit.

     - Because of these two reasons, an app might want to run the processing as
       a Service so that it executes persistently on its own thread.

     - For example, a user might want to trigger a backup manually.
       If the user has a lot of data, he should not have to wait several minutes for the
       backup to finish.
       Using a Service enables the user to start the backup, and then do other things
       with the device while the backup is occurring.
==================================================================================================*/

package com.eun1310434.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    private TextView tv_a_log;
    private TextView tv_b_log;
    private TextView tv_c_log;

    private Button button_a;
    private Button button_b;
    private Button button_c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("onCreate", "onCreate");

        // 서비스에 전송 할 데이터를 위해 intent 선언
        intent = new Intent(this, SendToService.class);

        tv_a_log = (TextView) findViewById(R.id.tv_a_log);
        tv_b_log = (TextView) findViewById(R.id.tv_b_log);
        tv_c_log = (TextView) findViewById(R.id.tv_c_log);

        button_a = (Button) findViewById(R.id.button_a);
        button_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_a.getText().equals("A-Start")) {
                    SenToService(intent, "A", "start");
                    button_a.setText("A-Quit");
                }else{
                    SenToService(intent, "A", "quit");
                    button_a.setText("A-Start");
                    button_a.setClickable(false);
                }
            }
        });

        button_b = (Button) findViewById(R.id.button_b);
        button_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_b.getText().equals("B-Start")) {
                    SenToService(intent, "B", "start");
                    button_b.setText("B-Quit");
                }else{
                    SenToService(intent, "B", "quit");
                    button_b.setText("B-Start");
                    button_b.setClickable(false);
                }
            }
        });

        button_c = (Button) findViewById(R.id.button_c);
        button_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_c.getText().equals("C-Start")) {
                    SenToService(intent, "C", "start");
                    button_c.setText("C-Quit");
                }else{
                    SenToService(intent, "C", "quit");
                    button_c.setText("C-Start");
                    button_c.setClickable(false);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Intent를 통해 오는 모든 것을 처리
        // onNewIntent가 만들어진 상태에서는 onCreate 호출 안됨
        // 어떤 액티비티에서 자기 자신을 호출때 중복되어 호출하는 경우를 방지하기 위해 사용되기도 함

        if (intent != null) {
            String tag = intent.getStringExtra("tag");
            String data = intent.getStringExtra("data");
            Log.d("onNewIntent", "data : " + data);

            if(tag.equals("A")){
                if(data.equals("Quit")){
                    button_a.setClickable(true);
                }
                tv_a_log.append(data +"\n");
            }

            if(tag.equals("B")){
                if(data.equals("Quit")){
                    button_b.setClickable(true);
                }
                tv_b_log.append(data +"\n");
            }

            if(tag.equals("C")){
                if(data.equals("Quit")){
                    button_c.setClickable(true);
                }
                tv_c_log.append(data +"\n");
            }
        }
        super.onNewIntent(intent);
    }

    private void SenToService(Intent intent, String tag, String request) {
        intent.putExtra("tag", tag);
        intent.putExtra("request", request);
        startService(intent);
    }


    @Override
    protected void onDestroy() {
        //어플을 종료시 모든 Thread를 종료
        SenToService(intent, "ALL", "quit");
        super.onDestroy();
    }
}
