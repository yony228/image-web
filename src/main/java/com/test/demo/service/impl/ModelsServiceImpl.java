package com.test.demo.service.impl;

import com.test.demo.common.FileWebConf;
import com.test.demo.common.PageUtil;
import com.test.demo.dao.ModelsDao;
import com.test.demo.dao.TrainsDao;
import com.test.demo.service.ModelsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ModelsServiceImpl implements ModelsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelsServiceImpl.class);

    @Autowired
    private ModelsDao modelsDao;

    @Autowired
    private TrainsDao trainsDao;

    @Autowired
    private FileWebConf fileWebConf;

    @Override
    public List<Map<String, Object>> queryModels(Map<String, Object> paramMap) {
        Map pageInfo = PageUtil.getPage(paramMap);
        return modelsDao.queryModels(paramMap, pageInfo);
    }

    @Override
    public int addModels(Map<String, Object> param) {
        //检查是否重名
        if (checkSameModels(param.get("name").toString(), null)) {
            return modelsDao.save(param);
        }
        return 0;
    }

    @Override
    public boolean updateModels(Map<String, Object> param) {
        //检查是否重名
        if (checkSameModels(param.get("name").toString(), param.get("id").toString())) {
            modelsDao.update(param);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateModelStatus(int modelId, int status) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", modelId);
        param.put("status", status);
        modelsDao.update(param);
        return true;
    }

    @Override
    public boolean checkSameModels(String name, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("not_id", id);
        List<Map<String, Object>> list = modelsDao.queryModels(map, null);
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }

    @Override
    public int queryClassCountByModelId(int modelsId) {
        return modelsDao.queryClassCountByModelId(modelsId);
    }


    /**
     * 模型上线
     *
     * @param modelId
     * @param status
     * @return
     */
    @Override
    @Transactional
    public boolean upModels(String modelId, int status) {
        //TODO 判断文件是否存在
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Map<String, Object> trainsMap = trainsDao.queryTrainsByModelId(modelId);
        String parentPath = fileWebConf.getTrainModelsUrl() + trainsMap.get("train_no");
        String linkUrl = fileWebConf.getModelOnlineUrl() + sf.format(new Date());

        LOGGER.info("parentPath:=========" + parentPath);
        File file = new File(parentPath);
        File f[] = file.listFiles();
        if (f == null) {
            LOGGER.error("ERROR:==========找不到训练文件.");
            return false;
        }
        String targetUrl = parentPath + "/" + f[0].getName();
        try {
            //创建文件软连接
            Path fileTarget = Paths.get(targetUrl);
            Path fileLink1 = Paths.get(linkUrl);
            Files.createSymbolicLink(fileLink1, fileTarget);
        } catch (Exception e) {
            LOGGER.error("ERROR:=========" + e.getMessage());
        }


        //修改数据
        this.updateModelStatus(Integer.parseInt(modelId), status);

        //修改其他模型为下线状态
        modelsDao.updateStatus(modelId);

        //修改训练表
        Map paramMap = new HashMap();
        paramMap.put("model_url", linkUrl);
        trainsDao.updateTrainsByTrainNo(paramMap, trainsMap.get("train_no").toString());

        return true;
    }
}
