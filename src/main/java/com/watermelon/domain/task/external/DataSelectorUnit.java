package com.watermelon.domain.task.external;

import com.watermelon.domain.config.TaskContext;

import java.util.List;


/**
 * 任务处理类
 * @author water
 */
public abstract class DataSelectorUnit<T> {

    public DataSelectorUnit() {
        register();
    }

    /**
     * 获取handler唯一的name
     */
    abstract public String handlerName();

    /**
     * 注册到Context
     */
    public void register(){
        TaskContext.register(this);
    }

    /**
     * 需要实现分页查询
     */
    abstract public List<T> page(String requestJson, Integer pageIndex, Integer pageSize);

    /**
     * 总数
     */
    abstract public int count(String requestJson);


    /**
     * 返回使用@ExcelHeader标注的导出类，用于生成excel
     */
    abstract public Class<T> excelModel();

}
