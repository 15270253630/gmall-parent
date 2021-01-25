package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.FileUploadService;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${nginx.fastdfs}")
    private String nginx;

    @Override
    public String fileUpload(MultipartFile file) {
        String urlPath = nginx;
        String conf_filename = FileUploadServiceImpl.class.getClassLoader().getResource("fdfs.conf").getPath();
        try {
            ClientGlobal.init(conf_filename);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(connection, null);

            String filename = file.getOriginalFilename();
            String extName = filename.substring(filename.lastIndexOf(".") + 1);
            String[] strings = storageClient.upload_file( file.getBytes(),extName,null);


            for (String string : strings) {
                urlPath += "/" + string;
                System.out.println(string);
            }

            System.out.println(urlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlPath;
    }
}
