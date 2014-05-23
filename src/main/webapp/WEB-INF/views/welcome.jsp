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
               <li class="active"><a href="<c:url value="/admin"/>"><i class="fa fa-home fa-lg"></i> Administration</a></li>
               <li><a href="<c:url value="/reports"/>"><i class="fa fa-bar-chart-o fa-lg"></i> Reports</a></li>        
             </ul>
           </div>
         </div>
       </nav>
        
       <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <div id="usersSyncPanel" class="panel panel-primary">
                           <c:if test="${usersJobInfo.jobStatus eq 'InProgress'}">
                                <c:set var="spinClass" value="fa-spin"></c:set>
                           </c:if>  
                           <div class="panel-heading"><i class="fa fa-gear fa-lg ${spinClass}"></i><strong> Users Sync Engine</strong></div>
                           <div class="panel-body">
                               <c:choose>
                                   <c:when test="${usersJobInfo.jobStatus eq 'Completed'}">
                                       <c:set var="badgeClass" value="badge-success"></c:set>
                                   </c:when>
                                   <c:when test="${usersJobInfo.jobStatus eq 'Paused'}">
                                       <c:set var="badgeClass" value="badge-error"></c:set>
                                   </c:when>
                                   <c:otherwise>
                                       <c:set var="badgeClass" value="badge-info"></c:set>
                                       <c:set var="activeClass" value="active"></c:set>
                                   </c:otherwise>
                               </c:choose>
                               <div id="usersProgressContainer" class="progress progress-striped ${activeClass}">
                                   <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="${usersJobPercent}" aria-valuemin="0" aria-valuemax="100" style="width:${usersJobPercent}%;">
                                       ${usersJobPercent}%
                                   </div>
                               </div>
                               <table class="table table-bordered">
                                   <tbody>
                                       <tr>
                                           <td>
                                               <span id="usersJobStatus" class="syncinfo badge pull-right ${badgeClass}">${usersJobInfo.jobStatus}</span>
                                               <i class="fa fa-flag fa-lg"></i><span class="syncinfoHeading">Status</span>
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
                                          <c:set var="startButtonDisplay" value=""></c:set>
                                          <c:set var="resumeButtonDisplay" value=""></c:set>
                                          <c:set var="stopButtonDisplay" value="display:none"></c:set>  
                                    </c:when>
                                    <c:when test="${usersJobInfo.jobStatus eq 'InProgress'}">
                                          <c:set var="startButtonDisplay" value="display:none"></c:set>
                                          <c:set var="resumeButtonDisplay" value="display:none"></c:set>
                                          <c:set var="stopButtonDisplay" value=""></c:set>  
                                    </c:when>
                                    <c:when test="${usersJobInfo.jobStatus eq 'Completed'}">
                                          <c:set var="startButtonDisplay" value=""></c:set>
                                          <c:set var="resumeButtonDisplay" value="display:none"></c:set>
                                          <c:set var="stopButtonDisplay" value="display:none"></c:set>  
                                    </c:when>
                                    <c:otherwise>
                                          <c:set var="startButtonDisplay" value=""></c:set>
                                          <c:set var="resumeButtonDisplay" value="display:none"></c:set>
                                          <c:set var="stopButtonDisplay" value="display:none"></c:set>
                                    </c:otherwise>
                               </c:choose>
                               <button id="usersResumeButton" style="${resumeButtonDisplay}" class="btn btn-primary" onclick="UsersSyncHelper.resumeSync()";><i class="fa fa-play-circle-o fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading">Resume Last Sync</span></button>   
                               <button id="usersStartButton" style="${startButtonDisplay}" class="btn btn-primary" onclick="UsersSyncHelper.startSync()";><i class="fa fa-play fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading">Start Fresh Sync</span></button>
                               <button id="usersStopButton" style="${stopButtonDisplay}" class="btn btn-danger" onclick="UsersSyncHelper.stopSync()";><i class="fa fa-stop fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading"> Stop Sync</span></button>
                           </div>
                       </div>
                   </div>  
                   <div class="col-md-6">
                    <div id="submissionsSyncPanel" class="panel panel-primary">
                       <c:if test="${submissionsJobInfo.jobStatus eq 'InProgress'}">
                            <c:set var="spinClass" value="fa-spin"></c:set>
                       </c:if>  
                       <div class="panel-heading"><i class="fa fa-gear fa-lg ${spinClass}"></i><strong> Submissions Sync Engine</strong></div>
                       <div class="panel-body">
                           <c:choose>
                           <c:when test="${submissionsJobInfo.jobStatus eq 'Completed'}">
                               <c:set var="badgeClass" value="badge-success"></c:set>
                           </c:when>
                           <c:when test="${submissionsJobInfo.jobStatus eq 'Paused'}">
                               <c:set var="badgeClass" value="badge-error"></c:set>
                           </c:when>
                           <c:otherwise>
                               <c:set var="badgeClass" value="badge-info"></c:set>
                               <c:set var="activeClass" value="active"></c:set>
                           </c:otherwise>
                           </c:choose>
                           <div id="submissionsProgressContainer" class="progress progress-striped ${activeClass}">
                           <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="${submissionsJobPercent}" aria-valuemin="0" aria-valuemax="100" style="width:${submissionsJobPercent}%;">
                               ${submissionsJobPercent}%
                           </div>
                           </div>
                           <table class="table table-bordered">
                           <tbody>
                               <tr>
                               <td>
                                   <span id="submissionsJobStatus" class="syncinfo badge pull-right ${badgeClass}">${submissionsJobInfo.jobStatus}</span>
                                   <i class="fa fa-flag fa-lg"></i><span class="syncinfoHeading">Status</span>
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
                              <c:set var="startButtonDisplay" value=""></c:set>
                              <c:set var="resumeButtonDisplay" value=""></c:set>
                              <c:set var="stopButtonDisplay" value="display:none"></c:set>  
                            </c:when>
                            <c:when test="${submissionsJobInfo.jobStatus eq 'InProgress'}">
                              <c:set var="startButtonDisplay" value="display:none"></c:set>
                              <c:set var="resumeButtonDisplay" value="display:none"></c:set>
                              <c:set var="stopButtonDisplay" value=""></c:set>  
                            </c:when>
                            <c:when test="${submissionsJobInfo.jobStatus eq 'Completed'}">
                              <c:set var="startButtonDisplay" value=""></c:set>
                              <c:set var="resumeButtonDisplay" value="display:none"></c:set>
                              <c:set var="stopButtonDisplay" value="display:none"></c:set>  
                            </c:when>
                            <c:otherwise>
                              <c:set var="startButtonDisplay" value=""></c:set>
                              <c:set var="resumeButtonDisplay" value="display:none"></c:set>
                              <c:set var="stopButtonDisplay" value="display:none"></c:set>
                            </c:otherwise>
                           </c:choose>
                           <button id="submissionsResumeButton" style="${resumeButtonDisplay}" class="btn btn-primary" onclick="SubmissionsSyncHelper.resumeSync()";><i class="fa fa-play-circle-o fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading">Resume Last Sync</span></button>   
                           <button id="submissionsStartButton" style="${startButtonDisplay}" class="btn btn-primary" onclick="SubmissionsSyncHelper.startSync()";><i class="fa fa-play fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading">Start Fresh Sync</span></button>
                           <button id="submissionsStopButton" style="${stopButtonDisplay}" class="btn btn-danger" onclick="SubmissionsSyncHelper.stopSync()";><i class="fa fa-stop fa-lg"></i><i style="display:none" class="fa fa-spin fa-spinner fa-lg"></i><span class="syncinfoHeading"> Stop Sync</span></button>
                       </div>
                    </div>
                </div>  
            </div>
        </div>
        <script>
            $(document).ready(function(){
                <c:if test="${usersJobInfo.jobStatus eq 'InProgress'}">
                    UsersSyncHelper.triggerFetchingSyncStatus();
                    $("#usersSyncPanel .panel-heading i").addClass("fa-spin");
               </c:if> 
               <c:if test="${submissionsJobInfo.jobStatus eq 'InProgress'}">
                    SubmissionsSyncHelper.triggerFetchingSyncStatus();
               </c:if>    
            });
        </script> 
    </body>
</html>
