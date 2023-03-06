package com.watermelon.domain.task.repository;

import com.watermelon.domain.task.entity.Task;
import com.watermelon.domain.task.enums.TaskStatusEnum;

import java.time.LocalDateTime;
import java.util.List;


/**
 * ● 提交任务
 * ● 查询任务
 * ● 绑定任务到instance
 * ● instance释放任务，并更新状态至成功
 *
 * @author watermelon
 */
public interface TaskRepository {

    /**
     * 提交任务
     * @param newTask 新任务
     * @return boolean
     */
    boolean submitTask(Task newTask);

    List<Task> listByTask(Task query);

    /**
     * 绑定准备就绪的任务
     * <pre>
     *   update task_info
     *   set instance_Id = instanceIdToUpdate, task_status='pending'
     *   where instance_Id == "" and sys_is_delete = false and task_status='reading' limit 1;
     * </pre>
     *
     * @return 准备就绪的任务
     */
    boolean bindTask(Task taskToSet, Task taskToSelect);

    /**
     * 查询一个绑定的任务
     * <pre>
     *   select *
     *   from task_info
     *   where instance_Id == instanceId and sys_is_delete = false limit 1;
     * </pre>
     *
     * @return 准备就绪的任务
     */
    Task pickBindTask(String instanceId);


    /**
     * instance释放任务，并更新状态至成功
     *
     * <pre>
     *   update task_info
     *   set instance_Id = "", task_status='done', file_down_url='xxx'
     *   where task_Id == taskIdToQuery and sys_is_delete = false and task_status='pending';
     * </pre>
     *
     * @return boolean
     */
    boolean releaseTaskAndDone(Task taskToSet, Task taskToSelect);

    /**
     * instance释放任务，并处理失败
     *      <pre>
     *        update task_info
     *        set instance_Id = "", task_status='ready', error_count='xxx', error_msg_list='xxx'
     *        where task_Id == taskId and sys_is_delete = false and task_status='pending';
     *      </pre>
     */
    boolean releaseTaskAndFail(Task taskToSet, Task taskToSelect);




    /**
     * update task_info
     * set expire_time = new_expire_time
     * where task_status='pending' and instance_id = current_instance_id and sys_is_delete = false;
     */
    void resetExpireTime(String instanceId, TaskStatusEnum taskStatusEnum, LocalDateTime newExpireTime);


    /**
     * update task_info
     * set instance_Id="" and task_status = "ready"
     * where instance_Id != "" and expire_time < now and sys_is_delete = false;
     *
     */
    boolean resetTimeoutTask(TaskStatusEnum taskStatusEnum);



}
