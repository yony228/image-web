package com.test.demo.dao;

import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
public interface TrainsDao {
    /**
     * 添加训练
     *
     * @param trainsMap
     * @return
     */
    int saveTrains(Map<String, Object> trainsMap);

    /**
     * @param modelId
     * @return
     */
    Map<String, Object> queryTrainsByModelId(String modelId);

    /**
     * @param trainsMap
     * @return
     */
    int updateTrainsByTrainNo(Map<String, Object> trainsMap, String trainNo);
}
