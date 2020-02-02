# tools
Useful executables and scripts


## SqlInsertIntoGrouper.java

Solves the problem when You have thousands of isert statment and execution is slow. The script batches them into bathes of 1+ insers. 

```sql
INSERT INTO table_name (column) VALUES (value_1),
INSERT INTO table_name (column) VALUES (value_2),

-- TO 

INSERT INTO table_name (column)
VALUES
    (value_1),
    (value_2),
```
