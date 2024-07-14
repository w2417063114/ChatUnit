package zju.cst.aces.api;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Project 接口定义了用于获取项目相关信息的方法。
 */
public interface Project {

    /**
     * 获取父项目。
     *
     * @return 父项目，如果没有父项目则返回 null。
     */
    Project getParent();

    /**
     * 获取项目的基目录。
     *
     * @return 项目的基目录。
     */
    File getBasedir();

    /**
     * 获取项目的打包类型。
     *
     * @return 项目的打包类型。
     */
    String getPackaging();

    /**
     * 获取项目的组 ID。
     *
     * @return 项目的组 ID。
     */
    String getGroupId();

    /**
     * 获取项目的工件 ID。
     *
     * @return 项目的工件 ID。
     */
    String getArtifactId();

    /**
     * 获取编译源文件的根目录列表。
     *
     * @return 编译源文件的根目录列表。
     */
    List<String> getCompileSourceRoots();

    /**
     * 获取项目工件的路径。
     *
     * @return 项目工件的路径。
     */
    Path getArtifactPath();

    /**
     * 获取构建输出目录的路径。
     *
     * @return 构建输出目录的路径。
     */
    Path getBuildPath();
}
