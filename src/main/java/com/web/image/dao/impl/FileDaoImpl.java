package com.web.image.dao.impl;

import com.web.image.common.BaseUtil;
import com.web.image.common.PinyinTool;
import com.web.image.dao.IClassificationsDao;
import com.web.image.dao.IFileDao;
import com.web.image.dao.ITagsDao;
import com.web.image.entity.Images;
import com.web.image.entity.dto.ClassificationsDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * @author zll
 *         2017/5/27.
 */
@Repository
public class FileDaoImpl implements IFileDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PinyinTool pinyinTool;

    @Autowired
    private ITagsDao tagDao;

    @Autowired
    private IClassificationsDao classDao;

    /**
     * 添加图片
     *
     * @param fastDfsUrl 图片地址
     * @param returnList 图片分类
     * @param imgType    图片类型
     * @param userId
     * @param modelId
     * @return
     * @throws Exception
     */
    @Override
    public int insertPic(String fastDfsUrl, List<String> returnList, int imgType, int userId, String modelId) throws Exception {
        LOGGER.info("insertPic params imgType = " + imgType);
        //保存图片
        int imagesId = insertImage(fastDfsUrl, imgType, userId, null, null);

        if (returnList != null && returnList.size() > 0) {
            //查询对应标签id
            String sqlParams = BaseUtil.getSqlParams(returnList);
            LOGGER.info("insertPic sqlParams:" + sqlParams);
            List<Integer> relList = jdbcTemplate.query("SELECT c.id from classifications c,r_model_class rmc WHERE c.id=rmc.class_id and rmc.model_id=" + modelId + " and c.classification IN (" + sqlParams + ")", new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt("id");
                }
            });

            //添加关系表
            addRImagesClass(imagesId, relList);

            //查询标签中文描述
            List<String> classDes = jdbcTemplate.query("SELECT c.alias from classifications c,r_model_class rmc WHERE c.id=rmc.class_id and rmc.model_id=" + modelId + " and c.classification IN (" + sqlParams + ")", new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getString("alias");
                }
            });

            //复制一份标签数据到用户自定义标签
            addImagesTags(imagesId, classDes.toArray(), userId);
        }

        return imagesId;
    }


    @Override
    public List<String> queryPicByClass(List<String> classList, int nowPage, int pageNum, String modelId) {
        if (classList == null || classList.size() == 0) {
            return new ArrayList<>();
        }
        String sqlParams = BaseUtil.getSqlParams(classList);
        LOGGER.info("queryPicByLabels sqlParams:" + sqlParams);

        StringBuffer sb = new StringBuffer("select distinct i.* from images i,r_images_classifications ric,classifications c,r_model_class rmc");
        sb.append(" where i.id=ric.img_id and c.id=ric.classification_id and c.id=rmc.class_id");
        if (StringUtils.isNotBlank(modelId)) {
            sb.append(" and rmc.model_id=" + modelId);
        }
        if (StringUtils.isNotBlank(sqlParams)) {
            sb.append(" and c.classification IN (" + sqlParams + ") ");
        }
        sb.append(" ORDER BY i.id desc");
        if (nowPage > 0 && pageNum > 0) {
            int startNum = (nowPage - 1) * pageNum;
            sb.append(" limit " + startNum + "," + pageNum);
        }

        LOGGER.info("queryPicByClass sql:" + sb.toString());
        //TODO 是否会有用户自定义标签
        List<String> returnList = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("url");
            }
        });
        return returnList;
    }

    @Override
    public List<String> queryPicByAlias(String description, String modelId) {
        StringBuffer sb = new StringBuffer("select distinct i.* from images i,r_images_classifications ric,classifications c,r_model_class rmc");
        sb.append(" where i.id=ric.img_id and c.id=ric.classification_id and rmc.class_id=c.id ");
        if (StringUtils.isNotBlank(description)) {
            sb.append(" and c.alias like '%" + description + "%'");
        }
        if (StringUtils.isNotBlank(modelId)) {
            sb.append(" and rmc.model_id=" + modelId);
        }
        List<String> returnList = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("url");
            }
        });
        return returnList;
    }

    @Override
    public List<ClassificationsDto> queryPicCountByClass(List<String> classList, String modelId) {
        if (classList == null || classList.size() == 0) {
            return new ArrayList<>();
        }

        String sqlParams = BaseUtil.getSqlParams(classList);
        LOGGER.info("queryPicCountByLabels sqlParams:" + sqlParams);

        StringBuffer sb = new StringBuffer("select count(*) imgSumNum,c.des,c.classification from images i,r_images_classifications ric,classifications c,r_model_class rmc ");
        sb.append(" where i.id=ric.img_id and c.id=ric.classification_id and c.id=rmc.class_id");
        if (StringUtils.isNotBlank(modelId)) {
            sb.append(" and rmc.model_id=" + modelId);
        }
        if (StringUtils.isNotBlank(sqlParams)) {
            sb.append(" and c.classification IN (" + sqlParams + ")");
        }
        sb.append(" group by c.des");
        List<ClassificationsDto> returnList = jdbcTemplate.query(sb.toString(), new RowMapper<ClassificationsDto>() {
            @Override
            public ClassificationsDto mapRow(ResultSet resultSet, int i) throws SQLException {
                ClassificationsDto dto = new ClassificationsDto();
                dto.setDescription(resultSet.getString("des"));
                dto.setClassification(resultSet.getString("classification"));
                dto.setCountImages(resultSet.getInt("imgSumNum"));
                return dto;
            }
        });
        return returnList;
    }

    @Override
    public List<Images> queryAllPicWithTag(int nowPage, Map<String, Object> map) {
        StringBuffer sql = new StringBuffer("SELECT i.id,i.url,i.upload_time,i.batch_no,i.bak,ifnull(group_concat(t.alias),'') alias " +
                "FROM images i left join r_images_tags rit on i.id=rit.img_id " +
                "left join tags t on t.id=rit.tag_id " +
                "where 1=1 ");
        if (StringUtils.isNotBlank((String) map.get("classId"))) {
            sql.append(" and t.id=" + map.get("classId"));
        }
        if (StringUtils.isNotBlank((String) map.get("classId"))) {
            sql.append(" and t.alias like '%" + map.get("className") + "%' ");
        }
        if (StringUtils.isNotBlank((String) map.get("userId"))) {
            sql.append(" and i.upload_user_id=" + map.get("userId"));
        }
        if (StringUtils.isNotBlank((String) map.get("imageId"))) {
            sql.append(" and i.id='" + map.get("imageId") + "'");
        }
        sql.append(" group by i.id");
        sql.append(" order by i.id desc");
        if (nowPage > 0) {
            int pageNum = 10;
            int startNum = (nowPage - 1) * pageNum;
            sql.append(" limit " + startNum + "," + pageNum);
        }
        System.out.println(sql.toString());

        return this.getImageBySql(sql.toString());
    }

    @Override
    public List<Images> queryAllPicWithClass(int nowPage, Map<String, Object> map) {
        StringBuffer sql = new StringBuffer("SELECT i.id,i.url,i.upload_time,i.batch_no,i.bak,group_concat(c.alias) alias " +
                "FROM images i,classifications c,r_images_classifications ric WHERE i.id=ric.img_id and c.id=ric.classification_id");
        if (StringUtils.isNotBlank((String) map.get("classId"))) {
            sql.append(" and c.id=" + map.get("classId"));
        }
        if (StringUtils.isNotBlank((String) map.get("className"))) {
            sql.append(" and c.alias like '%" + map.get("className") + "%' ");
        }
        if (StringUtils.isNotBlank((String) map.get("userId"))) {
            sql.append(" and c.create_user_id=" + map.get("userId"));
        }
        if (StringUtils.isNotBlank((String) map.get("imageId"))) {
            sql.append(" and i.id='" + map.get("imageId") + "'");
        }
        if (StringUtils.isNotBlank((String) map.get("batchNo"))) {
            sql.append(" and i.batch_no='" + map.get("batchNo") + "'");
        }
        sql.append(" group by i.id");
        sql.append(" order by i.id desc");

        if (nowPage > 0) {
            int pageNum = 10;
            int startNum = (nowPage - 1) * pageNum;
            sql.append(" limit " + startNum + "," + pageNum);
        }
        System.out.println(sql.toString());

        return this.getImageBySql(sql.toString());
    }

    @Override
    public int editImagesClass(int imagesId, String[] classDescription, int userId) throws Exception {
        //删除原有分类
        String sql = "delete from r_images_classifications where img_id=" + imagesId;
        jdbcTemplate.update(sql);

        //添加分类
        this.addImagesClass(imagesId, classDescription, userId);
        return 0;
    }

    @Override
    public int editImagesTags(int imagesId, String[] classDescription, int userId) throws Exception {
        //删除原有标签，只删除自定义标签关系
        String sql = "delete from r_images_tags where img_id=" + imagesId;
        jdbcTemplate.update(sql);

        //添加标签
        this.addImagesTags(imagesId, classDescription, userId);
        return 0;
    }

    /**
     * 添加图片分类关系
     *
     * @param imagesId
     * @param classDescription 分类名
     * @param userId
     * @return
     * @throws Exception
     */
    public int addImagesClass(int imagesId, Object[] classDescription, int userId) throws Exception {
        if (classDescription != null && classDescription.length > 0) {
            //查询对应标签id
            for (int i = 0; i < classDescription.length; i++) {
                if (StringUtils.isEmpty(classDescription[i].toString())) {
                    continue;
                }
                System.out.println(classDescription[i]);

                //查询标签
                Map<String, String> paramMap = new HashMap<>();
                String classAlias = classDescription[i].toString();
                paramMap.put("alias", classAlias);
                paramMap.put("create_user_id", String.valueOf(userId));
                int classId = 0;
                List<Map<String, Object>> classList = classDao.queryClassifications(paramMap, null);
                if (classList.size() > 0) {
                    classId = Integer.parseInt(classList.get(0).get("id").toString());
                }

                //没有的标签就添加入库
                if (classId == 0) {
                    Map<String, Object> classMap = new HashMap<>();
                    classMap.put("classification", pinyinTool.toPinYin(classAlias, "", PinyinTool.Type.LOWERCASE));
                    classMap.put("alias", classAlias);
                    classMap.put("des", classAlias);
                    classMap.put("create_user_id", userId);
                    classMap.put("create_time", new Date());
                    classId = classDao.save(classMap);
                }

                //添加关系表
                LOGGER.info("classId==========" + classId);
                List<Integer> classIds = new ArrayList<>();
                classIds.add(classId);
                this.addRImagesClass(imagesId, classIds);
            }
        }
        return 0;
    }

    /**
     * 上传图片后，对图片进行添加标签操作，添加的都为自定义标签
     *
     * @param classDescription 图片标签
     * @param userId
     * @return
     * @throws Exception
     * @paimagesId 图片id
     */
    @Override
    public int addImagesTags(int imagesId, Object[] classDescription, int userId) throws Exception {
        if (classDescription != null && classDescription.length > 0) {
            //查询对应标签id
            for (int i = 0; i < classDescription.length; i++) {
                if (StringUtils.isEmpty(classDescription[i].toString())) {
                    continue;
                }
                System.out.println(classDescription[i]);

                //自定义标签
                Integer tagId = 0;
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("alias", classDescription[i].toString());
                paramMap.put("user_id", String.valueOf(userId));
                List<Map<String, Object>> tagList = tagDao.queryTags(paramMap, null);
                if (tagList.size() > 0) {
                    tagId = Integer.parseInt(tagList.get(0).get("id").toString());
                }

                //没有的标签就添加入库
                if (tagId == 0) {
                    String tagName = classDescription[i].toString();
                    Map<String, Object> tagMap = new HashMap<>();
                    tagMap.put("tag", pinyinTool.toPinYin(tagName, "", PinyinTool.Type.LOWERCASE));
                    tagMap.put("alias", tagName);
                    tagMap.put("des", tagName);
                    tagMap.put("user_id", userId);
                    tagId = tagDao.save(tagMap);
                }

                //添加关系表
                LOGGER.info("tagId==========" + tagId);
                String relSql = "INSERT INTO r_images_tags(img_id,tag_id) VALUES (" + imagesId + "," + tagId + ")";
                jdbcTemplate.update(relSql);
            }
        }
        return 0;
    }

    @Override
    public int delImagesTagsRel(String imagesId, int tagId) {
        StringBuffer sql = new StringBuffer("delete from r_images_tags where img_id in (" + imagesId + ")");
        if (tagId > 0) {
            sql.append(" and tag_id=" + tagId);
        }
        LOGGER.info("delImagesTagsRel sql = " + sql);
        return jdbcTemplate.update(sql.toString());
    }

    @Override
    public int delImagesClassRel(String imagesId, int classId) {
        StringBuffer sql = new StringBuffer("delete from r_images_classifications where img_id in (" + imagesId + ")");
        if (classId > 0) {
            sql.append(" and classification_id=" + classId);
        }
        LOGGER.info("delImagesClassRel sql = " + sql);
        return jdbcTemplate.update(sql.toString());
    }

    @Override
    public List<Map<String, Object>> queryCanDelImg(String imgId) {
        StringBuffer sb = new StringBuffer("select group_concat(i.id) img_id,group_concat(i.url) img_url from(select id,url from images where id in(" + imgId + ")) i");
        sb.append(" left join r_images_tags t on i.id=t.img_id");
        sb.append(" left join r_images_classifications c on i.id=c.img_id");
        sb.append(" where t.img_id is null and c.img_id is null");

        LOGGER.info("queryCanDelImg sql = " + sb.toString());
        return jdbcTemplate.queryForList(sb.toString());
    }

    @Override
    public int delImages(String imgId) {
        String sql = "delete from images where id in (" + imgId + ")";
        LOGGER.info("delImages sql = " + sql);
        return jdbcTemplate.update(sql);
    }

    @Override
    public int insertImageTag(String picUrl, String tagId, int imgType, int userId) {
        LOGGER.info("insertImageTag params imgType = " + imgType);
        //保存图片
        int imagesId = insertImage(picUrl, imgType, userId, null, null);
        //添加关系表
        String relSql = "INSERT INTO r_images_tags(img_id,tag_id) VALUES (" + imagesId + "," + tagId + ")";
        jdbcTemplate.update(relSql);
        return imagesId;
    }

    @Override
    public int insertImageInClassification(String picUrl, String classificationId, int imgType, int userId, String batchNo, String bak) {
        LOGGER.info("insertImageInClassification params imgType = " + imgType);
        //保存图片
        int imagesId = insertImage(picUrl, imgType, userId, batchNo, bak);

        //添加关系表
        List<Integer> classIds = new ArrayList<>();
        classIds.add(Integer.parseInt(classificationId));
        this.addRImagesClass(imagesId, classIds);
        return imagesId;
    }

    /**
     * 将图片存入到images表
     *
     * @param imgUrl
     * @param imgType
     * @param userId
     * @return
     */
    private int insertImage(String imgUrl, int imgType, int userId, String batchNo, String bak) {
        LOGGER.info("insert a image to table images");
        //保存图片
        String sql = "INSERT INTO images(img_type,url,upload_time,upload_user_id,batch_no,bak) VALUES (?,?,now(),?,?,?)";
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, imgType);
                ps.setString(2, imgUrl);
                ps.setInt(3, userId);
                ps.setString(4, batchNo);
                ps.setString(5, bak);
                return ps;
            }
        }, key);
        return key.getKey().intValue();
    }

    /**
     * @param sql
     * @return
     */
    private List<Images> getImageBySql(String sql) {
        List<Images> returnList = jdbcTemplate.query(sql, new RowMapper<Images>() {
            @Override
            public Images mapRow(ResultSet resultSet, int i) throws SQLException {
                Images images = new Images();
                images.setId(resultSet.getInt("id"));
                images.setUrl(resultSet.getString("url"));
                images.setUploadTime(resultSet.getDate("upload_time"));
                images.setBatchNo(resultSet.getString("batch_no"));
                images.setBak(resultSet.getString("bak"));
                images.setAlias(resultSet.getString("alias"));
                return images;
            }
        });
        return returnList;
    }

    /**
     * 添加图片分类关系表
     *
     * @param imagesId
     * @param classIdList
     */
    private void addRImagesClass(int imagesId, List<Integer> classIdList) {
        //添加关系表
        String relSql = "INSERT INTO r_images_classifications(img_id,classification_id) VALUES (?,?)";
        jdbcTemplate.batchUpdate(relSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, imagesId);
                ps.setInt(2, classIdList.get(i).intValue());
            }

            @Override
            public int getBatchSize() {
                return classIdList.size();
            }
        });
    }
}
