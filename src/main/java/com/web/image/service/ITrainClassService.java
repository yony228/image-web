package com.web.image.service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface ITrainClassService {
    /**
     * 提交训练
     *
     * @param classIdList
     * @param userId
     * @return
     */
    boolean submitTrain(List<String> classIdList, String userId, Map<String, Object> paramMap);

    /**
     * 根据模型查询训练数据
     *
     * @param modelId
     * @return
     */
    Map<String, Object> queryTrainsByModelId(String modelId);

}
