package com.sun.mycutfileupload;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.sun.mycutfileupload.utils.ChooseVideoUtils;
import com.sun.mycutfileupload.utils.UploadFileUtils;
import java.util.List;


/**
 * @ProjectName: MyCutFileUpload
 * @Package: com.sun.mycutfileupload
 * @ClassName: CutFileUploadActivity
 * @Author: littletree
 * @CreateDate: 2020/9/16/016 11:28
 */
public class CutFileUploadActivity extends AppCompatActivity {
    RelativeLayout rl_video;
    ImageView iv_video_add;
    ImageView iv_video;
    ImageView iv_play;
    Button btn_choosevideo;
    Button btn_uploadvideo;

    String mSelectType;

    String videopath = "";
    String videoname;

    SVProgressHUD mSvp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutfileupload);
        initview();
    }

    private void initview(){
        rl_video = findViewById(R.id.rl_video);
        iv_video_add = findViewById(R.id.iv_video_add);
        iv_video = findViewById(R.id.iv_video);
        iv_play = findViewById(R.id.iv_play);
        btn_choosevideo = findViewById(R.id.btn_choosevideo);
        btn_uploadvideo = findViewById(R.id.btn_uploadvideo);

        btn_choosevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosevideo();
            }
        });

        btn_uploadvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(videopath)){
                    cutfileupload();
                }else {
                    Toast.makeText(CutFileUploadActivity.this, "请先选择视频", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cutfileupload(){
        UploadFileUtils.with(this)
                .loadFile(videopath,videoname)
                .setBustype("mbustype")
                .setUpLoadListener(new UploadFileUtils.OnUpLoadListener() {
                    @Override
                    public void start() {
                        showTip("上传中");
                    }

                    @Override
                    public void onUpload(int currentnum, int allnum) {
                        showTip(String.format("%s%d%s", "上传",currentnum * 100 / allnum, "%"));
                    }

                    @Override
                    public void onComplete(int filesourceId) {
                        showTip("上传100%");
                        //获取id，然后自己可以做处理
                    }

                    @Override
                    public void onUploadFailed(String errormessage) {
                        dismissTip();

                    }
                }).launch();
    }

    private void choosevideo(){
        XXPermissions.with(this)
                .permission(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> list, boolean b) {
                        //选择视频
                        mSelectType = "video";
                        ChooseVideoUtils.VideoShoot(CutFileUploadActivity.this);
                    }

                    @Override
                    public void noPermission(List<String> list, boolean b) {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            if ("video".equals(mSelectType)) {
                List<LocalMedia> mLocalMediaVideo = PictureSelector.obtainMultipleResult(data);

                if (!TextUtils.isEmpty(mLocalMediaVideo.get(0).getRealPath())) {
                    videopath = mLocalMediaVideo.get(0).getRealPath();
                } else {
                    videopath = mLocalMediaVideo.get(0).getPath();
                }
                if (!TextUtils.isEmpty(mLocalMediaVideo.get(0).getFileName())) {
                    videoname = mLocalMediaVideo.get(0).getFileName();
                } else {
                    videoname = System.currentTimeMillis() + ".mp4";
                }

                if (!TextUtils.isEmpty(videopath)) {
                    //显示本地视频封面
                    Glide.with(CutFileUploadActivity.this).load(videopath).into(iv_video);
                    iv_video.setVisibility(View.VISIBLE);
                    iv_play.setVisibility(View.VISIBLE);
                    iv_video_add.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, "获取视频失败", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }

    /**
     * 显示加载框
     */
    public void showTip(String txt) {
        Log.i("孙", "显示框: "+txt);
        if (null == mSvp) {
            mSvp = new SVProgressHUD(this);
        }
        if (!mSvp.isShowing()) {
            mSvp.showWithStatus("上传中");
        }else {
            mSvp.setText(txt);
        }
    }

    /**
     * 隐藏加载框
     */
    public void dismissTip() {
        if (null == mSvp) {
            mSvp = new SVProgressHUD(this);
        }

        if (mSvp.isShowing()) {
            mSvp.dismiss();
        }
    }
}
