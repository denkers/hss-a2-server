<img src="preview/AppIcon.png" align="left" />

# SafeSMS Server

[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)

## Getting started

### Prerequisites
- JDK 1.8+
- NetBeans 8.1 + EE
- [crypto-commons](https://github.com/kyleruss/crypto-commons)
- Glassfish 4.1
- MySQL 5.7+
- MySQL Connector/J
- Hibernate 4

### Installation
- Clone the repository
```
git clone https://github.com/kyleruss/safesms-server.git
```

- Import the enterprise application into NetBeans  
there should be two components: `safesms-ejb` and `safesms-war`

- Create the SafeSMS database  
First connect to MySQL, create the database and select it
```
mysql -u root -p
CREATE DATABASE safesms;
use safesms;
```
- Run both table creation scripts in `database/scripts`

- Start your glassfish server and go to the admin console (`localhost:DASTPORT`)
- Create a JDBC connection pool with a `javax.sql.DataSource` resource type and  
driver class name `com.mysql.jdbc.jdbc2.optional.MysqlDataSource`
- Set your database username, password, port etc. in the additional properties
- Create a JDBC resource called `jdbc/mysql` using the connection pool that was created
- Deploy both the `ejb` and `war` components to the Glassfish server in NetBeans 



## License
SafeSMS Server is available under the MIT License  
See [LICENSE](LICENSE)
