# MyCutFileUpload
分片校验上传工具类
使用须知

1.因为涉及到上传接口，使用者需在myapplication中填写token以及域名

2.在秒传接口以及上传接口填写详细接口名才能使用

3.如果接口存在差异，请自行修改接口入参以及返回的实体类，项目中接口使用的是字符串类型的code值，正确即为200

4.使用方式比较简单

```
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
```
