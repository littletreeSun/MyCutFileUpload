package com.sun.mycutfileupload.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: MyCutFileUpload
 * @Package: com.sun.mycutfileupload.utils
 * @ClassName: FileCutUtils
 * @Author: littletree
 * @CreateDate: 2020/9/16/016 14:30
 */
public class FileCutUtils {
    private List<File> littlefilelist=new ArrayList<>();
    String FileCathePath = Environment.getExternalStorageDirectory() + File.separator + "cutlittlefile";   //切片视频切割后缓存地址

    /**
     * 文件分割方法
     * @param targetFile 分割的文件
     * @param cutSize 分割文件的大小
     * @return int 文件切割的个数
     */
    public int getSplitFile(File targetFile, long cutSize) {

        //计算切割文件大小
        int count = targetFile.length() % cutSize == 0 ? (int) (targetFile.length() / cutSize) :
                (int) (targetFile.length() / cutSize + 1);

        RandomAccessFile raf = null;
        try {
            //获取目标文件 预分配文件所占的空间 在磁盘中创建一个指定大小的文件   r 是只读
            raf = new RandomAccessFile(targetFile, "r");
            long length = raf.length();//文件的总长度
            long maxSize = cutSize;//文件切片后的长度
            long offSet = 0L;//初始化偏移量

            for (int i = 0; i < count - 1; i++) { //最后一片单独处理
                long begin = offSet;
                long end = (i + 1) * maxSize;
                offSet = getWrite(targetFile.getAbsolutePath(), i, begin, end);
            }
            if (length - offSet > 0) {
                getWrite(targetFile.getAbsolutePath(), count-1, offSet, length);
            }

        } catch (FileNotFoundException e) {
//            System.out.println("没有找到文件");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
    /**
     * 指定文件每一份的边界，写入不同文件中
     * @param file 源文件地址
     * @param index 源文件的顺序标识
     * @param begin 开始指针的位置
     * @param end 结束指针的位置
     * @return long
     */
    public long getWrite(String file,int index,long begin,long end ){

        long endPointer = 0L;

        String a=file.split(suffixName(new File(file)))[0];

        try {
            //申明文件切割后的文件磁盘
            RandomAccessFile in = new RandomAccessFile(new File(file), "r");
            //定义一个可读，可写的文件并且后缀名为.tmp的二进制文件
            //判断文件夹是否存在,如果不存在则创建文件夹
            createFileFolder(FileCathePath);
            //读取切片文件
            File mFile = new File(FileCathePath + File.separator + "zdb_file" + "_" + index + file.substring(file.lastIndexOf(".")));
            littlefilelist.add(mFile);
            //如果存在
            if (!isFileExist(mFile)) {
                RandomAccessFile out = new RandomAccessFile(mFile, "rw");
                //申明具体每一文件的字节数组
                byte[] b = new byte[1024];
                int n = 0;
                //从指定位置读取文件字节流
                in.seek(begin);
                //判断文件流读取的边界
                while ((n = in.read(b)) != -1 && in.getFilePointer() <= end) {
                    //从指定每一份文件的范围，写入不同的文件
                    out.write(b, 0, n);
                }

                //定义当前读取文件的指针
                endPointer = in.getFilePointer();
                //关闭输入流
                in.close();
                //关闭输出流
                out.close();
            }else {
                //不存在

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endPointer - 1024;
    }

    /**
     * 获取文件后缀名 例如：.mp4 /.jpg /.apk
     * @param file 指定文件
     * @return String 文件后缀名
     */
    public static String suffixName (File file){
        String fileName=file.getName();
        String fileTyle=fileName.substring(fileName.lastIndexOf("."),fileName.length());
        return fileTyle;
    }

    /**
     *
     * @param path 文件夹路径
     */
    public void createFileFolder(String path) {
        File file = new File(path);
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            file.mkdir();
        }
    }

    // 判断文件是否存在
    public static boolean isFileExist(File file) {
        return file.exists();
    }

    public void deleteLittlelist() {
        if (littlefilelist!=null&&littlefilelist.size()>0){
            littlefilelist.clear();
        }
        deleteDirWihtFile(new File(FileCathePath));
    }

    public List<File> getLittlefilelist() {
        return littlefilelist;
    }

    /**
     * 删除文件
     *
     * @param dir
     */
    public void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
}
