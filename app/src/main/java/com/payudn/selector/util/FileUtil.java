package com.payudn.selector.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.payudn.selector.R;
import com.payudn.selector.entity.FileBean;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtil {
    private static final Uri EXTERNAL_STORAGE_URI_FILES = MediaStore.Files.getContentUri("external");
    public static Bitmap parseToBitmap(Context context,String filePath, int outWidth, int outHeight, int radius, int boarder){
        try {
            Bitmap bitmap;
            if(!filePath.equals ("")){
                FileInputStream in = new FileInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(in);
            }else{
                Drawable drawable = context.getDrawable (R.drawable.image_empty);
                BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
                bitmap = bitmapDrawable.getBitmap ();
            }
            if(bitmap!=null){
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float widthScale = outWidth * 1f / width;
                float heightScale = outHeight * 1f / height;

                Matrix matrix = new Matrix();
                matrix.setScale(widthScale, heightScale);
                //创建输出的bitmap
                Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
                //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
                Canvas canvas = new Canvas(desBitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                //创建着色器
                BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                //给着色器配置matrix
                bitmapShader.setLocalMatrix(matrix);
                paint.setShader(bitmapShader);
                //创建矩形区域并且预留出border
                RectF rect = new RectF(boarder, boarder, outWidth - boarder, outHeight - boarder);
                //把传入的bitmap绘制到圆角矩形区域内
                canvas.drawRoundRect(rect, radius, radius, paint);
                if (boarder > 0) {
                    //绘制boarder
                    Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    boarderPaint.setColor(Color.parseColor("#7C7C7C"));
                    boarderPaint.setStyle(Paint.Style.STROKE);
                    boarderPaint.setStrokeWidth(boarder);
                    canvas.drawRoundRect(rect, radius, radius, boarderPaint);
                }
                return desBitmap;
            }
        }catch (Exception e){
            Log.e ("FileUtil",e.getMessage (),e);
        }
        return null;
    }
    public static List<FileBean> getRootFile(Context context,OnFileListener onFileListener){
        return getFileByParentId (context,0,onFileListener);
    }
    public static List<FileBean> getFileByParentId(Context context,int parentId,OnFileListener onFileListener){
        List<FileBean> fileBeans = new ArrayList<> ();
        String selection = MediaStore.Files.FileColumns.PARENT + "= ?";
        String selectionArgs[] = {""+parentId};
        Cursor cursor = context.getContentResolver ().query(
                EXTERNAL_STORAGE_URI_FILES,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.MIME_TYPE);
        while (cursor.moveToNext ()){
            FileBean fileBean = parseToMediaBean (cursor);
            fileBeans.add (fileBean);
            if(onFileListener!=null){
                onFileListener.getFile (cursor);
            }
        }
        return fileBeans;
    }
    public static FileBean findById(Context context,int id){
        String selection = MediaStore.Files.FileColumns._ID + "= ?";
        String selectionArgs[] = {""+id};
        Cursor cursor = context.getContentResolver ().query(
                EXTERNAL_STORAGE_URI_FILES,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATA);
        while (cursor.moveToNext ()){
            FileBean fileBean = parseToMediaBean (cursor);
            return fileBean;
        }
        return null;
    }
    public interface OnFileListener{
        void getFile(Cursor cursor);
    }
    private static String projection[] = {
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,//文件大小
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
    };
    private static FileBean parseToMediaBean(Cursor mCursor){
        int mediaDataIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        // 获取图片的路径
        if(mediaDataIndex==-1){
            return null;
        }
        Integer parent = mCursor.getInt (mCursor.getColumnIndex (MediaStore.Files.FileColumns.PARENT));
        Integer id = mCursor.getInt (mCursor.getColumnIndex (MediaStore.Files.FileColumns._ID));
        String type = mCursor.getString (mCursor.getColumnIndex (MediaStore.Files.FileColumns.MIME_TYPE));
        String path = mCursor.getString (mCursor.getColumnIndex (MediaStore.Files.FileColumns.DATA));
        int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE))/1024;
        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
        Date createDate = new Date (mCursor.getLong (mCursor.getColumnIndex (MediaStore.Files.FileColumns.DATE_ADDED)));
        Date updateDate = new Date (mCursor.getLong (mCursor.getColumnIndex (MediaStore.Files.FileColumns.DATE_MODIFIED)));
        int width =  mCursor.getInt(mCursor.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
        int height =  mCursor.getInt(mCursor.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT));
        FileBean.Type beanType = null;
        if(type!=null){
            if(type.indexOf ("image")!=-1){
                beanType = FileBean.Type.Image;
            }else if(type.indexOf ("video")!=-1){
                beanType = FileBean.Type.Video;
            }
        }
        if(beanType==null){
            beanType = FileBean.Type.File;
        }
        FileBean fileBean = new FileBean (beanType,path,size,displayName);
        fileBean.setId (id);
        fileBean.setParentId (parent);
        fileBean.setCreateTime (createDate);
        fileBean.setUpdateTime (updateDate);
        fileBean.setWidth (width);
        fileBean.setHeight (height);
        return fileBean;

    }
}
