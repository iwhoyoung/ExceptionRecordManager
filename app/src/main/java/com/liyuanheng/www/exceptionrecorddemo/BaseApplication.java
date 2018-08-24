package com.liyuanheng.www.exceptionrecorddemo;

import android.app.Application;

import com.liyuanheng.www.exceptionrecordlibrary.ExceptionManager;
import com.liyuanheng.www.permissionlibrary.PermissionManager;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionManager.init(this).setExceptionToast("app occur an error",false).start();
    }
}
