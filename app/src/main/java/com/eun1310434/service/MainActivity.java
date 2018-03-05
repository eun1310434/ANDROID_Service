/**
 * 05.03.2018
 * eun1310434@naver.com
 * https://blog.naver.com/eun1310434
 * 참고) Do it android programming
 */

package com.eun1310434.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        Intent passedIntent = getIntent();
        processIntent(passedIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // onNewIntent가 만들어진 상태에서는 onCreate 호출 안됨
        processIntent(intent);
        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            String type = intent.getStringExtra("type");
            String data = intent.getStringExtra("data");

            Toast.makeText(this, "type : " + type + ", data : " + data, Toast.LENGTH_SHORT).show();
            editText.setText(data);
        }
    }

    public void onButton1Clicked(View v) {
        String data = editText.getText().toString();
        Intent intent = new Intent(this, SendToService.class);
        intent.putExtra("type", "SendToService");
        intent.putExtra("data", data);
        startService(intent);
    }

}
