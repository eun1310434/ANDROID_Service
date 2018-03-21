/*=====================================================================
□ Infomation
  ○ Data : 21.03.2018
  ○ Mail : eun1310434@naver.com
  ○ Blog : https://blog.naver.com/eun1310434
  ○ Reference
     - Do it android app Programming

□ Function
  ○ Service에서 Activity 데이터 받기
  ○ 받은데이터 Thread로 처리
  ○ 처리된 결과 Activity로 재 전송


□ Study
  ○ Service
    - 화면이 없는 상태에서 백그라운드로 실행됨
    - 서비스느 프로세스스가 종료되어도 시스템에서 자동으로 재시작함
    - 순서
       01) Activity - startService() : 시작시키기
       02) Service - onCreate() : 서비스시작
       03) Service - onDestroy() : 서비스종료

  ○ onNewIntent
    - Intent를 통해 오는 모든 것을 처리
    - 어떤 액티비티에서 자기 자신을 호출때 중복되어 호출하는 경우를 방지
    - onNewIntent가 만들어진 상태에서는 onCreate 호출 안됨
    - 일정 조건을 갖추었을 때, 자동 호출
      01) 인텐트에 Activity Flag값 설정 : intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP)
      02) Androidmanifest 에서 속성을 설정
=====================================================================*/

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



    public SendToService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        //서비스는 한번만 생성되기에 onCreate는 한번 호출

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
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | // 화면이 없는 Service에서 화면이 있는 Activity로 데이터 전송. 매우 중요
                        Intent.FLAG_ACTIVITY_SINGLE_TOP | // 화면이 현재 운영중이면 재사용 하라는 뜻
                        Intent.FLAG_ACTIVITY_CLEAR_TOP); // 현재 다른 화면이 있으면 재거하라는 뜻
        intent.putExtra("tag", tag);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SendToService", "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
