import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;

/**
 * Inserts and drops data from the OrderManager database.
 */
public class OrderManager_DML {
  // database connection variables
  private static Connection conn = null;
  private static Statement s = null;

  // file names are already provided to the class
  private static String customer = "data/customer.tsv";
  private static String inventory = "data/InventoryRecord.tsv";
  private static String product = "data/Product.tsv";
  private static String orders = "data/Orders.tsv";
  private static String orderRecord = "data/OrderRecord.tsv";

  // Prepared statements for insertions
  private static String insertIntoCustomer =
    "insert into customer (GivenName, FamilyName," +
      "Address, City, State, ZipCode, Country) " +
      "values(?,?,?,?,?,?,?)";
  private static String insertIntoProduct = "insert into product values(?,?,?)";
  private static String insertIntoOrders = "insert into orders " +
    "(order_date, shipment_date, CUSTOMER_ID) " +
    "values(?,?,?)";
  private static String insertIntoInventory = "insert into inventory_record values(?,?,?)";
  private static String insertIntoOrderRecord = "insert into order_record values(?,?,?,?)";

  /**
   * Create the DML class with the specified database connection and statement
   * @param conn the connection to database
   * @param s the sql statement
   */
  public OrderManager_DML(Connection conn, Statement s) {
    this.conn = conn;
    this.s = s;
  }

  /**
   * Calls all insert into table functions that insert data from respective files into the database.
   */
  public static void insertAll() {
    int success = 0;
    try {
      insertCustomerFile();
      success++;
    } catch (SQLException e) {
      System.err.printf("Unable to insert into customer\n");
      System.err.println(e.getMessage());
    }
    try {
      insertProductFile();
      success++;
    } catch(SQLException e) {
      System.err.printf("Unable to insert into product\n");
      System.err.println(e.getMessage());
    }
    try {
      insertInventoryFile();
      success++;
    } catch(SQLException e) {
      System.err.printf("Unable to insert into inventory record\n");
      System.err.println(e.getMessage());
    }

    try {
      insertOrdersFile();
      success++;
    } catch(SQLException e) {
      System.err.printf("Unable to insert into order\n");
      System.err.println(e.getMessage());
    }

    try {
      insertOrderRecordFile();
      success++;
    } catch(SQLException e) {
      System.err.printf("Unable to insert into order record\n");
      System.err.println(e.getMessage());
    }

    if(success == 5) {
      System.out.println("Successfully inserted all data.");
    }
  }

  /**
   * Inserts the given information into the customer table. Returns the customer id on success, or -1 if unable to
   * insert the customer.
   * @param givenname
   * @param familyname
   * @param address
   * @param city
   * @param state
   * @param zipcode
   * @param country
   * @return customer id or -1 if unable to insert
   */
  public int insertCustomer(String givenname, String familyname, String address, String city,
                            String state, String zipcode, String country){
    int cust_id = -1;
    try{
      PreparedStatement insertRow_customers = conn.prepareStatement(insertIntoCustomer, PreparedStatement.RETURN_GENERATED_KEYS);
      insertRow_customers.setString(1,givenname);
      insertRow_customers.setString(2,familyname);
      insertRow_customers.setString(3,address);
      insertRow_customers.setString(4,city);
      insertRow_customers.setString(5,state);
      insertRow_customers.setString(6,zipcode);
      insertRow_customers.setString(7,country);
      insertRow_customers.executeUpdate();

      ResultSet rs = insertRow_customers.getGeneratedKeys();
      if(rs.next()) {
        cust_id = rs.getInt(1);
      }
      insertRow_customers.close();
    } catch (SQLException e) {
      System.err.println("Unable to insert " + givenname + " " + familyname + ": " + e.getMessage());
    }
    return cust_id;
  }

  /**
   * Inserts the given information into the product table. Returns 0 on success, -1 on failure.
   * @param name product name
   * @param description product description
   * @param sku product sku
   * @return 0 on success, -1 on failure
   */
  public int insertProduct(String name, String description, String sku) {
    try {
      PreparedStatement insertRow_product = conn.prepareStatement(insertIntoProduct);

      insertRow_product.setString(1, name);
      insertRow_product.setString(2, description);
      insertRow_product.setString(3, sku);

      insertRow_product.executeUpdate();
      insertRow_product.close();
    } catch (SQLException e) {
      System.err.printf("Unable to insert %s: %s\n", name, e.getMessage());
      return -1;
    }
    return 0;
  }

