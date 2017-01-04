# forklift-batch-csv
Proof of concept for batch processing multiple source input files in file system of the spring application and posting them to a database (FILE to JDBC) using Spring Batch. This project is based on an adapted clone from the spring batch getting started guide and source code. I reconfigured their source code to meet my own use case requirements for Forklift.

Forklift will become an automated data pump for VExSS. I initially did manual tests using Kibana to enable me to index a custom JSON data model in Elasticsearch. 

This project takes a simplified CSV data structure from the original VExSS JSON data model and only uses the critical value fields. The next project will focus on the larger JSON data model.

I want to automate insertion of file data (from N files) into a database to give me ETL options on that data and HTTP POST options if I want to extract and post to Elasticsearch via the Elasticsearch Bulk API instead of using Kibana.

## OBJECTIVE
- Create a data pump PoC based on simplified version of original VExSS JSON data model. Use CSV files
- Integrate flat files (1 or Many) into a database (MariaDB or MySQL)
- Experiment with Spring Batch
- Test using single input csv file
- Test using multiple input csv files

## NOTES
- I have added multiple additional fields to the main object being stored versus original guide
- I use MariaDB instead of the in-memory default database
- I use a Spring profile via Application.properties
- I use multiple csv input files rather than a single csv file
- I have modified the class BatchConfiguration.java to close the readers. When using the default gs code, I started getting repeated "too many open files" errors and the application while working initially stopped running. I have explicitly closed the readers once they are finished processing per Spring Batch documentation guidelines. The "too many open files" has  not re-occurred since the change

### MAVEN BUILD
```
mvn clean install spring-boot:run -e
```

### DATABASE SETUP 
```
mysql -u root -p 
> CREATE USER 'vexss-admin'@'localhost' IDENTIFIED BY 'password';
> GRANT ALL PRIVILEGES ON *.* TO 'vexss-admin'@'localhost';
> FLUSH PRIVILEGES;
```

### REFERENCE LINKS
- [Spring Batch Getting Started Guide](https://spring.io/guides/gs/batch-processing/)
- [Spring Batch Readers and Writers Documentation](http://docs.spring.io/springbatch/trunk/reference/html/readersAndWriters.html#multiFileInput)
- [Stack Overflow Reading Multiple Files](http://stackoverflow.com/questions/8487041/sequentially-processing-multiple-files-in-spring-batch)
- [Stack Overflow MultiResourceItemReader](http://stackoverflow.com/questions/31700520/how-to-read-all-files-in-a-folder-with-spring-batch-and-multiresourceitemreader)
- [Stack Overflow MultiResourceItemReaderFlatFileItemReader](http://stackoverflow.com/questions/11758672/spring-batch-flatfileitemreader-read-multiple-files)
- [Dave Syer Blog from 2010 on Practical Use of Spring Batch and Spring Integration](http://spring.io/blog/2010/02/15/practical-use-of-spring-batch-and-spring-integration/)
- [mykong Spring Batch MultiResourceItemReader Example (uses XML configuration](https://www.mkyong.com/spring-batch/spring-batch-multiresourceitemreader-example/)
- [Elasticsearch Bulk API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html)
