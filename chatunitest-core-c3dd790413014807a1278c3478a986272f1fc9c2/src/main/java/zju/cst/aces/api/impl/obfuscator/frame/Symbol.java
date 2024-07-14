package zju.cst.aces.api.impl.obfuscator.frame;

import lombok.Data;

import java.util.List;

/**
 * Symbol 类用于表示符号信息，包括名称、所有者、类型和行号。
 */
@Data
public class Symbol {
    private String name;
    private String owner;
    private String type;
    private Integer lineNum;

    /**
     * 构造函数，用于初始化 Symbol 对象。
     *
     * @param name 符号名称。
     * @param owner 符号所有者。
     * @param type 符号类型。
     * @param line 符号所在行号。
     */
    public Symbol(String name, String owner, String type, Integer line) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.lineNum = line;
    }

    /**
     * 检查符号是否属于指定的组。
     *
     * @param groupIds 组 ID 列表。
     * @return 如果符号属于指定的组，则返回 true，否则返回 false。
     */
    public boolean isInGroup(List<String> groupIds) {
        for (String gid : groupIds) {
            if (owner.contains(gid) || type.contains(gid)) {
                return true;
            }
        }
        return false;
    }
}

