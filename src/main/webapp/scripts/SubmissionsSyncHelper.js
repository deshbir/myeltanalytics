var SubmissionsSyncHelper = new function() {
    
    var fetchSyncPoller = null ;
    
    this.triggerFetchingSyncStatus = function () {
        fetchSyncPoller = setInterval(function(){
            $.get("/myeltanalytics/submissions/getSyncStatus", function(data){
              var responseJson = eval("(" + data + ")");
              $("#submissionsProgressContainer .progress-bar").css('width', responseJson.percent +'%');
              $("#submissionsProgressContainer .progress-bar").html(responseJson.percent + '%');
              if (responseJson.jobStatus == "Completed") {
                  $("#submissionsJobStatus").removeClass().addClass("syncinfo badge badge-success pull-right");
                  $("#submissionsProgressContainer").removeClass().addClass("progress progress-striped");  
                  $("#submissionsSyncPanel .panel-heading i").removeClass("fa-spin");
                  $("#submissionsStopButton").hide();
                  $("#submissionsResumeButton").hide();
                  $("#submissionsStartButton").show();
                  SubmissionsSyncHelper.abortFetchingSyncStatus();
              }
              $("#submissionsJobStartDateTime").html(responseJson.startDateTime);
              $("#submissionsJobStatus").html(responseJson.jobStatus);
              $("#submissionsSuccessRecords").html(responseJson.successRecords);
              $("#submissionsErrorRecords").html(responseJson.errorRecords);
            })
        }, 5000);
    };
    
    this.abortFetchingSyncStatus = function () {
        clearInterval(fetchSyncPoller);
    };
    
    this.stopSync= function () {
        
        SubmissionsSyncHelper.abortFetchingSyncStatus();
        
        $("#submissionsStopButton").attr("disabled","disabled");
        $("#submissionsStopButton i.fa-stop").hide();
        $("#submissionsStopButton i.fa-spin").show();
        
        $.get("/myeltanalytics/submissions/stopSync", function(data){
            
           $("#submissionsStopButton").removeAttr("disabled");
           $("#submissionsStopButton i.fa-stop").show();
           $("#submissionsStopButton i.fa-spin").hide();
            
           $("#submissionsJobStatus").removeClass("badge-info").addClass("badge-error");
           $("#submissionsJobStatus").html("Paused");
           
           $("#submissionsProgressContainer").removeClass("active");
           $("#submissionsSyncPanel .panel-heading i").removeClass("fa-spin"); 
           
           $("#submissionsStopButton").hide();
           $("#submissionsResumeButton").show();
           $("#submissionsStartButton").show();
        });
    };
    
    this.startSync= function () {
        
        $("#submissionsStartButton").attr("disabled","disabled");
        $("#submissionsResumeButton").attr("disabled","disabled");
        $("#submissionsStartButton i.fa-play").hide();
        $("#submissionsStartButton i.fa-spin").show();
        
        $.get("/myeltanalytics/submissions/startSync", function(data){
            
            var responseJson = eval("(" + data + ")");
            $("#submissionsTotalRecords").html(responseJson.totalRecords);
            $("#submissionsJobStartDateTime").html(responseJson.startDateTime);
            $("#submissionsSuccessRecords").html("0");
            $("#submissionsErrorRecords").html("0");
            
            $("#submissionsJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
            $("#submissionsJobStatus").html("InProgress");
            
            $("#submissionsProgressContainer .progress-bar").css('width', '0%');
            $("#submissionsProgressContainer .progress-bar").html('0%');
            $("#submissionsProgressContainer").addClass("active"); 
            
            $("#submissionsStartButton").removeAttr("disabled");
            $("#submissionsResumeButton").removeAttr("disabled");
            $("#submissionsStartButton i.fa-play").show();
            $("#submissionsStartButton i.fa-spin").hide();
            $("#submissionsResumeButton").hide();
            $("#submissionsStartButton").hide();
            $("#submissionsStopButton").show();
          
            $("#submissionsSyncPanel .panel-heading i").addClass("fa-spin"); 
           
            SubmissionsSyncHelper.triggerFetchingSyncStatus();
        });
    };
    
    this.resumeSync= function () {
        
        $("#submissionsStartButton").attr("disabled","disabled");
        $("#submissionsResumeButton").attr("disabled","disabled");
        $("#submissionsResumeButton i.fa-play-circle-o").hide();
        $("#submissionsResumeButton i.fa-spin").show();
        
        $.get("/myeltanalytics/submissions/resumeSync", function(data){
            var responseJson = eval("(" + data + ")");
            $("#submissionsTotalRecords").html(responseJson.totalRecords);
            
            $("#submissionsJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
            $("#submissionsJobStatus").html("InProgress");
            
            $("#submissionsStartButton").removeAttr("disabled");
            $("#submissionsResumeButton").removeAttr("disabled");
            $("#submissionsResumeButton i.fa-play-circle-o").show();
            $("#submissionsResumeButton i.fa-spin").hide();
            $("#submissionsResumeButton").hide();
            $("#submissionsStartButton").hide();
            $("#submissionsStopButton").show();
          
            $("#submissionsSyncPanel .panel-heading i").addClass("fa-spin"); 
           
            SubmissionsSyncHelper.triggerFetchingSyncStatus();
        });
    };
    
}