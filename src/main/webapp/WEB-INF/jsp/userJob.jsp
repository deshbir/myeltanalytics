<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
 <head>
 <script type="text/javascript">
 function changeUrl(url){
     location = url;
 }
 </script>
    
 </head>
<body>
    <div>
        <h3>Current Job Status</h3>
        <div>
                <span>Status:</span>&nbsp;
            <c:choose>
                   <c:when test="${isCompleted}">
                       <span>Completed</span>
                   </c:when>
                   <c:otherwise>
                       <span> In-Process </span>
                   </c:otherwise>
             </c:choose>
             <div>
                 <span>Job Id:</span>&nbsp; <span>${jobStatus.jobId} </span>
             </div>
             <div>
                 <span>Last Pushed User Id:</span>&nbsp; <span>${jobStatus.lastId} </span>
             </div>
             <div>
                 <span>Successfull Users Records:</span>&nbsp; <span> ${jobStatus.successRecords}</span>
             </div>
             <div>
                 <span>Failed Users Records:</span>&nbsp; <span> ${jobStatus.errorRecords}</span>
             </div>
             <div>
                 <span>Total Users Records:</span>&nbsp; <span> ${jobStatus.totalRecords}</span>
             </div>
        </div>
        
        <div>
            <c:choose>
                <c:when test="${isCompleted}">
                    <button onclick="changeUrl('startFreshSync')";>Start New Job</button>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${isPaused}">
                            <button onclick="changeUrl('resumeSync')";>Resume Old Job</button>
                        </c:when>
                        <c:otherwise>
                            <button onclick="changeUrl('stopSync')";>Stop Job</button>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
</body>

</html>