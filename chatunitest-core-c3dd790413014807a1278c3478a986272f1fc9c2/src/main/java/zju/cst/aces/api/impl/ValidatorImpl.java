package zju.cst.aces.api.impl;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import lombok.Data;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import zju.cst.aces.api.Validator;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.util.TestCompiler;

import java.nio.file.Path;
import java.util.List;

/**
 * ValidatorImpl 类实现了 Validator 接口，提供了语法、语义和运行时验证代码的方法。
 */
@Data
public class ValidatorImpl implements Validator {

     TestCompiler compiler;

    /**
     * 使用给定的路径和类路径元素初始化 ValidatorImpl 的构造函数。
     *
     * @param testOutputPath 测试输出路径。
     * @param compileOutputPath 编译输出路径。
     * @param targetPath 目标路径。
     * @param classpathElements 类路径元素列表。
     */
    public ValidatorImpl(Path testOutputPath, Path compileOutputPath, Path targetPath, List<String> classpathElements) {
        this.compiler = new TestCompiler(testOutputPath, compileOutputPath, targetPath, classpathElements);
    }

    /**
     * 通过解析代码来进行语法验证。
     *
     * @param code 要验证的代码。
     * @return 如果代码语法正确，则返回 true，否则返回 false。
     */
    @Override
    public boolean syntacticValidate(String code) {
        try {
            StaticJavaParser.parse(code);
            return true;
        } catch (ParseProblemException e) {
            return false;
        }
    }

    /**
     * 通过编译代码来进行语义验证。
     *
     * @param code 要验证的代码。
     * @param className 包含代码的类名。
     * @param outputPath 保存编译代码的路径。
     * @param promptInfo 代码的提示信息。
     * @return 如果代码语义正确，则返回 true，否则返回 false。
     */
    @Override
    public boolean semanticValidate(String code, String className, Path outputPath, PromptInfo promptInfo) {
        compiler.setCode(code);
        return compiler.compileTest(className, outputPath, promptInfo);
    }

    /**
     * 通过执行编译的测试来进行运行时验证。
     *
     * @param fullTestName 测试类的全限定名。
     * @return 如果所有测试都通过，则返回 true，否则返回 false。
     */
    @Override
    public boolean runtimeValidate(String fullTestName) {
        return compiler.executeTest(fullTestName).getTestsFailedCount() == 0;
    }

    /**
     * 编译代码。
     *
     * @param className 要编译的类名。
     * @param outputPath 保存编译代码的路径。
     * @param promptInfo 代码的提示信息。
     * @return 如果编译成功，则返回 true，否则返回 false。
     */
    @Override
    public boolean compile(String className, Path outputPath, PromptInfo promptInfo) {
        return compiler.compileTest(className, outputPath, promptInfo);
    }

    /**
     * 执行编译的测试并返回执行摘要。
     *
     * @param fullTestName 测试类的全限定名。
     * @return 测试的执行摘要。
     */
    @Override
    public TestExecutionSummary execute(String fullTestName) {
        return compiler.executeTest(fullTestName);
    }
}

