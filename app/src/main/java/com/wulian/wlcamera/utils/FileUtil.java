package com.wulian.wlcamera.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.wulian.wlcamera.MainApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;



public class FileUtil {
    public static final int FILE_TYPE_JPEG = 255216;
    public static final int FILE_TYPE_GIF = 7173;
    public static final int FILE_TYPE_BMP = 6677;
    public static final int FILE_TYPE_PNG = 13780;

    // getDataDirectory() 获取 Android 数据目录。
    // getDownloadCacheDirectory() 获取 Android 下载/缓存内容目录。
    // getExternalStorageDirectory() 获取外部存储目录即 SDCard
    // getExternalStoragePublicDirectory(String type)
    // 获取一个高端的公用的外部存储器目录来摆放某些类型的文件
    // getExternalStorageState() 获取外部存储设备的当前状态
    // getRootDirectory() 获取 Android 的根目录

    public static String getBaseDirector() {
        MainApplication application = MainApplication.getApplication();
        String path = application.getFilesDir().getAbsolutePath();
        return path;
    }

    /**
     * 获取外存私有目录
     * 例如 sdcard/Android/data/cc.wulian.smarthomev6
     */
    public static String getExternalFilesDirPath() {
        String folder = null;
        File file = MainApplication.getApplication().getExternalFilesDir(null);
        if (file == null) {
            file = MainApplication.getApplication().getFilesDir();
        }
        folder = file.getAbsolutePath();
        return folder;
    }

    public static String getLoggerPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/logger";
        isFolderExists(folder);
        File file = new File(folder + "/logger.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }

    public static String getCrashLogPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/crash";
        isFolderExists(folder);
        return folder;
    }

    public static String getMscPath() {
//        String folder = Environment.getExternalStorageDirectory() + "/wulian/msc";
        String folder = getExternalFilesDirPath() + "/msc";
        isFolderExists(folder);
        return folder;
    }

    public static String getSplashPath() {
        String folder = getBaseDirector() + "/wulian/splash";
        isFolderExists(folder);
        return folder;
    }

