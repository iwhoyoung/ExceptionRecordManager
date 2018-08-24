package com.liyuanheng.www.exceptionrecordlibrary;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.liyuanheng.www.exceptionrecordlibrary.LocalFileHandler.OnExceptionCatchListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static com.liyuanheng.www.exceptionrecordlibrary.FileUtils.getDiskCacheDir;
import static java.text.DateFormat.getDateTimeInstance;

public class ExceptionManager implements OnExceptionCatchListener {

    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.CHINA);

    private static ExceptionManager exceptionManager;
    private Context mContext;
    private File recordFile;
    private String exceptionTip;

    private boolean isSingleStorageFile;
    private boolean isToastEx;
    private int storageLimit = 3 * 1024;

    private ExceptionManager(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(new LocalFileHandler(mContext, this));
    }

    public static ExceptionManager getInstance() {
        return exceptionManager;
    }

    public static ExceptionManager init(Context context) {
        if (exceptionManager == null)
            exceptionManager = new ExceptionManager(context);
        return exceptionManager;
    }

    public static ExceptionManager init(Context context, boolean isHost) {
        if (exceptionManager == null)
            exceptionManager = new ExceptionManager(context);
        return exceptionManager;
    }

    public void start() {
        if (recordFile == null)
            recordFile = new File(getDiskCacheDir(mContext) + "/log/");
        String path = recordFile.getAbsolutePath();
        if (!recordFile.exists())
             recordFile.mkdirs();

        if (FileUtils.getFileOrFolderSize(path) > storageLimit) {
            FileUtils.deleteAll(path);
        }
    }

    public ExceptionManager setStoragePath(File file) {
        recordFile = file;
        return exceptionManager;
    }

    public ExceptionManager setStoragePath(String path) {
        recordFile = new File(path);
        return exceptionManager;
    }

    public ExceptionManager setTimezone(Locale aLocale) {
        dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, aLocale);
        return exceptionManager;
    }

    public ExceptionManager setExceptionToast(String content, boolean isToastIncludeEx) {
        exceptionTip = content;
        isToastEx = isToastIncludeEx;
        return exceptionManager;
    }

    /**
     * 初始化检测，大于该值时，清理文件夹,默认3M
     *
     * @param limit 单位KB
     * @return
     */
    public ExceptionManager setStorageLimit(int limit) {
        storageLimit = limit;
        return exceptionManager;
    }

    public ExceptionManager setSingleFile(boolean isSingleStorageFile) {
        this.isSingleStorageFile = isSingleStorageFile;
        return exceptionManager;
    }

    @Override
    public String OnExceptionCatchToast(Throwable ex) {
        if (exceptionTip != null)
            if (isToastEx)
                return exceptionTip + ex.toString();
            else
                return exceptionTip;
        return ex.toString();
    }

    @Override
    public void OnExceptionCatch(Throwable ex) {
        saveLog(ex);
    }

    /**
     * 保存错误日志
     *
     * @param ex 异常
     */
    private void saveLog(Throwable ex) {
        try {
            File errorFile;
            if (isSingleStorageFile)
                errorFile = new File(recordFile + "/crash.txt");
            else
                errorFile = new File(recordFile + "/crash("
                        + getDateTimeInstance().format(new Date().getTime()) + ").txt");

            if (!errorFile.exists())
                errorFile.createNewFile();

            OutputStream out = new FileOutputStream(errorFile, true);
            out.write(("\n\n------------" + dateFormat.format(new Date()) + "-----------\n\n")
                    .getBytes());
            TelephonyManager mTm = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            //String imei = mTm.getDeviceId();
            String release = android.os.Build.VERSION.RELEASE;
            int sdk = android.os.Build.VERSION.SDK_INT;
            String mtype = android.os.Build.MODEL; // 手机型号
            String mtyb = android.os.Build.BRAND;// 手机品牌制造商
            //out.write(("\n imei: " + imei + "\n").getBytes());
            out.write(("\n android: " + release + "  sdk: " + sdk + "\n").getBytes());
            out.write(("\n Model: " + mtype + "\n").getBytes());
            out.write(("\n Manufacturer: " + mtyb + "\n\n").getBytes());
            PrintStream stream = new PrintStream(out);
            ex.printStackTrace(stream);
            stream.flush();
            out.flush();
            stream.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
