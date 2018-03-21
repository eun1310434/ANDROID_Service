package com.eun1310434.service;

public class ServiceThread extends Thread {
    private String tag;
    private boolean ing;


    //인터페이스를 활용하여 변경시 연결을 위한 리스너 새로 정의
    //innerClass
    public interface OnThreadListener {
        void SetContent(String tag, String data);
    }
    OnThreadListener listener;

    public ServiceThread(String _tag, OnThreadListener _listener){
        this.tag = _tag;
        this.listener = _listener;
        ing = true;
    }

    @Override
    public void run() {
        int count = 0;
        while(ing){
            //1초간격으로 데이터 전송
            try {
                Thread.sleep(1000);
            } catch(Exception e) {};
            listener.SetContent(tag,this.getName()+"-"+count++ );

        }
        listener.SetContent(tag,"Quit");
    }

    public void quit(){
        ing = false;
    }


    public Boolean IsServiceThread(String _tag){
        if(ing && this.tag.equals(_tag)){
            return true;
        }
        return false;
    }
}
