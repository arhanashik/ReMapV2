package com.bodytel.util.helper;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.ReMapApp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.FileChannel;

public class FileUtil {
    // Object for intrinsic lock (per docs 0 length array "lighter" than a normal Object
    public static final Object[] DATA_LOCK = new Object[0];

    public static void addNewLogOnSD() {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "job_dispatcher");
            if (!root.exists()) {
                Log.d("file util", "file not exists: " + "create new");
                root.mkdirs();
            }
            File gpxfile = new File(root, "job_dispatcher_log.txt");
            FileWriter writer = new FileWriter(gpxfile, true);
            String strToWrite = "\nJob dispatcher log on: " + DateFormat.getTimeFormat(ReMapApp.getContext())
                    .format(System.currentTimeMillis());
            writer.append(strToWrite);
            writer.flush();
            writer.close();
            Log.d("file util", "file writer: " + "done: " + gpxfile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy file.
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public static void copyFile(final File src, final File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    /**
     * Replace entire File with contents of String.
     *
     * @param fileContents
     * @param file
     * @return
     */
    public static boolean writeStringAsFile(final String fileContents, final File file) {
        boolean result = false;
        try {
            synchronized (DATA_LOCK) {
                if (file != null) {
                    file.createNewFile(); // ok if returns false, overwrite
                    Writer out = new BufferedWriter(new FileWriter(file), 1024);
                    out.write(fileContents);
                    out.close();
                    result = true;
                }
            }
        } catch (IOException e) {
            // Log.e(Constants.LOG_TAG, "Error writing string data to file " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * Append String to end of File.
     *
     * @param appendContents
     * @param file
     * @return
     */
    public static boolean appendStringToFile(final String appendContents, final File file) {
        boolean result = false;
        try {
            synchronized (DATA_LOCK) {
                if (file != null && file.canWrite()) {
                    file.createNewFile(); // ok if returns false, overwrite
                    Writer out = new BufferedWriter(new FileWriter(file, true), 1024);
                    out.write(appendContents);
                    out.close();
                    result = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //   Log.e(Constants.LOG_TAG, "Error appending string data to file " + e.getMessage(), e);
        }
        return result;
    }
}
