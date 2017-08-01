package com.test.demo.dao.tags.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface ITagsDao {

	List<Map<String,Object>> queryTags(Map param, Map pageInfo);

	List<Map<String,Object>> fuzzyQueryTags(String condition,String user_id, Map pageInfo);

	int queryTagsCount(Map param);

	long queryImageCountByTag(long id);

	int updateById(Map param);

	int delete(long id);

	int save(Map param);
}
