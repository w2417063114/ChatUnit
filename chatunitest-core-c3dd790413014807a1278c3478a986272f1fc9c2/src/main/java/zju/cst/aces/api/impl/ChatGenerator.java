package zju.cst.aces.api.impl;

import zju.cst.aces.api.Generator;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.ChatResponse;
import zju.cst.aces.dto.Message;
import zju.cst.aces.runner.AbstractRunner;
import zju.cst.aces.util.AskGPT;
import zju.cst.aces.util.CodeExtractor;

import java.util.List;

/**
 * ChatGenerator 类实现了 Generator 接口，通过聊天提示生成代码。
 */
public class ChatGenerator implements Generator {

    Config config;

    /**
     * 使用给定的配置初始化 ChatGenerator 的构造函数。
     *
     * @param config 包含项目设置的配置对象。
     */
    public ChatGenerator(Config config) {
        this.config = config;
    }

    /**
     * 基于提供的消息列表生成代码。
     *
     * @param messages 要发送给 GPT 模型的消息列表。
     * @return 从 GPT 响应中提取的代码。
     */
    @Override
    public String generate(List<Message> messages) {
        return extractCodeByResponse(chat(config, messages));
    }

    /**
     * 使用提供的消息列表向 GPT 发送聊天请求并返回响应。
     *
     * @param config 包含项目设置的配置对象。
     * @param messages 要发送给 GPT 模型的消息列表。
     * @return GPT 模型的响应。
     * @throws RuntimeException 如果响应为空。
     */
    public static ChatResponse chat(Config config, List<Message> messages) {
        ChatResponse response = new AskGPT(config).askChatGPT(messages);
        if (response == null) {
            throw new RuntimeException("响应为空，获取响应失败。");
        }
        return response;
    }

    /**
     * 从提供的 ChatResponse 对象中提取代码。
     *
     * @param response 要提取代码的响应对象。
     * @return 提取的代码。
     */
    public static String extractCodeByResponse(ChatResponse response) {
        return new CodeExtractor(getContentByResponse(response)).getExtractedCode();
    }

    /**
     * 从提供的 ChatResponse 对象中获取内容。
     *
     * @param response 要获取内容的响应对象。
     * @return 响应的内容。
     */
    public static String getContentByResponse(ChatResponse response) {
        return AbstractRunner.parseResponse(response);
    }

    /**
     * 从提供的内容字符串中提取代码。
     *
     * @param content 要提取代码的内容字符串。
     * @return 提取的代码。
     */
    public static String extractCodeByContent(String content) {
        return new CodeExtractor(content).getExtractedCode();
    }
}
