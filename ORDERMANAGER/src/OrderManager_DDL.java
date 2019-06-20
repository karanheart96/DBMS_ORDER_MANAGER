import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class has DDL functionality for the OrderManager database.
 * It contains functions to create and drop the tables, functions, and procedures.
 *
 */
public class OrderManager_DDL {
  // sql statement connected to OrderManager database
  private static Statement s = null;

  /**
   * Creates an instance of the class that has the sql statement variable for OrderManager
   * @param s the sql statement
   */
  public OrderManager_DDL(Statement s){
    this.s = s;
  }
  /**
   * Create stored functions in OrderManager.
   */
  public void createFunctions() {
    int success = 0;
    try {
      String func_isCountry = "CREATE FUNCTION isCountry(" +
        " Country VARCHAR(32)" +
        ") RETURNS BOOLEAN" +
        " PARAMETER STYLE JAVA" +
        " LANGUAGE JAVA" +
        " DETERMINISTIC" +
        " NO SQL" +
        " EXTERNAL NAME" +
        " 'DBMS_storedfunc.isCountry' ";

      s.executeUpdate(func_isCountry);
      success++;
    } catch (SQLException e) {
      System.err.printf("Did not create function isCountry: %s\n", e.getMessage());
    }

    try {
      String func_isState = "CREATE FUNCTION isState(" +
        " State VARCHAR(4)," +
        " country VARCHAR(32)" +
        ") RETURNS BOOLEAN" +
        " PARAMETER STYLE JAVA" +
        " LANGUAGE JAVA" +
        " DETERMINISTIC" +
        " NO SQL" +
        " EXTERNAL NAME" +
        " 'DBMS_storedfunc.isState'";
      s.executeUpdate(func_isState);
      success++;
    } catch (SQLException e) {
      System.err.printf("Did not create function isState: %s\n", e.getMessage());
    }

    try {
      String func_isUnit = "CREATE FUNCTION isUnit(" +
        " no_of_units INTEGER" +
        ") RETURNS BOOLEAN" +
        " PARAMETER STYLE JAVA" +
        " LANGUAGE JAVA" +
        " DETERMINISTIC" +
        " NO SQL" +
        " EXTERNAL NAME" +
        " 'DBMS_storedfunc.isUnit'";

      s.executeUpdate(func_isUnit);
      success++;
    } catch (SQLException e) {
      System.err.printf("Did not create function isUnit: %s\n", e.getMessage());
    }

    try {
      String func_isPrice = "CREATE FUNCTION isPrice(" +
        " price_per_unit DECIMAL(7,2)" +
        ") RETURNS BOOLEAN" +
        " PARAMETER STYLE JAVA" +
        " LANGUAGE JAVA" +
        " DETERMINISTIC" +
        " NO SQL" +
        " EXTERNAL NAME" +
        " 'DBMS_storedfunc.isPrice'";

      s.executeUpdate(func_isPrice);
      success++;
    } catch (SQLException e) {
      System.err.printf("Did not create function isPrice: %s\n", e.getMessage());
    }
    try {
      String func_isSKU = "CREATE FUNCTION isZipCode(" +
        " zipCode VARCHAR(16)," +
        " country VARCHAR(32)" +
        ") RETURNS BOOLEAN" +
        " PARAMETER STYLE JAVA" +
        " LANGUAGE JAVA" +
        " DETERMINISTIC" +
        " NO SQL" +
        " EXTERNAL NAME" +
        " 'DBMS_storedfunc.isZipCode'";

      s.executeUpdate(func_isSKU);
      success++;
    } catch (SQLException e) {
      System.err.printf("Did not create function isZipCode: %s\n", e.getMessage());
    }

    try {
      String func_isSKU = "CREATE FUNCTION isSKU(" +
        " product_SKU VARCHAR(12)" +
        ") RETURNS BOOLEAN" +
        " PARAMETER STYLE JAVA" +
        " LANGUAGE JAVA" +
        " DETERMINISTIC" +
        " NO SQL" +
        " EXTERNAL NAME" +
        " 'DBMS_storedfunc.isSKU'";

      s.executeUpdate(func_isSKU);
      success++;
    } catch (SQLException e) {
      System.err.printf("Did not create function isSKU: %s\n", e.getMessage());
    }

    if(success == 6) {
      System.out.println("Successfully created all functions.");
    }
  }

