package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.product.service.FileUploadService;
import com.atguigu.gmall.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/admin/product")
public class FileUploadController {
    @Autowired
    public FileUploadService fileUploadService;

    @ApiOperation("SPU图片上传")
    @PostMapping("/fileUpload")
    public Result<String> fileUpload(MultipartFile file){

        String urlPath = fileUploadService.fileUpload(file);

        return Result.ok(urlPath);
    }
}
