package com.test.demo.dao;

import com.test.demo.entity.TrainClass;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
public interface TrainClassDao {
    /**
     * 添加训练
     *
     * @param trainClassList
     * @return
     */
    int saveTrainClassBatch(List<TrainClass> trainClassList);

    /**
     * @param param
     * @param pageInfo
     * @return
     */
    List<Map<String, Object>> queryTrainClass(Map<String, Object> param, Map pageInfo);

    /**
     * 根据训练号查询
     * @param trainNo
     * @return
     */
    List<Map<String, Object>> queryClassByTrainNo(String trainNo);
}
