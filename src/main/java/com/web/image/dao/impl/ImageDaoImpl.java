package com.web.image.dao.impl;

import com.web.image.common.PinyinTool;
import com.web.image.dao.IModelsDao;
import com.web.image.dao.ImageDao;
import com.web.image.entity.Classifications;
import com.web.image.entity.Images;
import com.web.image.entity.dto.ClassificationsDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

/**
 * Created by Administrator on 2017/5/27.
 */
@Repository
public class ImageDaoImpl implements ImageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PinyinTool pinyinTool;

    @Autowired
    private IModelsDao modelsDao;

    @Override
    public int queryClassId(Map<String, String> paramMap) {
        StringBuffer sqlBuffer = new StringBuffer("SELECT max(id) id FROM classifications WHERE 1=1 ");
        if (StringUtils.isNotBlank(paramMap.get("classification"))) {
            sqlBuffer.append(" AND classification ='" + paramMap.get("classification") + "'");
        }
        if (StringUtils.isNotBlank(paramMap.get("alias"))) {
            sqlBuffer.append(" AND alias = '" + paramMap.get("alias") + "'");
        }
        if (StringUtils.isNotBlank(paramMap.get("des"))) {
            sqlBuffer.append(" AND des = '" + paramMap.get("des") + "'");
        }
        if (StringUtils.isNotBlank(paramMap.get("modelId"))) {
            sqlBuffer.append(" AND model_id = '" + paramMap.get("modelId") + "'");
        }

        LOGGER.info("queryClassId sql = " + sqlBuffer.toString());
        return this.getIdBySql(sqlBuffer.toString());
    }

    @Override
    public int insertPic(String fastDfsUrl, List<String> returnList, int imgType, int userId, String modelId) throws Exception {
        LOGGER.info("insertPic params imgType = " + imgType);
        //保存图片
        int imagesId = insertImage(fastDfsUrl, imgType, userId, null, null);

        if (returnList != null && returnList.size() > 0) {
            //查询对应标签id
            String sqlParams = this.getSqlParams(returnList);
            LOGGER.info("insertPic sqlParams:" + sqlParams);
            List<Integer> relList = jdbcTemplate.query("SELECT id from classifications WHERE model_id=" + modelId + " and classification IN (" + sqlParams + ")", new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt("id");
                }
            });

            //添加关系表
            String relSql = "INSERT INTO r_images_classifications(img_id,classification_id) VALUES (?,?)";
            jdbcTemplate.batchUpdate(relSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, imagesId);
                    ps.setInt(2, relList.get(i).intValue());
                }

                @Override
                public int getBatchSize() {
                    return relList.size();
                }
            });


            //查询标签中文描述
            List<String> classDes = jdbcTemplate.query("SELECT alias from classifications WHERE model_id=" + modelId + " and classification IN (" + sqlParams + ")", new RowMapper<String>() {
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
    public List<String> queryPicByLabels(List<String> classList, int nowPage, int pageNum, String modelId) {
        int startNum = (nowPage - 1) * pageNum;

        if (classList == null || classList.size() == 0) {
            return new ArrayList<>();
        }

        String sqlParams = this.getSqlParams(classList);
        LOGGER.info("queryPicByLabels sqlParams:" + sqlParams);

        String sql = "select distinct i.* from images i,r_images_classifications ric,classifications c\n" +
                "where i.id=ric.img_id and c.id=ric.classification_id and c.model_id=" + modelId + " and c.classification IN (" + sqlParams + ") ORDER BY i.id desc limit " + startNum + "," + pageNum;

        LOGGER.info("queryPicByLabels sql:" + sql);
        //TODO 是否会有用户自定义标签
        List<String> returnList = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("url");
            }
        });
        return returnList;
    }

    @Override
    public int queryPicByLabelsCount(List<String> classList, String modelId) {
        String sqlParams = this.getSqlParams(classList);
        LOGGER.info("queryPicByLabelsCount sqlParams:" + sqlParams);

        String sql = "select count(distinct i.id) count from images i,r_images_classifications ric,classifications c\n" +
                "where i.id=ric.img_id and c.id=ric.classification_id and c.model_id=" + modelId;
        if (StringUtils.isNotBlank(sqlParams)) {
            sql += " and c.classification IN (" + sqlParams + ")";
        }

        LOGGER.info("queryPicByLabelsCount sql:" + sql);
        //TODO 是否会有用户自定义标签
        Integer countNum = jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while (resultSet.next()) {
                    return resultSet.getInt("count");
                }
                return 0;
            }
        });
        return countNum;
    }

    @Override
    public List<String> queryPicByLabelStr(String description, String modelId) {
        String sql = "select distinct i.* from images i,r_images_classifications ric,classifications c\n" +
                "where i.id=ric.img_id and c.id=ric.classification_id and c.alias like '%" + description + "%' and c.model_id=" + modelId;
        List<String> returnList = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("url");
            }
        });
        return returnList;
    }

    @Override
    public List<ClassificationsDto> queryPicCountByLabels(List<String> classList, String modelId) {
        if (classList == null || classList.size() == 0) {
            return new ArrayList<>();
        }

        String sqlParams = this.getSqlParams(classList);
        LOGGER.info("queryPicCountByLabels sqlParams:" + sqlParams);

        String sql = "select count(*) imgSumNum,c.des,c.classification from images i,r_images_classifications ric,classifications c\n" +
                "where i.id=ric.img_id and c.id=ric.classification_id and c.model_id=" + modelId + " and c.classification IN (" + sqlParams + ") group by c.des";
        List<ClassificationsDto> returnList = jdbcTemplate.query(sql, new RowMapper<ClassificationsDto>() {
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
    public String queryClassDescription(List<String> classList, String modelId) {
        if (classList == null || classList.size() == 0) {
            return "无";
        }

        String sqlParams = this.getSqlParams(classList);
        LOGGER.info("queryClassDescription sqlParams:" + sqlParams);

        String sql = "SELECT GROUP_CONCAT(alias SEPARATOR ' ') alias from classifications WHERE model_id=" + modelId + " AND classification IN (" + sqlParams + ")";
        String returnStr = jdbcTemplate.query(sql, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while (resultSet.next()) {
                    return resultSet.getString("alias");
                }
                return "无";
            }
        });

        return returnStr;
    }

    @Override
    public List<Images> queryAllPicWithTag(int nowPage, Map<String, Object> map) {
        int pageNum = 10;
        int startNum = (nowPage - 1) * pageNum;

        StringBuffer sql = new StringBuffer("SELECT distinct i.* FROM images i,tags t,r_images_tags rit WHERE i.id=rit.img_id and t.id=rit.tag_id");
        if (StringUtils.isNotBlank((String) map.get("className"))) {
            sql.append(" and t.alias like '%" + map.get("className") + "%' ");
        }
        if (StringUtils.isNotBlank((String) map.get("userId"))) {
            sql.append(" and i.upload_user_id=" + map.get("userId"));
        }
        if (StringUtils.isNotBlank((String) map.get("imageId"))) {
            sql.append(" and i.id='" + map.get("imageId") + "'");
        }
        sql.append(" order by i.id desc limit " + startNum + "," + pageNum);
        System.out.println(sql.toString());

        return this.getImageBySql(sql.toString());
    }

    @Override
    public int queryAllPicWithTagCount(Map<String, Object> map) {
        StringBuffer sql = new StringBuffer("SELECT count(distinct i.id) count FROM images i,tags t,r_images_tags rit WHERE i.id=rit.img_id and t.id=rit.tag_id");
        if (StringUtils.isNotBlank((String) map.get("className"))) {
            sql.append(" and t.alias like '%" + map.get("className") + "%' ");
        }
        if (StringUtils.isNotBlank((String) map.get("userId"))) {
            sql.append(" and i.upload_user_id=" + map.get("userId"));
        }
        if (StringUtils.isNotBlank((String) map.get("imageId"))) {
            sql.append(" and i.id='" + map.get("imageId") + "'");
        }

        Integer countNum = jdbcTemplate.query(sql.toString(), new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while (resultSet.next()) {
                    return resultSet.getInt("count");
                }
                return 0;
            }
        });
        return countNum;
    }

    @Override
    public List<Images> queryAllPicWithClass(int nowPage, Map<String, Object> map) {
        int pageNum = 10;
        int startNum = (nowPage - 1) * pageNum;

        StringBuffer sql = new StringBuffer("SELECT distinct i.* FROM images i,classifications c,r_images_classifications ric WHERE i.id=ric.img_id and c.id=ric.classification_id");
        if (StringUtils.isNotBlank((String) map.get("className"))) {
            sql.append(" and c.alias like '%" + map.get("className") + "%' ");
        }
        if (StringUtils.isNotBlank((String) map.get("userId"))) {
            sql.append(" and i.upload_user_id=" + map.get("userId"));
        }
        if (StringUtils.isNotBlank((String) map.get("imageId"))) {
            sql.append(" and i.id='" + map.get("imageId") + "'");
        }
        if (StringUtils.isNotBlank((String) map.get("modelId"))) {
            sql.append(" and c.model_id='" + map.get("modelId") + "'");
        }
        if (StringUtils.isNotBlank((String) map.get("batchNo"))) {
            sql.append(" and i.batch_no='" + map.get("batchNo") + "'");
        }
        sql.append(" order by i.id desc limit " + startNum + "," + pageNum);
        System.out.println(sql.toString());

        return this.getImageBySql(sql.toString());
    }

    @Override
    public int queryAllPicWithClassCount(Map<String, Object> map) {
        StringBuffer sql = new StringBuffer("SELECT count(distinct i.id) count FROM images i,classifications c,r_images_classifications ric WHERE i.id=ric.img_id and c.id=ric.classification_id");
        if (StringUtils.isNotBlank((String) map.get("className"))) {
            sql.append(" and c.alias like '%" + map.get("className") + "%' ");
        }
        if (StringUtils.isNotBlank((String) map.get("userId"))) {
            sql.append(" and i.upload_user_id=" + map.get("userId"));
        }
        if (StringUtils.isNotBlank((String) map.get("imageId"))) {
            sql.append(" and i.id='" + map.get("imageId") + "'");
        }
        if (StringUtils.isNotBlank((String) map.get("modelId"))) {
            sql.append(" and c.model_id='" + map.get("modelId") + "'");
        }
        if (StringUtils.isNotBlank((String) map.get("batchNo"))) {
            sql.append(" and i.batch_no='" + map.get("batchNo") + "'");
        }

        Integer countNum = jdbcTemplate.query(sql.toString(), new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while (resultSet.next()) {
                    return resultSet.getInt("count");
                }
                return 0;
            }
        });
        return countNum;
    }


    @Override
    public List<Classifications> queryClassByImagesId(int imagesId, String modelId) {
        String sql = "select c.*,m.des modelDes from images i,classifications c left join models m on c.model_id=m.id,r_images_classifications ric " +
                "where i.id=ric.img_id and c.id=ric.classification_id and i.id=" + imagesId;
        if (StringUtils.isNotBlank(modelId)) {
            sql += " and c.model_id=" + modelId;
        }
        LOGGER.info("queryClassByImagesId sql = " + sql);

        List<Classifications> returnList = jdbcTemplate.query(sql, new RowMapper<Classifications>() {
            @Override
            public Classifications mapRow(ResultSet resultSet, int i) throws SQLException {
                Classifications classifications = new Classifications();
                classifications.setId(resultSet.getInt("id"));
                classifications.setClassification(resultSet.getString("classification"));
                classifications.setAlias(resultSet.getString("alias"));
                classifications.setDescription(resultSet.getString("des"));
                classifications.setModelName(resultSet.getString("modelDes"));

                return classifications;
            }
        });
        return returnList;
    }

    @Override
    public List<Classifications> queryTagByImagesId(int imagesId) {
        String sql = "select t.id,t.tag classification,t.alias,t.des from images i,r_images_tags rit,tags t " +
                "where i.id=rit.img_id and t.id=rit.tag_id and i.id=" + imagesId;
        LOGGER.info("queryTagByImagesId sql = " + sql);
        return this.getClassBySql(sql);
    }

    @Override
    public int editImagesClass(int imagesId, String[] classDescription, int userId) throws Exception {
        //删除原有标签
        String sql = "delete from r_images_classifications where img_id=" + imagesId;
        jdbcTemplate.update(sql);

        //添加标签
        this.addImagesClass(imagesId, classDescription);
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
     * @param classDescription 模型名-分类名
     * @return
     * @throws Exception
     */
    public int addImagesClass(int imagesId, Object[] classDescription) throws Exception {
        String sql = "";
        Map<String, String> paramMap = new HashMap<>();

        if (classDescription != null && classDescription.length > 0) {
            //查询对应标签id
            for (int i = 0; i < classDescription.length; i++) {
                System.out.println(classDescription[i]);

                String[] tempArray = classDescription[i].toString().split("-");//模型名-分类名
                String modelId = "";
                String classAlias = "";
                if (tempArray.length == 1) {
                    Map<String, Object> modelMap = modelsDao.getOnlineModels();
                    modelId = modelMap.get("id").toString();
                    classAlias = tempArray[0];
                } else {
                    Map<String, Object> param = new HashMap<>();
                    param.put("des", tempArray[0]);
                    List<Map<String, Object>> modelList = modelsDao.queryModels(param, null);
                    if (modelList != null && modelList.size() > 0) {
                        modelId = modelList.get(0).get("id").toString();
                    }else{
                        Map<String, Object> modelMap = modelsDao.getOnlineModels();
                        modelId = modelMap.get("id").toString();
                    }

                    classAlias = tempArray[1];
                }

                //查询标签
                paramMap.put("alias", classAlias);
                paramMap.put("modelId", modelId);
                int classId = this.queryClassId(paramMap);

                //没有的标签就添加入库
                if (classId == 0) {
                    String insertClassSql = "INSERT INTO classifications(classification,alias,des,model_id) VALUES (?,?,?,?)";
                    String className = classAlias;
                    String classPinyin = pinyinTool.toPinYin(classAlias, "", PinyinTool.Type.LOWERCASE);
                    String model_id = modelId;
                    KeyHolder key = new GeneratedKeyHolder();
                    jdbcTemplate.update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps = connection.prepareStatement(insertClassSql, Statement.RETURN_GENERATED_KEYS);
                            ps.setString(1, classPinyin);
                            ps.setString(2, className);
                            ps.setString(3, className);
                            ps.setString(4, model_id);
                            return ps;
                        }
                    }, key);
                    classId = key.getKey().intValue();
                }

                //添加关系表
                LOGGER.info("classId==========" + classId);
                String relSql = "INSERT INTO r_images_classifications(img_id,classification_id) VALUES (" + imagesId + "," + classId + ")";
                jdbcTemplate.update(relSql);
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
        String sql = "";

        if (classDescription != null && classDescription.length > 0) {
            //查询对应标签id
            for (int i = 0; i < classDescription.length; i++) {
                System.out.println(classDescription[i]);

                //自定义标签
                Integer tagId = this.queryTagIdByDes(classDescription[i].toString(), userId);

                //没有的标签就添加入库
                if (tagId == 0) {
                    String insertClassSql = "INSERT INTO tags(tag,alias,des,user_id) VALUES (?,?,?,?)";
                    String className = classDescription[i].toString();
                    String classPinyin = pinyinTool.toPinYin(className, "", PinyinTool.Type.LOWERCASE);
                    KeyHolder key = new GeneratedKeyHolder();
                    jdbcTemplate.update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps = connection.prepareStatement(insertClassSql, Statement.RETURN_GENERATED_KEYS);
                            ps.setString(1, classPinyin);
                            ps.setString(2, className);
                            ps.setString(3, className);
                            ps.setInt(4, userId);
                            return ps;
                        }
                    }, key);
                    tagId = key.getKey().intValue();
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
    public int delImagesTagsRel(int imagesId, int tagId) throws Exception {
        String sql = "delete from r_images_tags where img_id=" + imagesId + " and tag_id=" + tagId;
        LOGGER.info("delImagesTagsRel sql = " + sql);
        jdbcTemplate.update(sql);
        return 0;
    }

    @Override
    public int delImagesClassRel(int imagesId, int classId) throws Exception {
        String sql = "delete from r_images_classifications where img_id=" + imagesId + " and classification_id=" + classId;
        LOGGER.info("delImagesClassRel sql = " + sql);
        jdbcTemplate.update(sql);
        return 0;
    }

    @Override
    public int queryTagIdByDes(String des, int userId) {
        LOGGER.info("queryTagIdByDes params=" + des);
        //查询对应标签id
        Integer tagId = jdbcTemplate.query("SELECT id from tags WHERE alias='" + des + "' and user_id=" + userId, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while (resultSet.next()) {
                    return resultSet.getInt("id");
                }
                return 0;
            }
        });
        return tagId;
    }

    @Override
    public int delImages(String imgId) {
        String sql = "delete from images where id in (" + imgId + ")";
        LOGGER.info("delImages sql = " + sql);
        jdbcTemplate.update(sql);

        sql = "delete from r_images_classifications where img_id in ( " + imgId + ")";
        LOGGER.info("delImages sql = " + sql);
        jdbcTemplate.update(sql);

        sql = "delete from r_images_tags where img_id in (" + imgId + ")";
        LOGGER.info("delImages sql = " + sql);
        jdbcTemplate.update(sql);
        return 0;
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
        String relSql = "INSERT INTO r_images_classifications(img_id,classification_id) VALUES (" + imagesId + "," + classificationId + ")";
        jdbcTemplate.update(relSql);
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

    public List<Classifications> getClassBySql(String sql) {
        List<Classifications> returnList = jdbcTemplate.query(sql, new RowMapper<Classifications>() {
            @Override
            public Classifications mapRow(ResultSet resultSet, int i) throws SQLException {
                Classifications classifications = new Classifications();
                classifications.setId(resultSet.getInt("id"));
                classifications.setClassification(resultSet.getString("classification"));
                classifications.setAlias(resultSet.getString("alias"));
                classifications.setDescription(resultSet.getString("des"));

                return classifications;
            }
        });
        return returnList;
    }

    public List<Images> getImageBySql(String sql) {
        List<Images> returnList = jdbcTemplate.query(sql, new RowMapper<Images>() {
            @Override
            public Images mapRow(ResultSet resultSet, int i) throws SQLException {
                Images images = new Images();
                images.setId(resultSet.getInt("id"));
                images.setUrl(resultSet.getString("url"));
                images.setUploadTime(resultSet.getDate("upload_time"));
                images.setBatchNo(resultSet.getString("batch_no"));
                images.setBak(resultSet.getString("bak"));
                return images;
            }
        });
        return returnList;
    }

    public int getIdBySql(String sql) {
        Integer id = jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                while (resultSet.next()) {
                    return resultSet.getInt("id");
                }
                return 0;
            }
        });
        return id;
    }

    public String getSqlParams(List<String> paramList) {
        String sqlParams = "";
        for (int i = 0; i < paramList.size(); i++) {
            if (i == paramList.size() - 1) {
                sqlParams += "'" + paramList.get(i) + "'";
            } else {
                sqlParams += "'" + paramList.get(i) + "',";
            }
        }
        return sqlParams;
    }
}
