##Setup development environment for MyELTAnalytics##

>###Pre-Requisties###

1. Download Latest version of Gradle (‘http://www.gradle.org/downloads’) and extract it to ‘C:\gradle-2.1.1’. Add  “C:\gradle-2.1.1\bin” to PATH environment variable.
2. Download and install latest version of SmartGit (‘http://www.syntevo.com/smartgit/download’).
3. Download latest version of Elastic Search (‘http://www.elasticsearch.org/download/’) and extract it to ‘C:\elasticsearch-1.3.4’. Open command prompt and run following commands:
  1. cd C:\ elasticsearch-1.3.4\bin
  2.	service.bat install.
  3.	Go to service manager and start newly added Elasticsearch service.

>###Clone MyELTAnalytics repository###

1. Open SmartGit and from menu bar choose ‘Repository -> clone’
2. Enter Remote Repository Url (‘https://github.com/deshbir/myeltanalytics’’) and then select type of repository as Git.
3. Enter username and password of github account.
4. Choose Folder for cloning (e.g. “F:/myeltanalytics”) and Click Finish.

>###Custom configuration for Project###

1. Open file ‘config.js ’ change ‘elasticsearch: "http://"+window.location.hostname + "/search"’ to ‘elasticsearch: "http://"+window.location.hostname + ":9200"’.
2. Open file ‘application.properties’ change 
  1. spring.datasource.url=jdbc:mysql://mira1/users-26-07-11?zeroDateTimeBehavior=convertToNull
  2. spring.datasource.username=root.
  3. spring.datasource.password=. 

>###Build Project###

1. Open command prompt and navigate to directory “F:/myeltanalytics”
2. Run ‘gradle build’ to build project.
3. Run ‘gradle eclipse’ to build dependencies for eclipse.
4. Run ‘gradle bootRun’ to start MyELTAnalytics application.
5. Open ‘http://localhost:8080/myeltanalytics/’.

