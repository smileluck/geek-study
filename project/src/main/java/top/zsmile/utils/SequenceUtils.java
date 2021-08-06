package top.zsmile.utils;

import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class SequenceUtils {
    private long maxKey;        //当前Sequence载体的最大值
    private long minKey;        //当前Sequence载体的最小值
    private long nextKey;       //下一个Sequence值
    private int poolSize = 100;       //Sequence值缓存大小
    private String keyName;     //Sequence的名称

    private final int initNum = 1; //初始化当前值为0

    public SequenceUtils(String keyName) {
        this.keyName = keyName;
        searchSequence();
    }

    public SequenceUtils(String keyName, int poolSize) {
        this.keyName = keyName;
        this.poolSize = poolSize;
        searchSequence();
    }

    public synchronized long getNext() {
        if (this.nextKey >= this.maxKey) {
            searchSequence();
        }
        return this.nextKey++;
    }

    public void searchSequence() {
        JdbcUtils jdbcUtils = new JdbcUtils("jdbc:mysql://127.0.0.1:3306/geek_shop", "root", "root");

        Connection connection = jdbcUtils.getConnection();
        try {
            if (connection != null) {

                PreparedStatement preparedStatement = connection.prepareStatement("select * from tb_sequence where seq_name = ?");
                preparedStatement.setString(1, this.keyName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    long currentVal = resultSet.getLong("current_val");
                    long incrementVal = resultSet.getLong("increment_val");
                    this.minKey = currentVal;
                    this.nextKey = this.minKey;
                    this.maxKey = this.minKey + incrementVal;
                    PreparedStatement updateStatement = connection.prepareStatement("update tb_sequence set current_val = ? where seq_name = ?");
                    updateStatement.setLong(1, this.maxKey);
                    updateStatement.setString(2, this.keyName);
                    updateStatement.executeUpdate();
                    updateStatement.close();
                } else {
                    initSequence(connection);
                }

                preparedStatement.close();

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        jdbcUtils.closeConnection(connection);
    }

    public void initSequence(Connection connection) {
        try {
            this.maxKey = this.initNum + this.poolSize;
            this.minKey = this.initNum;
            this.nextKey = this.minKey;
            PreparedStatement preparedStatement = connection.prepareStatement("insert into tb_sequence(seq_name,current_val,increment_val) value(?,?,?)");
            preparedStatement.setString(1, this.keyName);
            preparedStatement.setLong(2, this.maxKey);
            preparedStatement.setInt(3, this.poolSize);
            preparedStatement.executeUpdate();
            preparedStatement.close();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SequenceUtils sequenceUtils = new SequenceUtils("order-good");
        int i = 1;
        while (i < 3) {
            long next = sequenceUtils.getNext();
            System.out.println(next);
            if (next == 0) i++;

        }
    }
}
