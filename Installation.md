Linux (Ubuntu) Installation Steps
==================================

A. Install Java
- sudo add-apt-repository ppa:webupd8team/java
- sudo apt-get update
- sudo apt-get install oracle-java7-installer        
- sudo nano /etc/environment         
- Add JAVA_HOME="/usr/lib/jvm/java-7-oracle" and save        
- source /etc/environment //To realod file        
- echo $JAVA_HOME  //To check JAVA_HOME        
- java -version // To check java version
