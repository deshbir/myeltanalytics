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
            <div class="row">
                <h3 class="reports-category">Unique Users / Logins / Registrations</h3>
                <div class="col-md-6">
                   <span class="reports-heading">Dashboards</span>
                   <ul compact="1" class="reports-list">
                        <li class="reports-list-seperator">
                            <span class="preferred"><a onclick="Util.openReport('index.html#/dashboard/file/users_all.json')" href="#">All Users</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a onclick="Util.openReport('index.html#/dashboard/file/users_capes.json')" href="#">CAPES</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">ELTeach</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">ICPNA</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">eStudy</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Seven</a></span>
                        </li>
                   </ul>
               </div>
               <div class="col-md-6">    
                   <span class="reports-heading">Reports</span>
                   <ul compact="1" class="reports-list">
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Registrations FY11</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Registrations FY12</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Registrations FY13</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Registrations FY14(Current)</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Users by product(year, last month or as specified)</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Users by country/region(year, last month or as specified)</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Self-paced vs instructor led(year, last month or as specified)</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">New Users(year, last month or as specified)</a></span>
                        </li>
                   </ul>                   
                </div>
            </div> 
            <div class="row">
                <h3 class="reports-category">Activated Access Codes</h3>
                <div class="col-md-6">
                   <span class="reports-heading">Dashboards</span>
                   <ul compact="1" class="reports-list">
                        <li class="reports-list-seperator">
                            <span class="preferred"><a onclick="Util.openReport('index.html#/dashboard/file/accesscodes_all.json')"  href="#">All Users</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">CAPES</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">ELTeach</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">ICPNA</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">eStudy</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Seven</a></span>
                        </li>
                   </ul>
               </div>
               <div class="col-md-6">    
                   <span class="reports-heading">Reports</span>
                   <ul compact="1" class="reports-list">
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">AACR FY11</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">AACR FY12</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">AACR FY13</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">AACR FY14(Current)</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Access codes activated by product</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Access codes activated by time period</a></span>
                        </li>
                   </ul>                   
                </div>
            </div>  
            <div class="row">
                <h3 class="reports-category">CAPES</h3>
                <div class="col-md-6">
                   <span class="reports-heading">Dashboards</span>
                   <ul compact="1" class="reports-list">
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">CAPES(Brazil)</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">All CAPES Model(OCC etc.)</a></span>
                        </li>
                   </ul>
               </div>
               <div class="col-md-6">    
                   <span class="reports-heading">Reports</span>
                   <ul compact="1" class="reports-list">
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#"># Users changed levels</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#"># Users had not started level after placement test</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Average time spent per level</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#"># Users without access for more than 30 days</a></span>
                        </li>
                   </ul>                   
                </div>
            </div> 
              <div class="row">
                <h3 class="reports-category">Completed Activities</h3>
                <div class="col-md-6">
                   <span class="reports-heading">Dashboards</span>
                   <ul compact="1" class="reports-list">
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">All Activities and Assignments</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Assignments only</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Examview</a></span>
                        </li>
                   </ul>
               </div>
               <div class="col-md-6">    
                   <span class="reports-heading">Reports</span>
                   <ul compact="1" class="reports-list">
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Submissions FY11</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Submissions FY12</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Submissions FY13</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Submissions FY14(Current)</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Completed activities(grades posted)(last month or as specified)</a></span>
                        </li>
                        <li class="reports-list-seperator">
                            <span class="preferred"><a class="disabled" href="#">Grades posted VS activity(last month or as specified)</a></span>
                        </li>
                   </ul>                   
                </div>
            </div>                                           
       </div>
    </body>
</html>
