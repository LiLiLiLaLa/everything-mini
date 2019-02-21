package everything.config;

import lombok.Getter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter  //不要Setter防止includePath和excludePath被修改
public class EverythingMiniConfig {
    private static volatile EverythingMiniConfig config;
    /**
     * 建立索引的路径
     */
    private Set<String> includePath = new HashSet<>();
    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>();

    //TODO 可配置的参数会在这里体现

    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "everything_mini";

    private EverythingMiniConfig(){

    }

    private void initDefaultPathsConfig(){
        //获取文件系统
        //FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录
        //Iterable<Path> iterable = fileSystem.getRootDirectories();
        //iterable.forEach(path -> config.includePath.add(path.toString()));
        //排除的目录  C:\Program Files   C:\Windows  C:\Program Files (x86)  C:\ProgramData
//        String osName = System.getProperty("os.name");
//        if (osName.startsWith("Windows")) {
//            config.getExcludePath().add("C:\\Program Files");
//            config.getExcludePath().add("C:\\Windows");
//            config.getExcludePath().add("C:\\Program Files (x86)");
//            config.getExcludePath().add("C:\\ProgramData");
//        } else {
//            config.getExcludePath().add("/tmp");
//            config.getExcludePath().add("/etc");
//            config.getExcludePath().add("/root");
//        }
        includePath.add("A:\\");
        includePath.add("C:\\");
        includePath.add("D:\\");
        excludePath.add("C:\\ProgramData");
        excludePath.add("C:\\Program Files (x86)");
        excludePath.add("C:\\Windows");
        excludePath.add("C:\\Program Files");

    }

    public static EverythingMiniConfig getInstance() {
        if (config == null) {
            synchronized (EverythingMiniConfig.class) {
                if (config == null) {
                    config = new EverythingMiniConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }
}
