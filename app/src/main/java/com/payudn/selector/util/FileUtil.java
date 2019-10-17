package com.payudn.selector.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.payudn.selector.entity.MediaBean;

import java.io.FileInputStream;

public class FileUtil {
    public static Bitmap parseToBitmap(MediaBean imageBean,int dst_w, int dst_h){
        try {
            FileInputStream in = new FileInputStream(imageBean.getPath ());
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            int src_w = bitmap.getWidth();
            int src_h = bitmap.getHeight();
            float scale_w = ((float) dst_w) / src_w;
            float scale_h = ((float) dst_h) / src_h;
            Matrix matrix = new Matrix ();
            matrix.postScale(scale_w, scale_h);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,true);
            return bitmap;
        }catch (Exception e){
            Log.e ("FileUtil",e.getMessage (),e);
        }
        return null;
    }
}
