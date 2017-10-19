package com.web.image.service.impl;

import com.web.image.common.PageUtil;
import com.web.image.common.PinyinTool;
import com.web.image.dao.IClassificationsDao;
import com.web.image.dao.IImagesDao;
import com.web.image.dao.ITagsDao;
import com.web.image.service.IClassificationService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yony on 17-6-5.
 */
@Transactional
@Service
public class ClassificationServiceImpl implements IClassificationService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class);

    @Autowired
    IClassificationsDao classificationsDao;

    @Autowired
    private IImagesDao imagesDao;

    @Autowired
    private PinyinTool pinyinTool;

    @Autowired
    ITagsDao tagsDao;

    /**
     * 分页条件查询分类
     *
     * @param param
     * @return
     */
    @Override
    public List<Map<String, Object>> queryClassifications(Map param) {
        Map pageInfo = PageUtil.getPage(param);
        return classificationsDao.queryClassifications(param, pageInfo);
    }

    /**
     * 查询分类下图片数量
     *
     * @param id
     * @return
     */
    @Override
    public long queryImageCountByClassification(long id) {
        return classificationsDao.queryImageCountByClassification(id);
    }

    /**
     * 添加分类
     *
     * @param param
     * @return
     */
    @Override
    public boolean addClassification(Map param) {
        List<Map<String, Object>> sameClass;
        try {
            sameClass = this.findSameClassification(null, param.get("create_user_id").toString(), param.get("alias").toString(), null);
        } catch (Exception e) {
            return false;
        }

        if (sameClass.size() == 0) {
            String classification = this.getClassification(null, param.get("create_user_id").toString(), param.get("alias").toString());
            param.put("classification", classification);
            param.put("create_time", new Date());
            classificationsDao.save(param);
            return true;
        }
        return false;
    }

    /**
     * 更新分类
     *
     * @param param
     * @return
     */
    @Override
    public boolean updateClassification(Map<String, String> param) {
        List<Map<String, Object>> sameClass;
        //查找模型下同名classification
        try {
            sameClass = this.findSameClassification(param.get("id"), param.get("create_user_id"), param.get("alias"), null);
        } catch (Exception e) {
            return false;
        }

        if (sameClass.size() == 0) {
            //TODO 如果已产生模型，不能修改分类classification，会影响搜索结果。
            String classification = this.getClassification(param.get("id"), param.get("create_user_id"), param.get("alias"));
            param.put("classification", classification);

            return classificationsDao.updateAllById(param) > 0;
        } else {
            Map classificationInDb = sameClass.get(0);
            //更新图片
            Map tmp = new HashMap();
            tmp.put("classification_id", param.get("id"));
            tmp.put("new_classification_id", classificationInDb.get("id"));
            imagesDao.updateByClassification(tmp);
            //删除冗余分类
            classificationsDao.delete(MapUtils.getLong(param, "id"));
            return true;
        }
    }

    @Override
    public boolean deleteClassification(long id) {
        if (0 == classificationsDao.queryImageCountByClassification(id)) {
            return classificationsDao.delete(id) > 0;
        } else {
            return false;
        }
    }


    /**
     * 查询分类是否重复
     *
     * @param id    分类id
     * @param alias 分类名
     * @return
     */
    @Override
    public List<Map<String, Object>> findSameClassification(String id, String userId, String alias, String classification) throws Exception {
        //alias，则抛出异常
        if (StringUtils.isEmpty(alias) && StringUtils.isEmpty(classification)) {
            throw new Exception("分类名不能为空");
        }
        Map tmp = new HashMap();
        tmp.put("alias", alias);
        tmp.put("create_user_id", userId);
        tmp.put("not_id", id);
        tmp.put("classification", classification);
        List<Map<String, Object>> list = classificationsDao.queryClassifications(tmp, null);
        return list;
    }

    /**
     * 生成分类别名
     *
     * @param id
     * @param userId
     * @param alias
     * @return
     */
    public String getClassification(String id, String userId, String alias) {
        List<Map<String, Object>> sameClass;
        String classification = "";
        try {
            classification = pinyinTool.toPinYin(alias, "", PinyinTool.Type.LOWERCASE);

            //校验classification是否重复,如果拼音重复，在后面添加随机数
            sameClass = this.findSameClassification(id, userId.toString(), null, classification);

            if (sameClass.size() != 0) {
                int x = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
                classification = classification + String.valueOf(x);
            }
        } catch (Exception e) {
            LOGGER.info(e.toString());
            return "";
        }
        return classification;
    }

    /**
     * 复制用户标签到分类
     *
     * @param userId
     * @param tagId
     * @return
     */
    @Override
    @Transactional
    public boolean copyTagsToClass(String userId, String tagId) {
        //查询标签
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("id", tagId);
        List<Map<String, Object>> tagMap = tagsDao.queryTags(paramMap, null);
        String tagAlias = tagMap.get(0).get("alias").toString();

        List<Map<String, Object>> sameClass;
        //查找模型下同名classification
        try {
            sameClass = this.findSameClassification(null, userId, tagAlias, null);
        } catch (Exception e) {
            LOGGER.info(e.toString());
            return false;
        }

        //分类id
        String classId;
        if (sameClass.size() == 0) {
            //新增分类
            Map<String, Object> param = new HashMap<>();

            String classification = this.getClassification(null, userId, tagAlias);
            param.put("classification", classification);
            param.put("alias", tagAlias);
            param.put("des", tagAlias);
            param.put("create_user_id", userId);
            param.put("create_time", new Date());
            classId = String.valueOf(classificationsDao.save(param));
        } else {
            classId = sameClass.get(0).get("id").toString();
        }
        LOGGER.info("copyTagsToClass,分类id:" + classId);

        //复制图片
        classificationsDao.copyImagesRel(tagId, classId);

        return true;
    }
}
