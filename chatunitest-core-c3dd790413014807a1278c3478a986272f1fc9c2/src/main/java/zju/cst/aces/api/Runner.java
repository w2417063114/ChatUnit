package zju.cst.aces.api;

import zju.cst.aces.dto.MethodInfo;

/**
 * Runner 接口定义了在类和方法级别运行任务的方法。
 */
public interface Runner {

    /**
     * 在类级别运行任务。
     *
     * @param className 要运行任务的类的名称。
     */
    void runClass(String className);

    /**
     * 在方法级别运行任务。
     *
     * @param className 包含该方法的类的名称。
     * @param methodInfo 方法信息。
     */
    void runMethod(String className, MethodInfo methodInfo);
}
