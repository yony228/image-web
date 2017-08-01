package com.test.demo.service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface TrainClassService {
    /**
     * 提交训练
     *
     * @param classIdList
     * @param userId
     * @return
     */
    String submitTrain(List<String> classIdList, String userId, Map<String, Object> paramMap);

    /**
     * 查询
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryTrainClass(Map<String, Object> map);

    /**
     * 生成模型
     *
     * @param trainNo
     * @return
     */
    boolean createModels(String trainNo, String modelName, String modelDes, String userId);
}
