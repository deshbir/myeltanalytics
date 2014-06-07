Linux (Ubuntu) Installation Steps
==================================

###Install Java
1. sudo add-apt-repository ppa:webupd8team/java
2. sudo apt-get update
3. sudo apt-get install oracle-java7-installer        
4. sudo apt-get install oracle-java7-set-default    
5. java -version

###Install ElasticSearch
1. mkdir compro
2. cd compro
3. wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.2.1.deb  
4. sudo dpkg -i elasticsearch-1.2.1.deb
5. sudo update-rc.d elasticsearch defaults 95 10
6. sudo nano /etc/elasticsearch/elasticsearch.yml
7. Add "script.disable_dynamic: true" at the end of file and save.  
8. sudo service elasticsearch start  

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
1. wget http://apache.mirrors.lucidnetworks.net/tomcat/tomcat-7/v7.0.54/bin/apache-tomcat-7.0.54.tar.gz  
2. tar xvzf apache-tomcat-7.0.54.tar.gz
3. rm apache-tomcat-7.0.54.tar.gz
4. sudo mv apache-tomcat-7.0.54/ /usr/share/tomcat7
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
9. sudo nano /etc/init.d/tomcat7
10. Add following snippet and save file  
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
11. sudo chmod 755 /etc/init.d/tomcat7
12. sudo ln -s /etc/init.d/tomcat7 /etc/rc1.d/K99tomcat
13. sudo ln -s /etc/init.d/tomcat7 /etc/rc2.d/S99tomcat
14. sudo service tomcat7 start  

###Install NGINX
1. sudo apt-get install nginx
2. sudo nano /etc/nginx/sites-enabled/default
3. In the "server" section, disable following lines    
`root /usr/share/nginx/html;`   
`index index.html index.htm`   
4. In the "server" section, delete default mapping for "/" and add following mappings
`location /`   
`{`   
`		proxy_pass				http://localhost:8080;`   
`		proxy_set_header		X-Real-IP $remote_addr;`   
`		proxy_set_header		X-Forwarded-For $proxy_add_x_forwarded_for;`   
`		proxy_set_header		Host $http_host;`   
`		proxy_connect_timeout	90000;`   
`		proxy_send_timeout		90000;`   
`		proxy_read_timeout		96000;`   
`		client_max_body_size	10M;`   
`	}`   
	
`	location /search/`  
`	{`   
`		proxy_pass			http://localhost:9200/;`   
`		proxy_set_header	X-Real-IP $remote_addr;`   
`		proxy_set_header	X-Forwarded-For $proxy_add_x_forwarded_for;`   
`		proxy_set_header	Host $http_host;`   
`	}`  
5. Save the file   
6. sudo service nginx restart
7. Open "http://IP/myeltanalytics/search" in browser to navigate to ElasticSearch.

###Deploy Application
1. cd compro
2. git clone https://github.com/deshbir/myeltanalytics
3. cd myeltanalytics
4. sudo service tomcat7 stop
5. gradle deploy
6. sudo service tomcat7 start  
7. Open "http://localhost:8080/myeltanalytics/admin/" in browser to navigate to MyELTAnalytics Application.


