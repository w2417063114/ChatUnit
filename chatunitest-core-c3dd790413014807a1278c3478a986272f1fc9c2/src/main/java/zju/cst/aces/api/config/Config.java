package zju.cst.aces.api.config;

import zju.cst.aces.api.Project;
import com.github.javaparser.JavaParser;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import zju.cst.aces.api.Validator;
import zju.cst.aces.api.impl.LoggerImpl;
import zju.cst.aces.api.Logger;
import zju.cst.aces.api.impl.ValidatorImpl;
import zju.cst.aces.prompt.PromptTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Config类定义了配置项目所需的各种参数和方法。
 */
@Getter
@Setter
public class Config {
    public String date;
    public Gson GSON;
    public Project project;
    public JavaParser parser;
    public JavaParserFacade parserFacade;
    public List<String> classPaths;
    public Path promptPath;
    public Properties properties;
    public String url;
    public String[] apiKeys;
    public Logger log;
    public String OS;
    public boolean stopWhenSuccess;
    public boolean noExecution;
    public boolean enableMultithreading;
    public boolean enableRuleRepair;
    public boolean enableMerge;
    public boolean enableObfuscate;
    public String[] obfuscateGroupIds;
    public int maxThreads;
    public int classThreads;
    public int methodThreads;
    public int testNumber;
    public int maxRounds;
    public int maxPromptTokens;
    public int maxResponseTokens;
    public int minErrorTokens;
    public int sleepTime;
    public int dependencyDepth;
    public Model model;
    public Double temperature;
    public int topP;
    public int frequencyPenalty;
    public int presencePenalty;
    public Path testOutput;
    public Path tmpOutput;
    public Path compileOutputPath;
    public Path parseOutput;
    public Path errorOutput;
    public Path classNameMapPath;
    public Path historyPath;
    public Path examplePath;
    public Path symbolFramePath;

    public String proxy;
    public String hostname;
    public String port;
    public OkHttpClient client;
    public static AtomicInteger sharedInteger = new AtomicInteger(0);
    public static Map<String, Map<String, String>> classMapping;
    public Validator validator;

    /**
     * ConfigBuilder类用于构建Config对象。
     */
    public static class ConfigBuilder {
        public String date;
        public Project project;
        public JavaParser parser;
        public JavaParserFacade parserFacade;
        public List<String> classPaths;
        public Path promptPath;
        public Properties properties;
        public String url;
        public String[] apiKeys;
        public Logger log;
        public String OS = System.getProperty("os.name").toLowerCase();
        public boolean stopWhenSuccess = true;
        public boolean noExecution = false;
        public boolean enableMultithreading = true;
        public boolean enableRuleRepair = true;
        public boolean enableMerge = true;
        public boolean enableObfuscate = false;
        public String[] obfuscateGroupIds;
        public int maxThreads = Runtime.getRuntime().availableProcessors() * 5;
        public int classThreads = (int) Math.ceil((double) this.maxThreads / 10);
        public int methodThreads = (int) Math.ceil((double) this.maxThreads / this.classThreads);
        public int testNumber = 5;
        public int maxRounds = 5;
        public int maxPromptTokens = 2600;
        public int maxResponseTokens = 1024;
        public int minErrorTokens = 500;
        public int sleepTime = 0;
        public int dependencyDepth = 1;
        public Model model = Model.GPT_3_5_TURBO;
        public Double temperature = 0.5;
        public int topP = 1;
        public int frequencyPenalty = 0;
        public int presencePenalty = 0;
        public Path testOutput;
        public Path tmpOutput = Paths.get(System.getProperty("java.io.tmpdir"), "chatunitest-info");
        public Path parseOutput;
        public Path compileOutputPath;
        public Path errorOutput;
        public Path classNameMapPath;
        public Path historyPath;
        public Path examplePath;
        public Path symbolFramePath;
        public String proxy = "null:-1";
        public String hostname = "null";
        public String port = "-1";
        public OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        public Validator validator;

        /**
         * 使用给定的项目初始化ConfigBuilder。
         *
         * @param project 项目对象。
         */
        public ConfigBuilder(Project project) {
            this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")).toString();
            this.project = project;
            this.log = new LoggerImpl();

            this.properties("config.properties");

            this.maxPromptTokens = this.model.getDefaultConfig().getContextLength() * 2 / 3;
            this.maxResponseTokens = 1024;
            this.minErrorTokens = this.maxPromptTokens * 1 / 3 - this.maxResponseTokens;
            if (this.minErrorTokens < 0) {
                this.minErrorTokens = 512;
            }

            Project parent = project.getParent();
            while (parent != null && parent.getBasedir() != null) {
                this.tmpOutput = this.tmpOutput.resolve(parent.getArtifactId());
                parent = parent.getParent();
            }
            this.tmpOutput = this.tmpOutput.resolve(project.getArtifactId());
            this.compileOutputPath = this.tmpOutput.resolve("build");
            this.parseOutput = this.tmpOutput.resolve("class-info");
            this.errorOutput = this.tmpOutput.resolve("error-message");
            this.classNameMapPath = this.tmpOutput.resolve("classNameMapping.json");
            this.historyPath = this.tmpOutput.resolve("history" + this.date);
            this.symbolFramePath = this.tmpOutput.resolve("symbolFrames.json");
            this.testOutput = project.getBasedir().toPath().resolve("chatunitest-tests");
            this.validator = new ValidatorImpl(this.testOutput, this.compileOutputPath,
                    this.project.getBasedir().toPath().resolve("target"), this.classPaths);
        }

