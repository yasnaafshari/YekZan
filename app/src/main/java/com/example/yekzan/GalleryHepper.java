package com.example.yekzan;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class GalleryHepper {

    public static ArrayList<Uri> fetchGalleryImages(Activity context) {
        ArrayList<Uri> imagePathList = new ArrayList<Uri>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns._ID};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                imagePathList.add(ContentUris.withAppendedId(uri, id));
            }
            cursor.close();
        }

        return imagePathList;
    }
}
