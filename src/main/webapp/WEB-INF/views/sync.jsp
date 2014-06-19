<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>MyELT Reporting App</title>
        
        <!--StyleSheets -->
        <link href="<c:url value="/styles/libs/bootstrap.min.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/libs/font-awesome.min.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/libs/odometer-theme-default.css"/>" rel="stylesheet">
        <link href="<c:url value="/styles/main.css"/>" rel="stylesheet">
        
        <!--JavaScript external libraries -->
        <script src="<c:url value="/scripts/libs/jquery-1.11.1.min.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/scripts/libs/bootstrap.min.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/scripts/libs/odometer.min.js"/>" type="text/javascript"></script>
        
        <!--JavaScript modules -->
        <script src="<c:url value="/scripts/Util.js"/>" type="text/javascript"></script>    
        <script src="<c:url value="/scripts/UsersSyncHelper.js"/>" type="text/javascript"></script>
        <script src="<c:url value="/scripts/SubmissionsSyncHelper.js"/>" type="text/javascript"></script>
        
    </head>
    <body>
        <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
         <div class="container-fluid">
           <div class="navbar-header">
             <a class="navbar-brand" href="#"> MyELT Reporting App</a>
           </div>
       
           <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
             <ul class="nav navbar-nav">
               <li><a href="app/index.html#/reports"><i class="fa fa-bar-chart-o fa-lg"></i> Reports</a></li>   
               <li class="active"><a href="<c:url value="/sync"/>"><i class="fa fa-refresh fa-lg"></i> Sync</a></li>
               <li><a href="app/index.html#/rules"><i class="fa fa-tasks fa-lg"></i> Rules</a></li>
               <li><a href="app/index.html#/settings"><i class="fa fa-gear fa-lg"></i> Settings</a></li>
             </ul>
           </div>
         </div>
       </nav>
        
       <div class="container">
            <div class="row" id="errorMessage"></div>
            <div class="row">
                <div class="col-md-6">
                    <div id="usersSyncPanel" class="panel panel-primary">
                           <c:if test="${usersJobInfo.jobStatus eq 'InProgress'}">
                                <c:set var="usersSpinClass" value="fa-spin"></c:set>
                           </c:if>  
                           <div class="panel-heading"><i class="fa fa-refresh fa-lg ${usersSpinClass}"></i><strong> Users (MySQL)</strong></div>
                           <div class="panel-body">
                               <c:choose>
                                   <c:when test="${usersJobInfo.jobStatus eq 'Completed'}">
                                       <c:set var="usersBadgeClass" value="badge-success"></c:set>
                                   </c:when>
                                   <c:when test="${usersJobInfo.jobStatus eq 'Paused'}">
                                       <c:set var="usersBadgeClass" value="badge-error"></c:set>
                                   </c:when>
                                   <c:otherwise>
                                       <c:set var="usersBadgeClass" value="badge-info"></c:set>
                                       <c:set var="usersActiveClass" value="active"></c:set>
                                   </c:otherwise>
                               </c:choose>
                               <div id="usersProgressContainer" class="progress progress-striped ${usersActiveClass}">
                                   <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="${usersJobPercent}" aria-valuemin="0" aria-valuemax="100" style="width:${usersJobPercent}%;">
                                       ${usersJobPercent}%
                                   </div>
                               </div>
                               <table class="table table-bordered">
                                   <tbody>
                                       <tr>
                                           <td>
                                               <span id="usersJobStatus" class="syncinfo badge pull-right ${usersBadgeClass}">${usersJobInfo.jobStatus}</span>
                                               <i class="fa fa-flag fa-lg"></i><span class="syncinfoHeading">Status</span>
                                           </td>
                                       </tr>
                                       <tr>
                                           <td>
                                               <span id="usersJobStartDateTime" class="syncinfo pull-right">${usersJobInfo.startDateTime}</span>
                                               <i class="fa fa-calendar fa-lg"></i><span class="syncinfoHeading">Started At</span>
                                           </td>
                                       </tr> 
                                       <tr>
                                           <td>
                                               <span id="usersTotalRecords" class="syncinfo odometer pull-right">${usersJobInfo.totalRecords}</span> 
                                               <i class="fa fa-road fa-lg"></i><span class="syncinfoHeading">Total records to Sync</span>
                                           </td>
                                       </tr>
                                       <tr>
                                           <td>
                                               <span id="usersSuccessRecords" class="syncinfo pull-right odometer text-success">${usersJobInfo.successRecords}</span>
                                               <i class="fa fa-check-circle fa-lg"></i><span class="syncinfoHeading">Records synced with MyELT Analytics</span>
                                           </td>
                                       </tr>
                                       <tr>
                                           <td>
                                               <span id="usersErrorRecords" class="syncinfo pull-right odometer text-danger">${usersJobInfo.errorRecords}</span>
                                               <i class="fa fa-warning fa-lg"></i><span class="syncinfoHeading">Failed records</span>
                                           </td>
                                       </tr>
                                   </tbody>
                               </table>
                               <c:choose>
                                    <c:when test="${usersJobInfo.jobStatus eq 'Paused'}">
                                          <c:set var="usersStartButtonDisplay" value=""></c:set>
                                          <c:set var="usersResumeButtonDisplay" value=""></c:set>
                                          <c:set var="usersStopButtonDisplay" value="display:none"></c:set>  
                                    </c:when>
                                    <c:when test="${usersJobInfo.jobStatus eq 'InProgress'}">
                                          <c:set var="usersStartButtonDisplay" value="display:none"></c:set>
                                          <c:set var="usersResumeButtonDisplay" value="display:none"></c:set>
                                          <c:set var="usersStopButtonDisplay" value=""></c:set>  
                                    </c:when>
                                    <c:when test="${usersJobInfo.jobStatus eq 'Completed'}">
                                          <c:set var="usersStartButtonDisplay" value=""></c:set>
                                          <c:set var="usersResumeButtonDisplay" value="display:none"></c:set>
                                          <c:set var="usersStopButtonDisplay" value="display:none"></c:set>  
                                    </c:when>
                                    <c:otherwise>
                                          <c:set var="usersStartButtonDisplay" value=""></c:set>
                                          <c:set var="usersResumeButtonDisplay" value="display:none"></c:set>
                                          <c:set var="usersStopButtonDisplay" value="display:none"></c:set>
                                    </c:otherwise>
                               </c:choose>
                               <button id="usersResumeButton" style="${usersResumeButtonDisplay}" class="btn btn-primary" onclick="UsersSyncHelper.resumeSync()";><i class="fa fa-play-circle-o fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading">Resume Last Sync</span></button>   
                               <button id="usersStartButton" style="${usersStartButtonDisplay}" class="btn btn-primary" onclick="UsersSyncHelper.startSync()";><i class="fa fa-play fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading">Start Fresh Sync</span></button>
                               <button id="usersStopButton" style="${usersStopButtonDisplay}" class="btn btn-danger" onclick="UsersSyncHelper.stopSync()";><i class="fa fa-stop fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading"> Stop Sync</span></button>
                           </div>
                       </div>
                   </div>  
                   <div class="col-md-6">
                    <div id="submissionsSyncPanel" class="panel panel-primary">
                       <c:if test="${submissionsJobInfo.jobStatus eq 'InProgress'}">
                            <c:set var="submissionsSpinClass" value="fa-spin"></c:set>
                       </c:if>  
                       <div class="panel-heading"><i class="fa fa-refresh fa-lg ${submissionsSpinClass}"></i><strong> Submissions (MySQL) </strong></div>
                       <div class="panel-body">
                           <c:choose>
                           <c:when test="${submissionsJobInfo.jobStatus eq 'Completed'}">
                               <c:set var="submissionsBadgeClass" value="badge-success"></c:set>
                           </c:when>
                           <c:when test="${submissionsJobInfo.jobStatus eq 'Paused'}">
                               <c:set var="submissionsBadgeClass" value="badge-error"></c:set>
                           </c:when>
                           <c:otherwise>
                               <c:set var="submissionsBadgeClass" value="badge-info"></c:set>
                               <c:set var="submissionsActiveClass" value="active"></c:set>
                           </c:otherwise>
                           </c:choose>
                           <div id="submissionsProgressContainer" class="progress progress-striped ${submissionsActiveClass}">
                           <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="${submissionsJobPercent}" aria-valuemin="0" aria-valuemax="100" style="width:${submissionsJobPercent}%;">
                               ${submissionsJobPercent}%
                           </div>
                           </div>
                           <table class="table table-bordered">
                           <tbody>
                               <tr>
                                <td>
                                    <span id="submissionsJobStatus" class="syncinfo badge pull-right ${submissionsBadgeClass}">${submissionsJobInfo.jobStatus}</span>
                                    <i class="fa fa-flag fa-lg"></i><span class="syncinfoHeading">Status</span>
                                </td>
                               </tr>
                               <tr>
                                   <td>
                                       <span id="submissionsJobStartDateTime" class="syncinfo pull-right">${submissionsJobInfo.startDateTime}</span>
                                       <i class="fa fa-calendar fa-lg"></i><span class="syncinfoHeading">Started At</span>
                                   </td>
                               </tr> 
                               <tr>
                               <td>
                                   <span id="submissionsTotalRecords" class="syncinfo odometer pull-right">${submissionsJobInfo.totalRecords}</span> 
                                   <i class="fa fa-road fa-lg"></i><span class="syncinfoHeading">Total records to Sync</span>
                               </td>
                               </tr>
                               <tr>
                               <td>
                                   <span id="submissionsSuccessRecords" class="syncinfo pull-right odometer text-success">${submissionsJobInfo.successRecords}</span>
                                   <i class="fa fa-check-circle fa-lg"></i><span class="syncinfoHeading">Records synced with MyELT Analytics</span>
                               </td>
                               </tr>
                               <tr>
                               <td>
                                   <span id="submissionsErrorRecords" class="syncinfo pull-right odometer text-danger">${submissionsJobInfo.errorRecords}</span>
                                   <i class="fa fa-warning fa-lg"></i><span class="syncinfoHeading">Failed records</span>
                               </td>
                               </tr>
                           </tbody>
                           </table>
                           <c:choose>
                            <c:when test="${submissionsJobInfo.jobStatus eq 'Paused'}">
                              <c:set var="submissionsStartButtonDisplay" value=""></c:set>
                              <c:set var="submissionsResumeButtonDisplay" value=""></c:set>
                              <c:set var="submissionsStopButtonDisplay" value="display:none"></c:set>  
                            </c:when>
                            <c:when test="${submissionsJobInfo.jobStatus eq 'InProgress'}">
                              <c:set var="submissionsStartButtonDisplay" value="display:none"></c:set>
                              <c:set var="submissionsResumeButtonDisplay" value="display:none"></c:set>
                              <c:set var="submissionsStopButtonDisplay" value=""></c:set>  
                            </c:when>
                            <c:when test="${submissionsJobInfo.jobStatus eq 'Completed'}">
                              <c:set var="submissionsStartButtonDisplay" value=""></c:set>
                              <c:set var="submissionsResumeButtonDisplay" value="display:none"></c:set>
                              <c:set var="submissionsStopButtonDisplay" value="display:none"></c:set>  
                            </c:when>
                            <c:otherwise>
                              <c:set var="submissionsStartButtonDisplay" value=""></c:set>
                              <c:set var="submissionsResumeButtonDisplay" value="display:none"></c:set>
                              <c:set var="submissionsStopButtonDisplay" value="display:none"></c:set>
                            </c:otherwise>
                           </c:choose>
                           <button id="submissionsResumeButton" style="${submissionsResumeButtonDisplay}" class="btn btn-primary" onclick="SubmissionsSyncHelper.resumeSync()";><i class="fa fa-play-circle-o fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading">Resume Last Sync</span></button>   
                           <button id="submissionsStartButton" style="${submissionsStartButtonDisplay}" class="btn btn-primary" onclick="SubmissionsSyncHelper.startSync()";><i class="fa fa-play fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading">Start Fresh Sync</span></button>
                           <button id="submissionsStopButton" style="${submissionsStopButtonDisplay}" class="btn btn-danger" onclick="SubmissionsSyncHelper.stopSync()";><i class="fa fa-stop fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading"> Stop Sync</span></button>
                       </div>
                    </div>
                </div>  
            </div>
            <div class="row">
                <div class="col-md-6">
                    <div id="gaSyncPanel" class="panel panel-primary">
                           <div class="panel-heading"><i class="fa fa-refresh fa-lg"></i><strong> Visits (Google Analytics)</strong></div>
                           <div class="panel-body">
                               <table class="table table-bordered">
                                   <tbody>
                                       <tr>
                                           <td>
                                               <span id="usersJobStatus" class="syncinfo badge pull-right"></span>
                                               <i class="fa fa-flag fa-lg"></i><span class="syncinfoHeading">Status</span>
                                           </td>
                                       </tr>
                                       <tr>
                                           <td>
                                               <span id="usersJobStartDateTime" class="syncinfo pull-right"></span>
                                               <i class="fa fa-calendar fa-lg"></i><span class="syncinfoHeading">Started At</span>
                                           </td>
                                       </tr> 
                                       <tr>
                                           <td>
                                               <span id="usersTotalRecords" class="syncinfo odometer pull-right"></span> 
                                               <i class="fa fa-road fa-lg"></i><span class="syncinfoHeading">Total records to Sync</span>
                                           </td>
                                       </tr>
                                       <tr>
                                           <td>
                                               <span id="usersSuccessRecords" class="syncinfo pull-right odometer text-success"></span>
                                               <i class="fa fa-check-circle fa-lg"></i><span class="syncinfoHeading">Records synced with MyELT Analytics</span>
                                           </td>
                                       </tr>
                                       <tr>
                                           <td>
                                               <span id="usersErrorRecords" class="syncinfo pull-right odometer text-danger"></span>
                                               <i class="fa fa-warning fa-lg"></i><span class="syncinfoHeading">Failed records</span>
                                           </td>
                                       </tr>
                                   </tbody>
                               </table>
                               <button id="gaStartButton" class="btn btn-primary" onclick="void(0);"><i class="fa fa-play fa-lg"></i><span class="syncinfoHeading">Start Fresh Sync</span></button>
                           </div>
                       </div>
                   </div>
            </div>
        </div>
        <script>
            $(document).ready(function(){
                <c:if test="${usersJobInfo.jobStatus eq 'InProgress'}">
                    UsersSyncHelper.triggerFetchingSyncStatus();
               </c:if> 
               <c:if test="${submissionsJobInfo.jobStatus eq 'InProgress'}">
                    SubmissionsSyncHelper.triggerFetchingSyncStatus();
               </c:if>    
            });
        </script> 
    </body>
</html>
