package com.sun.mycutfileupload.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: MyCutFileUpload
 * @Package: com.sun.mycutfileupload.entity
 * @ClassName: UploadEntity
 * @Author: littletree
 * @CreateDate: 2020/9/16/016 15:04
 */
public class UploadEntity implements Serializable {

    /**
     * success : true
     * message : null
     * code : 200
     * data : {"sourceId":0,"fileExists":false,"chunkList":[0,1,2,3]}
     * timeStamp : 1631784297939
     */

    private boolean success;
    private String message;
    private String code;
    /**
     * sourceId : 0
     * fileExists : false
     * chunkList : [0,1,2,3]
     */

    private DataBean data;
    private long timeStamp;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public static class DataBean {
        private int sourceId;
        private int commonSourceId;
        private boolean fileExists;
        private List<Integer> chunkList;

        public int getSourceId() {
            return sourceId;
        }

        public void setSourceId(int sourceId) {
            this.sourceId = sourceId;
        }

        public int getCommonSourceId() {
            return commonSourceId;
        }

        public void setCommonSourceId(int commonSourceId) {
            this.commonSourceId = commonSourceId;
        }

        public boolean isFileExists() {
            return fileExists;
        }

        public void setFileExists(boolean fileExists) {
            this.fileExists = fileExists;
        }

        public List<Integer> getChunkList() {
            return chunkList;
        }

        public void setChunkList(List<Integer> chunkList) {
            this.chunkList = chunkList;
        }
    }
}
