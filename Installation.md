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
8. cd /usr/share/elasticsearch/bin   
9. sudo ./plugin -install mobz/elasticsearch-head    
10. sudo ./plugin -i elasticsearch/marvel/latest    
11. cd /usr/share/elasticsearch/plugins/marvel/_site/kibana
12. sudo nano config.js   
13. Update value of "elasticsearch" variable to :    
	
	```
	window.location.protocol+"//"+window.location.hostname+(window.location.port !== '' ? ':'+window.location.port : '') + "/search"
	```
14. sudo service elasticsearch start  

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
  * PATH="...(other path):$JAVA_HOME:$JRE_HOME"
7. sudo nano /usr/share/tomcat7/bin/catalina.sh
8. Add following entries at the top and save file.
  * JAVA_HOME="/usr/lib/jvm/java-7-oracle"	   	   
  * JRE_HOME="/usr/lib/jvm/java-7-oracle/jre"
9. sudo nano /etc/init.d/tomcat7
10. Add following snippet and save file  

	```
	# Tomcat auto-start  
	# description: Auto-starts tomcat  
	# processname: tomcat  
	# pidfile: /var/run/tomcat.pid  
	case $1 in  
	start)  
	sh /usr/share/tomcat7/bin/startup.sh  
	;;  
	stop)  
	sh /usr/share/tomcat7/bin/shutdown.sh  
	;;  
	restart)  
	sh /usr/share/tomcat7/bin/shutdown.sh  
	sh /usr/share/tomcat7/bin/startup.sh  
	;;  
	esac  
	exit 0 
	```

11. sudo chmod 755 /etc/init.d/tomcat7  
12. sudo ln -s /etc/init.d/tomcat7 /etc/rc1.d/K99tomcat
13. sudo ln -s /etc/init.d/tomcat7 /etc/rc2.d/S99tomcat
14. sudo service tomcat7 start  

###Install NGINX
1. sudo apt-get install nginx
2. sudo apt-get install apache2-utils    
3. cd /home/ubuntu/compro   
4. mkdir nginx    
5. sudo htpasswd -c /home/ubuntu/compro/nginx/.htpasswd admin 
6. Enter password.
7. sudo nano /etc/nginx/nginx.conf    
8. In the end of "http" section, comment out following lines    
	```
        include /etc/nginx/conf.d/*.conf;

        include /etc/nginx/sites-enabled/*;
	```
9. In the end of "http" section, add following mappings  
	```
	server {
                listen       80;
	        server_name  localhost;   
	
	        	
	        location /myeltanalytics/
		{
			proxy_pass		http://localhost:8080/myeltanalytics/;
		        proxy_set_header	X-Real-IP $remote_addr;
			proxy_set_header	X-Forwarded-For $proxy_add_x_forwarded_for;
			proxy_set_header	Host $http_host;
			proxy_connect_timeout	90000;
			proxy_send_timeout	90000;
			proxy_read_timeout	96000;
			client_max_body_size	10M;
		}
		
		location /search/
		{
			proxy_pass		http://localhost:9200/;
			proxy_set_header	X-Real-IP $remote_addr;
			proxy_set_header	X-Forwarded-For $proxy_add_x_forwarded_for;    
			proxy_set_header	Host $http_host;    
                        auth_basic "Restricted";
                        auth_basic_user_file /home/ubuntu/compro/nginx/.htpasswd;
 		}
	}
	```
10. Save the file
11. sudo nano /usr/share/nginx/html/index.html
12. Replace the contents of file with following:  
	```
	<!DOCTYPE html>      
	<html>      
		<head>      
			<meta http-equiv="refresh" content="0; url=myeltanalytics" />       
		</head>       
		<body>        
			<h4>Redirecting to MyELT Reporting App...</h4>        
		</body>       
	</html>    
	```
13. sudo service nginx restart
14. Open "http://IP/search" in browser to navigate to ElasticSearch.

###Deploy Application
1. cd /home/ubuntu
2. sudo nano .netrc
3. Add "machine github.com login USERNAME password PASSWORD" snippet at the top of file (replace USERNAME and PASSWORD with actual values).
4. Save and close file.
5. sudo chmod 600 ~/.netrc
6. cd compro
7. sudo git clone https://github.com/deshbir/myeltanalytics
8. cd myeltanalytics
9. sudo service tomcat7 stop
10. sudo gradle deploy
11. sudo service tomcat7 start  
12. Open "http://IP/myeltanalytics/" and "http://IP/"in browser to navigate to MyELTAnalytics Application.


