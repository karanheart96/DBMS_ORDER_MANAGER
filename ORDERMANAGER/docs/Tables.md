---

title: "OrderManager Tables"

---

# OrderManager Tables

## product
### Description
The product table represents a product in the database. A product has a vendor product SKU that uniquely identifies it, as well as a name and a description. Has a 1-to-1 relationship with inventory_record.

| Column ID | Column | Data Type | Constraints | Description |
|-----------|---------------------|---------------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | product_name | VARCHAR(32) | Not null | Represents the product's name as a string of up to 32 characters. |
| 2 | product_description | VARCHAR(1000) | Not null | Contains the description of the product. Holds up to 1000 characters. |
| 3 | **product_SKU** | VARCHAR(12) | Not null | Primary key for the product table. Contains the vendor product SKU that identifies the product. The SKU is a 12-character value of the form "AA-NNNNNN-CC", where A is an upper-case letter, N is a digit from 0-9, and C is either a digit or an upper case letter. Example of a valid SKU: "AB-123456-0C". Validity of the SKU is checked by the stored function isSKU() defined by DBMS_storedfunc.java. |

## inventory_record
### Description
The inventory_record table represents available inventory in the store. For each product, the inventory_record keeps track of the price and number of available units to order. Has a 1-to-1 relationship with product. This table is checked when an order is placed to make sure the number of units ordered is valid, and to get the price for that product.

| Column ID | Column | Data Type | Constraints | Description |
|-----------|----------------|--------------|----------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | **product_SKU** | VARCHAR(12) | Not null | Primary key for inventory_record. Also a foreign key that references product_SKU in the product table. When product_SKU is deleted from product_SKU, rows with matching product_SKU in inventory_record will also be deleted. The product_SKU uniquely identifies the product and is a 12-character value of the form "AA-NNNNNN-CC", where A is an upper-case letter, N is a digit from 0-9, and C is either a digit or an upper case letter. Example of a valid SKU: "AB-123456-0C". |
| 2 | no_of_units | INTEGER | Not null, must be greater than or equal to 0 | Represents the number of available units for the product in the inventory. The constraint is checked by the stored function isUnit() which is defined by DBMS_storedfunc.java.  |
| 3 | price_per_unit | DECIMAL(7,2) | Not null, must be greater than or equal to 0 | Represents the price (in USD) per unit of the product. The constraint is checked by the stored function isPrice() which is defined by DBMS_storedfunc.java. |
## customer
### Description
The customer table represents the customers, each uniquely indentified by an automatically generated customer id. The customer table contains the customer's name and address, and does not contain any payment information. Has a 1-to-many relationship with the orders table.

| Column ID | Column | Data Type | Constraints | Description |
|-----------|------------|-------------|--------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | **CUST_ID** | INTEGER | Not null | Primary key for customer. Automatically generated starting at 1, incrementing by 1. Uniquely identifies a customer. |
| 2 | GivenName | VARCHAR(32) | Not null | Represents the customer's given name, holding up to 32 characters. |
| 3 | FamilyName | VARCHAR(32) | Not null | Represents the customer's family name, holding up to 32 characters. |
| 4 | Address | VARCHAR(64) | Not null | Contains the street address for the customer, holding up to 64 characters. |
| 5 | City | VARCHAR(64) | Not null | Contains the city of the address for the customer, holding up to 64 characters. |
| 6 | State | VARCHAR(4) | Not null, if country is US, checks if valid US state | Contains the abbreviated form of the state of the customer, holding up to 4 characters. If the state lies within the US, it is checked by the stored function isState() defined by DBMS_storedfunc.java to ensure it is a valid US state. |
| 7 | ZipCode | VARCHAR(16) | Not null, if country is US, checks if valid US zipcode | Contains the zip code for the address of the customer as a string. Can contain 5 digits or the complete 9 digit zip code. If the country is the US, it is checked by the stored function isZipCode() defined by DBMS_storedfunc.java to ensure it is a valid US zip code.  |
| 8 | Country | VARCHAR(32) | Not null, checks if valid country | Contains the country for the address of the customer, holding up to 32 characters. Checks if the country is a valid country by using the stored function isCountry() which is defined by DBMS_storedfunc.java. |
## orders
### Description
The orders table represents an order that is placed. Each order has an automatically generated order id, and an customer id that references the customer who placed that order. The table also contains the date of the order and date of shipment if the order has been shipped. The orders table does not contain information about what products are ordered, which is kept track of by the order_record table. It has a many-to-1 relationship with customer, and a 1-to-many relationship with order_record. 

| Column ID | Column | Data Type | Constraints | Description |
|-----------|---------------|-----------|-------------|------------------------------------------------------------------------------------------------------------------------------|
| 1 | **order_id** | INTEGER | Not null | Primary key for the order table. Automatically generated, starting at 1 and incrementing by 1. Uniquely identifies an order. |
| 2 | order_date | DATE | Not null | Holds the date the order was created. |
| 3 | shipment_date | DATE | None | Holds the date the order was shipped. If not yet shipped, the field is null. |
| 4 | CUSTOMER_ID | INTEGER | Not null | Foreign key that references CUST_ID from the customer table. Represents which customer placed the order. If a customer is deleted from the customer table, all orders corresponding to that customer are deleted as well.
## order_record
### Description
The order_record table contains information about an order, where each row of the table represents a product that has been ordered. The order_id column specifies what order, and the product_SKU column specifies what product was ordered. The order_record table also provides information on the number of units ordered and the price per unit at the time of order. It has a many-to-1 relationship with orders table.

| Column ID | Column | Data Type | Constraints | Description |
|-----------|----------------|--------------|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | order_id | INTEGER | Not null | Foreign key that references the order_id from the orders table. When an order is deleted from orders table, the corresponding order records are deleted from this table using order_id. |
| 2 | no_of_units | INTEGER | Not null, greater than or equal to 0 | Represents the ordered number of units for the product. Constraint is checked by the stored function isUnit() defined by DBMS_storedfunc.java. |
| 3 | price_per_unit | DECIMAL(7,2) | Not null, greater than or equal to 0 | Represents the price per unit of the ordered product. Constraint is checked by the stored function isPrice() defined by DBMS_storedfunc.java. |
| 4 | product_SKU | VARCHAR(12) | Not null | Foreign key that references the product_SKU from the product table. The no_of_units and price_per_unit refer to the product specified by this product_SKU. |
