package zju.cst.aces.api.impl.obfuscator.util;

import okio.BufferedSource;
import okio.Okio;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import zju.cst.aces.api.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * ASMParser 类用于解析类文件和 JAR 文件，提取类节点信息。
 */
public class ASMParser {

    private final Config config;

    /**
     * 使用给定的配置初始化 ASMParser 的构造函数。
     *
     * @param config 包含项目设置的配置对象。
     */
    public ASMParser(Config config) {
        this.config = config;
    }

    /**
     * 获取指定类节点集合中的方法签名条目。
     *
     * @param classNodes 类节点集合。
     * @param methodSigs 方法签名集合。
     * @return 方法签名条目集合。
     */
    Set<String> getEntries(Set<ClassNode> classNodes, Collection<String> methodSigs) {
        Set<String> entries = new HashSet<>();
        return entries;
    }

    /**
     * 加载指定类文件中的类节点。
     *
     * @param classFile 类文件。
     * @return 类节点集合。
     * @throws IOException 如果读取类文件时发生 I/O 错误。
     */
    public Set<ClassNode> loadClasses(File classFile) throws IOException {
        Set<ClassNode> classes = new HashSet<>();
        InputStream is = new FileInputStream(classFile);
        return readClass(classFile.getName(), is, classes);
    }

    /**
     * 加载指定 JAR 文件中的类节点。
     *
     * @param jarFile JAR 文件。
     * @return 类节点集合。
     * @throws IOException 如果读取 JAR 文件时发生 I/O 错误。
     */
    public Set<ClassNode> loadClasses(JarFile jarFile) throws IOException {
        Set<ClassNode> targetClasses = new HashSet<>();
        Stream<JarEntry> str = jarFile.stream();
        str.forEach(z -> readJar(jarFile, z, targetClasses));
        jarFile.close();
        return targetClasses;
    }

    /**
     * 从输入流中读取类文件，并将类节点添加到目标类集合中。
     *
     * @param className 类名。
     * @param is 输入流。
     * @param targetClasses 目标类集合。
     * @return 更新后的目标类集合。
     */
    private Set<ClassNode> readClass(String className, InputStream is, Set<ClassNode> targetClasses) {
        try {
            BufferedSource source = Okio.buffer(Okio.source(is));
            byte[] bytes = source.readByteArray();
            String cafebabe = String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
            if (!cafebabe.toLowerCase().equals("cafebabe")) {
                // 该类文件没有有效的 magic 值
                return targetClasses;
            }
            ClassNode cn = getNode(bytes);
            targetClasses.add(cn);
        } catch (Exception e) {
            throw new RuntimeException("无法读取类 " + className + ": " + e);
        }
        return targetClasses;
    }

    /**
     * 从 JAR 文件中读取类条目，并将类节点添加到目标类集合中。
     *
     * @param jar JAR 文件。
     * @param entry JAR 条目。
     * @param targetClasses 目标类集合。
     * @return 更新后的目标类集合。
     */
    private Set<ClassNode> readJar(JarFile jar, JarEntry entry, Set<ClassNode> targetClasses) {
        String name = entry.getName();
        if (name.endsWith(".class")) {
            String className = name.replace(".class", "").replace("/", ".");
            try (InputStream jis = jar.getInputStream(entry)) {
                return readClass(className, jis, targetClasses);
            } catch (IOException e) {
                config.getLog().warn("无法读取 JAR 文件 " + jar.getName() + " 中的类 " + entry + ": " + e);
            }
        } else if (name.endsWith("jar") || name.endsWith("war")) {
            // 处理嵌套的 JAR 或 WAR 文件
        }
        return targetClasses;
    }

    /**
     * 从字节数组中读取类节点。
     *
     * @param bytes 类文件的字节数组。
     * @return 类节点。
     */
    private ClassNode getNode(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        try {
            cr.accept(cn, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 有利于垃圾回收
        cr = null;
        return cn;
    }
}
