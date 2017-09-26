# socket-ftp-scala
To run this sample, you need to execute: <br /> 
- sbt run
- 1 in order to run MainClient.scala <br /> 
- 2 in order to run MainServer.scala <br />

Available commands: <br />

- DIR - show all directories inside server folder <br />
- PWD - prints working directory <br />
- PUT {full_path_to_file}- puts file to the server <br />
- GET {file_name_in_current_directory} - downloads file from server <br />
- CD {directory_name} - changes directory <br />

Client and Server codes contains some places where I didn't place try-catch blocks, so application may crash. :confused: <br />
Also, there is no JavaDoc for methods and classes. :pensive: <br />
Have a Good Luck :innocent:
