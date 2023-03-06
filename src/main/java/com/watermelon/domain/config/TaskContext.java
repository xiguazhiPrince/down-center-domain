package com.watermelon.domain.config;


import com.watermelon.domain.task.external.DataSelectorUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 配置类和任务注册类
 */
public class TaskContext {

    private final static Map<String, DataSelectorUnit<?>> taskHandlerMap = new HashMap<>();

    /**
     * 临时文件的路径
     */
    private static String tempFilePath = "/temp/";

    /**
     * excel文件后缀
     */
    private static String tempExcelSuffix = ".xlsx";


    /**
     * 每批数据的条数
     */
    private static Integer pageSize = 5000;


    /**
     * 失败重试停止的次数
     */
    private static Integer maxErrorCount = 5;


    /**
     * 绑定任务失效的时间，分钟
     */
    private static Integer bindTimeoutMinutes = 6;

    /**
     * 实例ID
     */
    private static String instanceId = UUID.randomUUID().toString().replace("-","");;

    private TaskContext() {
    }


    public static Integer getMaxErrorCount() {
        return maxErrorCount;
    }

    public static void setMaxErrorCount(Integer maxErrorCount) {
        TaskContext.maxErrorCount = maxErrorCount;
    }

    public static Integer getBindTimeoutMinutes() {
        return bindTimeoutMinutes;
    }

    public static void setBindTimeoutMinutes(Integer bindTimeoutMinutes) {
        TaskContext.bindTimeoutMinutes = bindTimeoutMinutes;
    }

    public static String getInstanceId() {
        return instanceId;
    }

    public static void setInstanceId(String instanceId) {
        TaskContext.instanceId = instanceId;
    }

    public static String getTempFilePath() {
        return tempFilePath;
    }

    public static void setTempFilePath(String tempFilePath) {
        TaskContext.tempFilePath = tempFilePath;
    }

    public static String getTempExcelSuffix() {
        return tempExcelSuffix;
    }

    public static void setTempExcelSuffix(String tempExcelSuffix) {
        TaskContext.tempExcelSuffix = tempExcelSuffix;
    }


    public static Integer getPageSize() {
        return pageSize;
    }

    public static void setPageSize(Integer pageSize) {
        TaskContext.pageSize = pageSize;
    }


    /**
     * 注册DataSelectorUnit
     */
    public static void register(DataSelectorUnit<?> dataSelectorUnit){
        if (dataSelectorUnit == null){
            throw new RuntimeException("无效的TaskHandler");
        }

        String handlerName = Optional
                .ofNullable(dataSelectorUnit.handlerName())
                .orElseThrow(() -> new RuntimeException("无效的taskHandlerEnum"));

        if (taskHandlerMap.containsKey(handlerName)){
            throw new RuntimeException("有同名的handler已经注册:"+handlerName);
        }

        taskHandlerMap.put(handlerName, dataSelectorUnit);
    }

    public static Optional<DataSelectorUnit<?>> getHandler(String handlerName){
        if (handlerName == null){
            throw new RuntimeException("无效的handlerName");
        }

        return Optional.ofNullable(taskHandlerMap.get(handlerName));
    }

}
