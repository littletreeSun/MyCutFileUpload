package com.sun.mycutfileupload.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName: MyCutFileUpload
 * @Package: com.sun.mycutfileupload.entity
 * @ClassName: CheckFileEntity
 * @Author: littletree
 * @CreateDate: 2020/9/16/016 15:03
 */
public class CheckFileEntity implements Serializable {

    /**
     * code : 200
     * data : {"chunkList":[],"fileExists":false,"sourceId":0}
     * success : true
     * timeStamp : 1584342078256
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * chunkList : []
         * fileExists : false
         * sourceId : 0
         */

        private boolean fileExists;
        private int sourceId;
        private String sourcePath;
        private String resolution;
        private String commonSourceId;
        private String thumb;
        private List<Integer> chunkList;

        public String getSourcePath() {
            return sourcePath;
        }

        public void setSourcePath(String sourcePath) {
            this.sourcePath = sourcePath;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

        public String getCommonSourceId() {
            return commonSourceId;
        }

        public void setCommonSourceId(String commonSourceId) {
            this.commonSourceId = commonSourceId;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public boolean isFileExists() {
            return fileExists;
        }

        public void setFileExists(boolean fileExists) {
            this.fileExists = fileExists;
        }

        public int getSourceId() {
            return sourceId;
        }

        public void setSourceId(int sourceId) {
            this.sourceId = sourceId;
        }

        public List<Integer> getChunkList() {
            return chunkList;
        }

        public void setChunkList(List<Integer> chunkList) {
            this.chunkList = chunkList;
        }
    }
}