    public static String getUpdatePath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/update";
        isFolderExists(folder);
        return folder;
    }

    public static String getGatewayDirectoryPath(String gwID) {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/data" + "/" + gwID;
        isFolderExists(folder);
        return folder;
    }

    public static String getTempDirectoryPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/temp";
        isFolderExists(folder);
        return folder;
    }

    public static String getLastFramePath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/temp/frame";
        isFolderExists(folder);
        return folder;
    }

    public static String getEquesTempPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/temp/eques";
        isFolderExists(folder);
        return folder;
    }

    public static String getEquesAlarmPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/temp/eques/alarm";
        isFolderExists(folder);
        return folder;
    }

    public static String getEquesRingPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/temp/eques/ring";
        isFolderExists(folder);
        return folder;
    }

    public static String getICamImgPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/temp/icam/img";
        isFolderExists(folder);
        return folder;
    }

    public static String getICamVideoPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/temp/icam/video";
        isFolderExists(folder);
        return folder;
    }

    public static String getUserDirectoryPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/account";
        isFolderExists(folder);
        return folder;
    }

    public static String getSnapshotPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/camera";
        isFolderExists(folder);
        return folder;
    }

    public static String getPrePositionPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/camera/preset";
        isFolderExists(folder);
        return folder;
    }

    public static String getVideoPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/video";
        isFolderExists(folder);
        return folder;
    }

    public static String getAdvertisementPath() {
        String folder = getBaseDirector() + "/wulian/advertisement";
        isFolderExists(folder);
        return folder;
    }

    public static String getAnnouncementPath() {
        String folder = getBaseDirector() + "/wulian/announcement";
        isFolderExists(folder);
        return folder;
    }

    public static String getHtmlResourcesPath() {
//        String folder = Environment.getExternalStorageDirectory() + "/wulian/h5/v6Html";
        String folder = getExternalFilesDirPath() + "/h5/v6Html";
        isFolderExists(folder);
        return folder;
    }

    public static String getDeviceHtmlResourcesPath() {
        String folder = getHtmlResourcesPath() + "/device";
        isFolderExists(folder);
        return folder;
    }

    public static String getSkinPath() {
//        String folder = Environment.getExternalStorageDirectory() + "/wulian/skin";
        String folder = getExternalFilesDirPath() + "/skin";
        isFolderExists(folder);
        return folder;
    }

    public static String getUeiAirDataPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/airdata";
        isFolderExists(folder);
        return folder;
    }

    public static String getUeiTvDataPath() {
        String folder = Environment.getExternalStorageDirectory() + "/wulian/tvdata";
        isFolderExists(folder);
        return folder;
    }

    public static boolean isFolderExists(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean saveBitmapToPng(Bitmap bm, String pathname) {
        File file = new File(pathname);
        return saveBitmapToPng(bm, file);
    }

    public static boolean saveBitmapToPng(Bitmap bm, String folder, String fileName) {
        File file = new File(folder, fileName);
        return saveBitmapToPng(bm, file);
    }

    public static boolean saveBitmapToPng(Bitmap bm, File file) {
        boolean result = false;
        if (bm == null)
            return result;

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public static boolean saveBitmapToJpeg(Bitmap bm, String folder, String fileName) {
        boolean result = false;
        if (bm == null)
            return result;

        BufferedOutputStream bos = null;
        try {
            File file = new File(folder, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public static boolean saveBitmapToJpeg(Bitmap bm, String folder) {
        String fileName = DateUtil.getFormatIMGTime(System.currentTimeMillis()) + ".jpg";
        return saveBitmapToJpeg(bm, folder, fileName);
    }

    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static String byteCountToDisplaySize(long paramLong) {
        String str2;
        if (paramLong / 1073741824L > 0L) {
            String str1 = String.valueOf(paramLong / 1073741824L);
            str2 = str1 + " GB";
        } else if (paramLong / 1048576L > 0L) {
            String str3 = String.valueOf(paramLong / 1048576L);
            str2 = str3 + " MB";
        } else if (paramLong / 1024L > 0L) {
            String str4 = String.valueOf(paramLong / 1024L);
            str2 = str4 + " KB";
        } else {
            String str5 = paramLong < 0 ? "0" : String.valueOf(paramLong);
            str2 = str5 + " bytes";
        }
        return str2;
    }

    public static boolean deleteFile(String filePath) {

        boolean flag = false;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                file.delete();
                flag = true;
            }
        } catch (Exception e) {

        }
        return flag;
    }

    public static boolean checkFileExistedAndAvailable(String filePath) {
        boolean result;
        File file = new File(filePath);
        try {
            if (!file.exists())
                file.createNewFile();
            int fileType = getFileType(file);
            switch (fileType) {
                case FILE_TYPE_BMP:
                case FILE_TYPE_GIF:
                case FILE_TYPE_JPEG:
                case FILE_TYPE_PNG:
                    result = true;
                    break;
                default:
                    result = false;
                    break;
            }
        } catch (FileNotFoundException e) {
            result = false;
            e.printStackTrace();
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断文件是否超时
     *
     * @param filePath 文件绝对路径
     * @param timeout  超时时间
     * @return 如果文件存在，且修改时间在超时时间内，则返回true 否则返回false
     * @author yanzy
     */
    public static boolean checkFileExistedAndIntime(String filePath, long timeout) {
        boolean result = false;
        File file = new File(filePath);
        if (file.exists()) {
            result = DateUtil.inXXXMilis(file.lastModified(), timeout);
        }
        return result;

    }

    public static int getFileType(File file) throws FileNotFoundException, IOException {
        FileInputStream inputStream = new FileInputStream(file);
        String filecode = "";
        byte[] buffer = new byte[2];
        if (inputStream.read(buffer) != -1) {
            for (int i = 0; i < buffer.length; i++) {
                filecode += Integer.toString((buffer[i] & 0xFF));
            }
        }
        if (inputStream != null)
            inputStream.close();
        return StringUtil.toInteger(filecode);
    }

    /**
     * 创建目录
     *
     * @param dir 目录
     */
    public static void mkdir(String dir) {
        try {
            String dirTemp = dir;
            File dirPath = new File(dirTemp);
            if (!dirPath.exists()) {
                dirPath.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新建文件
     *
     * @param fileName String 包含路径的文件名 如:E:\phsftp\src\123.txt
     * @param content  String 文件内容
     */
    public static void createNewFile(String fileName, String content) {
        try {
            String fileNameTemp = fileName;
            File filePath = new File(fileNameTemp);
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
            FileWriter fw = new FileWriter(filePath);
            PrintWriter pw = new PrintWriter(fw);
            String strContent = content;
            pw.println(strContent);
            pw.flush();
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 新建文件
     * <p>
     * 不会换行
     *
     * @param fileName String 包含路径的文件名 如:E:\phsftp\src\123.txt
     * @param content  String 文件内容
     */
    public static void createNewFileWithoutNewLine(String fileName, String content) {
        try {
            String fileNameTemp = fileName;
            File filePath = new File(fileNameTemp);
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
            FileWriter fw = new FileWriter(filePath);
            PrintWriter pw = new PrintWriter(fw);
            String strContent = content;
            pw.print(strContent);
            pw.flush();
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文件
     *
     * @param fileName 包含路径的文件名
     */
    public static void delFile(String fileName) {
        try {
            String filePath = fileName;
            File delFile = new File(filePath);
            delFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹路径
     */
    public static void delFolder(String folderPath) {
        try {
            // 删除文件夹里面所有内容
            delAllFile(folderPath);
            String filePath = folderPath;
            File myFilePath = new File(filePath);
            // 删除空文件夹
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path 文件夹路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] childFiles = file.list();
        File temp = null;
        for (int i = 0; i < childFiles.length; i++) {
            // File.separator与系统有关的默认名称分隔符
            // 在UNIX系统上，此字段的值为'/'；在Microsoft Windows系统上，它为 '\'。
            if (path.endsWith(File.separator)) {
                temp = new File(path + childFiles[i]);
            } else {
                temp = new File(path + File.separator + childFiles[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + childFiles[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + childFiles[i]);// 再删除空文件夹
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param srcFile 包含路径的源文件 如：E:/phsftp/src/abc.txt
     * @param dirDest 目标文件目录；若文件目录不存在则自动创建 如：E:/phsftp/dest
     * @throws IOException
     */
    public static void copyFile(String srcFile, String dirDest) {
        try {
            FileInputStream in = new FileInputStream(srcFile);
            mkdir(dirDest);
            FileOutputStream out = new FileOutputStream(dirDest + "/" + new File(srcFile).getName());
            copyFile(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(InputStream in, OutputStream out) {
        try {
            int len;
            byte buffer[] = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件夹
     *
     * @param oldPath String 源文件夹路径 如：E:/phsftp/src
     * @param newPath String 目标文件夹路径 如：E:/phsftp/dest
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            // 如果文件夹不存在 则新建文件夹
            mkdir(newPath);
            File file = new File(oldPath);
            String[] files = file.list();
            File temp = null;
            for (int i = 0; i < files.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + files[i]);
                } else {
                    temp = new File(oldPath + File.separator + files[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] buffer = new byte[1024 * 2];
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        output.write(buffer, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath 包含路径的文件名 如：E:/phsftp/src/ljq.txt
     * @param newPath 目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }

    /**
     * 移动文件到指定目录，不会删除文件夹
     *
     * @param oldPath 源文件目录 如：E:/phsftp/src
     * @param newPath 目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFiles(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delAllFile(oldPath);
    }

    /**
     * 移动文件到指定目录，会删除文件夹
     *
     * @param oldPath 源文件目录 如：E:/phsftp/src
     * @param newPath 目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    /**
     * 含子目录的文件解压缩
     *
     * @param zipFile    需要解压的文件的绝对路径
     * @param folderPath 目标文件目录，需要以"/"结尾
     */
    // 第一个参数就是，第二个就是
    public static boolean upZipFile(String zipFile, String folderPath) {
        File folderPathFile = new File(folderPath);
        if (!folderPathFile.exists()) {
            folderPathFile.mkdirs();
        }
        ZipFile zfile = null;
        try {
            // 转码为GBK格式，支持中文
            zfile = new ZipFile(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            // 列举的压缩文件里面的各个文件，判断是否为目录
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                dirstr.trim();
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = null;
            FileOutputStream fos = null;
            // ze.getName()会返回 script/start.script这样的，是为了返回实体的File
            File realFile = getRealFileName(folderPath, ze.getName());
            try {
                fos = new FileOutputStream(realFile);
            } catch (FileNotFoundException e) {
                return false;
            }
            os = new BufferedOutputStream(fos);
            InputStream is = null;
            try {
                is = new BufferedInputStream(zfile.getInputStream(ze));
            } catch (IOException e) {
                return false;
            }
            int readLen = 0;
            // 进行一些内容复制操作
            try {
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
            } catch (IOException e) {
                return false;
            }
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            zfile.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        absFileName = absFileName.replace("\\", "/");
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                ret = new File(ret, substr);
            }

            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            ret = new File(ret, substr);
            return ret;
        } else {
            ret = new File(ret, absFileName);
        }
        return ret;
    }

    /**
     * 压缩文件
     *
     * @param srcDir  压缩前存放的目录
     * @param destDir 压缩后存放的目录
     * @throws Exception
     */
    public static void yaSuoZip(String srcDir, String destDir) throws Exception {
        String tempFileName = null;
        byte[] buf = new byte[1024 * 2];
        int len;
        // 获取要压缩的文件
        File[] files = new File(srcDir).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    if (destDir.endsWith(File.separator)) {
                        tempFileName = destDir + file.getName() + ".zip";
                    } else {
                        tempFileName = destDir + "/" + file.getName() + ".zip";
                    }
                    FileOutputStream fos = new FileOutputStream(tempFileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    ZipOutputStream zos = new ZipOutputStream(bos);// 压缩包

                    ZipEntry ze = new ZipEntry(file.getName());// 压缩包文件名
                    zos.putNextEntry(ze);// 写入新的ZIP文件条目并将流定位到条目数据的开始处

                    while ((len = bis.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                        zos.flush();
                    }
                    bis.close();
                    zos.close();

                }
            }
        }
    }

    /**
     * 读取数据
     *
     * @param inSream
     * @param charsetName
     * @return
     * @throws Exception
     */
    public static String readData(InputStream inSream, String charsetName) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inSream.close();
        return new String(data, charsetName);
    }

    public static String readFileStr(String file) {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String f = new String(buffer);
            return f;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void writeLogger(String data) {
        writeData(getLoggerPath(), new Date() + ":" + data);
    }

    public static void writeData(String fileName, String data) {

        OutputStreamWriter n = null;
        try {
            n = new OutputStreamWriter(new FileOutputStream(fileName, true));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }// OutputStreamWriter 是字节流通向字符流的桥梁
        BufferedWriter s = new BufferedWriter(n);// 将文本写入字符输出流
        try {
            s.write(data + "\n");// 写入字符
        } catch (FileNotFoundException e) {
            System.out.println("找不到文件");
        } catch (IOException a) {
            System.out.println("写入数据失败");
        } finally {
            try {
                s.flush();
                s.close();
                n.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 一行一行读取文件，适合字符读取，若读取中文字符时会出现乱码
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static Set<String> readFile(String path) throws Exception {
        Set<String> datas = new HashSet<String>();
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            datas.add(line);
        }
        br.close();
        fr.close();
        return datas;
    }

    public static String readFileToString(String filePath) {
        return readFileToString(filePath, "utf-8");
    }

    /**
     * 读文件并转换为String
     * 因是一次性读取，建议只用作读取小文件，否则有性能问题
     *
     * @param filePath
     * @param charset
     * @return
     */
    public static String readFileToString(String filePath, String charset) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                int size = (int) file.length();
                byte[] data = new byte[size];
                inputStream.read(data, 0, size);
                String string = new String(data, charset);
                return string;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    /**
     * 返回文件路径所对应的文件或文件夹
     *
     * @param baseDir
     * @param absFileName 相对路径
     * @return
     */
    public static File getRealFile(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                ret = new File(ret, substr);
            }
        }
        if (!ret.exists())
            ret.mkdirs();
        substr = dirs[dirs.length - 1];
        ret = new File(ret, substr);
        if (!ret.exists() && ret.isDirectory()) {
            ret.mkdirs();
        }
        return ret;
    }


    /**
     * 解压缩功能. 将zipFile文件解压到folderPath目录下.
     *
     * @throws Exception
     */
    public static int unZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> zList = zfile.entries();
        ZipEntry ze = null;
        try {
            // 循环读取压缩包文件
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                // 遇到文件夹则创建
                if (ze.isDirectory()) {
                    getRealFile(folderPath, new String(ze.getName().getBytes(), "utf-8"));
                    continue;
                }
                // 不是文件夹则创建并写入
                OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFile(folderPath, new String(ze
                        .getName().getBytes(), "utf-8"))));
                InputStream is = zfile.getInputStream(ze);
                byte[] buf = new byte[1024];
                int count = 0;
                while ((count = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, count);
                    os.flush();
                }
                os.close();
                is.close();
            }
        } finally {
            zfile.close();
        }
        return 0;
    }

}