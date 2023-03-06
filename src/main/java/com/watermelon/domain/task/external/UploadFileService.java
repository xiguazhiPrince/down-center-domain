package com.watermelon.domain.task.external;

/**
 * 上传文件
 */
public interface UploadFileService {


    /**
     * 上传文件
     * @param filePath 本地文件地址
     * @return s3 url
     */
    String uploadFile(String filePath);
}
