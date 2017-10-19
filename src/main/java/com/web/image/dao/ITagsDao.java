package com.web.image.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface ITagsDao {

    List<Map<String, Object>> queryTags(Map<String, String> param, Map pageInfo);

    List<Map<String, Object>> fuzzyQueryTags(String condition, String user_id, Map pageInfo);

    long queryImageCountByTag(long id);

    int updateById(Map<String, String> param);

    int delete(long id);

    int save(Map param);
}
