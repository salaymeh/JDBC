package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.util.DataAccessObject;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.sun.org.slf4j.internal.Logger;

public class CustomerDAO extends DataAccessObject<Customer> {

  private static final Logger logger = LoggerFactory.getLogger(CustomerDAO.class);

  private static final String INSERT = "INSERT INTO customer (first_name,last_name,"
      + "email, phone, address, city,state,zipcode) VALUES(?,?,?,?,?,?,?,?)";

  private static final String GET_ONE = "SELECT customer_id, first_name, last_name,"
      + "email,phone,address,city,state,zipcode FROM customer WHERE customer_id = ?";

  private static final String UPDATE = "UPDATE customer SET first_name=?,last_name=?, "
      + "email=?, phone=?, address=?, city=?,state=?,zipcode=? WHERE customer_id = ?";


  private static final String DELETE = "DELETE FROM customer WHERE customer_id=?";

  private static final String GET_ALL_LMT = "SELECT customer_id, first_name, last_name, email, phone, "
      + "address, city, state, zipCode FROM customer ORDER BY last_name, first_name LIMIT ?";

  private static final String GET_ALL_PAGED = "SELECT customer_id, first_name, last_name, email, phone, "
      + "address, city, state, zipCode FROM customer ORDER BY last_name, first_name LIMIT ? OFFSET ?";

  public CustomerDAO(Connection connection) {
    super(connection);
  }

  @Override
  public Customer findById(long id) throws SQLException {
    Customer customer = new Customer();
    try(PreparedStatement statement = this.connection.prepareStatement(GET_ONE);){
      statement.setLong(1,id);
      ResultSet resultSet = statement.executeQuery();
      while(resultSet.next()){
        customer.setId(resultSet.getLong("customer_id"));
        customer.setFirstName(resultSet.getString("first_name"));
        customer.setLastName(resultSet.getString("last_name"));
        customer.setEmail(resultSet.getString("email"));
        customer.setPhone(resultSet.getString("phone"));
        customer.setAddress(resultSet.getString("address"));
        customer.setCity(resultSet.getString("city"));
        customer.setZipCode(resultSet.getString("zipcode"));

      }
    }catch (SQLException e){
      CustomerDAO.logger.error("ERROR CANNOT FIND ID: ",e);
      throw new SQLException("ERROR FINDBYID: ",e);
    }
    return customer;
  }

  @Override
  public List<Customer> findAll() {
    return null;
  }

  @Override
  public Customer update(Customer dto) throws SQLException {
    Customer customer = null;
    try{
      this.connection.setAutoCommit(false);
    }catch (SQLException e){
      CustomerDAO.logger.error("ERROR: CUSTOMER UPDATE AUTO COMMIT: ",e);
      throw new SQLException("ERROR: CUSTOMER UPDATE AUTO COMMIT: ",e);
    }
    try(PreparedStatement statement = this.connection.prepareStatement(UPDATE);){
      statement.setString(1,dto.getFirstName());
      statement.setString(2,dto.getLastName());
      statement.setString(3,dto.getEmail());
      statement.setString(4,dto.getPhone());
      statement.setString(5,dto.getAddress());
      statement.setString(6,dto.getCity());
      statement.setString(7,dto.getState());
      statement.setString(8,dto.getZipCode());
      statement.setLong(9,dto.getId());
      statement.execute();

      this.connection.commit();
      try {
        this.connection.rollback();
      }catch (SQLException e){
        CustomerDAO.logger.error("ERROR: TRANSACTION CUSTOMER UPDATE CANNOT BE COMPLETED: ",e);
        throw new SQLException("ERROR: TRANSACTION CUSTOMER UPDATE CANNOT BE COMPLETED: ",e);
      }

      customer = this.findById(dto.getId());

    }catch (SQLException e){
      CustomerDAO.logger.error("ERROR: CANNOT UPDATE CUSTOMER: ",e);
      throw new SQLException("ERROR: METHOD CUSTOMER UPDATE:  ",e);
    }
    return customer;
  }

  @Override
  public Customer create(Customer dto) throws SQLException {
    try(PreparedStatement statement = this.connection.prepareStatement(INSERT);){
      statement.setString(1,dto.getFirstName());
      statement.setString(2,dto.getLastName());
      statement.setString(3,dto.getEmail());
      statement.setString(4,dto.getPhone());
      statement.setString(5,dto.getAddress());
      statement.setString(6,dto.getCity());
      statement.setString(7,dto.getState());
      statement.setString(8,dto.getZipCode());
      statement.execute();
      int id = this.getLastVal(CUSTOMER_SEQUENCE);
      return this.findById(id);
    }catch (SQLException e){
      CustomerDAO.logger.error("ERROR: CANNOT CREATE CUSTOMER: ",e);
      throw new SQLException("ERROR: METHOD CUSTOMER CREATE:  ",e);
    }
  }

  @Override
  public void delete(long id) throws SQLException {
    try (PreparedStatement statement = this.connection.prepareStatement(DELETE);){
        statement.setLong(1,id);
        statement.execute();
    }catch (SQLException e){
      CustomerDAO.logger.error("ERROR: CANNOT DELETE CUSTOMER: ",e);
      throw new SQLException("ERROR: METHOD CUSTOMER DELETE:  ",e);
    }

  }

  public List<Customer> findAllSorted(int limit) throws SQLException{
    List<Customer> customers = new ArrayList<>();
    try(PreparedStatement statement = this.connection.prepareStatement(GET_ALL_LMT);){
      statement.setInt(1,limit);
      ResultSet resultSet = statement.executeQuery();
      while(resultSet.next()){
        Customer customer = new Customer();
        customer.setId(resultSet.getLong("customer_id"));
        customer.setFirstName(resultSet.getString("first_name"));
        customer.setLastName(resultSet.getString("last_name"));
        customer.setEmail(resultSet.getString("email"));
        customer.setPhone(resultSet.getString("phone"));
        customer.setAddress(resultSet.getString("address"));
        customer.setCity(resultSet.getString("city"));
        customer.setZipCode(resultSet.getString("zipcode"));
        customers.add(customer);
      }
    }catch (SQLException e){
      CustomerDAO.logger.error("ERROR: CANNOT FIND CUSTOMERS : ",e);
      throw new SQLException("ERROR: METHOD CUSTOMER findAllSorted:  ",e);
    }
    return customers;
  }

  public List<Customer> findAllPaged(int limit,int pageNumber) throws SQLException {
    List<Customer> customers = new ArrayList<>();
    int offset = ((pageNumber-1)* limit);
    try(PreparedStatement statement = this.connection.prepareStatement(GET_ALL_PAGED);){
      if(limit<1){
        limit = 10;
      }

      statement.setInt(1,limit);
      statement.setInt(2, offset);
      ResultSet resultSet = statement.executeQuery();
      while(resultSet.next()){
        Customer customer = new Customer();
        customer.setId(resultSet.getLong("customer_id"));
        customer.setFirstName(resultSet.getString("first_name"));
        customer.setLastName(resultSet.getString("last_name"));
        customer.setEmail(resultSet.getString("email"));
        customer.setPhone(resultSet.getString("phone"));
        customer.setAddress(resultSet.getString("address"));
        customer.setCity(resultSet.getString("city"));
        customer.setZipCode(resultSet.getString("zipcode"));
        customers.add(customer);
      }
    }catch (SQLException e){
      CustomerDAO.logger.error("ERROR: CANNOT PAGED CUSTOMERS : ",e);
      throw new SQLException("ERROR: METHOD CUSTOMER findAllPaged:  ",e);
    }
    return customers;
  }
}
