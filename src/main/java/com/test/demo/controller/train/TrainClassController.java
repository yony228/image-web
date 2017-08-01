package com.test.demo.controller.train;

import com.alibaba.fastjson.JSONObject;
import com.test.demo.common.FileWebConf;
import com.test.demo.controller.BaseController;
import com.test.demo.service.TrainClassService;
import edu.nudt.das.sansiro.login.interceptor.Auth;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
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
 * Created by Administrator on 2017/6/20.
 */
@Controller
@Auth
@RequestMapping("/train")
public class TrainClassController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainClassController.class);

    @Autowired
    private TrainClassService trainClassService;

    @Autowired
    private FileWebConf fileWebConf;

    @RequestMapping("{pageFile}/{page}")
    public String goPage(HttpServletRequest request, @PathVariable String pageFile, @PathVariable String page, ModelMap modelMap) throws Exception {
        modelMap.put("train_no", request.getParameter("train_no"));
        return "/" + pageFile + "/" + page;
    }

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

        Map<String, Object> returnMap = new HashMap<>();
        String trainNo = trainClassService.submitTrain(classIdList, userId, paramMap);
        if (trainNo != null) {
            returnMap.put("code", "100");
            returnMap.put("trainNo", trainNo);
        } else {
            returnMap.put("code", "200");
            returnMap.put("msg", "失败！");
        }
        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }


    @RequestMapping("queryTrainList")
    @ResponseBody
    public Object queryTrainList(HttpServletRequest request) throws Exception {
        String condition = request.getParameter("condition");

        Map<String, Object> map = new HashMap();
        map.put("condition", condition);
        map.put("page_index", request.getParameter("pageNumber"));
        map.put("page_size", request.getParameter("pageSize"));

        List<Map<String, Object>> list = trainClassService.queryTrainClass(map);

        //统计总数
        map.remove("page_index");
        map.remove("page_size");
        List<Map<String, Object>> countList = trainClassService.queryTrainClass(map);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", list);
        jsonObject.put("total", countList.size());
        return jsonObject;
    }

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

    @RequestMapping("createModels")
    @ResponseBody
    public Object createModels(HttpServletRequest request) throws Exception {
        String trainNo = request.getParameter("train_no");
        String modelName = request.getParameter("modelName");
        String modelDes = request.getParameter("modelDes");
        String userId = MapUtils.getString(getUser(request.getSession()), "id");

        Map<String, Object> returnMap = new HashMap<>();
        if(trainClassService.createModels(trainNo, modelName, modelDes, userId)) {
            returnMap.put("code", 100);
        }else {
            returnMap.put("code", 200);
            returnMap.put("msg", "模型名称已存在");
        }

        JSONObject jsonObject = new JSONObject(returnMap);
        return jsonObject;
    }
}
