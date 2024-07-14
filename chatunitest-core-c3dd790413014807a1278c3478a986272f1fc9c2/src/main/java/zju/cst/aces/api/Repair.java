package zju.cst.aces.api;

/**
 * Repair 接口定义了使用各种策略修复代码的方法。
 */
public interface Repair {

    /**
     * 基于预定义规则修复代码。
     *
     * @param code 要修复的代码。
     * @return 修复后的代码。
     */
    String ruleBasedRepair(String code);

    /**
     * 基于大语言模型 (LLM) 修复代码。
     *
     * @param code 要修复的代码。
     * @return 修复后的代码。
     */
    String LLMBasedRepair(String code);

    /**
     * 基于大语言模型 (LLM) 修复代码，尝试指定次数。
     *
     * @param code 要修复的代码。
     * @param attempts 尝试修复的次数。
     * @return 修复后的代码。
     */
    String LLMBasedRepair(String code, int attempts);
}
