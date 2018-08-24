package com.liyuanheng.www.exceptionrecordlibrary;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

public class FileUtils {

    /**
     * gb to byte
     **/
    public static final long GB_2_BYTE = 1073741824;
    /**
     * mb to byte
     **/
    public static final long MB_2_BYTE = 1048576;
    /**
     * kb to byte
     **/
    public static final long KB_2_BYTE = 1024;
    public static final int SIZETYPE_B = 1;
    public static final int SIZETYPE_KB = 2;
    public static final int SIZETYPE_MB = 3;
    public static final int SIZETYPE_GB = 4;

    /**
     * 获取硬盘缓存的路径地址
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getAbsolutePath();
        } else {
            cachePath = context.getCacheDir().getAbsolutePath();
        }
        return cachePath;
    }

    public static double getFileOrFolderSize(String filePath) {
        return getFileOrFolderSize(filePath, SIZETYPE_KB);
    }

    /**
     * Gets the size of the specified unit of the specified file
     *
     * @param filePath file path
     * @param sizeType get size type 1 is byte、2 is KB、3 is MB、4 is GB
     * @return size
     */
    public static double getFileOrFolderSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getDirSize(file);
            } else {
                blockSize = getFileSize(file);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * Get the specified folder
     *
     * @param dir
     * @return
     * @throws Exception
     */
    private static long getDirSize(File dir) throws Exception {
        if (dir == null || !dir.isDirectory())
            return 0;
        long size = 0;
        File files[] = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                size += getDirSize(file);  //递归调用继续统计
            } else {
                size += getFileSize(file);
            }
        }
        return size;
    }

    /**
     * Gets the specified file size
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        }
        return size;
    }

    /**
     * Convert file size, specify the type of conversion
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / KB_2_BYTE));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / MB_2_BYTE));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / GB_2_BYTE));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * Deletes all files in the specified folder, but does not delete the
     * directory.
     *
     * @param path
     */
    public static void deleteAll(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File f : files) {
            deleteAll(f);
            f.delete();
        }
    }

    /**
     * Recursively delete all files in the specified folder (including the
     * folder)
     *
     * @param file
     * @author andrew
     */
    public static void deleteAll(File file) {
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteAll(f);// delete file
                f.delete();// delete folder
            }
            file.delete();
        }
    }
}
