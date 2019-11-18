package com.lu.portable.detect.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Utils {
    public static int[] convertYUV420_NV21toARGB8888(byte[] data, int width, int height) {
        int size = width * height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;

        // i along Y and the final pixels
        // k along pixels U and V
        for (int i = 0, k = 0; i < size; i += 2, k += 2) {
            y1 = data[i] & 0xff;
            y2 = data[i + 1] & 0xff;
            y3 = data[width + i] & 0xff;
            y4 = data[width + i + 1] & 0xff;

            u = data[offset + k] & 0xff;
            v = data[offset + k + 1] & 0xff;
            u = u - 128;
            v = v - 128;
            pixels[i] = convertYUVtoARGB(y1, u, v);
            pixels[i + 1] = convertYUVtoARGB(y2, u, v);
            pixels[width + i] = convertYUVtoARGB(y3, u, v);
            pixels[width + i + 1] = convertYUVtoARGB(y4, u, v);

            if (i != 0 && (i + 2) % width == 0)
                i += width;
        }
        return pixels;
    }

    private static int convertYUVtoARGB(int y, int u, int v) {
        int r, g, b;
        r = y + (int) 1.402f * u;
        g = y - (int) (0.344f * v + 0.714f * u);
        b = y + (int) 1.772f * v;
        r = r > 255 ? 255 : r < 0 ? 0 : r;
        g = g > 255 ? 255 : g < 0 ? 0 : g;
        b = b > 255 ? 255 : b < 0 ? 0 : b;
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    public static String getDataColumn(Context context, Uri paramUri, String paramString, String[] paramArrayOfString) {
        try {
            Cursor cursor = context.getContentResolver().query(paramUri, new String[]{"_data"}, paramString, paramArrayOfString, null);
            if (context != null) {
                if (cursor.moveToFirst()) {
                    String data = context.getString(cursor.getColumnIndexOrThrow("_data"));
                    return data;
                }
            }
            cursor.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(19)
    public static String getPath(Context context, Uri uri) {
//        Object localObject1 = null;
//        int i;
//        if (Build.VERSION.SDK_INT >= 19) {
//            i = 1;
//            if ((i == 0) || (!DocumentsContract.isDocumentUri(context, uri)))
//                break label211;
//            if (!isExternalStorageDocument(uri))
//                break label87;
//            co = DocumentsContract.getDocumentId(uri).split(":");
//            if ("primary".equalsIgnoreCase(co[0]))
//                localObject1 = Environment.getExternalStorageDirectory() + "/" + co[1];
//        }
//        label87:
//        do {
//            do {
//                return localObject1;
//                i = 0;
//                break;
//                if (!isDownloadsDocument(uri))
//                    continue;
//                uri = DocumentsContract.getDocumentId(uri);
//                return getDataColumn(co, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(uri).longValue()), null, null);
//            }
//            while (!isMediaDocument(uri));
//            localObject1 = DocumentsContract.getDocumentId(uri).split(":");
//            Object localObject2 = localObject1[0];
//            uri = null;
//            if ("image".equals(localObject2))
//                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            while (true) {
//                return getDataColumn(co, uri, "_id=?", new String[]{localObject1[1]});
//                if ("video".equals(localObject2)) {
//                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                    continue;
//                }
//                if (!"audio".equals(localObject2))
//                    continue;
//                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//            }
//            if (!"content".equalsIgnoreCase(uri.getScheme()))
//                continue;
//            if (isGooglePhotosUri(uri))
//                return uri.getLastPathSegment();
//            return getDataColumn(co, uri, null, null);
//        }
//        while (!"file".equalsIgnoreCase(uri.getScheme()));
//        label211:
        return (String) uri.getPath();
    }

    public static boolean isDownloadsDocument(Uri paramUri) {
        return "com.android.providers.downloads.documents".equals(paramUri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri paramUri) {
        return "com.android.externalstorage.documents".equals(paramUri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri paramUri) {
        return "com.google.android.apps.photos.content".equals(paramUri.getAuthority());
    }

    public static boolean isMediaDocument(Uri paramUri) {
        return "com.android.providers.media.documents".equals(paramUri.getAuthority());
    }

    public static int setRotation(int width, int height, int paramInt3, int rotation) {
//        if (width >= height)
//
//            switch (paramInt3) {
//
//                default:
//                    break;
//                case 1:
//                    break;
//                case 2:
//                    break;
//                case 3:
//                    break;
//            }
//
//        do {
//            return paramInt4;
//            return 0;
//            return 180;
//            switch (paramInt3) {
//                case 1:
//                default:
//                    return paramInt4;
//                case 0:
//                    return 0;
//                case 2:
//            }
//            return 180;
//        }
//        while (paramInt2 < width);
//        if ((paramInt3 == 0) || (paramInt3 == 2)) {
//            switch (paramInt3) {
//                case 1:
//                default:
//                    return paramInt4;
//                case 0:
//                    return 90;
//                case 2:
//            }
//            return 270;
//        }
//        switch (paramInt3) {
//            case 2:
//            default:
//                return paramInt4;
//            case 1:
//                return 270;
//            case 3:
//        }
        return 90;
    }
}