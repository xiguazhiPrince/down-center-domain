package com.watermelon.domain.task.enums;



/**
 *  任务状态
 * @author water
 */
public enum TaskStatusEnum {

    /**
     *
     */
    ready("就绪"),
    pending("导出中"),
    //    fail("失败"),
    done("完成");

    TaskStatusEnum(String cnDesc) {
    }
}