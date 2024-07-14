package zju.cst.aces.api;

import org.junit.platform.launcher.listeners.TestExecutionSummary;
import zju.cst.aces.dto.PromptInfo;

import java.nio.file.Path;

/**
 * Validator接口定义了在不同级别验证代码的方法：语法、语义和运行时。
 */
public interface Validator {

    /**
     * 通过解析代码进行语法验证。
     *
     * @param code 要验证的代码。
     * @return 如果代码语法正确，则返回true，否则返回false。
     */
    boolean syntacticValidate(String code);

    /**
     * 通过编译代码进行语义验证。
     *
     * @param code 要验证的代码。
     * @param className 包含代码的类的名称。
     * @param outputPath 编译后代码保存的路径。
     * @param promptInfo 代码的提示信息。
     * @return 如果代码语义正确，则返回true，否则返回false。
     */
    boolean semanticValidate(String code, String className, Path outputPath, PromptInfo promptInfo);

    /**
     * 通过执行编译后的测试进行运行时验证。
     *
     * @param fullTestName 测试类的全限定名称。
     * @return 如果所有测试都通过，则返回true，否则返回false。
     */
    boolean runtimeValidate(String fullTestName);

    /**
     * 编译代码。
     *
     * @param className 要编译的类的名称。
     * @param outputPath 编译后代码保存的路径。
     * @param promptInfo 代码的提示信息。
     * @return 如果编译成功，则返回true，否则返回false。
     */
    boolean compile(String className, Path outputPath, PromptInfo promptInfo);

    /**
     * 执行编译后的测试并返回执行摘要。
     *
     * @param fullTestName 测试类的全限定名称。
     * @return 测试的执行摘要。
     */
    TestExecutionSummary execute(String fullTestName);
}
