package com.liyuanheng.www.exceptionrecordlibrary;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * 本地异常处理类
 */
public class LocalFileHandler extends BaseExceptionHandler {

    private Context context;
    private OnExceptionCatchListener onExceptionCatchListener;

    LocalFileHandler(Context context,OnExceptionCatchListener listener) {
        this.context = context;
        setOnExceptionCatchListener(listener);
    }

    /**
     * 自定义错误处理，收集错误信息发送错误报告等操作都在此完成，开发者可以根据自己的情况自定义异常处理逻辑
     *
     * @param ex Throwable
     * @return result
     */
    @Override
    public boolean handleException(final Throwable ex) {
        if (ex == null)
            return false;
            new Thread() {
                public void run() {
                    Looper.prepare();
                    Toast.makeText(context, onExceptionCatchListener.OnExceptionCatchToast(ex), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }.start();
            ex.printStackTrace();
        onExceptionCatchListener.OnExceptionCatch(ex);
        return true;
    }

    protected void setOnExceptionCatchListener(OnExceptionCatchListener listener) {
        onExceptionCatchListener = listener;
    }

    protected interface OnExceptionCatchListener {

        /**
         * @param ex 被捕获的异常
         * @return toast提示信息内容
         */
        String OnExceptionCatchToast(Throwable ex);

        /**
         * 异常出现时需进行的操作
         *
         * @param ex 被捕获的异常
         */
        void OnExceptionCatch(Throwable ex);

    }
}
