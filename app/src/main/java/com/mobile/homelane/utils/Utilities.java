package com.mobile.homelane.utils;

import android.content.Context;
import android.net.Uri;

import com.mobile.homelane.note.CreateNoteFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by ankit on 27/06/17.
 */

public class Utilities {
    public static String getReadableTime(long timestamp) {

        Date date = new Date(timestamp);
        DateFormat format = new SimpleDateFormat("dd MMM yyyy',' hh:mm a", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        format.setTimeZone(tz);
        return format.format(date);
    }

    public static File createImageFile(Context context, String prefix, String extension) throws IOException {
        String imageFileName = prefix + UUID.randomUUID();
        File storageDir = context.getExternalFilesDir(null);
        return File.createTempFile(imageFileName, extension, storageDir);
    }

    public static Uri getNewFileFromGalleryImage(Context context, String filePath) {
        File file = new File(filePath);
        File newFile;
        try {
            newFile = createImageFile(context, CreateNoteFragment.FILE_NAME_PREFIX, CreateNoteFragment.FILE_EXTENSION);
            createFileCopy(file, newFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return Uri.fromFile(newFile);
    }

    private static void createFileCopy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
