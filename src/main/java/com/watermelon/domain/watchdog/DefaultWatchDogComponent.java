package com.watermelon.domain.watchdog;

import com.watermelon.domain.config.TaskContext;
import com.watermelon.domain.task.enums.TaskStatusEnum;
import com.watermelon.domain.task.repository.TaskRepository;

import java.time.LocalDateTime;

/**
 * @author water
 */
public class DefaultWatchDogComponent implements WatchDogComponent {

    private final TaskRepository taskRepository;

    public DefaultWatchDogComponent(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public synchronized void delayCurrentInstanceTask(){
        LocalDateTime newTime = LocalDateTime.now().plusMinutes(TaskContext.getBindTimeoutMinutes());
        taskRepository.resetExpireTime(TaskContext.getInstanceId(), TaskStatusEnum.pending, newTime);
    }


    @Override
    public synchronized void resetTimeoutTask(){
        taskRepository.resetTimeoutTask(TaskStatusEnum.ready);
    }

}