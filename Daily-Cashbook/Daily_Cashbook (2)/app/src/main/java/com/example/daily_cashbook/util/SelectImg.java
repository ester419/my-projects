package com.example.daily_cashbook.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectImg {
    Activity activity;
    public static final int IMG = 8000;
    public static final int CAMARA = 9000;
    File file;
    String filmename;

    public SelectImg(Activity activity) {
        this.activity = activity;
    }

    /**
     * 打开相册
     */
    public void takePhoto() {
        //打开系统默认的相册(系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(picture, IMG);
    }


    public void takeCamara(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // 获取文件
        filmename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try {
            file = createFileIfNeed(filmename + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //拍照后原图回存入此路径下
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        activity.startActivityForResult(intent, CAMARA);
    }

    public String getCamaraPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = Uri.fromFile(file);
            Log.e("CAMARA_PATH", contentUri.toString());
            //文件的路径
            String absolutePath = file.getAbsolutePath();
            //文件的名字
            String parent = file.getName();
            Log.e("CAMARA_PATH", parent + "===" + absolutePath);
            return absolutePath;
        } else {
            File photoFile = new File(filmename);
            if (photoFile.exists()) {
                Log.e("CAMARA_PATH", photoFile.getAbsolutePath());
                return photoFile.getAbsolutePath();
            }
        }
        return "";

    }


    // 在sd卡中创建一保存图片（原图和缩略图共用的）文件夹
    private File createFileIfNeed(String fileName) throws IOException {
        String fileA = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/nbinpic";
        File fileJA = new File(fileA);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = new File(fileA, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


    //-------------获取相册图片相关

    /**
     * 根据Uri获取图片的绝对路径
     * @param uri 图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public String getPhotoPath(Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) {
            return getRealPathFromUriAboveApi19(activity, uri);
        } else {
            return getRealPathFromUriBelowAPI19(activity, uri);
        }
    }

    /**

     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**

     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) {
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


}
