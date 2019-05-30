package com.xingge.carble.util;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author 星哥的
 * @date 2018/7/25
 */
public class LogUtils {

    private static LogUtils logUtils = null;
    private WriteLog writeLog;
    private String fileName;
    private String directory;

    public static LogUtils getLogUtils() {
        if (logUtils == null) {
            logUtils = new LogUtils();
        }
        return logUtils;
    }

    private LogUtils() {
        directory = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName = "debug.log";
        String filePath = directory + File.separator + fileName;

        writeLog = new WriteLog(filePath);
    }

    public void releaseWrite() {
        if (writeLog != null) {
            writeLog.closeThread();
        }
        logUtils = null;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFileName() {
        return fileName;
    }

    private String getCurrentDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sDateFormat.format(new Date());
    }

    private String getDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sDateFormat.format(new Date());
    }

    public synchronized void writeLog(String log) {
        if (writeLog == null || writeLog.isStop()) {
            directory = Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName = "debug.log";
            String filePath = directory + File.separator + fileName;
            writeLog = new WriteLog(filePath);
        } else {
            if (!fileName.contains(getCurrentDate())) {
                writeLog.closeThread();
                writeLog = null;
                writeLog(log);
            }
        }
        writeLog.sendData(/*getDate() + ": " + */log);
    }

    private class WriteLog extends Thread {
        private final Object mPauseLock;
        private boolean isSendingData = false;
        private boolean stop = false;
        private ConcurrentLinkedQueue<String> commands = new ConcurrentLinkedQueue<>();
        private String filePath;
        //FileOutputStream会自动调用底层的close()方法，不用关闭
        private FileOutputStream fos = null;
        private BufferedWriter bw = null;

        public WriteLog(String filePath) {
            this.filePath = filePath;
            mPauseLock = new Object();

            if (null == filePath) {
                return;
            }
            //如果父路径不存在
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();//创建父路径
            }
            start();
        }

        public void sendData(String msg) {
            commands.offer(msg);
            if (!isSendingData) {
                onResume();
            }
        }

        public void onResume() {
            synchronized (mPauseLock) {
                mPauseLock.notifyAll();
            }
        }

        private void pauseThread() {
            synchronized (mPauseLock) {
                try {
                    mPauseLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }

        public boolean isStop() {
            return stop;
        }

        public void closeThread() {
            stop = true;
            try {
                if (bw != null) {
                    bw.close();//关闭缓冲流
                    bw = null;
                }
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!stop) {
                processCommand();
            }
        }

        private void processCommand() {
            String msg;
            while ((msg = commands.poll()) != null) {
                writeToFile(msg);
            }
            isSendingData = false;
            if (!stop) {
                pauseThread();
            }
        }

        /**
         * 将log信息写入文件中
         *
         * @param msg
         */
        private void writeToFile(String msg) {
            String log = msg/* + "\n"*/;
            try {
                //这里的第二个参数代表追加还是覆盖，true为追加，false
                if (fos == null) {
                    fos = new FileOutputStream(filePath, true);
                    bw = new BufferedWriter(new OutputStreamWriter(fos));
                }
                bw.write(log);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
                closeThread();
            }
        }

    }
}
