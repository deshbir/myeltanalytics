var UsersSyncHelper = new function() {
    
    var fetchSyncPoller = null ;
    
    this.triggerFetchingSyncStatus = function () {
        fetchSyncPoller = setInterval(function(){
            $.get("/myeltanalytics/sync/users/getSyncStatus", function(data){
              var responseJson = eval("(" + data + ")");
              $("#usersProgressContainer .progress-bar").css('width', responseJson.percent +'%');
              $("#usersProgressContainer .progress-bar").html(responseJson.percent + '%');
              if (responseJson.jobStatus == "Completed") {
                  $("#usersJobStatus").removeClass().addClass("syncinfo badge badge-success pull-right");
                  $("#usersProgressContainer").removeClass().addClass("progress progress-striped");  
                  $("#usersSyncPanel .panel-heading i").removeClass("fa-spin");
                  $("#usersStopButton").hide();
                  $("#usersResumeButton").hide();
                  $("#usersStartButton").show();
                  if(responseJson.errorRecords > 0){
                	  $("#userFailedRecord i.fa-spin").hide();
                	  $("#userFailedRecord i.fa-repeat").show().css("cursor","pointer");
                  }
                  UsersSyncHelper.abortFetchingSyncStatus();
              }
              $("#usersJobStartDateTime").html(responseJson.startDateTime);
              $("#usersJobStatus").html(responseJson.jobStatus);
              $("#usersSuccessRecords").html(responseJson.successRecords);
              $("#usersErrorRecords").html(responseJson.errorRecords);
              if(responseJson.failedsUserStatus == "Completed" && !(responseJson.jobStatus == "Completed")){
            	  $("#usersJobStatus").removeClass().addClass("syncinfo badge badge-success pull-right");
                  $("#usersProgressContainer").removeClass().addClass("progress progress-striped");  
                  $("#usersSyncPanel .panel-heading i").removeClass("fa-spin");
                  $("#usersStopButton").hide();
                  $("#usersResumeButton").removeAttr("disabled");
                  $("#usersResumeButton").show();
                  $("#usersStartButton").show();
                  if(responseJson.errorRecords > 0){
	                  $("#userFailedRecord i.fa-repeat").show();
	                  $("#userFailedRecord i.fa-spin").hide();
                  }else{
                	  $("#userFailedRecord i.fa-repeat").hide();
	                  $("#userFailedRecord i.fa-spin").hide();
                  }
                  UsersSyncHelper.abortFetchingSyncStatus();
              }
            })
        }, 5000);
    };
    
    this.abortFetchingSyncStatus = function () {
        clearInterval(fetchSyncPoller);
    };
    
    this.stopSync= function () {
        
        UsersSyncHelper.abortFetchingSyncStatus();
        
        $("#usersStopButton").attr("disabled","disabled");
        $("#usersStopButton i.fa-stop").hide();
        $("#usersStopButton i.fa-spin").show();
        
        
        $.get("/myeltanalytics/sync/users/stopSync", function(data){
           
           var responseJson = eval("(" + data + ")");
           if (responseJson.status == "error") {
               Util.showError(responseJson.errorMessage);
           } else {
        	   if(responseJson.errorRecords > 0){
        		   $("#userFailedRecord i.fa-repeat").show().css("cursor","pointer");
        		   $("#userFailedRecord i.fa-spin").hide();
        	   }
               $("#usersJobStatus").removeClass("badge-info").addClass("badge-error");
               $("#usersJobStatus").html("Paused");
               $("#usersProgressContainer").removeClass("active");
               $("#usersSyncPanel .panel-heading i").removeClass("fa-spin"); 
               $("#usersStopButton").hide();
               	$("#usersResumeButton").show();
               $("#usersStartButton").show();
           }  
           $("#usersStopButton").removeAttr("disabled");
           $("#usersStopButton i.fa-stop").show();
           $("#usersStopButton i.fa-spin").hide();
        });
    };
    
    this.startSync= function () {
        
        $("#usersStartButton").attr("disabled","disabled");
        $("#usersResumeButton").attr("disabled","disabled");
        $("#usersStartButton i.fa-play").hide();
        $("#usersStartButton i.fa-spin").show();
        $("#userFailedRecord i.fa-repeat").hide();
        $.get("/myeltanalytics/sync/users/startSync", function(data){
            
            var responseJson = eval("(" + data + ")");
            if (responseJson.status == "error") {
                Util.showError(responseJson.errorMessage);
            } else {
                $("#usersTotalRecords").html(responseJson.totalRecords);
                $("#usersJobStartDateTime").html(responseJson.startDateTime);
                $("#usersSuccessRecords").html("0");
                $("#usersErrorRecords").html("0");
                
                $("#usersJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
                $("#usersJobStatus").html("InProgress");
                
                $("#usersProgressContainer .progress-bar").css('width', '0%');
                $("#usersProgressContainer .progress-bar").html('0%');
                $("#usersProgressContainer").addClass("active"); 
                
                $("#usersResumeButton").hide();
                $("#usersStartButton").hide();
                $("#usersStopButton").show();              
                $("#usersSyncPanel .panel-heading i").addClass("fa-spin"); 
               
                UsersSyncHelper.triggerFetchingSyncStatus();
            }
            $("#usersStartButton").removeAttr("disabled");
            $("#usersResumeButton").removeAttr("disabled");
            $("#usersStartButton i.fa-play").show();
            $("#usersStartButton i.fa-spin").hide();
        });
    };
    
    this.resumeSync= function () {
        
        $("#usersStartButton").attr("disabled","disabled");
        $("#usersResumeButton").attr("disabled","disabled");
        $("#usersResumeButton i.fa-play-circle-o").hide();
        $("#usersResumeButton i.fa-spin").show();
        $("#userFailedRecord i.fa-repeat").hide();
        $.get("/myeltanalytics/sync/users/resumeSync", function(data){
            var responseJson = eval("(" + data + ")");
            
            if (responseJson.status == "error") {
                Util.showError(responseJson.errorMessage);
            } else {
                $("#usersTotalRecords").html(responseJson.totalRecords);
                
                $("#usersJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
                $("#usersJobStatus").html("InProgress");
               
                $("#usersResumeButton").hide();
                $("#usersStartButton").hide();
                $("#usersStopButton").show();
              
                $("#usersSyncPanel .panel-heading i").addClass("fa-spin"); 
               
                UsersSyncHelper.triggerFetchingSyncStatus();
            } 
            $("#usersStartButton").removeAttr("disabled");
            $("#usersResumeButton").removeAttr("disabled");
            $("#usersResumeButton i.fa-play-circle-o").show();
            $("#usersResumeButton i.fa-spin").hide();
            
           
        });
    };
    
this.startFailedUserSync = function () {
        
        $("#usersStartButton").attr("disabled","disabled");
        $("#usersResumeButton").attr("disabled","disabled");
        $("#userFailedRecord i.fa-repeat").hide();
        $("#userFailedRecord i.fa-spin").show();
        $.get("/myeltanalytics/sync/users/failedUserSync", function(data){
            var responseJson = eval("(" + data + ")");
            if (responseJson.status == "error") {
                Util.showError(responseJson.errorMessage);
            } else {
                $("#usersTotalRecords").html(responseJson.totalRecords);
                $("#usersJobStartDateTime").html(responseJson.startDateTime);
                $("#usersSuccessRecords").html("0");
                $("#usersErrorRecords").html("0");
                $("#usersJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
                $("#usersJobStatus").html("InProgress");
                $("#usersProgressContainer .progress-bar").css('width', '0%');
                $("#usersProgressContainer .progress-bar").html('0%');
                $("#usersProgressContainer").addClass("active"); 
                $("#usersResumeButton").hide();
                $("#usersStartButton").hide();
                $("#usersStopButton").show();              
                $("#usersSyncPanel .panel-heading i").addClass("fa-spin"); 
                UsersSyncHelper.triggerFetchingSyncStatus();
            }
            $("#usersStartButton").removeAttr("disabled");
            $("#usersStartButton i.fa-play").show();
            $("#usersStartButton i.fa-spin").hide();
        });
    };
    
}