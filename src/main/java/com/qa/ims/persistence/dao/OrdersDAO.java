package com.qa.ims.persistence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qa.ims.persistence.domain.Orders;
import com.qa.ims.utils.DBUtils;

public class OrdersDAO implements Dao<Orders> {

	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public Orders modelFromResultSet(ResultSet resultSet) throws SQLException {
		long OrderID = resultSet.getLong("OrderID");
		long id = resultSet.getLong("id");
		long productID = resultSet.getLong("productID");
		long quantity = resultSet.getLong("quantity");
		return new Orders(OrderID, id, productID, quantity);
	}

	/**
	 * Reads all ORDERS from the database
	 * @return A list of Orders
	*/

	
	@Override
	public List<Orders> readAll() {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM Orders");) {
			List<Orders> Orders = new ArrayList<>();
			while (resultSet.next()) {
				Orders.add(modelFromResultSet(resultSet));
			}
			return Orders;
		} catch (SQLException e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return new ArrayList<>();
	}

	public Orders readLatest() {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM Orders ORDER BY OrderID DESC LIMIT 1");) {
			resultSet.next();
			return modelFromResultSet(resultSet);
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Creates an Order in the database
	 * 
	 * @param Orders - takes in a customer object. OrderID will be ignored
	 */
	@Override
	public Orders create(Orders order) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				PreparedStatement statement = connection
						.prepareStatement("INSERT INTO Orders(id, productID, quantity) VALUES (?, ?, ?)");) {
			statement.setLong(1, order.getId());
			statement.setLong(2, order.getproductID());
			statement.setLong(3, order.getquantity());
			statement.executeUpdate();
			return readLatest();
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Orders read(Long orderID) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM Orders WHERE orderID = ?");) {
			statement.setLong(1, orderID);
			try (ResultSet resultSet = statement.executeQuery();) {
				resultSet.next();
				return modelFromResultSet(resultSet);
			}
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Updates an ORDER in the database
	 * 
	 * @param Orders - takes in an Orders object, the id field will be used to
	 *                 update that Order in the database
	 * @return
	 */
	@Override
	public Orders update(Orders order) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				PreparedStatement statement = connection
						.prepareStatement("UPDATE Orders SET id = ?, productID = ?, quantity = ? WHERE orderID = ?");) {
			statement.setLong(1, order.getId());
			statement.setLong(2, order.getproductID());
			statement.setLong(3, order.getquantity());
			statement.executeUpdate();
			return read(order.getId());
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Deletes an ORDER in the database by orderID
	 * 
	 * @param orderID - orderID of the the ORDER
	 */
	@Override
	public int delete(long orderID) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement("DELETE FROM Orders WHERE orderID = ?");) {
			statement.setLong(1, orderID);
			return statement.executeUpdate();
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return 0;
	}

}
