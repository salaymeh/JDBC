package ca.jrvs.apps.jdbc;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class JDBCExecutor {
  private static final Logger logger = LoggerFactory.getLogger(JDBCExecutor.class);

  public static void main(String[] args) {
    DataBaseConnectionManager dataBaseConnectionManager = new DataBaseConnectionManager("localhost"
    ,"hplussport","host","password");



    try{
      Connection connection = dataBaseConnectionManager.getConnection();
      CustomerDAO customerDAO = new CustomerDAO(connection);
      OrderDAO orderDAO = new OrderDAO(connection);


    }catch (SQLException e){
      JDBCExecutor.logger.error("ERROR: JDBCExecutor: ",e);
    }
  }
}