  /**
   * Inserts the given information into the inventory record table. Returns 0 on success, -1 on failure.
   * @param sku product sku
   * @param no_of_units number of units
   * @param price_per_unit price per unit
   * @return 0 on success, -1 on failure
   */
  public int insertInventoryRecord(String sku, int no_of_units, double price_per_unit){
    try {
      PreparedStatement insertRow_product_inventory = conn.prepareStatement(insertIntoInventory);
      insertRow_product_inventory.setString(1,sku);
      insertRow_product_inventory.setInt(2,no_of_units);
      insertRow_product_inventory.setDouble(3,price_per_unit);

      insertRow_product_inventory.executeUpdate();
      insertRow_product_inventory.close();
    } catch (SQLException e) {
      System.err.printf("Unable to insert %s: %s\n", sku, e.getMessage());
      return -1;
    }
    return 0;
  }

  /**
   * Inserts the given information into the orders table. Returns the order id on success,
   * or -1 if unable to insert.
   * @param order_date the date of the order
   * @param shipment_date the shipment date of the order
   * @param cust_id the customer id making the order
   * @return order id or -1 if unable to insert
   */
  public int insertOrder(Date order_date, Date shipment_date, int cust_id) {
    int order_id = -1;
    try{
      PreparedStatement insertRow_orders = conn.prepareStatement(insertIntoOrders, PreparedStatement.RETURN_GENERATED_KEYS);
      insertRow_orders.setDate(1, order_date);
      insertRow_orders.setDate(2, shipment_date);
      insertRow_orders.setInt(3, cust_id);
      insertRow_orders.executeUpdate();

      ResultSet rs = insertRow_orders.getGeneratedKeys();
      if (rs.next()) {
        order_id = rs.getInt(1);
      }

      insertRow_orders.close();
      rs.close();
    } catch (SQLException e) {
      System.err.println("Unable to insert order: " + e.getMessage());
    }
    return order_id;
  }

  /**
   * Inserts the given information into the order_record table. Returns 0 on success and -1 on failure.
   *
   * @param order_id the order id
   * @param units the number of units
   * @param price the price per unit
   * @param sku the product sku
   * @return 0 on success, -1 on failure
   */
  public int insertOrderRecord(int order_id, int units, double price, String sku){
    try{
      PreparedStatement insertRow_order_record = conn.prepareStatement(insertIntoOrderRecord);
      insertRow_order_record.setInt(1, order_id);
      insertRow_order_record.setInt(2, units);
      insertRow_order_record.setDouble(3,
              price);
      insertRow_order_record.setString(4, sku);
      insertRow_order_record.executeUpdate();
      insertRow_order_record.close();
    } catch (SQLException e) {
      System.err.println("Unable to insert order_record: " + e.getMessage());
      return -1;
    }
    return 0;
  }

  /**
   * Inserts information into Customer table from a txt file.
   */
  public static void insertCustomerFile() throws SQLException {
    // column order: (CUST_ID is first, but auto-generated)
    // GivenName FamilyName Address City State ZipCode Country
    PreparedStatement insertRow_customer = conn.prepareStatement(insertIntoCustomer);

    // open file
    try (
      BufferedReader br = new BufferedReader(new FileReader(new File(customer)));
    ) {

      // begin insert line by line
      String line;
      while((line = br.readLine()) != null) {
        String[] data = line.split("\t");

        String givenName = data[0];
        String familyName = data[1];
        String address = data[2];
        String city = data[3];
        String state = data[4];
        String zipCode = data[5];
        String country = data[6];

        insertRow_customer.setString(1,givenName);
        insertRow_customer.setString(2,familyName);
        insertRow_customer.setString(3,address);
        insertRow_customer.setString(4,city);
        insertRow_customer.setString(5,state);
        insertRow_customer.setString(6,zipCode);
        insertRow_customer.setString(7,country);

        insertRow_customer.executeUpdate();

        if (insertRow_customer.getUpdateCount() != 1) {
          System.err.printf("Unable to insert Customer table");
        }

      } // end while
    } catch (FileNotFoundException e) {
      System.err.printf("Unable to open the file: %s\n", customer);
    } catch (IOException e) {
      System.err.printf("Error reading line.\n");
    }

  }

  /**
   * Inserts product information into OrderManager from text file.
   */
  public static void insertProductFile() throws SQLException {
    // order of columns
    // product_name, product_description, product_SKU
    PreparedStatement insertRow_product = conn.prepareStatement(insertIntoProduct);

    // open file
    try (
      BufferedReader br = new BufferedReader(new FileReader(new File(product)));
    ) {

      // begin insert line by line
      String line;
      while((line = br.readLine()) != null) {
        String[] data = line.split("\t");
        String productname = data[0];
        String productdesc = data[1];
        String productsku = data[2];

        insertRow_product.setString(1,productname);
        insertRow_product.setString(2,productdesc);
        insertRow_product.setString(3,productsku);
        insertRow_product.executeUpdate();
        if (insertRow_product.getUpdateCount() != 1) {
          System.err.printf("Unable to insert into product table");
        }
      }
    } catch (FileNotFoundException e) {
      System.err.printf("Unable to open the file: %s\n", product);
    } catch (IOException e) {
      System.err.printf("Error reading line.\n");
    }
  }

  /**
   * Inserts inventory record information into OrderManager from a text file.
   */
  public static void insertInventoryFile() throws SQLException {
    // column order
    // product_SKU, no_of_units, price_per_unit
    PreparedStatement insertRow_inv = conn.prepareStatement(insertIntoInventory);

    // open file
    try (
      BufferedReader br = new BufferedReader(new FileReader(new File(inventory)));
    ) {

      // begin insert line by line
      String line;
      while((line = br.readLine()) != null) {
        String[] data = line.split("\t");
        String inv_units = data[0];
        int inv_units1 = Integer.parseInt(inv_units);
        String inv_price = data[1];
        double dnum = Double.parseDouble(inv_price);
        String inv_sku = data[2];

        insertRow_inv.setString(1,inv_sku);
        insertRow_inv.setInt(2,inv_units1);
        insertRow_inv.setDouble(3,dnum);
        insertRow_inv.executeUpdate();

        if (insertRow_inv.getUpdateCount() != 1) {
          System.err.printf("Unable to insert inventory table");
        }

      }
    } catch (FileNotFoundException e) {
      System.err.printf("Unable to open the file: %s\n", inventory);
    } catch (IOException e) {
      System.err.printf("Error reading line.\n");
    }
  }

  /**
   * Inserts order information into OrderManager from a text file.
   */
  public static void insertOrdersFile() throws SQLException {
    // column order
    // order_id, order_date, shipment_date, customer_id
    PreparedStatement  insertRow_orders = conn.prepareStatement(insertIntoOrders);
    // open file
    try (
      BufferedReader br = new BufferedReader(new FileReader(new File(orders)));
    ) {
      // begin insert line by line
      String line;
      while((line = br.readLine()) != null) {
        String[] data = line.split("\t");
        String order_date = data[0];
        String shipment_date = data[1];
        String cust_id = data[2];
        int cust_id1 = Integer.parseInt(cust_id);

        Date date1 = Date.valueOf(order_date);
        Date date2 = null;
        if(!shipment_date.isEmpty()) {
          date2 = Date.valueOf(shipment_date);
        }

        insertRow_orders.setDate(1, date1);
        insertRow_orders.setDate(2, date2);
        insertRow_orders.setInt(3, cust_id1);
        insertRow_orders.executeUpdate();

        if (insertRow_orders.getUpdateCount() != 1) {
          System.err.printf("Unable to insert orders table");
        }
      }
    } catch (FileNotFoundException e) {
      System.err.printf("Unable to open the file: %s\n", orders);
    } catch (IOException e) {
      System.err.printf("Error reading line.\n");
    }
  }

  /**
   * Inserts order record information into OrderManager from a text file
   */
  public static void insertOrderRecordFile() throws SQLException {
    // column order
    // order_id, no_of_untis, price_per_unit, product_sku
    PreparedStatement insertRow_orderRecord = conn.prepareStatement(insertIntoOrderRecord);
    try (
      BufferedReader br = new BufferedReader(new FileReader(new File(orderRecord)));
    ) {
      // begin insert line by line
      String line;
      while((line = br.readLine()) != null) {
        String[] data = line.split("\t");

        String order_id= data[0];
        int order_id1= Integer.parseInt(order_id);
        String no_of_units = data[1];
        int no_of_units1 = Integer.parseInt(no_of_units);
        String price_per_unit = data[2];
        double price_per_unit1 = Double.parseDouble(price_per_unit);
        String product_sku = data[3];

        insertRow_orderRecord.setInt(1, order_id1);
        insertRow_orderRecord.setInt(2, no_of_units1);
        insertRow_orderRecord.setDouble(3, price_per_unit1);
        insertRow_orderRecord.setString(4, product_sku);
        insertRow_orderRecord.executeUpdate();

        if (insertRow_orderRecord.getUpdateCount() != 1) {
          System.err.printf("Unable to insert order record table");
        }
      }
    } catch (FileNotFoundException e) {
      System.err.printf("Unable to open the file: %s\n", orderRecord);
    } catch (IOException e) {
      System.err.printf("Error reading line.\n");
    }
  }

  /**
   * Truncate tables, clearing them of existing data
   *
   * @param dbTables an array of table names
   */
  public static void truncateTables(String dbTables[]) {
    int deleted = 0;
    for(String table : dbTables) {
      try {
        s.executeUpdate("delete from " + table);
        deleted++;
      }
      catch (SQLException e) {
        System.out.println("Did not truncate table " + table);
      }
    }
    if(deleted == dbTables.length){
      System.out.println("Successfully truncated all tables.");
    }
  }
}