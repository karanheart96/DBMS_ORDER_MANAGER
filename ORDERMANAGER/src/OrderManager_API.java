import java.sql.*;

/**
 * This class provides an API for the OrderManager database.
 */
public class OrderManager_API {
  // holds an instance of the DML class for insert statements
  private OrderManager_DML dml = null;

  // holds an instance of the DQL class for select statements
  private OrderManager_DQL dql = null;

  /**
   * Creates an instance of the API class with instances of the DML and DQL classes
   */
  public OrderManager_API(OrderManager_DML dml, OrderManager_DQL dql){
    this.dml = dml;
    this.dql = dql;
  }

  /**
   * Creates an order at the current date for the customer of the given id.
   * If one of the requested products is out of stock for requested number of units,
   * it skips over that product and adds any other products it can. If, as a result, number of products
   * in the order is 0, then the order is not created.
   *
   * @param s     sql statement
   * @param conn  connection to database
   * @param cust_id the id of the customer placing the order
   * @param sku the product skus
   * @param units the number of units bought for each sku
   */
  public void createOrder(Statement s, Connection conn, int cust_id, String[] sku, int[] units) {
    // set order and shipment date
    java.util.Date uDate = new java.util.Date();
    java.sql.Date order_date = new java.sql.Date(uDate.getTime());
    Date shipment_date = null;

    // counter variable to keep track of products added in the order
    int added = 0;

    try {
      conn.setAutoCommit(false);
      Savepoint sp1 = conn.setSavepoint("InsertOrder");
      try {

        // create the order and get its order id
        int order_id = dml.insertOrder(order_date, shipment_date, cust_id);

        if (order_id != -1) {
          // add to order record
          for(int i=0; i<sku.length; i++){
            // make sure can insert this sku for number of units
            double price = checkInventory(s, sku[i], units[i]);
            if(price != -1) {
              int success = dml.insertOrderRecord(order_id, units[i], price, sku[i]);
              if(success != -1) added++;
            } else {
              continue;
            }
          }
        }

        if (added == 0) {
          // no products added, rollback to savepoint
          conn.rollback(sp1);
          System.err.println("Order was not created.");
        } else if(added < sku.length) {
          // partial order was made
          System.out.printf("Only %d out of %d products were added to the order.\n", added, sku.length);
        } else {
          // all products were added
          System.out.printf("Order #%d successfully created.\n", order_id);
        }

        // commit transaction
        conn.commit();
        conn.setAutoCommit(true);
      } catch (SQLException e) {
        System.err.printf("%s\n", e.getMessage());
        conn.rollback(sp1);
        conn.setAutoCommit(true);
      }
    } catch (SQLException e2) {
      System.err.println(e2.getMessage());

    }


  }

  /**
   * Checks to see if the product exists in inventory, and if there are enough units to create an order.
   * If the product exists and there are available units, returns the price for that product. Otherwise,
   * returns -1.
   *
   * @param sku product sku
   * @param units number of units requested
   * @return the price of the product or -1 if requested units exceed available or product does not exist
   */
  public double checkInventory(Statement s, String sku, int units){
    ResultSet rs;
    double price = -1;
    try {
      rs = s.executeQuery("select * from inventory_record where product_SKU = '" + sku + "'");
      if(rs.next()) {
        int units_available = rs.getInt("no_of_units");
        if(units_available < units) {
          System.out.printf("Requested %d units, but only %d available for %s.\n", units, units_available, sku);
        } else {
          price = rs.getDouble("price_per_unit");
        }
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      return price;
    }
    return price;
  }

  /**
   * Creates a product with the given attributes
   * @param s sql statement
   * @param conn connection to database
   * @param name name of the product
   * @param sku product's sku
   * @param description product description
   * @param no_of_units number of units
   * @param price_per_unit price per unit
   */
  public void createProduct(Statement s, Connection conn, String name, String sku, String description,
                            int no_of_units, double price_per_unit) {
    // create savepoint
    try {
      conn.setAutoCommit(false);
      Savepoint sp1 = conn.setSavepoint("InsertProduct");
      if(dml.insertProduct(name, description, sku) != -1) {
        // if product was inserted, try to insert into inventory record
        int success = dml.insertInventoryRecord(sku, no_of_units, price_per_unit);
        if(success == -1) {
          System.err.printf("Product was not created: %s (%s)\n", name, sku);
          conn.rollback(sp1);
        } else {
          System.out.println("Successfully added " + name);
        }
      } else {
        System.err.printf("Product was not created: %s (%s)\n", name, sku);
      }

        conn.commit();
        conn.setAutoCommit(true);

    } catch (SQLException e) {

      System.err.println(e.getMessage());
    }

  }


  /**
   * Creates a product with the given attributes and assumes 0 units and 0 for price.
   *
   * @param s sql statement
   * @param conn connection to database
   * @param name name of the product
   * @param sku product's sku
   * @param description product description
   */
  public void createProduct(Statement s, Connection conn, String name, String sku, String description) {
    // insert into product
    createProduct(s, conn, name, sku, description, 0, 0.0);
  }


  /**
   * Creates a customer with the specified fields and returns the created customer's id, or -1 if unable to create a customer.
   *
   * @param s sql statement
   * @param conn connection to database
   * @param givenname customer's given name
   * @param familyname customer's family name
   * @param address customer's address
   * @param city customer's city
   * @param state customer's state, null if no state
   * @param zipcode customer's zipcode
   * @param country customer's country
   * @return customer id or -1 if unable to create a customer
   */
  public int createCustomer(Statement s ,Connection conn,String givenname ,String familyname,String address,String city,String state,String zipcode,String country) {
    //insert into customer
    int cust_id = dml.insertCustomer(givenname, familyname, address, city, state, zipcode, country);
    if(cust_id != -1) {
      System.out.printf("Successfully added %s %s\n", givenname, familyname);
    }
    return cust_id;
  }

  /**
   * Gets the total cost of an order.
   * @param s sql statement
   * @param conn connection to database
   * @param order_id the order id
   * @return the total cost of an order
   */
  public double getOrderTotal(Statement s, Connection conn, int order_id) {
    double total = 0;
    double price = 0;
    double units = 0;
    ResultSet rs;
    // get order information
    try {
      rs = s.executeQuery("Select * from order_record where order_id = " + order_id);
      while (rs.next()) {
        price = rs.getDouble("price_per_unit");
        units = rs.getInt("no_of_units");
        total += price * units;
      }
      rs.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }

    return total;
  }

}