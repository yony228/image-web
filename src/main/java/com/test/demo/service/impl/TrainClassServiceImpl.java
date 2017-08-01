package com.test.demo.service.impl;

import com.test.demo.common.FileWebConf;
import com.test.demo.common.PageUtil;
import com.test.demo.common.TrainClassStatus;
import com.test.demo.dao.TrainClassDao;
import com.test.demo.dao.TrainsDao;
import com.test.demo.dao.classifications.interfaces.IClassificationsDao;
import com.test.demo.entity.TrainClass;
import com.test.demo.service.ModelsService;
import com.test.demo.service.TrainClassService;
import edu.nudt.das.image.grpc.client.train.TrainClient;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
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
public class TrainClassServiceImpl implements TrainClassService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainClassServiceImpl.class);

    @Autowired
    private TrainClassDao trainClassDao;

    @Autowired
    private FileWebConf fileWebConf;

    @Autowired
    private TrainsDao trainsDao;

    @Autowired
    private ModelsService modelsService;

    @Autowired
    private IClassificationsDao classificationsDao;

    @Override
    @Transactional
    public String submitTrain(List<String> classIdList, String userId, Map<String, Object> paramMap) {
        List<TrainClass> trainClassList = new ArrayList<>();

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        int x = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
        String trainNo = "T" + sf.format(new Date()) + String.valueOf(x);

        for (String classId : classIdList) {
            TrainClass trainClass = new TrainClass();
            trainClass.setClassId(Integer.parseInt(classId));
            trainClass.setTrainNo(trainNo);
            trainClass.setStatus(TrainClassStatus.WAIT_TRAIN.getStatus());
            trainClass.setCreateUserId(Integer.parseInt(userId));

            trainClassList.add(trainClass);
        }
        trainClassDao.saveTrainClassBatch(trainClassList);

        //添加trains表
        paramMap.put("trainNo", trainNo);
        paramMap.put("status", TrainClassStatus.WAIT_TRAIN.getStatus());
        paramMap.put("createUserId", userId);
        trainsDao.saveTrains(paramMap);


        //调用训练
        long trainStep = Long.parseLong(paramMap.get("trainStepNum").toString());
        int numValidation = Integer.parseInt(paramMap.get("numValidation").toString());
        int numShard = Integer.parseInt(paramMap.get("numShard").toString());
        TrainClient client = new TrainClient(fileWebConf.getTrainHost(), Integer.parseInt(fileWebConf.getTrainPort()));
        client.train(trainNo, trainStep, numValidation, numShard);

        return trainNo;
    }

    @Override
    public List<Map<String, Object>> queryTrainClass(Map<String, Object> param) {
        Map pageInfo = PageUtil.getPage(param);
        return trainClassDao.queryTrainClass(param, pageInfo);
    }

    @Override
    public boolean createModels(String trainNo, String modelName, String modelDes, String userId) {
        //创建模型
        Map<String, Object> map = new HashMap();
        map.put("name", modelName);
        map.put("des", modelDes);
        map.put("user_id", userId);
        int modelId = modelsService.addModels(map);
        if (modelId == 0) {
            return false;
        }

        //修改训练表
        Map<String, Object> paramMap = new HashMap();
        paramMap.put("model_id", modelId);
        paramMap.put("status", TrainClassStatus.CREATE_MODELS.getStatus());
        trainsDao.updateTrainsByTrainNo(paramMap, trainNo);

        //根据训练号查分类
        List<Map<String, Object>> classList = trainClassDao.queryClassByTrainNo(trainNo);
        for (Map<String, Object> classMap : classList) {
            if (StringUtils.isNotBlank(MapUtils.getString(classMap, "model_id")) && !MapUtils.getString(classMap, "model_id").equals("0")) {
                //复制分类
                classMap.put("model_id", modelId);
                int newClassId = classificationsDao.save(classMap);

                //复制分类图片关系
                String oldClassId = classMap.get("id").toString();
                classificationsDao.batchImageClass(oldClassId, String.valueOf(newClassId));
            } else {
                classMap.put("model_id", modelId);
                classificationsDao.updateById(classMap);
            }
        }
        return true;
    }
}
