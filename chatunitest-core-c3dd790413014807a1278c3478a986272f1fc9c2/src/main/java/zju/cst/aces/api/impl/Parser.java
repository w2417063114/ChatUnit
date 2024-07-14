package zju.cst.aces.api.impl;

import lombok.Data;
import zju.cst.aces.api.PreProcess;
import zju.cst.aces.api.Task;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.parser.ProjectParser;

/**
 * Parser 类实现了 PreProcess 接口，用于处理项目文件的预处理和解析。
 * 它使用 ProjectParser 根据提供的配置解析项目。
 */
@Data
public class Parser implements PreProcess {

    ProjectParser parser;
    Config config;

    /**
     * 使用给定的配置初始化 Parser 的构造函数。
     *
     * @param config 包含项目设置的配置对象。
     */
    public Parser(Config config) {
        this.config = config;
        this.parser = new ProjectParser(config);
    }

    /**
     * 执行项目的预处理和解析。
     */
    @Override
    public void process() {
        this.parse();
    }

    /**
     * 根据提供的配置解析项目。
     * 检查目标文件夹，如果项目类型为 "pom" 则跳过，
     * 如果输出不存在则执行解析。
     */
    public void parse() {
        try {
            Task.checkTargetFolder(config.getProject());
        } catch (RuntimeException e) {
            config.getLog().error(e.toString());
            return;
        }
        if (config.getProject().getPackaging().equals("pom")) {
            config.getLog().info("\n==========================\n[ChatUniTest] 跳过 pom 打包类型的项目...");
            return;
        }
        if (!config.getParseOutput().toFile().exists()) {
            config.getLog().info("\n==========================\n[ChatUniTest] 正在解析类信息...");
            parser.parse();
            config.getLog().info("\n==========================\n[ChatUniTest] 解析完成");
        } else {
            config.getLog().info("\n==========================\n[ChatUniTest] 解析输出已存在，跳过解析！");
        }
    }
}

