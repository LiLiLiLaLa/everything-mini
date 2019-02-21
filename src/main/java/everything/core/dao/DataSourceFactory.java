package everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import everything.config.EverythingMiniConfig;
import org.apache.commons.io.IOUtils;
import java.lang.String;
import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceFactory{
    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory(){

    }

    public static DataSource dataSource(){
        if(dataSource == null){
            synchronized (DataSourceFactory.class){
                if(dataSource == null){
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC Driver class
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url,username,password
                    //采用H2的嵌入式数据库，数据库是以本地文件的形式存储，只需要提高url接口
                    //JDBC规范中关于MySQL    jdbc:mysql://ip:port/databaseName
                    //获取当前工程路径
                    String workDir = System.getProperty("user.dir");
                    //JDBC规范中关于H2    jdbc:h2:filpath   ——>存储到本地文件
                    //JDBC规范中关于H2    jdbc:h2:~/filpath   ——>存储到当前用户的home目录本地文件
                    //JDBC规范中关于H2    jdbc:h2://ip:port/databaseName   ——>存储到服务器
                    dataSource.setUrl("jdbc:h2:" + EverythingMiniConfig.getInstance().getH2IndexPath());
                    dataSource.setTestWhileIdle(true);
                }
            }
        }
        return dataSource;
    }

    /**
     * 初始化数据库
     */
    public static void initDatase(){
        //获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //获取SQL语句（读取本项目里面的文件）
        //不采取读取绝对路径，而是读取classpath路径下的文件
        //try-with-resource
//        try(InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("everything_mini.sql")){
//            if(in == null){
//                //没读到
//                throw new RuntimeException("Not read init database script,please check it!");
//            }
//            StringBuilder sqlBuider = new StringBuilder();
//            //读到了转化会String获取到SQL语句
//            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
//                String line = null;
//                while((line = reader.readLine()) != null){
//                    if(line.startsWith("--")){
//                        sqlBuider.append(line);
//                    }
//                }
//            }
//            //获取数据库连接和名称执行SQL
//            String sql = sqlBuider.toString();
//            //JDBC(4)
//            //1.获取数据库连接
//            Connection connection = dataSource.getConnection();
//            //2.创建命令
//            PreparedStatement statement = connection.prepareStatement(sql);
//            //3.执行SQL语句
//            statement.execute();
//            connection.close();
//            statement.close();
//        }catch(IOException e){
//            e.printStackTrace();
//        }catch(SQLException e){
//            e.printStackTrace();
//        }
        InputStream stream = DataSourceFactory.class.getClassLoader()
                .getResourceAsStream("everything_mini.sql");
        if(stream == null){
            throw new RuntimeException("everything_mini.sql exits");
        }
        Statement statement = null;
        try {
            String sql = IOUtils.toString(stream, "UTF-8");
            System.out.println(dataSource);
             statement = dataSource.getConnection().createStatement();
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try{
                if(statement != null)
                    statement.close();
            }catch (SQLException e){
                e.printStackTrace();
                System.out.println("statement is null");
            }
        }
    }
}


