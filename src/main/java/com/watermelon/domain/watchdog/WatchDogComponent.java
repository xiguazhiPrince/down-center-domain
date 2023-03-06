package com.watermelon.domain.watchdog;


/**
 * 两个组件之一：watchDog
 * <li> 给处理中的task续期
 * <li> 处理超时的task,重置它的状态
 *
 * @author water
 */
public interface WatchDogComponent {


    /**
     * 给绑定当前instanceId的task续期
     * 每TaskContext.getBindTimeoutMinutes()/2的时间调用一次该方法
     */
    void delayCurrentInstanceTask();


    /**
     * 重置绑定了instanceId的过期的task
     */
    void resetTimeoutTask();
}
