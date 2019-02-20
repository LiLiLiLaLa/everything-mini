package everything.core.dao.impl;

import everything.core.dao.FileIndexDao;
import everything.core.model.Condition;
import everything.core.model.FileType;
import everything.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements FileIndexDao {
    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //获取数据库连接
            connection = dataSource.getConnection();
            //准备SQL语句
            String sql = "insert into file_index(name, path, depth, file_type) VALUES (?,?,?,?)";
            //准备命令
            statement = connection.prepareStatement(sql);
            //设置参数1 2 3 4
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
            statement.setString(4, thing.getFileType().name());
            //执行命令
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(null, statement, connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();
        //TODO
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //获取数据库连接
            connection = dataSource.getConnection();
            //name: like
            //file_Type: =
            //limit: limit offset
            //orderbyAsc: order by
            StringBuilder sqlBuilder = new StringBuilder();//不用StringBuffer是因为这个搜索一般不会出现多线程访问，不太可能存在线程安全问题
            sqlBuilder.append("select name, path, depth, file_type from file_index ");
            //name匹配原则：前模糊、后模糊、前后模糊，此处选择前后模糊
            sqlBuilder.append(" where ").append("name like '%").append(condition.getName()).append("%' ");
            if(condition.getFileType() != null){
                sqlBuilder.append(" and file_type = '").append(condition.getFileType().toUpperCase()).append("'");
            }
            //limit,order by必选的
            sqlBuilder.append(" order by depth ").append(condition.getOrderByAsc() ? "asc" : "desc");
            sqlBuilder.append("limit ").append(condition.getLimit()).append("offset 0 ");
            //准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            //执行命令
            resultSet = statement.executeQuery();
            //处理结果
            while(resultSet.next()){
                //数据库中的行记录变成Java中的对象Thing
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.looByName(fileType));
                things.add(thing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(resultSet, statement, connection);
        }
        return null;
    }

    //解决内部大量代码重复问题：重构
    private void releaseResource(ResultSet resultSet, PreparedStatement statement, Connection connection){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
