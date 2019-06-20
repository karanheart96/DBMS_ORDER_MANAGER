import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class OrderManager_Connection {

    /* Framework and protocol information */
    private static String framework = "embedded";
    private static String protocol = "jdbc:derby:";

    /* Database connection information */
    private Connection conn = null;
    private Properties props;
    private Statement s = null;

    /**
     * Establishes connection with the OrderManager database
     * @param user1 the user1 information
     * @param password the password
     */
    public void startConnection(String user1, String password, String dbName){
        // establish properties
        props = new Properties();
        props.put("user", user1);
        props.put("password", password);

        // attempt to establish connection
        try {
            conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            s = conn.createStatement();
            System.out.printf("Established connection to database: %s\n", dbName);
        } catch (SQLException e) {
            System.err.printf("Unable to establish connection to database: %s\n", dbName);
            printSQLException(e);
        }

    }

    /**
     * Closes connection with the OrderManager database
     */
    public void closeConnection(String dbName){
        try {
            if(conn != null) {
                conn.close();
                conn = null;
                System.out.printf("\nConnection to %s closed successfully\n", dbName);
            }
        } catch (SQLException e) {
            System.err.printf("Was unable to close %s\n", dbName);
            printSQLException(e);
        }
    }

    /**
     * Gets the connection to the OrderManager database
     * @return the connection to OrderManager
     */
    public Connection getConnection(){
        return conn;
    }

    /**
     * Gets the statement already connected to OrderManager database
     * @return the statement
     */
    public Statement getStatement(){
        return s;
    }

    // TODO: Could just print our own error messages when exceptions are thrown
    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     * from: https://svn.apache.org/repos/asf/db/derby/code/trunk/java/demo/simple/SimpleApp.java
     *
     * @param e the SQLException from which to print details.
     */
    public void printSQLException(SQLException e)
    {
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }
}
