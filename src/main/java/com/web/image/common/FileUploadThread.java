package com.web.image.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2017/6/29.
 */
public class FileUploadThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadThread.class);

    private MultipartFile file;
    private List<Map<String, String>> returnList;
    private String fileFolder;
    private String uploadUrl;
    private List<Map<String, Object>> imageTypesList;

    public FileUploadThread(MultipartFile file, List<Map<String, String>> returnList, String fileFolder, String uploadUrl, List<Map<String, Object>> imageTypesList) {
        this.file = file;
        this.returnList = returnList;
        this.fileFolder = fileFolder;
        this.uploadUrl = uploadUrl;
        this.imageTypesList = imageTypesList;
    }

    @Override
    public void run() {
        try {
            String fileName = "";
            String fileType = file.getOriginalFilename().split("\\.")[1];

            //校验文件格式
            boolean flag = false;
            for (Map<String, Object> imageType : imageTypesList) {
                if (fileType.equals(imageType.get("value"))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                LOGGER.info("saveMultipartFile error : " + file.getOriginalFilename() + "格式不正确！");
                return;
            }
//            Map<String, String> paramMap = new HashMap<>();
//            paramMap.put("value", fileType);
//            List<Map<String, Object>> imageTypesList = dImageTypesDao.queryImageTypes(paramMap);
//            if (imageTypesList == null || imageTypesList.size() == 0) {
//                LOGGER.info("saveMultipartFile error : " + file.getOriginalFilename() + "格式不正确！");
//                return;
//            }

            fileName = fileFolder + UUID.randomUUID() + "." + fileType;
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
            stream.write(bytes);
            stream.close();

            LOGGER.info("文件目录:" + fileName);
            if (fileType.equals("zip")) {
                //解压zip包
                returnList = getZipFile(fileName, fileFolder);
            } else {
                //去掉文件目录
                fileName = fileName.replace(uploadUrl, "");

                Map<String, String> rtn = new HashMap<>();
                rtn.put("fileName", fileName);
                rtn.put("fileType", fileType);
                returnList.add(rtn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<Map<String, String>> getZipFile(String fileUrl, String fileFolder) {
        ZipUtil.unZip(fileUrl);
        //
        String filePath = fileUrl.substring(0, fileUrl.lastIndexOf(".")) + File.separator;
        File file = new File(fileUrl);
        String zipName = file.getName();
        List<Map<String, String>> returnList = new ArrayList<>();
        this.readFiles(filePath, zipName, fileFolder, returnList);
        return returnList;
    }

    public void readFiles(String filePath, String zipName, String fileFolder, List<Map<String, String>> returnList) {
        String fileName = "";
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                readFiles(file2.getAbsolutePath(), zipName, fileFolder, returnList);
            } else {
                //TODO 判断文件类型

                Map<String, String> rtn = new HashMap<>();
                String path = file2.getAbsolutePath();
                path = path.replace(fileFolder, "");
                rtn.put("fileName", path);
                rtn.put("fileType", file2.getName().split("\\.")[1]);
                rtn.put("bak", zipName);
                returnList.add(rtn);
            }
        }
    }
}