        public ConfigBuilder maxThreads(int maxThreads) {
            if (maxThreads <= 0) {
                this.maxThreads = Runtime.getRuntime().availableProcessors() * 5;
            } else {
                this.maxThreads = maxThreads;
            }
            this.classThreads = (int) Math.ceil((double) this.maxThreads / 10);
            this.methodThreads = (int) Math.ceil((double) this.maxThreads / this.classThreads);
            if (!this.stopWhenSuccess) {
                this.methodThreads = (int) Math.ceil((double) this.methodThreads / this.testNumber);
            }
            return this;
        }

        public ConfigBuilder proxy(String proxy) {
            setProxy(proxy);
            return this;
        }

        public ConfigBuilder tmpOutput(Path tmpOutput) {
            this.tmpOutput = tmpOutput;
            Project parent = project.getParent();
            while (parent != null && parent.getBasedir() != null) {
                this.tmpOutput = this.tmpOutput.resolve(parent.getArtifactId());
                parent = parent.getParent();
            }
            this.tmpOutput = this.tmpOutput.resolve(project.getArtifactId());
            this.compileOutputPath = this.tmpOutput.resolve("build");
            this.parseOutput = this.tmpOutput.resolve("class-info");
            this.errorOutput = this.tmpOutput.resolve("error-message");
            this.classNameMapPath = this.tmpOutput.resolve("classNameMapping.json");
            this.historyPath = this.tmpOutput.resolve("history" + this.date);
            this.symbolFramePath = this.tmpOutput.resolve("symbolFrames.json");
            this.validator = new ValidatorImpl(this.testOutput, this.compileOutputPath,
                    this.project.getBasedir().toPath().resolve("target"), this.classPaths);
            return this;
        }

        public ConfigBuilder project(Project project) {
            this.project = project;
            return this;
        }

        public ConfigBuilder promptPath(File promptPath) {
            if (promptPath != null) {
                this.promptPath = promptPath.toPath();
            }
            return this;
        }

        public ConfigBuilder parser(JavaParser parser) {
            this.parser = parser;
            return this;
        }

        public ConfigBuilder parserFacade(JavaParserFacade parserFacade) {
            this.parserFacade = parserFacade;
            return this;
        }

        public ConfigBuilder classPaths(List<String> classPaths) {
            this.classPaths = classPaths;
            this.validator = new ValidatorImpl(this.testOutput, this.compileOutputPath,
                    this.project.getBasedir().toPath().resolve("target"), this.classPaths);
            return this;
        }

        public ConfigBuilder log(Logger log) {
            this.log = log;
            return this;
        }

        public ConfigBuilder OS(String OS) {
            this.OS = OS;
            return this;
        }

        public ConfigBuilder stopWhenSuccess(boolean stopWhenSuccess) {
            this.stopWhenSuccess = stopWhenSuccess;
            return this;
        }

        public ConfigBuilder noExecution(boolean noExecution) {
            this.noExecution = noExecution;
            return this;
        }

        public ConfigBuilder enableMultithreading(boolean enableMultithreading) {
            this.enableMultithreading = enableMultithreading;
            return this;
        }

        public ConfigBuilder enableRuleRepair(boolean enableRuleRepair) {
            this.enableRuleRepair = enableRuleRepair;
            return this;
        }

        public ConfigBuilder enableMerge(boolean enableMerge) {
            this.enableMerge = enableMerge;
            return this;
        }

        public ConfigBuilder enableObfuscate(boolean enableObfuscate) {
            this.enableObfuscate = enableObfuscate;
            return this;
        }

        public ConfigBuilder properties(String configFile) {
            try {
                Properties properties = new Properties();
                InputStream inputStream = PromptTemplate.class.getClassLoader().getResourceAsStream(configFile);
                properties.load(inputStream);
                this.properties = properties;
                return this;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load properties file: " + configFile);
            }
        }

        public ConfigBuilder obfuscateGroupIds(String[] obfuscateGroupIds) {
            this.obfuscateGroupIds = obfuscateGroupIds;
            return this;
        }

