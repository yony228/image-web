package com.web.image.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */
public interface IModelsDao {
    /**
     * 查询模型
     *
     * @param paramMap
     * @return
     */
    List<Map<String, Object>> queryModels(Map<String, Object> paramMap, Map pageInfo);

    int save(Map<String, Object> param);

    int queryClassCountByModelId(int modelsId);

    int update(Map<String, Object> param);

    int updateStatus(String modelsId);

    /**
     * 获取当前线上使用模型
     * @return
     */
    Map<String, Object> getOnlineModels();
}
