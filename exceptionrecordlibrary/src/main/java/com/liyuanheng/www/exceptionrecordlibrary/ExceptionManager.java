package com.liyuanheng.www.exceptionrecordlibrary;

import android.content.Context;
import android.telephony.TelephonyManager;

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

/**
 * @author huyang
 * @version 1.0.0
 */
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

    /**
     * 获取ExceptionManager实例化对象
     * <p> 使用该方法去获取ExceptionManager实例化对象</p>
     *
     * @return ExceptionManager对象
     */
    public static ExceptionManager getInstance() {
        return exceptionManager;
    }

    /**
     * 初始化方法
     * <p>该方法一般放入Application的子类onCreate方法中，参数传入this即可</p>
     *
     * @param context 传入Context参数
     * @return ExceptionManager实例化对象
     */
    public static ExceptionManager init(Context context) {
        if (exceptionManager == null)
            exceptionManager = new ExceptionManager(context);
        return exceptionManager;
    }

    /**
     * 初始化方法
     * <p>该方法一般放入Application的子类onCreate方法中
     * <br>@param context传入this即可
     * <br>@param isHost目前传入true即可，功能尚未完善</p>
     *
     * @param context 传入Context参数
     * @param isHost  代表是否托管，按照默认配置执行
     * @return ExceptionManager实例化对象
     */
    public static ExceptionManager init(Context context, boolean isHost) {
        if (exceptionManager == null)
            exceptionManager = new ExceptionManager(context);
        return exceptionManager;
    }

    /**
     * 使异常捕捉本地保存功能开始工作
     * <p>一般在配置完毕后，调用该方法，随后，异常捕捉保存才真正开始工作</p>
     */
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

    /**
     * 设置保存路径
     *
     * @param file 保存的文件路径
     * @return ExceptionManager实例化对象
     */
    public ExceptionManager setStoragePath(File file) {
        recordFile = file;
        return exceptionManager;
    }

    /**
     * 设置保存路径
     *
     * @param path 保存的文件路径
     * @return ExceptionManager实例化对象
     */
    public ExceptionManager setStoragePath(String path) {
        recordFile = new File(path);
        return exceptionManager;
    }

    /**
     * 设置时区
     *
     * @param aLocale 所处地区
     * @return ExceptionManager实例化对象
     */
    public ExceptionManager setTimezone(Locale aLocale) {
        dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, aLocale);
        return exceptionManager;
    }

    /**
     * 设置异常发生时，Toast提示内容
     * <p>设置异常发生时，对用户的Toast提示内容</p>
     *
     * @param content          Toast提示内容
     * @param isToastIncludeEx 是否在Toast内容末尾附上异常内容
     * @return ExceptionManager实例化对象
     */
    public ExceptionManager setExceptionToast(String content, boolean isToastIncludeEx) {
        exceptionTip = content;
        isToastEx = isToastIncludeEx;
        return exceptionManager;
    }

    /**
     * 初始化检测，大于该值时，清理文件夹,默认3M
     * <p>在调用start()方法时，会检测缓存异常日志文件缓存大小，超过限制时，将清空文件夹</p>
     *
     * @param limit 单位KB
     * @return ExceptionManager实例化对象
     */
    public ExceptionManager setStorageLimit(int limit) {
        storageLimit = limit;
        return exceptionManager;
    }

    /**
     * 设置保存异常文件为单一文件还是多个文件
     *
     * @param isSingleStorageFile 是否为单一文件
     * @return ExceptionManager实例化对象
     */
    public ExceptionManager setSingleFile(boolean isSingleStorageFile) {
        this.isSingleStorageFile = isSingleStorageFile;
        return exceptionManager;
    }

    /**
     * @param ex 被捕获的异常
     * @return
     */
    @Override
    public String OnExceptionCatchToast(Throwable ex) {
        if (exceptionTip != null)
            if (isToastEx)
                return exceptionTip + ex.toString();
            else
                return exceptionTip;
        return ex.toString();
    }

    /**
     * @param ex 被捕获的异常
     */
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
