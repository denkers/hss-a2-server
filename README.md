<img src="preview/AppIcon.png" align="left" />

# SafeSMS Server

[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)

SafeSMS Server is the back-end component of the [SafeSMS Client](https://github.com/kyleruss/safesms-client)  
The server acts as a public-key authority, storing public keys for each client in the system  
The server manages a mobile client user base and allows users to register, connect, change settings, view other online users, send and receive encrypted SMS messages to each other


### How it works
- When a new client wishes to join SafeSMS, they first register an account  
- All requests to the server are encrypted with AES 128bit using the servers public RSA key  
- If the client successfully registers, the server generates a random one-time limited 
password (for PBE) and emails it to the client for verification  
- This password is used by the client to generate a an ephemeral key for the request where the client  
generates a public RSA key and encrypts it along with a nonce value (to ensure it was not altered in transaction)  
with AES using the ephemeral key 
- The encrypted public key & nonce are sent to the server where the sever then stores the clients public key  
and responds to the client with the status of the request and the same nonce value (so the that client can verify)  
all encrypted using with AES using the clients public key
- Thereafter, a client can view a list of online users and their phone numbers then request from the server the public  
key of another user then encrypt their SMS messages with AES + the public key of the recipient to securely message

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
