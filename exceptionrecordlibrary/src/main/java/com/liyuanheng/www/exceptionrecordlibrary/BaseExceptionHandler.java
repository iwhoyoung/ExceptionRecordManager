package com.liyuanheng.www.exceptionrecordlibrary;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.util.Locale;

/**
 * 系统异常处理基类
 */
public abstract class BaseExceptionHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    /**
     * 未捕获异常跳转
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 如果正确处理了未捕获异常
        if (handleException(ex)) {
            try {
                // 如果处理了，让程序继续运行3s后再退出，保证错误日志能够保存
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理，收集错误信息发送错误报告等操作都在此完成，开发者可以根据自己的情况自定义异常处理逻辑
     *
     * @param ex Throwable
     * @return result
     */
    public abstract boolean handleException(Throwable ex);
}
