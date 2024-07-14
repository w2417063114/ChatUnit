package zju.cst.aces.api.config;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Model 枚举类定义了支持的模型及其默认配置。
 */
public enum Model {
    GPT_3_5_TURBO("gpt-3.5-turbo", new ModelConfig.Builder()
            .withModelName("gpt-3.5-turbo")
            .withUrl("https://api.openai.com/v1/chat/completions")
            .withContextLength(4096)
            .withTemperature(0.5)
            .withFrequencyPenalty(0)
            .withPresencePenalty(0)
            .build()),
    GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106", new ModelConfig.Builder()
            .withModelName("gpt-3.5-turbo-1106")
            .withUrl("https://api.openai.com/v1/chat/completions")
            .withContextLength(16385)
            .withTemperature(0.5)
            .withFrequencyPenalty(0)
            .withPresencePenalty(0)
            .build()),
    CODE_LLAMA("code-llama", new ModelConfig.Builder()
            .withModelName("code-llama")
            .withUrl(null)
            .withContextLength(16385)
            .withTemperature(0.5)
            .withFrequencyPenalty(0)
            .withPresencePenalty(0)
            .build());
    // 添加更多模型

    private final String modelName;
    private final ModelConfig defaultConfig;

    /**
     * 构造函数，用于初始化 Model 枚举实例。
     *
     * @param modelName 模型名称。
     * @param defaultConfig 模型的默认配置。
     */
    Model(String modelName, ModelConfig defaultConfig) {
        this.modelName = modelName;
        this.defaultConfig = defaultConfig;
    }

    /**
     * 获取模型名称。
     *
     * @return 模型名称。
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 获取模型的默认配置。
     *
     * @return 模型的默认配置。
     */
    public ModelConfig getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * 根据模型名称获取对应的 Model 枚举实例。
     *
     * @param modelName 模型名称。
     * @return 对应的 Model 枚举实例。
     * @throws IllegalArgumentException 如果模型名称不存在，则抛出异常。
     */
    public static Model fromString(String modelName) {
        for (Model model : Model.values()) {
            if (model.getModelName().equalsIgnoreCase(modelName)) {
                return model;
            }
        }
        throw new IllegalArgumentException("没有名称为 " + modelName + " 的模型。" +
                "\n支持的模型有: " + Arrays.stream(Model.values()).map(Model::getModelName).collect(Collectors.joining(", ")));
    }
}
