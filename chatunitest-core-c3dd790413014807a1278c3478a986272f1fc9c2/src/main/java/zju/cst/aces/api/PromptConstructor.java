package zju.cst.aces.api;

import zju.cst.aces.dto.Message;

import java.util.List;

/**
 * PromptConstructor 接口定义了生成提示信息的方法。
 */
public interface PromptConstructor {

    /**
     * 生成提示信息的列表。
     *
     * @return 生成的提示信息列表。
     */
    List<Message> generate();
}

