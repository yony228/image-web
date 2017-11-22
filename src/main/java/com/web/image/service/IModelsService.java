package com.web.image.service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/16.
 */
public interface IModelsService {
    /**
     * 查询模型
     *
     * @param paramMap
     * @return
     */
    List<Map<String, Object>> queryModels(Map<String, Object> paramMap);

    /**
     * 添加模型
     *
     * @param map
     * @return
     */
    int addModels(Map<String, Object> map);

    /**
     * @param name
     * @param id
     * @return
     */
    boolean checkSameModels(String name, String id);

    /**
     * @param modelsId
     * @return
     */
    int queryClassCountByModelId(int modelsId);

    /**
     * 修改模型信息
     *
     * @param map
     * @return
     */
    boolean updateModels(Map<String, Object> map);

    /**
     * 修改模型状态
     *
     * @param modelId
     * @return
     */
    boolean updateModelStatus(int modelId, int status);

    /**
     * 模型上线
     * @param modelId
     * @param status
     * @return
     */
    boolean upModels(String modelId, int status);
}
