<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="<c:url value="/styles/libs/bootstrap.min.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/libs/font-awesome.min.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/main.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/libs/odometer-theme-default.css"/>" rel="stylesheet">
        <script src="<c:url value="/scripts/UsersSyncHelper.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/scripts/SubmissionsSyncHelper.js"/>" type="text/javascript"></script>
         <script src="<c:url value="/scripts/Util.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/scripts/libs/jquery-1.11.1.min.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/scripts/libs/odometer.min.js"/>" type="text/javascript"></script>    
    </head>
    <body>
        <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
         <div class="container-fluid">
           <!-- Brand and toggle get grouped for better mobile display -->
           <div class="navbar-header">
             <a class="navbar-brand" href="#"> MyELT Analytics</a>
           </div>
       
           <!-- Collect the nav links, forms, and other content for toggling -->
           <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
             <ul class="nav navbar-nav">
               <li class="active"><a href="<c:url value="/reports"/>"><i class="fa fa-bar-chart-o fa-lg"></i> Reports</a></li>   
               <li><a href="<c:url value="/admin"/>"><i class="fa fa-gear fa-lg"></i> Administration</a></li>
             </ul>
           </div>
         </div>
       </nav>
       <div class="container">           
           <div class="panel panel-primary">
             <div class="panel-heading"><strong>Unique Users / Logins / Registrations</strong></div>
             <div class="panel-body">
                <div class="row">
                    <div class="col-md-6">
                        <h4>Dashboards</h4>
                        <ul class="reports-list">
                            <li><a onclick="Util.openReport('index.html#/dashboard/file/users_all.json')" href="#">All Users</a></li>
                            <li><a onclick="Util.openReport('index.html#/dashboard/file/users_capes.json')" href="#">CAPES</a></li>
                            <li><a href="#">ELTeach</a></li>
                            <li><a href="#">ICPNA</a></li>
                            <li><a href="#">eStudy</a></li>
                            <li><a href="#">Seven</a></li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h4>Reports</h4>
                        <ul class="reports-list">
                            <li><a href="#">Registrations FY11</a></li>
                            <li><a href="#">Registrations FY12</a></li>
                            <li><a href="#">Registrations FY12</a></li>
                            <li><a href="#">Registrations FY14(Current)</a></li>
                            <li><a href="#">Users by product(year, last month or as specified)</a></li>
                            <li><a href="#">Users by country/region(year, last month or as specified)</a></li>
                            <li><a href="#">Self-paced vs instructor led(year, last month or as specified)</a></li>
                            <li><a href="#">New Users(year, last month or as specified)</a></li>
                        </ul>
                    </div>
                </div>
             </div>  
           </div>
           <div class="panel panel-primary">
             <div class="panel-heading"><strong>Activated Access Codes</strong></div>
             <div class="panel-body">
                <div class="row">
                    <div class="col-md-6">
                        <h4>Dashboards</h4>
                        <ul class="reports-list"> 
                            <li><a href="#">All Users</a></li>
                            <li><a href="#">CAPES</a></li>                           
                            <li><a href="#">ELTeach</a></li>
                            <li><a href="#">ICPNA</a></li>
                            <li><a href="#">eStudy</a></li>
                            <li><a href="#">Seven</a></li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h4>Reports</h4>
                        <ul class="reports-list">
                            <li><a href="#">AACR FY11</a></li>
                            <li><a href="#">AACR FY12</a></li>
                            <li><a href="#">AACR FY12</a></li>
                            <li><a href="#">AACR FY14(Current)</a></li>
                            <li><a href="#">Access codes activated by product</a></li>
                            <li><a href="#">Access codes activated by time period</a></li>
                        </ul>
                    </div>
                </div>
             </div>  
           </div> 
           <div class="panel panel-primary">
             <div class="panel-heading"><strong>CAPES</strong></div>
             <div class="panel-body">
                <div class="row">
                    <div class="col-md-6">
                        <h4>Dashboards</h4>
                        <ul class="reports-list"> 
                            <li><a href="#">CAPES(Brazil)</a></li>
                            <li><a href="#">All CAPES Model(OCC etc.)</a></li>                           
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h4>Reports</h4>
                        <ul class="reports-list">
                            <li><a href="#"># Users changed levels</a></li>
                            <li><a href="#"># Users had not started level after placement test</a></li>
                            <li><a href="#">Average time spent per level</a></li>
                            <li><a href="#"># Users without access for more than 30 days</a></li>
                        </ul>
                    </div>
                </div>
             </div>  
           </div> 
           <div class="panel panel-primary">
             <div class="panel-heading"><strong>Completed Activities</strong></div>
             <div class="panel-body">
                <div class="row">
                    <div class="col-md-6">
                        <h4>Dashboards</h4>
                        <ul class="reports-list"> 
                            <li><a href="#">All Activities and Assignments</a></li>
                            <li><a href="#">Assignments only</a></li>
                            <li><a href="#">Examview</a></li>                           
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h4>Reports</h4>
                        <ul class="reports-list">
                            <li><a href="#">Submissions FY11</a></li>
                            <li><a href="#">Submissions FY12</a></li>
                            <li><a href="#">Submissions FY12</a></li>
                            <li><a href="#">Submissions FY14(Current)</a></li>
                            <li><a href="#">Completed activities(grades posted)(last month or as specified)</a></li>
                            <li><a href="#">Grades posted VS activity(last month or as specified)</a></li>
                        </ul>
                    </div>
                </div>
             </div>  
           </div>             
       </div>                   
    </body>
</html>
