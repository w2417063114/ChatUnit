package zju.cst.aces.api.impl.obfuscator.frame;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SymbolFrame 类用于存储和操作类及其成员的符号信息。
 */
@Data
public class SymbolFrame {
    private String className;
    private String superName;
    private List<String> interfaces;
    private Set<Symbol> fieldDef = new HashSet<>();
    private Set<Symbol> fieldUse = new HashSet<>();
    private Set<Symbol> varDef = new HashSet<>();
    private Set<Symbol> varUse = new HashSet<>();
    private Set<Symbol> methodDef = new HashSet<>();
    private Set<Symbol> methodUse = new HashSet<>();

    /**
     * 添加字段定义符号。
     *
     * @param symbol 字段定义符号。
     */
    public void addFieldDef(Symbol symbol) {
        fieldDef.add(symbol);
    }

    /**
     * 添加字段使用符号。
     *
     * @param symbol 字段使用符号。
     */
    public void addFieldUse(Symbol symbol) {
        fieldUse.add(symbol);
    }

    /**
     * 添加变量定义符号。
     *
     * @param symbol 变量定义符号。
     */
    public void addVarDef(Symbol symbol) {
        varDef.add(symbol);
    }

    /**
     * 添加变量使用符号。
     *
     * @param symbol 变量使用符号。
     */
    public void addVarUse(Symbol symbol) {
        varUse.add(symbol);
    }

    /**
     * 添加方法定义符号。
     *
     * @param symbol 方法定义符号。
     */
    public void addMethodDef(Symbol symbol) {
        methodDef.add(symbol);
    }

    /**
     * 添加方法使用符号。
     *
     * @param symbol 方法使用符号。
     */
    public void addMethodUse(Symbol symbol) {
        methodUse.add(symbol);
    }

    /**
     * 合并另一个 SymbolFrame 的符号信息。
     *
     * @param frame 要合并的 SymbolFrame。
     */
    public void merge(SymbolFrame frame) {
        if (frame == null) {
            return;
        }
        if (frame.fieldDef != null) {
            fieldDef.addAll(frame.fieldDef);
        }
        if (frame.fieldUse != null) {
            fieldUse.addAll(frame.fieldUse);
        }
        if (frame.varDef != null) {
            varDef.addAll(frame.varDef);
        }
        if (frame.varUse != null) {
            varUse.addAll(frame.varUse);
        }
        if (frame.methodDef != null) {
            methodDef.addAll(frame.methodDef);
        }
        if (frame.methodUse != null) {
            methodUse.addAll(frame.methodUse);
        }
    }

