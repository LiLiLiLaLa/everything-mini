package everything.config;

import lombok.Getter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter  //不要Setter防止修改引用
public class EverythingMiniConfig{
    private static volatile EverythingMiniConfig config = getInstance();
    /**
     * 建立索引的路径
     */

    private Set<String> includePath = new HashSet<>();
    public Set<String> getIncludePath(){
        return includePath;
    }
    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>();

    //TODO 可配置的参数会在这里提现

    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir" + File.separator + "everything_mini");

    private EverythingMiniConfig(){
        this.initDefaultPathsConfig();
    }

    private void initDefaultPathsConfig(){
        //获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        Set<String> includePath = config.getIncludePath();

        for(Path path:iterable){
            System.out.println(path);
            includePath.add(path.toString());
        }
        //iterable.forEach(path -> config.getIncludePath().add(path.toString()));
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

    public static EverythingMiniConfig getInstance(){
        if(config == null){
            synchronized(EverythingMiniConfig.class){
                if(config == null){
                    config = new EverythingMiniConfig();
                }
            }
        }
        return config;
    }
}
