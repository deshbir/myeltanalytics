<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="<c:url value="/styles/libs/bootstrap.min.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/libs/font-awesome.min.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/main.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/libs/jquery.fancybox.css"/>" rel="stylesheet">
        <script src="<c:url value="/scripts/libs/jquery-1.11.1.min.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/scripts/libs/jquery.fancybox.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/scripts/Util.js"/>" type="text/javascript"></script>
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
                <div class="col-md-4">
                     <div class="panel panel-primary reports-panel">
                        <div class="panel-heading"><strong><i class="fa fa-bar-chart-o fa-lg"></i> Registrations</strong></div>
                         <div class="panel-body">
                            <table class="table table-bordered">
                                <tr>
                                     <td>
                                         <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                         <span class="reportsHeading">Registrations FY11</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Registrations FY12</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Registrations FY13</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Registrations FY14(Current)</span>
                                     </td>
                                </tr>
                            </table>
                         </div>
                     </div>
                </div>
                <div class="col-md-4">
                     <div class="panel panel-primary reports-panel">
                        <div class="panel-heading"><strong><i class="fa fa-bar-chart-o fa-lg"></i> Unique users</strong></div>
                         <div class="panel-body">
                            <table class="table table-bordered">
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#" onClick="Util.openReport('index.html#/dashboard/file/users_all.json')"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">All users</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Users by product</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Users by country / region</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Self-paced vs instructor-led</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">New Users</span>
                                     </td>
                                </tr>
                            </table>
                         </div>
                     </div>
                </div>           
                <div class="col-md-4">
                     <div class="panel panel-primary reports-panel">
                        <div class="panel-heading"><strong><i class="fa fa-bar-chart-o fa-lg"></i> Users by Institutes / Customers</strong></div>
                         <div class="panel-body">
                            <table class="table table-bordered">
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#" onClick="Util.openReport('index.html#/dashboard/file/users_capes.json')"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">CAPES</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">ELTeach</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>                                        
                                        <span class="reportsHeading">ICPNA</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">eStudy</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Seven</span>
                                     </td>
                                </tr>
                            </table>
                         </div>
                     </div>
                </div>
            </div>
            <div class="row">  
                <div class="col-md-4">
                     <div class="panel panel-primary reports-panel">
                        <div class="panel-heading"><strong><i class="fa fa-bar-chart-o fa-lg"></i> Activated Access Codes</strong></div>
                         <div class="panel-body">
                            <table class="table table-bordered">
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">All Access codes</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">AACR FY11</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">AACR FY12</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">AACR FY13</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">AACR FY14 (Current)</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>   
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Access codes activated by product</span>
                                     </td>
                                </tr>                               
                            </table>
                         </div>
                     </div>
                </div>           
                <div class="col-md-4">
                     <div class="panel panel-primary reports-panel">
                        <div class="panel-heading"><strong><i class="fa fa-bar-chart-o fa-lg"></i> CAPES Reports</strong></div>
                         <div class="panel-body">
                            <table class="table table-bordered">
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">All CAPES Model(OCC etc.)</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">CAPES (Brazil)</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Users changed levels</span>                                                                              
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Users had not started level after placement test</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Average time spent per level</span>
                                     </td>
                                </tr>
                                 <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Users without access for more than 30 days</span>
                                     </td>
                                </tr>
                            </table>
                         </div>
                     </div>
                </div>
                <div class="col-md-4">
                     <div class="panel panel-primary reports-panel">
                        <div class="panel-heading"><strong><i class="fa fa-bar-chart-o fa-lg"></i> Completed Activities</strong></div>
                         <div class="panel-body">
                            <table class="table table-bordered">
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">All Activities and Assignments</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Assignments only</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Examview</span>
                                     </td>
                                </tr>
                            </table>
                         </div>
                     </div>
                </div>
            </div>   
            <div class="row">
                <div class="col-md-4">
                     <div class="panel panel-primary reports-panel">
                        <div class="panel-heading"><strong><i class="fa fa-bar-chart-o fa-lg"></i> Submissions</strong></div>
                         <div class="panel-body">
                            <table class="table table-bordered">
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Submissions FY11</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Submissions FY12</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Submissions FY13</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Submissions FY14 (Current)</span>
                                     </td>
                                </tr>
                                <tr>
                                     <td>
                                        <span class="pull-right"><a href="#"><i class="fa fa-lg fa-external-link"></i></a></span>
                                        <span class="reportsHeading">Grades posted VS activity</span>
                                     </td>
                                </tr>
                            </table>
                         </div>
                     </div>
                </div>
            </div>                                                
       </div>                   
    </body>
</html>
