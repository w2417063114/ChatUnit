package zju.cst.aces.api.impl;

import zju.cst.aces.api.Runner;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.runner.ClassRunner;
import zju.cst.aces.runner.MethodRunner;

import java.io.IOException;

/**
 * RunnerImpl 类实现了 Runner 接口，提供在类和方法级别运行任务的方法。
 */
public class RunnerImpl implements Runner {
     Config config;

    /**
     * 使用给定的配置初始化 RunnerImpl 的构造函数。
     *
     * @param config 包含项目设置的配置对象。
     */
    public RunnerImpl(Config config) {
        this.config = config;
    }

    /**
     * 使用 ClassRunner 在类级别运行任务。
     *
     * @param fullClassName 要运行任务的类的全限定名。
     * @throws RuntimeException 如果在执行过程中发生 IOException。
     */
    @Override
    public void runClass(String fullClassName) {
        try {
            new ClassRunner(config, fullClassName).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用 MethodRunner 在方法级别运行任务。
     *
     * @param fullClassName 包含该方法的类的全限定名。
     * @param methodInfo 方法信息。
     * @throws RuntimeException 如果在执行过程中发生 IOException。
     */
    @Override
    public void runMethod(String fullClassName, MethodInfo methodInfo) {
        try {
            new MethodRunner(config, fullClassName, methodInfo).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

