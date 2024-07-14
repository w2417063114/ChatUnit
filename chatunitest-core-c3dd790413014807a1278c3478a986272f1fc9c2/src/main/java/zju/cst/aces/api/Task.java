package zju.cst.aces.api;

import zju.cst.aces.api.Project;
import zju.cst.aces.api.Runner;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.ClassInfo;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.parser.ProjectParser;
import zju.cst.aces.runner.AbstractRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import zju.cst.aces.api.Logger;
import zju.cst.aces.util.Counter;

/**
 * Task类提供在方法、类和项目级别启动任务的方法。
 * 包括运行特定方法或类上的测试以及整个项目的测试。
 */
public class Task {

     Config config;
     Logger log;
     Runner runner;

    /**
     * 构造一个Task对象，使用给定的配置和runner。
     *
     * @param config 包含项目设置的配置对象。
     * @param runner 用于执行任务的runner对象。
     */
    public Task(Config config, Runner runner) {
        this.config = config;
        this.log = config.getLog();
        this.runner = runner;
    }

    /**
     * 启动任务以为类中的特定方法生成测试。
     *
     * @param className 包含方法的类名。
     * @param methodName 要生成测试的方法名。
     */
    public void startMethodTask(String className, String methodName) {
        try {
            checkTargetFolder(config.getProject());
        } catch (RuntimeException e) {
            log.error(e.toString());
            return;
        }
        if (config.getProject().getPackaging().equals("pom")) {
            log.info("\n==========================\n[ChatUniTest] Skip pom-packaging ...");
            return;
        }
        ProjectParser parser = new ProjectParser(config);
        parser.parse();
        log.info("\n==========================\n[ChatUniTest] Generating tests for class: < " + className
                + "> method: < " + methodName + " > ...");

        try {
            String fullClassName = getFullClassName(config, className);
            ClassInfo classInfo = AbstractRunner.getClassInfo(config, fullClassName);
            MethodInfo methodInfo = null;
            if (methodName.matches("\\d+")) { // 使用方法id而不是方法名
                String methodId = methodName;
                for (String mSig : classInfo.methodSigs.keySet()) {
                    if (classInfo.methodSigs.get(mSig).equals(methodId)) {
                        methodInfo = AbstractRunner.getMethodInfo(config, classInfo, mSig);
                        break;
                    }
                }
                if (methodInfo == null) {
                    throw new IOException("方法 " + methodName + " 在类 " + fullClassName + " 中未找到");
                }
                try {
                    this.runner.runMethod(fullClassName, methodInfo);
                } catch (Exception e) {
                    log.error("生成测试时出错 " + methodName + " 在 " + className + " " + config.getProject().getArtifactId() + "\n" + e.getMessage());
                }
            } else {
                for (String mSig : classInfo.methodSigs.keySet()) {
                    if (mSig.split("\\(")[0].equals(methodName)) {
                        methodInfo = AbstractRunner.getMethodInfo(config, classInfo, mSig);
                        if (methodInfo == null) {
                            throw new IOException("方法 " + methodName + " 在类 " + fullClassName + " 中未找到");
                        }
                        try {
                            this.runner.runMethod(fullClassName, methodInfo);
                        } catch (Exception e) {
                            log.error("生成测试时出错 " + methodName + " 在 " + className + " " + config.getProject().getArtifactId() + "\n" + e.getMessage());
                        }
                    }
                }
            }

        } catch (IOException e) {
            log.warn("未找到方法: " + methodName + " 在 " + className + " " + config.getProject().getArtifactId());
            return;
        }

        log.info("\n==========================\n[ChatUniTest] 生成完成");
    }

    /**
     * 启动任务以为特定类生成测试。
     *
     * @param className 要生成测试的类名。
     */
    public void startClassTask(String className) {
        try {
            checkTargetFolder(config.getProject());
        } catch (RuntimeException e) {
            log.error(e.toString());
            return;
        }
        if (config.getProject().getPackaging().equals("pom")) {
            log.info("\n==========================\n[ChatUniTest] Skip pom-packaging ...");
            return;
        }
        ProjectParser parser = new ProjectParser(config);
        parser.parse();
        log.info("\n==========================\n[ChatUniTest] Generating tests for class < " + className + " > ...");
        try {
            this.runner.runClass(getFullClassName(config, className));
        } catch (IOException e) {
            log.warn("未找到类: " + className + " 在 " + config.getProject().getArtifactId());
        }
        log.info("\n==========================\n[ChatUniTest] 生成完成");
    }

