package com.watermelon.domain.task;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.watermelon.domain.config.TaskContext;
import com.watermelon.domain.task.entity.Task;
import com.watermelon.domain.task.external.DataSelectorUnit;
import com.watermelon.domain.task.external.UploadFileService;
import com.watermelon.domain.task.service.TaskDomainService;
import com.watermelon.domain.utils.ExcelHeader;
import com.watermelon.domain.utils.ExcelUtilsX;
import com.watermelon.domain.utils.HeaderField;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class DefaultTaskHandlerComponent implements TaskHandlerComponent {

    private final TaskDomainService taskDomainService;

    private final UploadFileService uploadFileService;

    public DefaultTaskHandlerComponent(TaskDomainService taskDomainService, UploadFileService uploadFileService) {
        this.taskDomainService = taskDomainService;
        this.uploadFileService = uploadFileService;
    }


    @Override
    public synchronized boolean createAndUploadHandler() {

        // 查询并绑定一个任务
        Optional<Task> taskOptional = taskDomainService.pickReadyTask(TaskContext.getInstanceId());
        if (!taskOptional.isPresent()){
            return true;
        }
        final Task bindTask = taskOptional.get();

        Optional<DataSelectorUnit<?>> handlerOpt = TaskContext.getHandler(bindTask.getHandlerName());
        if (!handlerOpt.isPresent()){
            return true;
        }
        DataSelectorUnit<?> handlerService = handlerOpt.get();
        LinkedHashMap<String, HeaderField> headerAlias = ExcelUtilsX
                .getHeaderField(handlerService.excelModel(), ExcelHeader.class, HeaderField.class);

        final String path = TaskContext.getTempFilePath() + UUID.randomUUID() + TaskContext.getTempExcelSuffix();

        try {
            // 分页查询该任务的数据，并生成文件
            int count = handlerService.count(bindTask.getRequestJson());
            int partSize = NumberUtil.count(count, TaskContext.getPageSize());
            for (int pageIndex = 1; pageIndex <= partSize; pageIndex++) {
                List<?> data = handlerService.page(bindTask.getRequestJson(), pageIndex, TaskContext.getPageSize());
                ExcelUtilsX.createExcelFile(data, path, headerAlias);
            }

            // 上传文件
            String fileUrl = uploadFileService.uploadFile(path);
            taskDomainService.releaseTaskAndDone(bindTask, TaskContext.getInstanceId(), fileUrl);

        }catch (Exception err){
            // 生成文件失败
            taskDomainService.releaseTaskAndFail(bindTask, TaskContext.getInstanceId(), err.getMessage(), LocalDateTime.now());
        }finally {
            // 删除临时文件
            FileUtil.del(path);
        }

        return true;
    }


}
