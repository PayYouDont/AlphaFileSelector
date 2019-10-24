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
import android.graphics.PixelFormat;
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
    public static Bitmap parseToBitmap(Context context,String filePath, int outWidth, int outHeight, int radius, int boarder){
        try {
            //创建输出的bitmap
            Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
            //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
            Canvas canvas = new Canvas(desBitmap);
            RectF rect = new RectF(boarder, boarder, outWidth - boarder, outHeight - boarder);
            Bitmap bitmap;
            if(!filePath.equals ("")){
                FileInputStream in = new FileInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(in);
                if(bitmap == null){
                    bitmap =  createBitmapByPath (context,filePath);
                }
            }else{
                Drawable drawable = context.getDrawable (R.drawable.image_empty);
                bitmap = crateByDrawable (drawable);
            }
            if(bitmap!=null){
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float widthScale = outWidth * 1f / width;
                float heightScale = outHeight * 1f / height;
                Matrix matrix = new Matrix();
                matrix.setScale(widthScale, heightScale);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                //创建着色器
                BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                //给着色器配置matrix
                bitmapShader.setLocalMatrix(matrix);
                paint.setShader(bitmapShader);
                //创建矩形区域并且预留出border
                //把传入的bitmap绘制到圆角矩形区域内
                canvas.drawRoundRect(rect, radius, radius, paint);
            }
            if (boarder > 0) {
                //绘制boarder
                Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                boarderPaint.setColor(Color.parseColor("#7C7C7C"));
                boarderPaint.setStyle(Paint.Style.STROKE);
                boarderPaint.setStrokeWidth(boarder);
                canvas.drawRoundRect(rect, radius, radius, boarderPaint);
            }
            return desBitmap;
        }catch (Exception e){
            Log.e ("FileUtil",e.getMessage (),e);
        }
        return null;
    }
    public static List<FileBean> getRootFile(Context context,OnFileListener onFileListener){
        return getFileByParentId (context,0,onFileListener);
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
    private static final Uri EXTERNAL_STORAGE_URI_FILES = MediaStore.Files.getContentUri("external");
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
            if(onFileListener!=null){
                onFileListener.getFile (cursor);
            }
            FileBean fileBean = parseToMediaBean (cursor);
            fileBeans.add (fileBean);
        }
        return fileBeans;
    }
    /**
    * @Author peiyongdong
    * @Description ( 根据id查询 )
    * @Date 16:16 2019/10/23
    * @Param [context, id]
    * @return com.payudn.selector.entity.FileBean
    **/
    public static FileBean findById(Context context,int id){
        String selection = MediaStore.Files.FileColumns._ID + "= ?";
        String selectionArgs[] = {""+id};
        Cursor cursor = context.getContentResolver ().query(
                EXTERNAL_STORAGE_URI_FILES,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.MIME_TYPE + " asc , "+ MediaStore.Files.FileColumns.DATA + " asc");
        while (cursor.moveToNext ()){
            FileBean fileBean = parseToMediaBean (cursor);
            return fileBean;
        }
        return null;
    }
    public interface OnFileListener{
        void getFile(Cursor cursor);
    }
    /**
    * @Author peiyongdong
    * @Description ( 将查询结果解析成FileBean )
    * @Date 16:15 2019/10/23
    * @Param [mCursor]
    * @return com.payudn.selector.entity.FileBean
    **/
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
        if(mCursor.getColumnIndex (MediaStore.Files.FileColumns.DATE_ADDED)!=-1){
            Date createDate = new Date (mCursor.getLong (mCursor.getColumnIndex (MediaStore.Files.FileColumns.DATE_ADDED))*1000);
            fileBean.setCreateTime (createDate);
        }
        if(mCursor.getColumnIndex (MediaStore.Files.FileColumns.DATE_MODIFIED)!=-1){
            Date updateDate = new Date (mCursor.getLong (mCursor.getColumnIndex (MediaStore.Files.FileColumns.DATE_MODIFIED))*1000);
            fileBean.setUpdateTime (updateDate);
        }
        fileBean.setWidth (width);
        fileBean.setHeight (height);
        return fileBean;
    }
    public static List<FileBean> findByParam(Context context, String selection,String[] selectionArgs){
        List<FileBean> fileBeans = new ArrayList<> ();
        Cursor cursor = context.getContentResolver ().query(
                EXTERNAL_STORAGE_URI_FILES,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");
        while (cursor.moveToNext ()){
            FileBean fileBean = parseToMediaBean (cursor);
            fileBeans.add (fileBean);
        }
        return fileBeans;
    }
    /**文档类型*/

    public static final int TYPE_DOC = 0;

    /**apk类型*/
    public static final int TYPE_APK = 1;

    /**压缩包类型*/
    public static final int TYPE_ZIP = 2;
    public static int getFileType(String path) {
        path= path.toLowerCase();
        if( path.endsWith( ".doc") || path.endsWith( ".docx") ||
                path.endsWith( ".xls") || path.endsWith( ".xlsx")||
                path.endsWith( ".ppt") || path.endsWith( ".pptx") ||
                path.endsWith (".pdf") || path.endsWith (".ppt") ||
                path.endsWith (".txt")) {
            return TYPE_DOC;
        } else if( path.endsWith( ".apk")) {
            return TYPE_APK;
        } else if( path.endsWith( ".zip") || path.endsWith( ".rar") || path.endsWith( ".tar") || path.endsWith( ".gz")) {
            return TYPE_ZIP;
        } else{
            return-1;
        }

    }
    public static List<FileBean> getFilesByType(Context context,int fileType) {
        List<FileBean> fileBeans = new ArrayList<> ();
        Cursor cursor = context.getContentResolver ().query(
                EXTERNAL_STORAGE_URI_FILES,
                projection,
                null,
                null,
                MediaStore.Files.FileColumns.MIME_TYPE);
        while (cursor.moveToNext ()){
            String path = cursor.getString (cursor.getColumnIndex (MediaStore.Files.FileColumns.DATA));
            if(getFileType (path)!=fileType){
                continue;
            }
            fileBeans.add (parseToMediaBean (cursor));
        }
        return fileBeans;
    }
    public static Bitmap createBitmapByPath(Context context,String path){
        Bitmap bitmap = null;
        String suffix = path.substring (path.lastIndexOf ("."));
        int resourceId = getDrawResourceId (suffix);
        if(resourceId!=-1){
            Drawable drawable = context.getDrawable (resourceId);
            bitmap = crateByDrawable (drawable);
        }
        return bitmap;
    }
    private static int getDrawResourceId(String suffix){
        if(suffix.indexOf ("doc")!=-1){
            return R.drawable.ic_file_doc;
        }else if(suffix.indexOf ("xls")!=-1){
            return R.drawable.ic_file_xls;
        }else if(suffix.indexOf ("ppt")!=-1){
            return R.drawable.ic_file_ppt;
        }else if(suffix.indexOf ("txt")!=-1){
            return R.drawable.ic_file_txt;
        }
        return -1;
    }
    private static Bitmap crateByDrawable(Drawable drawable){
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
}
