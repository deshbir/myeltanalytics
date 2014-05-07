<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
 <head>
 <style>
    table.jobInfo {
        border:1px solid black;
        border-collapse:collapse;
        width:100%;
    }
    table.jobInfo th, table.jobInfo td{
        border:1px solid black;   
        text-align:center;    
    }
 </style>
 <script type="text/javascript"> 
 function changeUrl(url){
     location = url;
 }
 </script>    
 </head>
<body>
    <div>
        <h1>MyELT Analytics Admin Tool</h1>
        <p>This tool synchronizes all the users and related information in MyELT with MyELT Analytics.</p> 
        <c:choose>
            <c:when test="${jobInfo.jobId != null}">
                 <h3>Job Status</h3>
                 <table class="jobInfo">
                     <tr>
                         <th>JobId</th>
                         <th>Status</th>
                         <th>Last Synced UserId</th>
                         <th>Total Records</th>
                         <th>Success Records</th>
                         <th>Failed Records</th>
                         <th>Actions</th>
                     </tr>
                     <tr>
                         <td>${jobInfo.jobId}</td>
                         <td>${jobInfo.jobStatus}</td>
                         <td>${jobInfo.lastId}</td>
                         <td>${jobInfo.totalRecords}</td>
                         <td>${jobInfo.successRecords}</td>
                         <td>${jobInfo.errorRecords}</td>
                         <td>
                            <c:choose>
                                 <c:when test="${jobInfo.jobStatus eq 'Completed'}">Job Completed</c:when>
                                 <c:when test="${jobInfo.jobStatus eq 'Paused'}">
                                     <button onclick="changeUrl('resumeSync')";>Resume Job</button>
                                 </c:when>
                                 <c:when test="${jobInfo.jobStatus eq 'InProgress'}">
                                     <button onclick="changeUrl('pauseSync')";>Pause Job</button>
                                     <button onclick="changeUrl('abortSync')";>Abort Job</button>
                                 </c:when>
                                 <c:when test="${jobInfo.jobStatus eq 'Aborted'}">Job Aborted</c:when>
                                 <c:otherwise>-</c:otherwise>
                            </c:choose>
                         </td>
                     </tr>
                 </table>
                  <c:if test="${jobInfo.jobStatus eq 'Aborted'}">
                       <p>Last job was aborted. Click "Start Sync" button to start a fresh sync job.</p>
                       <button onclick="changeUrl('startSync')";>Start Sync</button>
                 </c:if>
           </c:when>
           <c:otherwise>
                 <p>Click "Start Sync" button to start a new sync job.</p>
                 <button onclick="changeUrl('startSync')";>Start Sync</button>
           </c:otherwise>
        </c:choose>
    </div>
</body>

</html>