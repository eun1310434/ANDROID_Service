/*=====================================================================
□ Infomation
  ○ Data : 21.03.2018
  ○ Mail : eun1310434@naver.com
  ○ Blog : https://blog.naver.com/eun1310434
  ○ Reference
     - Do it android app Programming

□ Function
  ○ Activity에서 Service로 데이터 보내기

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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Intent intent;

    TextView tv_a_log;
    TextView tv_b_log;
    TextView tv_c_log;

    Button button_a;
    Button button_b;
    Button button_c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("onCreate", "onCreate");

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
        Log.d("onNewIntent", "onNewIntent");
        // Intent를 통해 오는 모든 것을 처리
        // onNewIntent가 만들어진 상태에서는 onCreate 호출 안됨
        // 어떤 액티비티에서 자기 자신을 호출때 중복되어 호출하는 경우를 방지하기 위해 사용되기도 함

        if (intent != null) {
            String tag = intent.getStringExtra("tag");
            String data = intent.getStringExtra("data");

            if(tag.equals("A")){
                if(data.equals("Quit")){
                    button_a.setClickable(true);
                }else{
                    button_a.setText("A-Quit");
                }
                tv_a_log.append(data +"\n");
            }
            if(tag.equals("B")){
                if(data.equals("Quit")){
                    button_b.setClickable(true);
                }else{
                    button_b.setText("B-Quit");
                }
                tv_b_log.append(data +"\n");
            }
            if(tag.equals("C")){
                if(data.equals("Quit")){
                    button_c.setClickable(true);
                }else{
                    button_c.setText("C-Quit");
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
        //SenToService(intent, "ALL", "quit");
        super.onDestroy();
    }
}
