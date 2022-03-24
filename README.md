# NoSQL-database
Final project of Atypon training program 2022

# [Master project](https://github.com/ahmaddrabkah/NoSQL-database/tree/master/Master):
Is the main point of the whole project it control all 
the write processes done and connect client to the node. Also it is 
responsible for creat Nodes as replicates.

# [Node project](https://github.com/ahmaddrabkah/NoSQL-database/tree/master/Node):
Represent the backend server, its responsibilities 
is to handle the client request read and write operations, the read 
operation is done locally from the cache but if the operation is 
write, the node collect user information necessary for this 
operation (like in create schema it need schema name, types 
names and keys set ..Etc) and then send it to the master controller.

# [Client project](https://github.com/ahmaddrabkah/NoSQL-database/tree/master/Client):
Is a command line application, it is interacte with user operation by read data from the 
user and show him the result of his requested operation.

# [BookStore project](https://github.com/ahmaddrabkah/NoSQL-database/tree/master/BookStore):
Is a web application built using Spring Boot based in the database implemented above.

# You can read [Final_Report.pdf](https://github.com/ahmaddrabkah/NoSQL-database/tree/master/Final_Report.pdf) for the whole implementation details and technologies used.
