package com.watermelon.domain.task.service;


import com.watermelon.domain.task.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 处理task
 *
 * @author watermelon
 */
public interface TaskDomainService {


    /**
     * 提交任务
     * @param newTask 新任务
     * @return boolean
     */
    boolean submitTask(Task newTask);

    List<Task> listByTask(Task query);

    /**
     * 查询准备就绪的任务
     *
     * @return 准备就绪的任务
     */
    Optional<Task> pickReadyTask(String instance);


    /**
     * instance释放任务，并更新状态至成功
     *
     * @param bindTask 要释放的绑定的任务
     * @param instance 当前实例
     * @return boolean
     */
    boolean releaseTaskAndDone(Task bindTask, String instance, String fileUrl);

    /**
     * instance释放任务，并处理失败
     *
     * @param bindTask 要释放的绑定的任务
     * @param instance 当前实例
     * @param errorMsg 失败信息
     * @param errorTime 失败时间
     * @return boolean
     */
    boolean releaseTaskAndFail(Task bindTask, String instance, String errorMsg, LocalDateTime errorTime);



}