    /**
     * 启动任务以为整个项目生成测试。
     */
    public void startProjectTask() {
        Project project = config.getProject();
        try {
            checkTargetFolder(project);
        } catch (RuntimeException e) {
            log.error(e.toString());
            return;
        }
        if (project.getPackaging().equals("pom")) {
            log.info("\n==========================\n[ChatUniTest] Skip pom-packaging ...");
            return;
        }
        ProjectParser parser = new ProjectParser(config);
        parser.parse();
        List<String> classPaths = ProjectParser.scanSourceDirectory(project);
        if (config.isEnableMultithreading()) {
            projectJob(classPaths);
        } else {
            for (String classPath : classPaths) {
                String className = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
                try {
                    String fullClassName = getFullClassName(config, className);
                    log.info("\n==========================\n[ChatUniTest] Generating tests for class < " + className + " > ...");
                    ClassInfo info = AbstractRunner.getClassInfo(config, fullClassName);
                    if (!Counter.filter(info)) {
                        config.getLog().info("跳过类: " + classPath);
                        continue;
                    }
                    this.runner.runClass(fullClassName);
                } catch (IOException e) {
                    log.error("[ChatUniTest] 为类生成测试 " + className + " 失败: " + e);
                }
            }
        }

        log.info("\n==========================\n[ChatUniTest] 生成完成");
    }

    /**
     * 如果启用多线程，则使用多线程执行项目任务。
     *
     * @param classPaths 要处理的类路径列表。
     */
    public void projectJob(List<String> classPaths) {
        ExecutorService executor = Executors.newFixedThreadPool(config.getClassThreads());
        List<Future<String>> futures = new ArrayList<>();
        for (String classPath : classPaths) {
            Callable<String> callable = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String className = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
                    try {
                        String fullClassName = getFullClassName(config, className);
                        log.info("\n==========================\n[ChatUniTest] Generating tests for class < " + className + " > ...");
                        ClassInfo info = AbstractRunner.getClassInfo(config, fullClassName);
                        if (!Counter.filter(info)) {
                            return "跳过类: " + classPath;
                        }
                        runner.runClass(fullClassName);
                    } catch (IOException e) {
                        log.error("[ChatUniTest] 为类生成测试 " + className + " 失败: " + e);
                    }
                    return "已处理 " + classPath;
                }
            };
            Future<String> future = executor.submit(callable);
            futures.add(future);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executor.shutdownNow();
            }
        });

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                System.out.println(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    /**
     * 获取给定类名的全限定类名。
     *
     * @param config 包含项目设置的配置对象。
     * @param name 类名。
     * @return 全限定类名。
     * @throws IOException 如果发生I/O错误。
     */
    public static String getFullClassName(Config config, String name) throws IOException {
        if (isFullName(name)) {
            return name;
        }
        Path classMapPath = config.getClassNameMapPath();
        Map<String, List<String>> classMap = config.getGSON().fromJson(Files.readString(classMapPath, StandardCharsets.UTF_8), Map.class);
        if (classMap.containsKey(name)) {
            if (classMap.get(name).size() > 1) {
                throw new RuntimeException("[ChatUniTest] 多个类命名为 " + name + ": " + classMap.get(name)
                        + " 请使用全限定名称！");
            }
            return classMap.get(name).get(0);
        }
        return name;
    }

    /**
     * 检查给定类名是否是全限定名。
     *
     * @param name 类名。
     * @return 如果名称是全限定名，则返回true，否则返回false。
     */
    public static boolean isFullName(String name) {
        return name.contains(".");
    }

    /**
     * 检查类是否已编译。
     *
     * @param project 项目对象。
     */
    public static void checkTargetFolder(Project project) {
        if (project.getPackaging().equals("pom")) {
            return;
        }
        if (!new File(project.getBuildPath().toString()).exists()) {
            throw new RuntimeException("在 ProjectTestMojo.checkTargetFolder 中: " +
                    "项目未编译到目标目录。请先运行 'mvn install'。");
        }
    }
}
