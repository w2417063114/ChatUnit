package zju.cst.aces.api.impl;

import zju.cst.aces.util.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * LoggerImpl 类实现了 Logger 接口，使用 java.util.logging 提供日志记录功能。
 */
public class LoggerImpl implements zju.cst.aces.api.Logger {

    java.util.logging.Logger log;

    /**
     * 使用控制台处理程序和自定义日志格式化程序初始化 LoggerImpl 的构造函数。
     */
    public LoggerImpl() {
        this.log = java.util.logging.Logger.getLogger("ChatUniTest");
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new LogFormatter());
        this.log.addHandler(consoleHandler);
        this.log.setUseParentHandlers(false);
    }

    /**
     * 记录一条信息消息。
     *
     * @param msg 要记录的消息。
     */
    @Override
    public void info(String msg) {
        log.info(msg);
    }

    /**
     * 记录一条警告消息。
     *
     * @param msg 要记录的消息。
     */
    @Override
    public void warn(String msg) {
        log.warning(msg);
    }

    /**
     * 记录一条错误消息。
     *
     * @param msg 要记录的消息。
     */
    @Override
    public void error(String msg) {
        log.severe(msg);
    }

    /**
     * 记录一条调试消息。
     *
     * @param msg 要记录的消息。
     */
    @Override
    public void debug(String msg) {
        log.config(msg);
    }
}
