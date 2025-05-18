
# 简介

* 解决导出大数据量Excel时候的下载问题
* 采用异步提交下载任务，并定时清除任务的方式实现
* 采用DDD将核心逻辑抽取了domain模块

# 架构设计


有2个核心组件：任务处理器、watchDog

##  任务处理器: TaskHandlerComponent
1. 用于异步处理task任务, 目前是串行处理任务
2. 把task任务绑定到instance，防止重复处理


## watchDog: WatchDogComponent
1. 对于超时未完成处理的任务（假如instance挂了），重置task的绑定和处理状态
2. 给正在处理中的任务进行续期，防止处理时间过长导致task被重置


<br>

![image](https://github.com/xiguazhiPrince/down-center-domain/blob/master/mind.png "img")

时序流程图：
![时序图](https://github.com/xiguazhiPrince/down-center-domain/blob/master/diagram.jpg "时序图")


# 使用


需要在下载中心服务中引入domain module，实现接口和注入bean

使用案例：[down-center-sample](https://github.com/xiguazhiPrince/down-center-sample)



```java
@Configuration
public class DownCenterBeanConfig {

    /**
     * 需要自己实现task的Repository接口 {@link com.watermelon.domain.task.repository.TaskRepository}
     * <br>
     * 可以自定义数据库，但是字段需要和 {@link com.watermelon.domain.task.entity.Task} 的一致
     */
    @Bean
    public TaskRepository taskRepository(){
        return new TestTaskRepository();
    }

    /**
     * 自己实现文件上传类 {@link com.watermelon.domain.task.external.UploadFileService}
     */
    @Bean
    public S3UploadFileServiceImpl uploadFileService(){
        return new S3UploadFileServiceImpl();
    }

    /**
     * 注入{@link TaskDomainServiceImpl}
     */
    @Bean
    public TaskDomainService taskDomainService(TaskRepository testTaskRepository){
        return new TaskDomainServiceImpl(testTaskRepository);
    }

    /**
     * 注入组件：任务处理器 {@link TaskHandlerComponent}
     */
    @Bean
    public DefaultTaskHandlerComponent taskHandler(TaskDomainService taskDomainService,
                                                   S3UploadFileServiceImpl uploadFileService){
        return new DefaultTaskHandlerComponent(taskDomainService, uploadFileService);
    }

    /**
     * 注入组件: watchDog {@link WatchDogComponent}
     */
    @Bean
    public DefaultWatchDogComponent watchDog(TaskRepository taskRepository){
        return new DefaultWatchDogComponent(taskRepository);
    }


    /**
     * 注入自己实现生成excel数据的Selector {@link com.watermelon.domain.task.external.DataSelectorUnit}
     */
    @Bean
    public TestExcelHandlerUnit testExcelHandlerUnit(){
        return new TestExcelHandlerUnit();
    }

    /**
     * 可以自定义一些配置
     */
    @Bean
    @ConfigurationProperties(prefix = "down-center", ignoreUnknownFields = true)
    public DownSettingConfig downConfig(){
        return new DownSettingConfig();
    }


    /**
     * 启动两个组件
     */
    @PostConstruct
    public void init(){
        DownSettingConfig downSettingConfig = downConfig();
        TaskContext.setPageSize(downSettingConfig.getPageSize());
        TaskContext.setBindTimeoutMinutes(downSettingConfig.getBindTimeoutMinutes());

        startTask();

        startWatchDog();
    }


    /**
     * 开启异步处理任务
     */
    private void startTask() {
        DefaultTaskHandlerComponent defaultTaskHandler = taskHandler(taskDomainService(taskRepository()), uploadFileService());
        Executors
                .newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(
                        defaultTaskHandler::createAndUploadHandler,
                        1, 1, TimeUnit.SECONDS
                );
    }

    /**
     * 启动watchDog
     */
    private void startWatchDog() {
        DefaultWatchDogComponent defaultWatchDog = watchDog(taskRepository());
        Executors
                .newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(
                        ()->{
                            defaultWatchDog.delayCurrentInstanceTask();
                            defaultWatchDog.resetTimeoutTask();
                        },
                        1, 1, TimeUnit.SECONDS
                );
    }

}

```


# 功能清单

总共有两个核心组件:
1. 任务处理器: 查询数据、生成文件、上传文件
2. watchDog: 清理失效任务，给长耗时任务续期

还有2个工具类:
1. 使用注解指定表头，生成excel文件
2. 数据分页查询的通用工具




# 后续规划

## 添加多线程处理能力
目前可以解决多个服务同时部署的任务抢占问题  
但是如果创建了多个任务处理器，使用多线程去处理的话，会导致任务在同一实例间被抢占  
后续计划引入多线程处理能力，充分利用资源加快任务吞吐能力



# 更新日志

* 添加了合并单元格功能
