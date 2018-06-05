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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SendToService extends Service {

    private static final String TAG = "SendToService";

    private ServiceThread serviceThread[] = new ServiceThread[100];

    private int serviceThreadCount = 0;

    private ServiceThread.OnThreadListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        //서비스는 한번 만 생성되기에 onCreate()는 한번 호출
        Log.d(TAG, "onCreate()");

        listener = new ServiceThread.OnThreadListener(){
            @Override
            public void SetContent(String tag, String data) {
                SendToActivity(new Intent(getApplicationContext(), MainActivity.class), tag, data );
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        //이후 호출되는 서비스는 onStartCommand에서 처리

        if (intent == null) {
            //서비스가 종료되어도 다시 자동으로 실행 됨.
            return Service.START_STICKY;
        } else {
            String tag = intent.getStringExtra("tag");
            String request = intent.getStringExtra("request");

            if(100 < serviceThreadCount || (tag.equals("ALL") && request.equals("quit"))){
                for(int i = 0 ; i < serviceThread.length; i++){
                    if (serviceThread[i] != null) {
                        serviceThread[i].quit();
                        serviceThread[i] = null;
                    }
                }
            }else if(request.equals("start")){
                serviceThread[serviceThreadCount] = new ServiceThread(tag, listener);
                serviceThread[serviceThreadCount].start();
                serviceThreadCount++;

            }else if(request.equals("quit")){
                for(int i = 0 ; i < serviceThread.length; i++){
                    if (serviceThread[i] != null && serviceThread[i].IsServiceThread(tag)) {
                        serviceThread[i].quit();
                        serviceThread[i] = null;
                        break;
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }



    private void SendToActivity(Intent intent, String tag, String data) {
        // SendToService : SendToActivity -> MaincActivity : onNewIntent(Intent intent)
        Log.d(TAG, "SendToActivity()");
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | // 화면이 없는 Service에서 화면이 있는 Activity로 데이터 전송. 매우 중요
                        Intent.FLAG_ACTIVITY_SINGLE_TOP | // 화면이 현재 운영중이면 재사용 하라는 뜻
                        Intent.FLAG_ACTIVITY_CLEAR_TOP); // 현재 다른 화면이 있으면 재거하라는 뜻
        intent.putExtra("tag", tag);
        intent.putExtra("data", data);
        startActivity(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        // TODO: Return the communication channel to the service.
        // A client is binding to the service with bindService()
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");
        // All clients have unbound with unbindService()
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind()");
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
