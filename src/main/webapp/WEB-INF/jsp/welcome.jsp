<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
    <body>
        <h1>MyELT Analytics Admin Tools</h1>
        <ul>        
          <li>
              <h2><a href="<c:url value="/myeltanalytics/admin/users/getSyncStatus"/>">Sync Users Tool</a></h2>              
          </li>       
           <li>
              <h2><a href="javascript: void(0)">Sync Submissions Tool</a></h2>
          </li>
        </ul>
    </body>
</html>