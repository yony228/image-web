package com.test.demo.service.classificationandtag.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
public interface IClassificationService {

	/**
	 * 分页条件查询分类
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> queryClassifications(Map param);

	List<Map<String, Object>> fuzzyQueryClassification(Map param);
	/**
	 * 查询分类下图片数量
	 * @param id
	 * @return
	 */
	long queryImageCountByClassification(long id);

	/**
	 * 更新Classification
	 * @param param
	 * @return
	 */
	boolean updateClassification(Map param);

	List<Map<String,Object>> findSameClassificationOnModel(Map param);

	boolean checkSameClassificationOnModel(Map param);

	boolean deleteClassification(long id);

	boolean addClassification(Map param);
}