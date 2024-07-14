package zju.cst.aces.api.config;

import lombok.Data;

/**
 * ModelConfig 类用于配置模型的相关参数，包括模型名称、URL、上下文长度、温度、频率惩罚和存在惩罚。
 */
@Data
public class ModelConfig {
    public String modelName;
    public String url;
    public int contextLength;
    public double temperature;
    public int frequencyPenalty;
    public int presencePenalty;

    /**
     * 私有构造函数，通过 Builder 初始化 ModelConfig 对象。
     *
     * @param builder 用于构建 ModelConfig 对象的 Builder 实例。
     */
    private ModelConfig(Builder builder) {
        this.modelName = builder.modelName;
        this.url = builder.url;
        this.contextLength = builder.contextLength;
        this.temperature = builder.temperature;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.presencePenalty = builder.presencePenalty;
    }

    /**
     * Builder 类用于构建 ModelConfig 对象，提供了一系列链式调用的方法来设置各项配置参数。
     */
    public static class Builder {
        private String modelName = "gpt-3.5-turbo";
        private String url = "https://api.openai.com/v1/chat/completions";
        private int contextLength = 4096;
        private double temperature = 0.5;
        private int frequencyPenalty = 0;
        private int presencePenalty = 0;

        /**
         * 设置模型名称。
         *
         * @param modelName 模型名称。
         * @return Builder 实例。
         */
        public Builder withModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        /**
         * 设置 URL。
         *
         * @param url 模型服务的 URL。
         * @return Builder 实例。
         */
        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置上下文长度。
         *
         * @param contextLength 上下文长度。
         * @return Builder 实例。
         */
        public Builder withContextLength(int contextLength) {
            this.contextLength = contextLength;
            return this;
        }

        /**
         * 设置存在惩罚。
         *
         * @param penalty 存在惩罚值。
         * @return Builder 实例。
         */
        public Builder withPresencePenalty(int penalty) {
            this.presencePenalty = penalty;
            return this;
        }

        /**
         * 设置频率惩罚。
         *
         * @param penalty 频率惩罚值。
         * @return Builder 实例。
         */
        public Builder withFrequencyPenalty(int penalty) {
            this.frequencyPenalty = penalty;
            return this;
        }

        /**
         * 设置温度参数。
         *
         * @param temperature 温度参数。
         * @return Builder 实例。
         */
        public Builder withTemperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        /**
         * 构建 ModelConfig 对象。
         *
         * @return 初始化后的 ModelConfig 对象。
         */
        public ModelConfig build() {
            return new ModelConfig(this);
        }
    }
}
