package zju.cst.aces.api.impl;

import com.google.j2objc.annotations.ObjectiveCName;
import lombok.Data;
import zju.cst.aces.api.PromptConstructor;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.ClassInfo;
import zju.cst.aces.dto.Message;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.prompt.PromptGenerator;
import zju.cst.aces.runner.AbstractRunner;
import zju.cst.aces.util.TokenCounter;

import java.io.IOException;
import java.util.List;

/**
 * PromptConstructorImpl类实现了PromptConstructor接口，提供了构造提示信息的方法。
 */
@Data
public class PromptConstructorImpl implements PromptConstructor {

     Config config;
     PromptInfo promptInfo;
     List<Message> messages;
     int tokenCount = 0;
     String testName;
     String fullTestName;
     static final String separator = "_";

    /**
     * 使用给定的配置初始化PromptConstructorImpl。
     *
     * @param config 包含项目设置的配置对象。
     */
    public PromptConstructorImpl(Config config) {
        this.config = config;
    }

    /**
     * 生成提示消息的列表。
     *
     * @return 生成的提示消息列表。
     */
    @Override
    public List<Message> generate() {
        try {
            if (promptInfo == null) {
                throw new RuntimeException("PromptInfo is null, you need to initialize it first.");
            }
            this.messages = new PromptGenerator(config).generateMessages(promptInfo);
            countToken();
            return this.messages;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用依赖项设置提示信息。
     *
     * @param classInfo 类信息对象。
     * @param methodInfo 方法信息对象。
     * @throws IOException 如果发生I/O错误。
     */
    public void setPromptInfoWithDep(ClassInfo classInfo, MethodInfo methodInfo) throws IOException {
        this.promptInfo = AbstractRunner.generatePromptInfoWithDep(config, classInfo, methodInfo);
    }

    /**
     * 在没有依赖项的情况下设置提示信息。
     *
     * @param classInfo 类信息对象。
     * @param methodInfo 方法信息对象。
     * @throws IOException 如果发生I/O错误。
     */
    public void setPromptInfoWithoutDep(ClassInfo classInfo, MethodInfo methodInfo) throws IOException {
        this.promptInfo = AbstractRunner.generatePromptInfoWithoutDep(config, classInfo, methodInfo);
    }

    /**
     * 设置全限定的测试名称，并相应地更新测试名称。
     *
     * @param fullTestName 全限定的测试名称。
     */
    public void setFullTestName(String fullTestName) {
        this.fullTestName = fullTestName;
        this.testName = fullTestName.substring(fullTestName.lastIndexOf(".") + 1);
        this.promptInfo.setFullTestName(this.fullTestName);
    }

    /**
     * 设置测试名称。
     *
     * @param testName 测试名称。
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * 计算提示消息中的令牌数量。
     */
    public void countToken() {
        for (Message p : messages) {
            this.tokenCount += TokenCounter.countToken(p.getContent());
        }
    }

    /**
     * 检查令牌数量是否超过最大允许数量。
     *
     * @return 如果令牌数量超过最大值，则返回true，否则返回false。
     */
    public boolean isExceedMaxTokens() {
        return this.tokenCount > config.maxPromptTokens;
    }
}

