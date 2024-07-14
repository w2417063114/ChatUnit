package zju.cst.aces.api;

import zju.cst.aces.dto.Message;

import java.util.List;

/**
 * Generator 接口定义了生成代码的方法。
 */
public interface Generator {

    /**
     * 根据消息列表生成代码。
     *
     * @param messages 消息列表。
     * @return 生成的代码。
     */
    String generate(List<Message> messages);
}

