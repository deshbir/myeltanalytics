Linux (Ubuntu) Installation Steps
==================================

###Install Java
1. sudo add-apt-repository ppa:webupd8team/java
2. sudo apt-get update
3. sudo apt-get install oracle-java7-installer        
4. sudo nano /etc/environment
5. Add JAVA_HOME="/usr/lib/jvm/java-7-oracle" to this file and save file        
6. source /etc/environment
7. echo $JAVA_HOME
8. java -version

###Install ElasticSearch
1. mkdir compro
2. cd compro
3. wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.1.1.deb
4. sudo dpkg -i elasticsearch-1.1.1.deb
5. sudo update-rc.d elasticsearch defaults 95 10
6. sudo nano /etc/elasticsearch/elasticsearch.yml
7. Add "script.disable_dynamic: true" at the end of file and save.  
8. cd /usr/share/elasticsearch/bin
9. Run "sudo ./plugin -install royrusso/elasticsearch-HQ" to install ElasticSearch-HQ plugin (http://www.elastichq.org/)
10. sudo /etc/init.d/elasticsearch start  
11. sudo /etc/init.d/elasticsearch stop  

###Install Git 
1. sudo apt-get install git-core

###Install Gradle
1. cd compro
2. wget https://services.gradle.org/distributions/gradle-1.12-bin.zip
3. sudo apt-get install unzip
4. unzip gradle-1.12-bin
5. sudo nano /etc/environment 
6. Add "/home/ubuntu/compro/gradle-1.12/bin" to $PATH and save file
7. source /etc/environment
8. echo $PATH
9. gradle -version
10. sudo ln -sf /home/ubuntu/compro/gradle-1.12/bin/* /usr/bin/.
11. sudo gradle -version


###Install Tomcat
1. wget http://mirror.reverse.net/pub/apache/tomcat/tomcat-7/v7.0.53/bin/apache-tomcat-7.0.53.tar.gz
2. tar xvzf apache-tomcat-7.0.53.tar.gz
3. rm apache-tomcat-7.0.53.tar.gz
4. sudo mv apache-tomcat-7.0.53/ /usr/share/tomcat7
5. sudo nano /etc/environment
6. Add following enviornment variables and save file
  * CATALINA_HOME="/usr/share/tomcat7"
  * CATALINA_BASE="/usr/share/tomcat7"
  * JAVA_HOME="/usr/lib/jvm/java-7-oracle"
  * JRE_HOME="/usr/lib/jvm/java-7-oracle/jre"
  * PATH="...(other path):$JAVA_HOME:$JRE_HOME"`
7. sudo nano /usr/share/tomcat7/bin/catalina.sh
8. Add following entries and save file.
  * JAVA_HOME="/usr/lib/jvm/java-7-oracle"	   	   
  * JRE_HOME="/usr/lib/jvm/java-7-oracle/jre"
9. sudo nano /usr/share/tomcat7/conf/tomcat-users.xml	
10. Add following entry and save file  
`<role rolename="admin"/>`  
`<role rolename="manager"/>`  
`<role rolename="manager-gui"/>`  
`<user username="admin" password="admin" roles="admin,manager,manager-gui"/>`  
11. sudo nano /usr/share/tomcat7/conf/server.xml
12. Change connector port from 8080 to 80 and save file
13. sudo nano /etc/init.d/tomcat7
14. Add following snippet and save file  
`# Tomcat auto-start`  
`# description: Auto-starts tomcat`  
`# processname: tomcat`  
`# pidfile: /var/run/tomcat.pid`  
`case $1 in`  
`start)`  
`sh /usr/share/tomcat7/bin/startup.sh`  
`;;`  
`stop)`  
`sh /usr/share/tomcat7/bin/shutdown.sh`  
`;;`  
`restart)`  
`sh /usr/share/tomcat7/bin/shutdown.sh`  
`sh /usr/share/tomcat7/bin/startup.sh`  
`;;`  
`esac`  
`exit 0`  
15. sudo chmod 755 /etc/init.d/tomcat7
16. sudo ln -s /etc/init.d/tomcat7 /etc/rc1.d/K99tomcat
17. sudo ln -s /etc/init.d/tomcat7 /etc/rc2.d/S99tomcat
18. sudo /etc/init.d/tomcat7 start

###Run Application
1. cd compro
2. git clone https://github.com/deshbir/myeltanalytics
3. cd myeltanalytics
4. sudo /etc/init.d/tomcat7 stop
5. gradle deploy
6. sudo /etc/init.d/tomcat7 start
7. Open "http://localhost:8080/myeltanalytics/admin/" in browser


