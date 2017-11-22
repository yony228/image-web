package com.web.image.service.impl;

import com.web.image.common.FileWebConf;
import com.web.image.common.TrainClassStatus;
import com.web.image.dao.ITrainsDao;
import com.web.image.dao.IClassificationsDao;
import com.web.image.service.IModelsService;
import com.web.image.service.ITrainClassService;
import edu.nudt.das.image.grpc.client.train.TrainClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/5/16.
 */
@Service
public class TrainClassServiceImpl implements ITrainClassService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainClassServiceImpl.class);

    @Autowired
    private FileWebConf fileWebConf;

    @Autowired
    private ITrainsDao trainsDao;

    @Autowired
    private IModelsService modelsService;

    @Autowired
    private IClassificationsDao classificationsDao;

    @Override
    @Transactional
    public boolean submitTrain(List<String> classIdList, String userId, Map<String, Object> paramMap) {
        //创建模型
        Map<String, Object> map = new HashMap();
        map.put("name", paramMap.get("modelName"));
        map.put("des", paramMap.get("modelDes"));
        map.put("user_id", userId);
        int modelId = modelsService.addModels(map);
        LOGGER.info("modelId=" + modelId);
        if (modelId == 0) {
            return false;
        }

        //添加模型分类关系
        classificationsDao.saveRModelClassBatch(classIdList, modelId);

        //添加训练表
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        int x = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
        String trainNo = "T" + sf.format(new Date()) + String.valueOf(x);

        paramMap.put("trainNo", trainNo);
        paramMap.put("status", TrainClassStatus.WAIT_TRAIN.getStatus());
        paramMap.put("createUserId", userId);
        paramMap.put("model_id", modelId);
        trainsDao.saveTrains(paramMap);

        //TODO 添加日志表


        //调用训练
        long trainStep = Long.parseLong(paramMap.get("trainStepNum").toString());
        int numValidation = Integer.parseInt(paramMap.get("numValidation").toString());
        int numShard = Integer.parseInt(paramMap.get("numShard").toString());
        TrainClient client = new TrainClient(fileWebConf.getTrainHost(), Integer.parseInt(fileWebConf.getTrainPort()));
        client.train(trainNo, trainStep, numValidation, numShard);

        return true;
    }

    @Override
    public Map<String, Object> queryTrainsByModelId(String modelId) {
        return trainsDao.queryTrainsByModelId(modelId);
    }
}
