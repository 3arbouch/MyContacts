package io.interact.mohamedbenarbia.benmycontacts.Util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Manages all log file for the application
 */
public class FileLogger {

    /**
     * The debugging log tag
     */
    private static final String LOG_TAG = FileLogger.class.getName();

    /**
     * Gets a given log file to write to
     *
     * @param fileName
     *         the file name
     *
     * @return the logger instance writing to the given {@code fileName}
     */
    public static FileLogger getInstance(String fileName) {
        return new FileLogger((fileName));
    }

    /**
     * Constructs a log file to application's log folder
     *
     * @param fileName
     *         the logfile name
     */
    private FileLogger(String fileName) {
        mFileName = fileName;
    }

    public static boolean hasLog(String fileName) {
        File storageRootDir = android.os.Environment
                .getExternalStorageDirectory();

        File storageDir = new File(storageRootDir.getAbsolutePath() +
                "/" +
                SharedAttributes.NAME_DIR_APP_DATA +
                "/" +
                SharedAttributes.NAME_DIR_LOG);

        File logFile = new File(storageDir, fileName);

        return logFile.exists();
    }

    /**
     * Removes a log file
     *
     * @param fileName
     *         the name of the log file to be removed
     */
    public static void remove(String fileName) {
        File storageRootDir = android.os.Environment
                .getExternalStorageDirectory();

        File storageDir = new File(storageRootDir.getAbsolutePath() +
                "/" +
                SharedAttributes.NAME_DIR_APP_DATA +
                "/" +
                SharedAttributes.NAME_DIR_LOG);

        File logFile = new File(storageDir, fileName);

        if (logFile.exists()) {
            if (!logFile.delete()) {
                Log.w(LOG_TAG, "Failed to remove log " + fileName);
            }
        }
    }

    /**
     * Writes out a log line, a newline will be automatically appended
     *
     * @param line
     *         the line to log
     *
     * @return {@code true}, if succeeded, {@code false}, otherwise
     */
    public boolean logLine(String line) {
        try {
            FileOutputStream outStream = openStream();

            if (null != outStream) {
                outStream.write((line + "\n").getBytes());
                outStream.flush();
                outStream.close();

                return true;
            }
        } catch (IOException ioE) {
            ioE.printStackTrace();
        }

        return false;
    }

    public boolean logList(List<?> lineList) {
        try {
            FileOutputStream outStream = openStream();

            if (null != outStream) {
                Iterator<?> it = lineList.iterator();
                while (it.hasNext()) {
                    outStream.write((it.next().toString() + "\n").getBytes());
                    outStream.flush();
                }
                outStream.close();
                return true;
            }
        } catch (IOException ioE) {
            ioE.printStackTrace();
        }

        return false;
    }

    /**
     * Opens the stream to write into the current log file
     *
     * @return the output stream to write or {@code null}
     */
    private FileOutputStream openStream() {
        File storageRootDir = android.os.Environment.getExternalStorageDirectory();

        if (storageRootDir.canWrite()) {
            File storageDir = new File(storageRootDir.getAbsolutePath() +
                    "/" +
                    SharedAttributes.NAME_DIR_APP_DATA +
                    "/" +
                    SharedAttributes.NAME_DIR_LOG);

            try {
                if (!storageDir.exists()) {
                    if (!storageDir.mkdirs()) {
                        Log.w(LOG_TAG, "Failed to create " + storageDir.getAbsolutePath());
                    }
                }

                if (storageDir.exists() && storageDir.canWrite()) {
                    return new FileOutputStream(storageDir + "/" + mFileName, true);
                }
            } catch (IOException ioE) {
                ioE.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Failed to gain write access to " + storageRootDir.getAbsolutePath());
        }

        return null;
    }

    /**
     * Opens the stream to read into the current log file
     *
     * @return the output stream to write or {@code null}
     */
    private FileInputStream openInStream() {
        File storageRootDir = android.os.Environment.getExternalStorageDirectory();

        if (storageRootDir.canRead()) {
            File storageDir = new File(storageRootDir.getAbsolutePath() +
                    "/" +
                    SharedAttributes.NAME_DIR_APP_DATA +
                    "/" +
                    SharedAttributes.NAME_DIR_LOG);

            try {
                if (!storageDir.exists()) {

                    Log.w(LOG_TAG, "No such directory " + storageDir.getAbsolutePath());

                }

                if (storageDir.exists() && storageDir.canRead()) {
                    return new FileInputStream(storageDir + "/" + mFileName);
                }
            } catch (IOException ioE) {
                ioE.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Failed to gain read access to " + storageRootDir.getAbsolutePath());
        }

        return null;
    }


    /**
     *
     * @return null if a problem occurs
     */
    public List<String> fetchStringList() {
        List<String> res = null;
        try {
            FileInputStream inStream = openInStream();

            if (null != inStream) {
                res = new ArrayList<>();
                Scanner scanner = new Scanner(inStream);
                while (scanner.hasNextLine()) {
                    res.add(scanner.nextLine());
                }
                inStream.close();
                return res;
            }
        } catch (IOException ioE) {
            ioE.printStackTrace();
        }

        return res;
    }

    /**
     * The current logfile name
     */
    private String mFileName = null;
}