package com.watermelon.domain.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.watermelon.domain.config.TaskContext;
import com.watermelon.domain.task.entity.ErrorMsg;
import com.watermelon.domain.task.entity.Task;
import com.watermelon.domain.task.enums.TaskStatusEnum;
import com.watermelon.domain.task.repository.TaskRepository;
import com.watermelon.domain.task.service.TaskDomainService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author watermelon
 */
public class TaskDomainServiceImpl implements TaskDomainService {

    private final TaskRepository taskRepository;

    public TaskDomainServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public boolean submitTask(Task newTask) {
        String taskId = newTask.getTaskId();
        assert taskId != null;
        String handlerName = newTask.getHandlerName();
        assert handlerName != null;

        String requestJson = newTask.getRequestJson();

        String sysUpdateUserId = newTask.getSysUpdateUserId();
        String operatorUserId = newTask.getOperatorUserId();
        String sysCreateUserId = newTask.getSysCreateUserId();

        Task task = new Task()
                .setTaskId(taskId)
                .setHandlerName(handlerName)
                .setRequestJson(requestJson)
                .setOperatorUserId(operatorUserId)
                .setSysCreateTime(LocalDateTime.now())
                .setSysUpdateTime(LocalDateTime.now())
                .setSysCreateUserId(sysCreateUserId)
                .setSysUpdateUserId(sysUpdateUserId)
                .setInstanceId("")
                .setExpireTime(LocalDateTime.now())
                .setTaskStatus(TaskStatusEnum.ready)
                .setErrorMsgList(new ArrayList<>())
                .setErrorCount(0)
                .setFileDownUrl("")
                .setSysIsDelete(false)
                .setSysVersion(0);

        return taskRepository.submitTask(task);
    }

    @Override
    public List<Task> listByTask(Task query) {
        return taskRepository.listByTask(query);
    }

    @Override
    public Optional<Task> pickReadyTask(String instanceId) {

        Task taskToSet = new Task().setInstanceId(instanceId).setTaskStatus(TaskStatusEnum.pending);
        Task taskToQuery = new Task().setInstanceId("")
                .setErrorCount(TaskContext.getMaxErrorCount())
                .setTaskStatus(TaskStatusEnum.ready);
        boolean bind = taskRepository.bindTask(taskToSet, taskToQuery);
        if (bind){
            return Optional.ofNullable(taskRepository.pickBindTask(instanceId));
        }
        return Optional.empty();
    }

    @Override
    public boolean releaseTaskAndDone(Task bindTask, String instance, String fileUrl) {
        Task taskToSet = new Task().setInstanceId("").setTaskStatus(TaskStatusEnum.done).setFileDownUrl(fileUrl);
        Task taskToSelect = new Task().setTaskId(bindTask.getTaskId()).setTaskStatus(TaskStatusEnum.pending);
        return taskRepository.releaseTaskAndDone(taskToSet, taskToSelect);
    }

    @Override
    public boolean releaseTaskAndFail(Task bindTask, String instance, String errorMsg, LocalDateTime errorTime) {

        List<ErrorMsg> errorMsgList = CollUtil.defaultIfEmpty(bindTask.getErrorMsgList(), new ArrayList<>());
        errorMsgList.add(new ErrorMsg(errorMsg, errorTime));

        int errorCount = bindTask.getErrorCount() == null ? 0 : bindTask.getErrorCount() + 1;

        Task taskToSet = new Task()
                .setTaskId(bindTask.getTaskId())
                .setInstanceId(instance)
                .setErrorMsgList(errorMsgList)
                .setErrorCount(errorCount)
                .setTaskStatus(TaskStatusEnum.ready)
                .setExpireTime(LocalDateTime.now())
                .setInstanceId("");

        Task taskToSelect = new Task().setTaskId(bindTask.getTaskId()).setTaskStatus(TaskStatusEnum.pending);

        return taskRepository.releaseTaskAndFail(taskToSet, taskToSelect);
    }


}