  /**
   * Creates the tables for the OrderManager database.
   * Created tables: customer, product, orders,  inventory_record, and order_record
   */
  public void createTables() {
    int success = 0; // keeps track of successfully tables created
    try {
      s.executeUpdate("Create table customer( " +
        "CUST_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
        "GivenName VARCHAR(32) NOT NULL," +
        "FamilyName VARCHAR(32) NOT NULL," +
        "Address VARCHAR(64) NOT NULL," +
        "City VARCHAR(64) NOT NULL," +
        "State VARCHAR(4)," + // state can be null for countries that do not have states
        "ZipCode VARCHAR(16) NOT NULL," +
        "Country VARCHAR(32) NOT NULL," +
        "PRIMARY KEY(CUST_ID)," +
        "CHECK (isState(State, Country))," +
        "CHECK (isCountry(Country))," +
        "CHECK (isZipCode(ZipCode, Country))" +
        ")"
      );
      success++;
    } catch (SQLException e) {
      System.err.println("Unable to create customer table: " + e.getMessage());
    }

    try {
      s.executeUpdate("Create table product(" +
        "product_name VARCHAR(32) NOT NULL," +
        "product_description VARCHAR(1000) NOT NULL," +
        "product_SKU VARCHAR(12) NOT NULL," +
        "PRIMARY KEY(product_SKU)," +
        "CHECK (isSKU(product_SKU)))"
      );
      success++;
    } catch (SQLException e) {
      System.err.println("Unable to create product table: " + e.getMessage());
    }

    try {
      s.executeUpdate("Create table orders(" +
        "order_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
        "order_date DATE NOT NULL," +
        "shipment_date DATE," +
        "CUSTOMER_ID INTEGER NOT NULL references customer(CUST_ID) ON DELETE CASCADE," +
        "PRIMARY KEY(order_id))"
      );
      success++;
    } catch (SQLException e) {
      System.err.println("Unable to create orders table: " + e.getMessage());
    }

    try {
      s.executeUpdate("Create table inventory_record(" +
        "product_SKU VARCHAR(12) NOT NULL references product(product_SKU) ON DELETE CASCADE," +
        "no_of_units INTEGER NOT NULL," +
        "price_per_unit DECIMAL(7,2) NOT NULL," +
        "PRIMARY KEY(product_SKU)," +
        "CHECK (isUnit(no_of_units))," +
        "CHECK (isPrice(price_per_unit)))"
      );
      success++;
    } catch (SQLException e) {
      System.err.println("Unable to create inventory_record table: " + e.getMessage());
    }

    try {
      s.executeUpdate("Create table order_record(" +
        "order_id INTEGER NOT NULL references orders(order_id) ON DELETE CASCADE ON UPDATE RESTRICT," +
        "no_of_units INTEGER NOT NULL," +
        "price_per_unit DECIMAL(7,2) NOT NULL," +
        "product_SKU VARCHAR(12) NOT NULL references product(product_SKU)," +
        "CHECK (isUnit(no_of_units))," +
        "CHECK (isPrice(price_per_unit)))"
      );
      success++;
    } catch (SQLException e) {
      System.err.println("Unable to create order_record table: " + e.getMessage());
    }

    if(success == 5) {
      System.out.println("Successfully created all tables.");
    }

  }

  /**
   * Creates triggers for OrderManager
   */
  public void createTriggers() {
    int success = 0;
    /** inventory_delete updates number of units in inventory_record when an order_record is deleted */
    try {
      String inven_delete = "create trigger inventory_delete " +
        "after delete on order_record " +
        "referencing old as deletedorderrecord " +
        "for each row mode DB2SQL " +
        "update inventory_record " +
        "set no_of_units = no_of_units + deletedorderrecord.no_of_units  " +
        "where product_SKU = deletedorderrecord.product_SKU";
      s.executeUpdate(inven_delete);
      success++;
    } catch (SQLException e) {
      System.err.printf("Error creating trigger inven_delete\n");
    }
    /** inventory_update1 is a trigger that updates the number of units in inventory_record when an order_record is created */
    try {
      String inven_update = "create trigger inventory_update1 " +
        "after insert on order_record " +
        "referencing new as insertedorderrecord " +
        "for each row mode DB2SQL " +
        "update inventory_record " +
        "set no_of_units = no_of_units - insertedorderrecord.no_of_units  " +
        "where product_SKU = insertedorderrecord.product_SKU";
      s.executeUpdate(inven_update);
      success++;
    }
    catch (SQLException e) {
      System.err.printf("Error creating trigger inven_update\n");
    }

    if(success == 2) {
      System.out.println("Successfully created all triggers.");
    }

  }

  /**
   * Creates procedures for OrderManager (no procedures created, currently unused)
   */
  public void createProcedures() throws SQLException {
    // no procedures, unused
  }

  /**
   * Drops all tables in OrderManager
   */
  public void dropTables(String dbTables[]){
    // Drops tables if they already exist
    int dropped = 0;
    for(String table : dbTables) {
      try {
        s.execute("drop table " + table);
        dropped++;
      } catch (SQLException e) {
        System.out.println("did not drop " + table);
      }
    }
    if(dropped == dbTables.length){
      System.out.println("Successfully dropped all tables.");
    }
  }

  /**
   * Drops all functions in OrderManager
   */
  public void dropFunctions(String dbFunctions[]){
    // Drop functions if they already exist
    int dropped = 0;
    for(String fn : dbFunctions) {
      try {
        s.execute("drop function " + fn);
        dropped++;
      } catch (SQLException e) {
        System.out.println("did not drop " + fn);
      }
    }
    if(dropped == dbFunctions.length) {
      System.out.println("Successfully dropped all functions.");
    }
  }

  /**
   * Drops all triggers in OrderManager
   */
  public void dropTriggers(String dbTriggers[]){
    //Drops triggers if they already exists.
    for(String tri : dbTriggers) {
      try {
        s.execute("drop trigger " + tri);
        System.out.println("dropped trigger " + tri);
      } catch (SQLException e) {
        System.out.println("did not drop trigger " + tri);
      }
    }
  }

  /**
   * Drops all procedures in OrderManager (currently unused, no procedures)
   */
  public void dropProcedures(String dbProcedures[]) {
    // Drops procedures if they already exist
    for(String proc : dbProcedures) {
      try {
        s.execute("drop procedure " + proc);
        System.out.println("dropped procedure " + proc);
      } catch (SQLException e) {
        System.out.println("did not drop procedure " + proc);
      }
    }
  }

}