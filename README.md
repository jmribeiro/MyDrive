# "MyDrive"

Java FileSystem with Shell access developed as a six-member SCRUM team.

### Dependencies:
1) Maven 

### To run:

  `$ mvn pt.tecnico.plugin:dbclean-maven-plugin:1.0-SNAPSHOT:dbclean clean package exec:java`

or to load filesystem from xml file: 
  
  `$ mvn pt.tecnico.plugin:dbclean-maven-plugin:1.0-SNAPSHOT:dbclean clean package exec:java -Dexec.args="drive.xml"`