        public ConfigBuilder classThreads(int classThreads) {
            this.classThreads = classThreads;
            return this;
        }

        public ConfigBuilder methodThreads(int methodThreads) {
            this.methodThreads = methodThreads;
            return this;
        }

        public ConfigBuilder url(String url) {
            if (!this.model.getModelName().contains("gpt-4") && !this.model.getModelName().contains("gpt-3.5") && url.equals("https://api.openai.com/v1/chat/completions")) {
                throw new RuntimeException("Invalid url for model: " + this.model + ". Please configure the url in plugin configuration.");
            }
            this.url = url;
            this.model.getDefaultConfig().setUrl(url);
            return this;
        }

        public ConfigBuilder apiKeys(String[] apiKeys) {
            this.apiKeys = apiKeys;
            return this;
        }

        public ConfigBuilder testNumber(int testNumber) {
            this.testNumber = testNumber;
            return this;
        }

        public ConfigBuilder maxRounds(int maxRounds) {
            this.maxRounds = maxRounds;
            return this;
        }

        public ConfigBuilder maxPromptTokens(int maxPromptTokens) {
            this.maxPromptTokens = maxPromptTokens;
            return this;
        }

        public ConfigBuilder maxResponseTokens(int maxResponseTokens) {
            this.maxResponseTokens = maxResponseTokens;
            return this;
        }

        public ConfigBuilder minErrorTokens(int minErrorTokens) {
            this.minErrorTokens = minErrorTokens;
            return this;
        }

        public ConfigBuilder sleepTime(int sleepTime) {
            this.sleepTime = sleepTime;
            return this;
        }

        public ConfigBuilder dependencyDepth(int dependencyDepth) {
            this.dependencyDepth = dependencyDepth;
            return this;
        }

        public ConfigBuilder model(String model) {
            this.model = Model.fromString(model);
            this.maxPromptTokens = this.model.getDefaultConfig().getContextLength() * 2 / 3;
            this.maxResponseTokens = 1024;
            this.minErrorTokens = this.maxPromptTokens * 1 / 2 - this.maxResponseTokens;
            if (this.minErrorTokens < 0) {
                this.minErrorTokens = 512;
            }
            return this;
        }

        public ConfigBuilder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public ConfigBuilder topP(int topP) {
            this.topP = topP;
            return this;
        }

        public ConfigBuilder frequencyPenalty(int frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public ConfigBuilder presencePenalty(int presencePenalty) {
            this.presencePenalty = presencePenalty;
            return this;
        }

        public ConfigBuilder testOutput(Path testOutput) {
            if (testOutput == null) {
                this.testOutput = project.getBasedir().toPath().resolve("chatunitest-tests");
            } else {
                this.testOutput = testOutput;
                Project parent = project.getParent();
                while (parent != null && parent.getBasedir() != null) {
                    this.testOutput = this.testOutput.resolve(parent.getArtifactId());
                    parent = parent.getParent();
                }
                this.testOutput = this.testOutput.resolve(project.getArtifactId());
            }
            return this;
        }

        public ConfigBuilder compileOutputPath(Path compileOutputPath) {
            this.compileOutputPath = compileOutputPath;
            return this;
        }

        public ConfigBuilder parseOutput(Path parseOutput) {
            this.parseOutput = parseOutput;
            return this;
        }

        public ConfigBuilder errorOutput(Path errorOutput) {
            this.errorOutput = errorOutput;
            return this;
        }

        public ConfigBuilder classNameMapPath(Path classNameMapPath) {
            this.classNameMapPath = classNameMapPath;
            return this;
        }

        public ConfigBuilder examplePath(Path examplePath) {
            this.examplePath = examplePath;
            return this;
        }

        public ConfigBuilder symbolFramePath(Path symbolFramePath) {
            this.symbolFramePath = symbolFramePath;
            return this;
        }

        public ConfigBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public ConfigBuilder port(String port) {
            this.port = port;
            return this;
        }

        public ConfigBuilder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        /**
         * 设置代理。
         *
         * @param proxy 代理字符串，格式为 "hostname:port"。
         */
        public void setProxy(String proxy) {
            this.proxy = proxy;
            setProxyStr();
            if (!hostname.equals("null") && !port.equals("-1")) {
                setClinetwithProxy();
            } else {
                setClinet();
            }
        }

        /**
         * 设置代理字符串。
         */
        public void setProxyStr() {
            this.hostname = this.proxy.split(":")[0];
            this.port = this.proxy.split(":")[1];
        }

        /**
         * 设置无代理的客户端。
         */
        public void setClinet() {
            this.client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();
        }

        /**
         * 设置带代理的客户端。
         */
        public void setClinetwithProxy() {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.hostname, Integer.parseInt(this.port)));
            this.client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .proxy(proxy)
                    .build();
        }

