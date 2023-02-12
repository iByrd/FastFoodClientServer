# Fast Food Client and Server
Host must be set in IDE to establish connection. I used Eclipse to program this and set the sever's program attribute value to 8080 in the IDE and the clients to localhost 8080.

## Byrd's Nest: Famous Burger's and Shakes
This application simulates the need for a small restaurant to have a GUI based order taking system. User's are presented with a plesant and clear interface and can choose their desired quantities for menu items.

### Client 
![ByrdsNestGUI](https://user-images.githubusercontent.com/122894342/218334781-f1af4820-db90-444e-98b9-e39a3c100637.JPG)

#### Client Details
The GUI has an easy-to-understand layout and provides details for menu items as well as input fields for their selections. Client-side validation of input handles blank entries as well as entry of non-numeric values, ensuring the information sent to the server is valid. After the order is placed and sent to the server, the client listens to the response and displays the order number and total. 

### Server
Once the java server is started, it listens for connections from the client. The server ultilizes multi-thread to handle several connections simutanously. It also keeps track of the current order number and protects its modification with locks to ensure accuracy between thread and to prevent race coditions and deadlocks. 
