# User-Application 

Application contains two Spring Boot Microservices : Consumer-interface-service and Consumer-data-service. Consumer-interface-service acts as a interface for all users. Request are taken at consumer-interace-service and then sent over to other consumer-data-service for saving, updating, fetching of user specific data. 
Communication between microservice is done through Message Driven concept where a queue is mainatined and the services read data from the queue. (NO http or HTTPS) 
Only for Read operations http mode is used.
A queue named user-data-queue is used which will be transferring the data. (ActiveMq JMS).

All data is encrypted and decrypted using AES algorithm for data transmission (save, update and read).

User can select two type for storing files either CSV or XML. The request files are stored at a particular location in your system.

Path for CSV : "D:\UserFiles\CSV\user_1.csv" &&  
Path for XML : "D:\UserFiles\XML\user_1.xml"

Directory will be automatically created based on fileType once the /store url is run based on your fileType param.
File will be saved as User_<userId>.csv/xml. 
Multiple user details can be passed from interface service, each user file will be created separately as Unserid is unique. And user details are saved to database with userId and fileUrl (FILE LOCATION) so that for read and update opertions the saved file can be fetched and we can perform the required operation.
  
Any updation for user details occurs at file level CSV or XML. ll changes are updated to file as per requirement. Multiple user data can be passed at interface service. All will be updated separately based on UserId.
FileType is required for both save and update.
Read operation is independent of filetype. If file present in system as CSV then it will read data from CSV and if present as XML then it will read data from XML and send back to consumer-interface-service.

Requirments to run service:
Download ActiveMq for queue data handle. (id: admin, password:admin) for login. Unzip and Go to bin folder then start activemq batch file to run the queue locally.

Database used:
h2 db
Table structure used for fetching file: 
Table name : User
Fields : userId(Integer) , fileUrl (String)
All the other details are present in csv or xml files.

Exception handling and Loggers are provided whereve necessary.
Junit unit test cases are provided for both the services. (Junit 4)
Attaching a text file for all 3 RestAPIs with request body for Reference.