    /**
     * 根据组 ID 过滤符号。
     *
     * @param groupIds 组 ID 列表。
     */
    public void filterSymbolsByGroupId(List<String> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return;
        }
        List<String> targets = groupIds.stream().map(id -> id.replace(".", "/")).collect(Collectors.toList());
        className = isClassInGroup(className, groupIds) ? className.substring(className.lastIndexOf("/") + 1) : null;
        superName = isClassInGroup(superName, groupIds) ? superName.substring(superName.lastIndexOf("/") + 1) : null;
        for (int i = 0; i < interfaces.size(); i++) {
            String interfaceName = interfaces.get(i);
            if (isClassInGroup(interfaceName, groupIds)) {
                interfaces.set(i, interfaceName.substring(interfaceName.lastIndexOf("/") + 1));
            } else {
                interfaces.remove(i);
                i--;
            }
        }
        fieldDef.removeIf(symbol -> !symbol.isInGroup(targets));
        fieldUse.removeIf(symbol -> !symbol.isInGroup(targets));
        varDef.removeIf(symbol -> !symbol.isInGroup(targets));
        varUse.removeIf(symbol -> !symbol.isInGroup(targets));
        methodDef.removeIf(symbol -> !symbol.isInGroup(targets));
        methodUse.removeIf(symbol -> !symbol.isInGroup(targets));
    }

    /**
     * 获取需要混淆的符号名称集合。
     *
     * @param groupIds 组 ID 列表。
     * @return 需要混淆的符号名称集合。
     */
    public Set<String> toObNames(List<String> groupIds) {
        Set<String> obNames = new HashSet<>();
        if (className != null) {
            obNames.add(className);
        }
        if (superName != null) {
            obNames.add(superName);
        }
        if (interfaces != null) {
            obNames.addAll(interfaces);
        }
        obNames.addAll(fieldDef.stream().map(Symbol::getName).collect(Collectors.toSet()));
        obNames.addAll(fieldDef.stream().map(s -> splitTypeName(s.getOwner(), groupIds)).collect(Collectors.toSet()));
        obNames.addAll(fieldDef.stream().map(s -> splitTypeName(s.getType(), groupIds)).collect(Collectors.toSet()));

        obNames.addAll(fieldUse.stream().map(Symbol::getName).collect(Collectors.toSet()));
        obNames.addAll(fieldUse.stream().map(s -> splitTypeName(s.getOwner(), groupIds)).collect(Collectors.toSet()));
        obNames.addAll(fieldUse.stream().map(s -> splitTypeName(s.getType(), groupIds)).collect(Collectors.toSet()));

        obNames.addAll(varDef.stream().map(Symbol::getName).collect(Collectors.toSet()));
        obNames.addAll(varDef.stream().map(s -> splitTypeName(s.getOwner(), groupIds)).collect(Collectors.toSet()));
        obNames.addAll(varDef.stream().map(s -> splitTypeName(s.getType(), groupIds)).collect(Collectors.toSet()));

        obNames.addAll(varUse.stream().map(Symbol::getName).collect(Collectors.toSet()));
        obNames.addAll(varUse.stream().map(s -> splitTypeName(s.getOwner(), groupIds)).collect(Collectors.toSet()));
        obNames.addAll(varUse.stream().map(s -> splitTypeName(s.getType(), groupIds)).collect(Collectors.toSet()));

        obNames.addAll(methodDef.stream().map(Symbol::getName).collect(Collectors.toSet()));
        obNames.addAll(methodDef.stream().map(s -> splitTypeName(s.getOwner(), groupIds)).collect(Collectors.toSet()));
        obNames.addAll(methodDef.stream().map(s -> splitTypeName(s.getType(), groupIds)).collect(Collectors.toSet()));

        obNames.addAll(methodUse.stream().map(Symbol::getName).collect(Collectors.toSet()));
        obNames.addAll(methodUse.stream().map(s -> splitTypeName(s.getOwner(), groupIds)).collect(Collectors.toSet()));
        obNames.addAll(methodUse.stream().map(s -> splitTypeName(s.getType(), groupIds)).collect(Collectors.toSet()));
        obNames.remove("");
        return obNames;
    }

    /**
     * 拆分类型名称并检查是否在组中。
     *
     * @param type 类型名称。
     * @param groupIds 组 ID 列表。
     * @return 拆分后的类型名称。
     */
    public String splitTypeName(String type, List<String> groupIds) {
        if (type == null || type.isEmpty() || !isClassInGroup(type, groupIds)) {
            return "";
        }
        String[] parts = type.split("/");
        String ret = parts[parts.length - 1];
        if (ret.contains("$")) {
            ret = ret.substring(ret.lastIndexOf("$") + 1);
        }
        if (ret.contains(";")) {
            ret = ret.substring(0, ret.indexOf(";"));
        }
        return ret;
    }

    /**
     * 检查类名是否在组中。
     *
     * @param fullClassName 类的全限定名。
     * @param groupIds 组 ID 列表。
     * @return 如果类名在组中，则返回 true，否则返回 false。
     */
    public static boolean isClassInGroup(String fullClassName, List<String> groupIds) {
        for (String gid : groupIds) {
            if (fullClassName.contains(gid.replace(".", "/"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符串是否在组中。
     *
     * @param str 字符串。
     * @param groupIds 组 ID 列表。
     * @return 如果字符串在组中，则返回 true，否则返回 false。
     */
    public static boolean isInGroup(String str, List<String> groupIds) {
        for (String gid : groupIds) {
            if (str.contains(gid)) {
                return true;
            }
        }
        return false;
    }
}

