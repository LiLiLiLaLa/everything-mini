package com.every.everything.config;

import lombok.Getter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter  //不要Setter防止修改引用
public class EverythingMiniConfig{
    private static volatile EverythingMiniConfig config;
    /**
     * 建立索引的路径
     */
    private Set<String> includePath = new HashSet<>();
    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>();


    private EverythingMiniConfig(){}
    public static EverythingMiniConfig getInstance(){
        if(config == null){
            synchronized(EverythingMiniConfig.class){
                if(config == null){
                    config = new EverythingMiniConfig();
                    //获取文件系统
                    FileSystem fileSystem = FileSystems.getDefault();
                    //遍历的目录
                    Iterable<Path> iterable = fileSystem.getRootDirectories();
                    iterable.forEach(path -> config.getIncludePath().add(path.toString()));
                    //排除的目录  C:\Program Files   C:\Windows  C:\Program Files (x86)  C:\ProgramData
                    String osName = System.getProperty("os.name");
                    if(osName.startsWith("Windows")){
                        config.getExcludePath().add("C:\\Program Files");
                        config.getExcludePath().add("C:\\Windows");
                        config.getExcludePath().add("C:\\Program Files (x86)");
                        config.getExcludePath().add("C:\\ProgramData");
                    }else{
                        config.getExcludePath().add("/tmp");
                        config.getExcludePath().add("/etc");
                        config.getExcludePath().add("/root");
                    }
                }
            }
        }
        return config;
    }

    public static void main(String[] args) {
        EverythingMiniConfig config = EverythingMiniConfig.getInstance();
        System.out.println(config.getIncludePath());
        System.out.println(config.getExcludePath());
    }
}
