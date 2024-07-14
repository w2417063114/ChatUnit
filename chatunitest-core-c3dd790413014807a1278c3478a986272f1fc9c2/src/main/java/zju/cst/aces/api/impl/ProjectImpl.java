package zju.cst.aces.api.impl;

import org.apache.maven.project.MavenProject;
import zju.cst.aces.api.Project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * ProjectImpl 类实现了 Project 接口，提供管理 Maven 项目相关信息和路径的方法。
 */
public class ProjectImpl implements Project {

    MavenProject project;

    /**
     * 使用给定的 Maven 项目初始化 ProjectImpl 的构造函数。
     *
     * @param project Maven 项目对象。
     */
    public ProjectImpl(MavenProject project) {
        this.project = project;
    }

    /**
     * 返回父项目。
     *
     * @return 父项目，如果没有父项目则返回 null。
     */
    @Override
    public Project getParent() {
        if (project.getParent() == null) {
            return null;
        }
        return new ProjectImpl(project.getParent());
    }

    /**
     * 返回项目的基本目录。
     *
     * @return 项目的基本目录。
     */
    @Override
    public File getBasedir() {
        return project.getBasedir();
    }

    /**
     * 返回项目的打包类型。
     *
     * @return 项目的打包类型。
     */
    @Override
    public String getPackaging() {
        return project.getPackaging();
    }

    /**
     * 返回项目的 group ID。
     *
     * @return 项目的 group ID。
     */
    @Override
    public String getGroupId() {
        return project.getGroupId();
    }

    /**
     * 返回项目的 artifact ID。
     *
     * @return 项目的 artifact ID。
     */
    @Override
    public String getArtifactId() {
        return project.getArtifactId();
    }

    /**
     * 返回编译源根的列表。
     *
     * @return 编译源根的列表。
     */
    @Override
    public List<String> getCompileSourceRoots() {
        return project.getCompileSourceRoots();
    }

    /**
     * 返回项目工件的路径。
     *
     * @return 项目工件的路径。
     */
    @Override
    public Path getArtifactPath() {
        return Paths.get(project.getBuild().getDirectory()).resolve(project.getBuild().getFinalName() + ".jar");
    }

    /**
     * 返回构建输出目录的路径。
     *
     * @return 构建输出目录的路径。
     */
    @Override
    public Path getBuildPath() {
        return Paths.get(project.getBuild().getOutputDirectory());
    }
}


