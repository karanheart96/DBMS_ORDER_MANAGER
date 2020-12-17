

# OrderManager Database
Authors: Heartlin Karan Machado 

## Overview
OrderManager is written in Java 1.8 and is a relational database management system using Apache Derby. The database contains information about customers, products, inventory records, orders, and order records. The code here provides a foundation for a back-end for an application for managing products, product inventory, and customer orders for an online store. It keeps tracks of inventory for each product and orders placed by customers.  

## Usage
OrderManager class contains initialization of the database by creating instances of the other classes to handle connecting to the database, DDL, DML, and DQL. An instance of the API class is also created in OrderManager, enabling function calls to API methods. Currently, OrderManager is meant to test functionality of these other classes. 

## Classes

### OrderManager
Initializes the database and functions from other classes should be called in main. 
Holds information about the database as private variables accessible to itself, with crucial 
ones passed as parameters to other functions (most notably: Statement s and Connection conn).

### OrderManager_Connection
Establishes connection to the database and handles closing it. Should not need any changes or additions.

### OrderManager_DDL
Has functions to create all tables, triggers, and functions (and procedures, but we do not have any right now).
Also has functions to drop tables, triggers, and functions (and procedures, if we add any).
This is where we should edit if we want to change how tables are created, modify domain constraints, etc.

### OrderManager_DML
Handles insert, alter, and delete. Has functions to insert for customer, product, and inventory_record. 
Will also truncate all tables to clear them of data.

### OrderManager_DQL
Contains functions that select for certain rows. Functions could use more work, particularly in modularizing them so that they don't just print to standard out (returning results in some way would be useful, especially if API could call on these functions directly). Useful functions that maybe should be in API are the selectAllAvailableProducts() and selectAllCustomerOrders() which display product names and inventory information and all orders of a specified customer, respectively. 

### OrderManager_API
Contains functions that should be primarily called upon by a user to interact with the database. Current functionality includes creating a customer, creating an order and order record for a specified customer, creating a product, and getting the total cost of a specific order.


### DBMS_storedfunc
Contains stored functions used by the OrderManager_DDL class when creating constraints. Does not need any more additions or changes.

## Docs
Directory containing documentation information, including the UML diagram for the database.

## Data
Directory containing files used for data insertion when initializing the database, as well as text files containing valid US states and valid countries for constraint checks in the database.
