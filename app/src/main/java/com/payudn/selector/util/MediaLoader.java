package com.payudn.selector.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.payudn.selector.entity.FileBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import lombok.Setter;

public class MediaLoader {
    public static Queue<FileBean> mediaBeanQueue = new LinkedList<> ();
    private Context context;
    @Setter
    private onLoadDataListener onLoadDataListener;
    private static final String[] projImage = {
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.DATA,// 文件路径
            MediaStore.Images.Media.SIZE,//文件大小
            MediaStore.Images.Media.DISPLAY_NAME,// 文件名
            MediaStore.Images.Media.DATE_ADDED, //添加时间
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_MODIFIED};// 最后一次修改时间
    private static final String[] projThumbnailsImage = {
            MediaStore.Images.Thumbnails._ID, // id
            MediaStore.Images.Thumbnails.DATA,// 文件路径
            MediaStore.Images.Thumbnails.IMAGE_ID,//图片id
            MediaStore.Images.Thumbnails.KIND, //种类
            MediaStore.Images.Thumbnails.WIDTH,
            MediaStore.Images.Thumbnails.HEIGHT};
    private static final String[] projVideo = {
            MediaStore.Video.Thumbnails._ID,
            MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Video.Media.DATE_MODIFIED};

    public MediaLoader(Context context) {
        this.context = context;
    }

    public static FileBean getMediaByUri(Uri uri, ContentResolver contentResolver){
        if (!uri.toString().contains("image")) {
            return null;
        }
        Cursor mCursor = contentResolver.query(uri, projImage,null, null, null);
        if (mCursor == null) {
            return null;
        }
        mCursor.moveToLast();    // 倒序读取
        return parseToMediaBean (mCursor);
    }
    private static FileBean parseToMediaBean(Cursor mCursor){
        int mediaDataIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        // 获取图片的路径
        if(mediaDataIndex==-1){
            return null;
        }
        String path = mCursor.getString(mediaDataIndex);
        int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE))/1024;
        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        Date createDate = new Date (mCursor.getLong (mCursor.getColumnIndex (MediaStore.Images.Media.DATE_ADDED)));
        Date updateDate = new Date (mCursor.getLong (mCursor.getColumnIndex (MediaStore.Images.Media.DATE_MODIFIED)));
        int width =  mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
        int height =  mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
        FileBean mediaBean = new FileBean (FileBean.Type.Image,path,size,displayName);
        mediaBean.setCreateTime (createDate);
        mediaBean.setUpdateTime (updateDate);
        mediaBean.setWidth (width);
        mediaBean.setHeight (height);
        return mediaBean;

    }
    public static Integer getPictureCount(Context context,int startDay) {
        Date now = new Date ();
        Date dayAgo = new Date (now.getTime ()-startDay*24*60*60*1000);
        return getPictureCount (context,dayAgo.getTime (),now.getTime ());
    }
    public static Integer getPictureCount(Context context,Long startTime,Long endTime) {
        String start = null;
        if(startTime!=null){
            start = startTime.toString ().substring (0,startTime.toString ().length ()-3);
        }
        String end = null;
        if(endTime!=null){
            end = endTime.toString ().substring (0,endTime.toString ().length ()-3);
        }
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = "";
        String selectionArgs = "";
        if(start!=null){
            selection += "(("+MediaStore.Images.Media.DATE_ADDED + ">? or ";
            selectionArgs += start+",";
            selection += MediaStore.Images.Media.DATE_MODIFIED + ">? ) and (";
            selectionArgs += start+",";
        }
        if(end!=null){
            selection += MediaStore.Images.Media.DATE_ADDED + "<? or ";
            selectionArgs += end+",";
            selection +=  MediaStore.Images.Media.DATE_MODIFIED + "<? )) and ";
            selectionArgs += end+",";
        }
        selection += MediaStore.Images.Media.MIME_TYPE + " like ?";
        selectionArgs += "image%";
        Cursor mCursor = context.getContentResolver().query(mImageUri,projImage,selection,selectionArgs.split (","), MediaStore.Images.Media.DATE_MODIFIED+" desc");
        return mCursor.getCount ();
    }
    public static List<FileBean> getMediaBeans(Context context) {
        List<FileBean> mediaBeanList = new ArrayList<> ();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor mCursor = context.getContentResolver().query(mImageUri,projImage, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED+" desc");
        if(mCursor!=null){
            while (mCursor.moveToNext()) {
                mediaBeanList.add (parseToMediaBean (mCursor));
            }
            mCursor.close();
        }
        return mediaBeanList;
    }
    /**
     * 读取手机中所有图片信息
     */
    public void getAllPhotoInfo() {
        new Thread (() -> {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor mCursor = context.getContentResolver().query(mImageUri,projImage, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED+" desc");
            if(mCursor!=null){
                while (mCursor.moveToNext()) {
                    FileBean mediaBean = parseToMediaBean (mCursor);
                    mediaBeanQueue.offer (mediaBean);
                    if(onLoadDataListener!=null){
                        onLoadDataListener.loadData (mediaBean);
                    }
                }
                mCursor.close();
            }}).start();
    }
    public Map<Date,Integer> getPhotoCountOrderByDay(List<Date> dateList) {
        Map<Date,Integer> dateMap = new LinkedHashMap<> ();
        int sum = 0;
        int datdCount = getPictureCount (context,dateList.size ());
        for(int i=0;i<dateList.size ()-1;i++){
            Date startDate = dateList.get (i+1);
            Date endDate = dateList.get (i);
            int count = getPictureCount (context,startDate.getTime (),endDate.getTime ());
            if(count>0){
                sum += count;
                dateMap.put (startDate,count);
            }
            if(sum==datdCount){
                break;
            }
        }
        return dateMap;
    }
    /**
     * 获取手机中所有视频的信息
     */
    private void getAllVideoInfos(){
        new Thread (() -> {
            HashMap<String, List<FileBean>> allPhotosTemp = new HashMap<> ();//所有照片
            Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            Cursor mCursor = context.getContentResolver().query(mImageUri,
                    projVideo,
                    MediaStore.Video.Media.MIME_TYPE + "=?",
                    new String[]{"video/mp4"},
                    MediaStore.Video.Media.DATE_MODIFIED+" desc");
            if(mCursor!=null){
                while (mCursor.moveToNext()) {
                    // 获取视频的路径
                    int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE))/1024; //单位kb
                    if(size<0){
                        //某些设备获取size<0，直接计算
                        Log.e("dml","this video size < 0 " + path);
                        size = new File (path).length()/1024;
                    }
                    String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    long modifyTime = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));//暂未用到

                    //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                    MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                    String[] projection = { MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                    Cursor cursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                            , projection
                            , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                            , new String[]{videoId+""}
                            , null);
                    String thumbPath = "";
                    while (cursor.moveToNext()){
                        thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    }
                    cursor.close();
                    // 获取该视频的父路径名
                    String dirPath = new File (path).getParentFile().getAbsolutePath();
                    FileBean mediaBean = new FileBean (FileBean.Type.Video,path,size,displayName,thumbPath,duration);
                    //存储对应关系
                    if (allPhotosTemp.containsKey(dirPath)) {
                        List<FileBean> data = allPhotosTemp.get(dirPath);
                        data.add(mediaBean);
                        continue;
                    } else {
                        List<FileBean> data = new ArrayList<> ();
                        data.add(mediaBean);
                        allPhotosTemp.put(dirPath,data);
                    }
                }
                mCursor.close();
            }
            //更新界面
               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //...
                    }
                });*/
        }).start();
    }
    public interface onLoadDataListener{
        void loadData(FileBean mediaBean);
    }
}
