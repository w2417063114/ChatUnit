package zju.cst.aces.api;

/**
 * Logger 接口定义了用于记录日志消息的方法。
 */
public interface Logger {

    /**
     * 记录信息消息。
     *
     * @param msg 要记录的信息消息。
     */
    void info(String msg);

    /**
     * 记录警告消息。
     *
     * @param msg 要记录的警告消息。
     */
    void warn(String msg);

    /**
     * 记录错误消息。
     *
     * @param msg 要记录的错误消息。
     */
    void error(String msg);

    /**
     * 记录调试消息。
     *
     * @param msg 要记录的调试消息。
     */
    void debug(String msg);
}
