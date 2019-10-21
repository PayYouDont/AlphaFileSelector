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
import com.payudn.selector.entity.MediaBean;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class FileUtil {
    private static final Uri EXTERNAL_STORAGE_URI_FILES = MediaStore.Files.getContentUri("external");
    public static Bitmap parseToBitmap(Context context, MediaBean imageBean, int outWidth, int outHeight, int radius, int boarder){
        try {
            Bitmap bitmap;
            if(!imageBean.getPath ().equals ("")){
                FileInputStream in = new FileInputStream(imageBean.getPath ());
                bitmap = BitmapFactory.decodeStream(in);
            }else{
                Drawable drawable = context.getDrawable (R.drawable.image_empty);
                BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
                bitmap = bitmapDrawable.getBitmap ();
            }
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
        }catch (Exception e){
            Log.e ("FileUtil",e.getMessage (),e);
        }
        return null;
    }
    public static List<File> getRootFile(Context context,OnFileListener onFileListener){
        return getFileByParentId (context,0,onFileListener);
    }
    public static List<File> getFileByParentId(Context context,int parentId,OnFileListener onFileListener){
        List<File> fileList = new ArrayList<> ();
        String projection[] = {
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATA,
        };
        String selection = MediaStore.Files.FileColumns.PARENT + "= ?";
        String selectionArgs[] = {""+parentId};
        Cursor cursor = context.getContentResolver ().query(EXTERNAL_STORAGE_URI_FILES, projection, selection, selectionArgs, null);
        while (cursor.moveToNext ()){
            String path = cursor.getString (cursor.getColumnIndex (MediaStore.Files.FileColumns.DATA));
            File file = new File (path);
            fileList.add (file);
            if(onFileListener!=null){
                onFileListener.getFile (cursor);
            }
        }
        return fileList;
    }
    public interface OnFileListener{
        void getFile(Cursor cursor);
    }
}
