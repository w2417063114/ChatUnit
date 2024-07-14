package zju.cst.aces.api.impl;

import lombok.Data;
import zju.cst.aces.api.Repair;
import zju.cst.aces.dto.ChatResponse;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.runner.MethodRunner;

import static zju.cst.aces.runner.AbstractRunner.*;
import static zju.cst.aces.api.impl.ChatGenerator.*;

/**
 * RepairImpl 类实现了 Repair 接口，提供基于规则和大语言模型（LLM）的代码修复方法。
 */
@Data
public class RepairImpl implements Repair {

     Config config;
     PromptConstructorImpl promptConstructorImpl;
     boolean success = false;

    /**
     * 使用给定的配置和提示构造器初始化 RepairImpl 的构造函数。
     *
     * @param config 配置对象，包含项目设置。
     * @param promptConstructorImpl 提示构造器实现，用于生成修复提示。
     */
    public RepairImpl(Config config, PromptConstructorImpl promptConstructorImpl) {
        this.config = config;
        this.promptConstructorImpl = promptConstructorImpl;
    }

    /**
     * 基于预定义规则修复代码。
     *
     * @param code 要修复的代码。
     * @return 修复后的代码。
     */
    @Override
    public String ruleBasedRepair(String code) {
        code = changeTestName(code, promptConstructorImpl.getTestName());
        code = repairPackage(code, promptConstructorImpl.getPromptInfo().getClassInfo().getPackageName());
        code = repairImports(code, promptConstructorImpl.getPromptInfo().getClassInfo().getImports());
        return code;
    }

    /**
     * 基于大语言模型（LLM）修复代码，指定修复轮次。
     *
     * @param code 要修复的代码。
     * @param rounds 修复轮次。
     * @return 修复后的代码。
     */
    @Override
    public String LLMBasedRepair(String code, int rounds) {
        PromptInfo promptInfo = promptConstructorImpl.getPromptInfo();
        promptInfo.setUnitTest(code);
        String fullClassName = promptInfo.getClassInfo().getPackageName() + "." + promptInfo.getClassInfo().getClassName();
        if (MethodRunner.runTest(config, promptConstructorImpl.getFullTestName(), promptInfo, rounds)) {
            this.success = true;
            return code;
        }

        promptConstructorImpl.generate();
        if (promptConstructorImpl.isExceedMaxTokens()) {
            config.getLog().error("超过最大提示令牌数量: " + promptInfo.methodInfo.methodName + "，已跳过。");
            return code;
        }
        ChatResponse response = chat(config, promptConstructorImpl.getMessages());
        String newcode = extractCodeByResponse(response);
        if (newcode.isEmpty()) {
            config.getLog().warn("方法 < " + promptInfo.methodInfo.methodName + " > 的测试代码提取失败");
            return code;
        } else {
            return newcode;
        }
    }

    /**
     * 基于大语言模型（LLM）修复代码。
     *
     * @param code 要修复的代码。
     * @return 修复后的代码。
     */
    @Override
    public String LLMBasedRepair(String code) {
        PromptInfo promptInfo = promptConstructorImpl.getPromptInfo();
        promptInfo.setUnitTest(code);
        String fullClassName = promptInfo.getClassInfo().getPackageName() + "." + promptInfo.getClassInfo().getClassName();
        if (MethodRunner.runTest(config, promptConstructorImpl.getFullTestName(), promptInfo, 0)) {
            config.getLog().info("方法 < " + promptInfo.methodInfo.methodName + " > 的测试不需要修复");
            return code;
        }

        promptConstructorImpl.generate();

        if (promptConstructorImpl.isExceedMaxTokens()) {
            config.getLog().error("超过最大提示令牌数量: " + promptInfo.methodInfo.methodName + "，已跳过。");
            return code;
        }
        ChatResponse response = chat(config, promptConstructorImpl.getMessages());
        String newcode = extractCodeByResponse(response);
        if (newcode.isEmpty()) {
            config.getLog().warn("方法 < " + promptInfo.methodInfo.methodName + " > 的测试代码提取失败");
            return code;
        } else {
            return newcode;
        }
    }
}
