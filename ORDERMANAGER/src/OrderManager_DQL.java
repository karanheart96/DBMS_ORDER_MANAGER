import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;

/**
 * This class provides support for DQL for OrderManager. It contains functions for selecting relations
 * from the OrderManager database.
 */
public class OrderManager_DQL {
  // connection variables
  Connection conn = null;
  Statement s = null;

  // holds results from queries
  private ResultSet rs = null;

  /**
   * Creates an instance of the class with the sql statement for OrderManager database.
   * @param s the sql statement
   */
  public OrderManager_DQL(Connection conn, Statement s) {
    this.conn = conn;
    this.s = s;
  }

  /**
   * Selects all entries in customer table and prints all the info.
   *
   */
  public void selectAllCustomer() {
    try {
      ResultSet rs = s.executeQuery("select * from customer");
      while(rs.next()){
        System.out.println(rs.getString("CUST_ID"));
        System.out.println(rs.getString("givenName") + " " + rs.getString("familyName"));
        System.out.println(rs.getString("Address") + "\n"
          + rs.getString("City") + ", " + rs.getString("State") + " "
          + rs.getString("ZipCode") + "\n" + rs.getString("Country")
        );
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Selects a result from Customer based on customer id primary key
   *
   * @param cust_id the customer id
   */
  public void selectCustomerID(int cust_id) {
    try {
      rs = s.executeQuery("Select * from customer where CUST_ID = " + cust_id);
      while (rs.next()) {
        System.out.println(rs.getInt("CUST_ID"));
        System.out.println(rs.getString("givenName"));
        System.out.println(rs.getString("familyName"));
        System.out.println(rs.getString("Address"));
        System.out.println(rs.getString("City"));
        System.out.println(rs.getString("State"));
        System.out.println(rs.getString("ZipCode"));
        System.out.println(rs.getString("Country"));
      }
      rs.close();
    } catch (SQLException e) {
      System.err.printf("Customer ID: %d was not found\n", cust_id);
    }
  }

  /**
   * Selects all products from product table and prints the information.
   *
   */
  public void selectAllProduct() {
    try {
      rs = s.executeQuery("Select * from product");
      while (rs.next()) {
        System.out.println("Product Name: " + rs.getString("product_name"));
        System.out.println("Product Description: " + rs.getString("product_description"));
        System.out.println("Product SKU: " + rs.getString("product_SKU"));
        System.out.println(); // line break to separate products
      }
      rs.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Selects a result from Product based on product sku primary key.
   *
   * @param sku the product sku
   */
  public void selectProductSKU(String sku) {
    try {
      rs = s.executeQuery("Select * from product where product_SKU = '" + sku + "'");
      while (rs.next()) {
        System.out.println("Product Name: " + rs.getString("product_name"));
        System.out.println("Product Description: " + rs.getString("product_description"));
        System.out.println("Product SKU: " + rs.getString("product_SKU"));
      }
      rs.close();
    } catch (SQLException e) {
      System.err.printf("Product with SKU: %s was not found\n", sku);
      System.err.println(e.getMessage());
    }
  }

  /**
   * Selects all records in inventory record table and prints out the information
   *
   */
  public void selectAllInventory() {
    try {
      rs = s.executeQuery("Select * from inventory_record");
      while (rs.next()) {
        System.out.printf("Price per Unit: $%.2f\n",rs.getDouble("price_per_unit"));
        System.out.println("No of units: " + rs.getString("no_of_units"));
        System.out.println("Product SKU: "+rs.getString("product_SKU"));
        System.out.println();
      }
      rs.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Selects all records in inventory record matching the product sku and prints out the information.
   * @param sku the product sku
   */
  public void selectInventorySKU(String sku) {
    try {
      rs = s.executeQuery("Select * from inventory_record where product_SKU='" + sku + "'");
      while (rs.next()) {
        System.out.printf("Price per Unit: $%.2f\n",rs.getDouble("price_per_unit"));
        System.out.println("No of units: " + rs.getString("no_of_units"));
        System.out.println("Product SKU: "+rs.getString("product_SKU"));
        System.out.println();
      }
      rs.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Selects all records in orders table and prints out the information.
   */
  public void selectAllOrders(){
    try {
      rs = s.executeQuery("Select * from orders");
      while (rs.next()) {
        System.out.println("Order id: " + rs.getInt("order_id"));
        System.out.println("Order date: " + rs.getDate("order_date"));
        if(rs.getDate("shipment_date") == null) {
          System.out.println("Shipped: Not yet shipped");
        } else {
          System.out.println("Shipped: " +rs.getDate("shipment_date"));
        }
        System.out.println("Customer id: "+rs.getInt("CUSTOMER_ID"));
        System.out.println();
      }
      rs.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Selects all records in orders table and prints out the information.
   */
  public void selectAllOrderRecord(){
    try {
      rs = s.executeQuery("Select * from order_record");
      while (rs.next()) {
        System.out.println("Order id: " + rs.getInt("order_id"));
        System.out.println("Number of units: " + rs.getInt("no_of_units"));
        System.out.printf("Price per unit: $%.2f\n",rs.getDouble("price_per_unit"));
        System.out.println("Product SKU: "+rs.getString("product_SKU"));
        System.out.println();
      }
      rs.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Selects all orders under a certain customer and prints out each order with order record details
   * @param cust_id the customer id
   */
  public void selectCustomerOrders(int cust_id) {
    try {
      rs = s.executeQuery("Select * from orders where CUSTOMER_ID = " + cust_id);
      while(rs.next()) {
       System.out.println("Order id: " + rs.getInt("order_id"));
       System.out.println("Order date: " + rs.getDate("order_date"));
       if(rs.getDate("shipment_date") == null) {
          System.out.println("Shipped: Not yet shipped");
        } else {
          System.out.println("Shipped: " +rs.getDate("shipment_date"));
        }
       ResultSet rs_record = conn.createStatement().executeQuery("Select * from order_record where order_id = " + rs.getInt("order_id"));
       while(rs_record.next()){
         System.out.printf("Product SKU: %s\n", rs_record.getString("product_SKU"));
         System.out.println("Number of units: " + rs_record.getInt("no_of_units"));
         System.out.printf("Price per unit: $%.2f\n",rs_record.getDouble("price_per_unit"));
       }
       System.out.println();
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Prints a list of available products, their prices, and the number of available units to standard output.
   */
  public void selectAvailableProducts() {
    try {
      rs = s.executeQuery("Select * from product");
      while(rs.next()) {
        System.out.println(rs.getString("product_name"));
        System.out.println(rs.getString("product_description"));
        ResultSet rsInv = conn.createStatement().executeQuery("Select * from inventory_record where product_SKU='" + rs.getString("product_SKU") + "'");
        if(rsInv.next()) {
          double price = rsInv.getDouble("price_per_unit");
          if(price == 0) {
            System.out.println("Price : Free");
          } else {
            System.out.printf("Price: $%.2f\n", price);
          }
          int units = rsInv.getInt("no_of_units");
          if(units == 0) {
            System.out.println("Out of stock.");
          } else {
            System.out.println("Units: " + units);
          }
          System.out.println();
        }
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

}