        public void setValidator(Validator validator) {
            this.validator = validator;
        }

        /**
         * 构建Config对象。
         *
         * @return 构建的Config对象。
         */
        public Config build() {
            Config config = new Config();
            config.setDate(this.date);
            config.setGSON(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create());
            config.setProject(this.project);
            config.setParser(this.parser);
            config.setParserFacade(this.parserFacade);
            config.setClassPaths(this.classPaths);
            config.setPromptPath(this.promptPath);
            config.setProperties(this.properties);
            config.setUrl(this.url);
            config.setApiKeys(this.apiKeys);
            config.setOS(this.OS);
            config.setStopWhenSuccess(this.stopWhenSuccess);
            config.setNoExecution(this.noExecution);
            config.setEnableMultithreading(this.enableMultithreading);
            config.setEnableRuleRepair(this.enableRuleRepair);
            config.setEnableMerge(this.enableMerge);
            config.setEnableObfuscate(this.enableObfuscate);
            config.setObfuscateGroupIds(this.obfuscateGroupIds);
            config.setMaxThreads(this.maxThreads);
            config.setClassThreads(this.classThreads);
            config.setMethodThreads(this.methodThreads);
            config.setTestNumber(this.testNumber);
            config.setMaxRounds(this.maxRounds);
            config.setMaxPromptTokens(this.maxPromptTokens);
            config.setMaxResponseTokens(this.maxResponseTokens);
            config.setMinErrorTokens(this.minErrorTokens);
            config.setSleepTime(this.sleepTime);
            config.setDependencyDepth(this.dependencyDepth);
            config.setModel(this.model);
            config.setTemperature(this.temperature);
            config.setTopP(this.topP);
            config.setFrequencyPenalty(this.frequencyPenalty);
            config.setPresencePenalty(this.presencePenalty);
            config.setTestOutput(this.testOutput);
            config.setTmpOutput(this.tmpOutput);
            config.setCompileOutputPath(this.compileOutputPath);
            config.setParseOutput(this.parseOutput);
            config.setErrorOutput(this.errorOutput);
            config.setClassNameMapPath(this.classNameMapPath);
            config.setHistoryPath(this.historyPath);
            config.setExamplePath(this.examplePath);
            config.setSymbolFramePath(this.symbolFramePath);
            config.setProxy(this.proxy);
            config.setHostname(this.hostname);
            config.setPort(this.port);
            config.setClient(this.client);
            config.setLog(this.log);
            config.setValidator(this.validator);
            return config;
        }
    }

    /**
     * 获取随机API密钥。
     *
     * @return 随机API密钥。
     */
    public String getRandomKey() {
        Random rand = new Random();
        if (apiKeys.length == 0) {
            throw new RuntimeException("apiKeys is null!");
        }
        String apiKey = apiKeys[rand.nextInt(apiKeys.length)];
        return apiKey;
    }

    /**
     * 打印配置信息。
     */
    public void print() {
        log.info("\n========================== Configuration ==========================\n");
        log.info(" Multithreading >>>> " + this.isEnableMultithreading());
        if (this.isEnableMultithreading()) {
            log.info(" - Class threads: " + this.getClassThreads() + ", Method threads: " + this.getMethodThreads());
        }
        log.info(" Stop when success >>>> " + this.isStopWhenSuccess());
        log.info(" No execution >>>> " + this.isNoExecution());
        log.info(" Enable Merge >>>> " + this.isEnableMerge());
        log.info(" --- ");
        log.info(" TestOutput Path >>> " + this.getTestOutput());
        log.info(" TmpOutput Path >>> " + this.getTmpOutput());
        log.info(" Prompt path >>> " + this.getPromptPath());
        log.info(" Example path >>> " + this.getExamplePath());
        log.info(" --- ");
        log.info(" Model >>> " + this.getModel());
        log.info(" Url >>> " + this.getUrl());
        log.info(" MaxPromptTokens >>> " + this.getMaxPromptTokens());
        log.info(" MaxResponseTokens >>> " + this.getMaxResponseTokens());
        log.info(" MinErrorTokens >>> " + this.getMinErrorTokens());
        log.info(" MaxThreads >>> " + this.getMaxThreads());
        log.info(" TestNumber >>> " + this.getTestNumber());
        log.info(" MaxRounds >>> " + this.getMaxRounds());
        log.info(" MinErrorTokens >>> " + this.getMinErrorTokens());
        log.info(" MaxPromptTokens >>> " + this.getMaxPromptTokens());
        log.info(" SleepTime >>> " + this.getSleepTime());
        log.info(" DependencyDepth >>> " + this.getDependencyDepth());
        log.info("\n===================================================================\n");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
