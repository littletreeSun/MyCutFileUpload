package com.sun.mycutfileupload.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.sun.mycutfileupload.entity.CheckFileEntity;
import com.sun.mycutfileupload.entity.UploadEntity;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.request.GetRequest;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @ProjectName: MyCutFileUpload
 * @Package: com.sun.mycutfileupload.utils
 * @ClassName: UploadFileUtils
 * @Author: littletree
 * @CreateDate: 2020/9/16/016 13:58
 */
public class UploadFileUtils {
    private String getfilepath;
    private String getfilename;
    private String getbustype;
    private Context mcontext;
    private String filemd5;

    private List<Integer> haveuploadfilenum;   //校验的返回的已上传的分片序号，已存在的分片无需再上传，可跳过循环

    private FileCutUtils fileCutUtils;  //文件切割工具类
    private int littlefilecount;  //切割文件个数
    private List<File> littlefilelist = new ArrayList<>();

    private OnUpLoadListener onUpLoadListener;

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private String getfilepath;
        private String getfilename;
        private String getbustype;
        private Context mcontext;
        private String filemd5;

        private List<Integer> haveuploadfilenum;

        private FileCutUtils fileCutUtils;  //文件切割工具类
        private int littlefilecount;  //切割文件个数
        private List<File> littlefilelist;

        private OnUpLoadListener onUpLoadListener;

        Builder(Context context) {
            this.mcontext = context;
            this.haveuploadfilenum = new ArrayList<>();
            this.littlefilelist = new ArrayList<>();
        }

        private UploadFileUtils build() {
            return new UploadFileUtils(this);
        }

        public Builder loadFile(String mfilepath, String mfilename) {
            this.getfilepath = mfilepath;
            this.getfilename = mfilename;
            return this;
        }

        public Builder setBustype(String mbustype) {
            this.getbustype = mbustype;
            return this;
        }

        public Builder setUpLoadListener(OnUpLoadListener onUpLoadListener) {
            this.onUpLoadListener = onUpLoadListener;
            return this;
        }

        public void launch() {
            build().start();
        }
    }

    private UploadFileUtils(Builder builder) {
        this.getfilepath = builder.getfilepath;
        this.getfilename = builder.getfilename;
        this.getbustype = builder.getbustype;
        this.mcontext = builder.mcontext;
        this.filemd5 = builder.filemd5;
        this.haveuploadfilenum = builder.haveuploadfilenum;
        this.fileCutUtils = builder.fileCutUtils;
        this.littlefilecount = builder.littlefilecount;
        this.littlefilelist = builder.littlefilelist;
        this.onUpLoadListener = builder.onUpLoadListener;
    }

    private void start(){
        if (null != onUpLoadListener) {
            onUpLoadListener.start();
        }
        new Thread() {
            @Override
            public void run() {
                filemd5 = MD5Util.getFileMD5(getfilepath);   //文件转md5耗时操作，需要在子线程执行
                mHandler.sendEmptyMessage(0x123);
            }
        }.start();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123){
                checkvideofile(getfilepath,getfilename,getbustype);
            }
        }
    };

    //秒传接口 校验文件
    private void checkvideofile(String mfilepath, String mfilename,String mbustype){
        EasyHttp.get("秒传接口")
                .params("busType", mbustype)   //文件类型 固定为doc
                .params("checksum", filemd5)   //文件 md5加密
                .params("fileName", mfilename)   //文件名
                .params("size", ((int) FileSizeUtil.getFileOrFilesSize(mfilepath, FileSizeUtil.SIZETYPE_B)) + "")
                .execute(new SimpleCallBack<CheckFileEntity>() {
                    @Override
                    public void onError(ApiException e) {
                        Toast.makeText(mcontext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(CheckFileEntity response) {
                        if (response.getData().isFileExists()){
                            if (null != onUpLoadListener) {
                                onUpLoadListener.onComplete(response.getData().getSourceId());
                            }
                        }else {
                            haveuploadfilenum = new ArrayList<>();
                            if (null!=response.getData().getChunkList()&&response.getData().getChunkList().size()>0){
                                haveuploadfilenum = response.getData().getChunkList();
                            }
                            cutfile(mfilepath);
                        }

                    }
                });
    }

    private void cutfile(String filePath){
        try {
            long mBufferSize = 1024 * 1024 * 2; //分片的大小，可自定义
            fileCutUtils = new FileCutUtils();
            littlefilecount = fileCutUtils.getSplitFile(new File(filePath), mBufferSize);
            littlefilelist = fileCutUtils.getLittlefilelist();

            uploadFile(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFile(int filenum) {
        boolean isRepeat = false;
        if (null!=haveuploadfilenum&&haveuploadfilenum.size()>0){   //之前传过的num就不用传
            for (int uploadfilenum:haveuploadfilenum){
                if (filenum == uploadfilenum){
                    isRepeat = true;
                }
            }
        }

        if (isRepeat&&null!=haveuploadfilenum&&haveuploadfilenum.size()>0){
            uploadFile(filenum+1);
        }else {
            EasyHttp.post("上传接口")
                    .params("busType", getbustype)   //上传类型
                    .params("checksum", filemd5)   //文件路径 md5加密
                    .params("chunk", String.valueOf(filenum))   //切片编号
                    .params("chunks", String.valueOf(littlefilecount))   //切片总数
                    .params("chunkSize", "分片的大小")   //分片大小
                    .params("file", littlefilelist.get(filenum), getfilename, null)
                    .execute(new SimpleCallBack<UploadEntity>() {
                        @Override
                        public void onError(ApiException e) {
                            if (null != onUpLoadListener) {
                                onUpLoadListener.onUploadFailed(e.getMessage());
                            }
                            getfilepath = "";
                            getfilename = "";
                            getbustype = "";
                            filemd5 = "";

                            littlefilecount = 0;
                            if (null!=haveuploadfilenum){
                                haveuploadfilenum.clear();
                            }
                            if (null!=littlefilelist){
                                littlefilelist.clear();
                            }

                            fileCutUtils.deleteLittlelist();
                        }

                        @Override
                        public void onSuccess(UploadEntity response) {
                            if (filenum<(littlefilecount-1)){
                                if (null != onUpLoadListener) {
                                    onUpLoadListener.onUpload(filenum+1,littlefilecount);
                                    uploadFile(filenum+1);
                                }
                            }else {   //上传完成
                                if (null != onUpLoadListener) {
                                    onUpLoadListener.onComplete(response.getData().getCommonSourceId());
                                }

                                getfilepath = "";
                                getfilename = "";
                                getbustype = "";
                                filemd5 = "";

                                littlefilecount = 0;
                                if (null!=haveuploadfilenum){
                                    haveuploadfilenum.clear();
                                }
                                if (null!=littlefilelist){
                                    littlefilelist.clear();
                                }

                                fileCutUtils.deleteLittlelist();
                            }
                        }
                    });
        }
    }

    public interface OnUpLoadListener {
        void start();
        void onUpload(int currentnum, int allnum);
        void onComplete(int filesourceId);
        void onUploadFailed(String errormessage);
    }

    public OnUpLoadListener getOnUpLoadListener() {
        return onUpLoadListener;
    }

    public void setOnUpLoadListener(OnUpLoadListener onUpLoadListener) {
        this.onUpLoadListener = onUpLoadListener;
    }
}
