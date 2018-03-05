/**
 * 05.03.2018
 * eun1310434@naver.com
 * https://blog.naver.com/eun1310434
 * 참고) Do it android programming
 */

package com.eun1310434.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SendToService extends Service {
    private static final String TAG = "SendToService";

    public SendToService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        //서비스는 한번만 생성되기에 onCreate는 한번 호출
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        //이후 호출되는 서비스는 onStartCommand에서 처리

        if (intent == null) {
            return Service.START_STICKY;//서비스가 종료되어도 다시 자동으로 실행 됨.
        } else {
            processCommand(intent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent) {
        String type = intent.getStringExtra("type");
        String data = intent.getStringExtra("data");
        Log.d(TAG, "type : " + type + ", data : " + data);

        /*
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
            } catch(Exception e) {};

            Log.d(TAG, "Waiting " + i + " seconds.");
        }
        */

        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);

        showIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | //
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        showIntent.putExtra("type", "SendToActivity");
        showIntent.putExtra("data", data + " from service.");
        startActivity(showIntent);

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
