package com.web.image.controller.train;

import com.alibaba.fastjson.JSONObject;
import com.web.image.common.FileWebConf;
import com.web.image.controller.BaseController;
import com.web.image.service.ITrainClassService;
import edu.nudt.das.sansiro.login.interceptor.Auth;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zll
 *         2017/6/20.
 *         训练管理
 */
@Controller
@Auth
@RequestMapping("/train")
public class TrainController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainController.class);

    @Autowired
    private ITrainClassService trainClassService;

    @Autowired
    private FileWebConf fileWebConf;

    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, @PathVariable String pageFile, @PathVariable String page, ModelMap modelMap) throws Exception {
        modelMap.put("train_no", request.getParameter("train_no"));
        return "/" + pageFile + "/" + page;
    }

    /**
     * 提交训练
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("submitTrain")
    @ResponseBody
    public Object submitTrain(HttpServletRequest request) throws Exception {
        String[] classIds = request.getParameter("classId").split(",");
        List<String> classIdList = Arrays.asList(classIds);
        String userId = MapUtils.getString(getUser(request.getSession()), "id");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("numTrain", request.getParameter("numTrain"));
        paramMap.put("numShard", request.getParameter("numShard"));
        paramMap.put("numValidation", request.getParameter("numValidation"));
        paramMap.put("trainStepNum", request.getParameter("trainStepNum"));
        paramMap.put("modelName", request.getParameter("modelName"));
        paramMap.put("modelDes", request.getParameter("modelDes"));

        Map<String, Object> returnMap = new HashMap<>();
        if (trainClassService.submitTrain(classIdList, userId, paramMap)) {
            returnMap.put("code", 100);
        } else {
            returnMap.put("code", 200);
            returnMap.put("msg", "模型名称已存在");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 查询训练日志
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("queryTrainLog")
    @ResponseBody
    public Object queryTrainLog(HttpServletRequest request) throws Exception {
        String trainNo = request.getParameter("train_no");
        String lastfileSize = request.getParameter("lastFileSize");
        System.out.println("trainNo=" + trainNo + ",lastfileSize=" + lastfileSize);

        String logStr = "";
        long lastTimeFileSize = 0;
        if (StringUtils.isNotEmpty(lastfileSize)) {
            lastTimeFileSize = Long.parseLong(lastfileSize);
        }
        File file = new File(fileWebConf.getTrainLogUrl() + trainNo + "/stdout.txt");//文件路径

        Map<String, Object> returnMap = new HashMap<>();
        if (file.exists()) {
            RandomAccessFile randomFile = new RandomAccessFile(file, "r");//只读 rw:读写
            //TODO 第一次加载只读取最后几行，不然文件太大加载过慢
            if (lastTimeFileSize == 0 && randomFile.length() > 10000) {
                lastTimeFileSize = randomFile.length() - 10000;
            }

            randomFile.seek(lastTimeFileSize);
            String tmp = "";
            while ((tmp = randomFile.readLine()) != null) {
//                System.out.println(new String(tmp.getBytes("utf-8")));
                logStr += new String(tmp.getBytes("utf-8")) + "<br>";
            }
            lastTimeFileSize = randomFile.length();

            returnMap.put("code", 100);
            returnMap.put("logStr", logStr);
            returnMap.put("lastFileSize", lastTimeFileSize);
        } else {
            returnMap.put("code", 200);
            returnMap.put("msg", "日志文件不存在");
        }

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }

    /**
     * 根据模型查询训练数据
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("queryTrainByModelId")
    @ResponseBody
    public Object queryTrainByModelId(HttpServletRequest request) throws Exception {
        String modelId = request.getParameter("modelId");
        Map<String, Object> returnMap = trainClassService.queryTrainsByModelId(modelId);
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }
}
