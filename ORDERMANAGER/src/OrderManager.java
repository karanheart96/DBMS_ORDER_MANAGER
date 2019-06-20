import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 * OrderManager: A Database for Managing Products and Orders
 * @author Karan Machado
 * @author Kai Wu
 *
 * The RDBMS maintains information about products that can be ordered by costumers,
 * tracks inventory levels of each product, and handles orders for product by customers.
 * It enables a business to track sales of products, and allows customers to determine product availability and place orders.
 *
 */
public class OrderManager {
  // connection variables
  private static Connection conn = null;
  private static Statement s = null;

  /** Name of the database */
  private static String dbName = "OrderManager";

  /** Contains the names of the tables in OrderManager */
  private static String dbTables[]= {
    "order_record", "orders", "customer",
    "inventory_record", "product"
  };

  /** Contains names of stored functions in OrderManager */
  private static String dbFunctions[]={
    "isCountry", "isState", "isUnit",
    "isPrice", "isZipCode", "isSKU"
  };

  /* Contains names of triggers in OrderManager (unused)*/
  private static String dbTriggers[] = {
    "inventory_update", "inventory_update1"
  };

  /* Contains names of procedures in OrderManager (unused)*/
  private static String dbProcedures[] = {
    // none
  };

  /** Initializes the database and creates the tables */
  public static void main(String[] args)
  {
    // start connection
    OrderManager_Connection orderManager = new OrderManager_Connection();
    orderManager.startConnection("user1", "password", dbName);
    conn = orderManager.getConnection();
    s = orderManager.getStatement();


    OrderManager_DDL ddl = new OrderManager_DDL(s);
    OrderManager_DML dml = new OrderManager_DML(conn, s);
    OrderManager_DQL dql = new OrderManager_DQL(conn, s);
    OrderManager_API api = new OrderManager_API(dml, dql);



    System.out.println("Initializing database...");
    System.out.println("\nDropping tables and functions and triggers:");
    ddl.dropTables(dbTables);
    ddl.dropFunctions(dbFunctions);
    // trigger drop not needed, because dropping tables drops the triggers as well
    // ddl.dropTriggers(s, dbTriggers);


    System.out.println("\nCreating functions:");
    ddl.createFunctions();


    System.out.println("\nCreating tables:");
    ddl.createTables();

    System.out.println("\nCreating triggers:");
    ddl.createTriggers();

    // truncate, then insert data into tables
    System.out.println("\nTruncating tables:");
    dml.truncateTables(dbTables);
    System.out.println("\nInserting values:");
    dml.insertAll();



    // start API and wait for inputs
    System.out.println("\nAPI tests (Valid):\n");

    // valid product
    api.createProduct(s,conn,"Shoes","TY-568762-0M","Footwear for people",5,2.33);

    // create product with no units and no price
    api.createProduct(s,conn,"iPhone6S","FV-347321-0X","Electronics");

    // valid customer
    api.createCustomer(s,conn,"machado","heartlin karan","2118 canoas garden",
            "san jose","CA","95125","United States");

    // valid order (expect 1 order to be created and 2 inserts into order record
    api.createOrder(s, conn, 3, new String[]{"AB-123456-0N", "AX-238965-1M"}, new int[]{2, 5});

    // valid order
    api.createOrder(s, conn, 4, new String[]{"AB-123456-0N"}, new int[]{1});



    System.out.println("\nAPI tests (Invalids):\n");

    //invalid no of units for a product
    api.createProduct(s,conn,"Cosmetics","XS-332278-0A","Beauty Products",-1,5.33);

    //invalid price for a product
    api.createProduct(s,conn,"Colgate","AZ-564432-0N","Dental care",3,-1);

    //repeated SKU for a product -> Primary Key violation
    api.createProduct(s,conn,"Axe","TY-568762-0M","Body care",3,3.44);

    //invalid Zipcode
    api.createCustomer(s,conn,"tim","chen","2355 claire ave",
      "san francisco","CA","-88888","Unites States");

    //invalid state
    api.createCustomer(s,conn,"lisa","rogers","7845 branham lane","san jose","QZ","95124","United States");

    //invalid country
    api.createCustomer(s,conn,"phil","gust","2378 cottle road","san jose","CA","95126","ambnhab");

    // invalid order (more units than available
    api.createOrder(s, conn, 3, new String[]{"AX-238965-1M"}, new int[]{999});

    // invalid order (sku does not exist)
    api.createOrder(s, conn, 3, new String[]{"fakeSKU"}, new int[]{1});

    // invalid order (invalid number of units bought)
    api.createOrder(s, conn, 4, new String[]{"AB-123456-0N"}, new int[]{-1});

    System.out.println("\nGetting an order total:");
    double total = api.getOrderTotal(s, conn, 5);
    System.out.printf("Order #5's total: $%.2f (Expected: $179.93)\n", total);

    // view data (for testing purposes)
//    System.out.println("\nViewing all customers\n");
//    dql.selectAllCustomer();
//    System.out.println("\nViewing all products\n");
//    dql.selectAllProduct();
//    System.out.println("\nViewing all inventory record\n");
//    dql.selectAllInventory();
//    System.out.println("\nViewing all orders\n");
//    dql.selectAllOrders();
//    System.out.println("\nViewing all order record\n");
//    dql.selectAllOrderRecord();

    System.out.println("\nCreating a partial order for customer 2:");
    api.createOrder(s, conn, 2, new String[]{"TY-568762-0M", "AB-123456-0N", "FV-347321-0X", "NB-456789-0N"}, new int[]{1, 1, 999, 1});

    System.out.println("\nViewing all orders under customer #2:\n");
    dql.selectCustomerOrders(2);

    System.out.println("\nViewing available products:\n");
    dql.selectAvailableProducts();

    // close connection
    orderManager.closeConnection(dbName);

  }

}