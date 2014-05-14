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
              }
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
        $.get("/myeltanalytics/submissions/stopSync", function(data){
           $("#submissionsJobStatus").removeClass("badge-info").addClass("badge-error");
           $("#submissionsJobStatus").html("Paused");
           $("#submissionsProgressContainer").removeClass("active");
           $("#submissionsSyncPanel .panel-heading i").removeClass("fa-gear-animated"); 
           $("#submissionsStopButton").hide();
           $("#submissionsResumeButton").show();
           $("#submissionsStartButton").show();
        });
    };
    
    this.startSync= function () {
        $.get("/myeltanalytics/submissions/startSync", function(data){
            var responseJson = eval("(" + data + ")");
            
            $("#submissionsJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
            $("#submissionsJobStatus").html("InProgress");
            
            $("#submissionsTotalRecords").html(responseJson.totalRecords);
            $("#submissionsSuccessRecords").html("0");
            $("#submissionsErrorRecords").html("0");
            
            $("#submissionsProgressContainer").addClass("active"); 
            $("#submissionsSyncPanel .panel-heading i").addClass("fa-gear-animated"); 
            $("#submissionsResumeButton").hide();
            $("#submissionsStartButton").hide();
            $("#submissionsStopButton").show();
            SubmissionsSyncHelper.triggerFetchingSyncStatus();
        });
    };
    
    this.resumeSync= function () {
        $.get("/myeltanalytics/submissions/resumeSync", function(data){
            var responseJson = eval("(" + data + ")");
            $("#submissionsJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
            $("#submissionsJobStatus").html("InProgress");
            $("#submissionsTotalRecords").html(responseJson.totalRecords);
            $("#submissionsProgressContainer").addClass("active"); 
            $("#submissionsSyncPanel .panel-heading i").addClass("fa-gear-animated"); 
            $("#submissionsResumeButton").hide();
            $("#submissionsStartButton").hide();
            $("#submissionsStopButton").show();
            SubmissionsSyncHelper.triggerFetchingSyncStatus();
        });
    };
    
}