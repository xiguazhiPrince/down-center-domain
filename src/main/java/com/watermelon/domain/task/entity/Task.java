package com.watermelon.domain.task.entity;

import com.watermelon.domain.task.enums.TaskStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务
 * @author watermelon
 */
@Data
@Accessors(chain = true)
public class Task implements Serializable {

    /**
     * 主键，任务ID
     */
    private String taskId;

    /**
     * 绑定的服务的实例ID
     */
    private String instanceId;

    /**
     * 任务绑定到服务的到期时间
     */
    private LocalDateTime expireTime;

    /**
     * 任务状态
     */
    private TaskStatusEnum taskStatus;

    /**
     * 处理错误信息
     */
    private List<ErrorMsg> errorMsgList;

    /**
     * 任务出错总数
     */
    private Integer errorCount;

    /**
     * 任务处理单元的唯一标识符
     */
    private String handlerName;

    /**
     * 请求分页数据的request参数
     */
    private String requestJson;

    /**
     * 操作员编号
     */
    private String operatorUserId;

    /**
     * 任务处理成功后生成的下载路径
     */
    private String fileDownUrl;


    /**
     * 系统参数
     */
    private LocalDateTime sysCreateTime;
    private LocalDateTime sysUpdateTime;
    private String sysCreateUserId;
    private String sysUpdateUserId;
    private Boolean sysIsDelete;
    private Integer sysVersion;
}
