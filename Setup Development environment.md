##Setup development environment for MyELTAnalytics##

>###Pre-Requisties###

1. Download Latest version of Gradle (http://www.gradle.org/downloads) and extract it to "C:\gradle-2.1.1" (Assuming 2.1.1 is latest version). Add  "C:\gradle-2.1.1\bin" to PATH environment variable.
2. Download and install latest version of SmartGit (http://www.syntevo.com/smartgit/download).
3. Download latest version of ElasticSearch (http://www.elasticsearch.org/download/) and extract it to "C:\elasticsearch-1.3.4". Open command prompt and run following commands to install elasticsearch as windows service:
  1.  cd C:\ elasticsearch-1.3.4\bin
  2.	service.bat install
4.	Go to service manager and start newly added Elasticsearch service.

>###Clone MyELTAnalytics###

1. Open SmartGit and choose "Repository -> clone" from menu bar.
2. Enter "https://github.com/deshbir/myeltanalytics" as Remote Repository Url. Select type of repository as "Git".
3. Enter username and password of your github account.
4. Choose Folder for cloning (e.g. “F:/myeltanalytics”) and Click Finish.

>###Custom configuration###

1. Open file "src\main\webapp\reports\config.js" and change ‘elasticsearch: "http://"+window.location.hostname + "/search"’ to ‘elasticsearch: "http://"+window.location.hostname + ":9200"’.
2. Open file ‘application.properties’ and update MySQL credentials: 
  1. spring.datasource.url=jdbc:mysql://mira1/users-26-07-11?zeroDateTimeBehavior=convertToNull
  2. spring.datasource.username=root.
  3. spring.datasource.password=. 

>###Build Project###

1. Open command prompt and navigate to directory “F:/myeltanalytics”
2. Run "gradle build" to build project.
3. Run "gradle eclipse" to build dependencies for eclipse.
4. Run "gradle bootRun" to start MyELTAnalytics application.
5. Open "http://localhost:8080/myeltanalytics/" and verify if application is running.

