var UsersSyncHelper = new function() {
    
    var fetchSyncPoller = null ;
    
    this.triggerFetchingSyncStatus = function () {
        fetchSyncPoller = setInterval(function(){
            $.get("/myeltanalytics/users/getSyncStatus", function(data){
              var responseJson = eval("(" + data + ")");
              $("#usersProgressContainer .progress-bar").css('width', responseJson.percent +'%');
              $("#usersProgressContainer .progress-bar").html(responseJson.percent + '%');
              if (responseJson.jobStatus == "Completed") {
                  $("#usersJobStatus").removeClass().addClass("syncinfo badge badge-success pull-right");
                  $("#usersProgressContainer").removeClass().addClass("progress progress-striped");  
                  $("#usersSyncPanel .panel-heading i").removeClass("fa-gear-animated");
                  $("#usersStopButton").hide();
                  $("#usersResumeButton").hide();
                  $("#usersStartButton").show();
                  UsersSyncHelper.abortFetchingSyncStatus();
              }
              $("#usersJobStatus").html(responseJson.jobStatus);
              $("#usersSuccessRecords").html(responseJson.successRecords);
              $("#usersErrorRecords").html(responseJson.errorRecords);
            })
        }, 5000);
    };
    
    this.abortFetchingSyncStatus = function () {
        clearInterval(fetchSyncPoller);
    };
    
    this.stopSync= function () {
        UsersSyncHelper.abortFetchingSyncStatus();
        $.get("/myeltanalytics/users/stopSync", function(data){
           $("#usersJobStatus").removeClass("badge-info").addClass("badge-error");
           $("#usersJobStatus").html("Paused");
           $("#usersProgressContainer").removeClass("active");
           $("#usersSyncPanel .panel-heading i").removeClass("fa-gear-animated"); 
           $("#usersStopButton").hide();
           $("#usersResumeButton").show();
           $("#usersStartButton").show();
        });
    };
    
    this.startSync= function () {
        $("#usersJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
        $("#usersJobStatus").html("InProgress");
        $("#usersProgressContainer .progress-bar").css('width', '0%');
        $("#usersProgressContainer .progress-bar").html('0%');
        $("#usersSuccessRecords").html("0");
        $("#usersErrorRecords").html("0");
        
        $.get("/myeltanalytics/users/startSync", function(data){
            var responseJson = eval("(" + data + ")");
            $("#usersTotalRecords").html(responseJson.totalRecords);
            $("#usersProgressContainer").addClass("active"); 
            $("#usersSyncPanel .panel-heading i").addClass("fa-gear-animated"); 
            $("#usersResumeButton").hide();
            $("#usersStartButton").hide();
            $("#usersStopButton").show();
            UsersSyncHelper.triggerFetchingSyncStatus();
        });
    };
    
    this.resumeSync= function () {
        $.get("/myeltanalytics/users/resumeSync", function(data){
            var responseJson = eval("(" + data + ")");
            $("#usersJobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
            $("#usersJobStatus").html("InProgress");
            $("#usersTotalRecords").html(responseJson.totalRecords);
            $("#usersProgressContainer").addClass("active"); 
            $("#usersSyncPanel .panel-heading i").addClass("fa-gear-animated"); 
            $("#usersResumeButton").hide();
            $("#usersStartButton").hide();
            $("#usersStopButton").show();
            UsersSyncHelper.triggerFetchingSyncStatus();
        });
    };
    
}