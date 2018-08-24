package com.liyuanheng.www.exceptionrecorddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liyuanheng.www.permissionlibrary.PermissionManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionManager.init(this).oneKeyRequest("android.permission.WRITE_EXTERNAL_STORAGE");
    }

    public void test(View view) {
        s.add("a");
    }
}
