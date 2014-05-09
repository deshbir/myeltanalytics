var UsersSyncHelper = new function() {
    
    var fetchSyncPoller = null ;
    
    this.changeUrl = function (url) {
        location = url;
    };
    
    this.triggerFetchingSyncStatus = function (url) {
        fetchSyncPoller = setInterval(function(){
            $.get("/myeltanalytics/users/getSyncStatus", function(data){
              var responseJson = eval("(" + data + ")");
              $("#usersProgressContainer .progress-bar").css('width', responseJson.percent +'%');
              $("#usersProgressContainer .progress-bar").html(responseJson.percent + '%');
              if (responseJson.jobStatus == "Completed") {
                  $("#jobStatus").removeClass().addClass("syncinfo badge badge-success pull-right");
                  $("#usersProgressContainer").removeClass().addClass("progress progress-striped");  
              }
              $("#jobStatus").html(responseJson.jobStatus);
              $("#lastId").html(responseJson.lastId);
              $("#successRecords").html(responseJson.successRecords);
              $("#errorRecords").html(responseJson.errorRecords);
            })
        }, 5000);
    };
    
    this.abortFetchingSyncStatus = function () {
        clearInterval(fetchSyncPoller);
    };
    
    this.stopSync= function () {
        this.abortFetchingSyncStatus();
        $.get("/myeltanalytics/users/stopSync", function(data){
           $("#jobStatus").removeClass("badge-info").addClass("badge-error");
           $("#jobStatus").html("Paused");
           $("#usersProgressContainer").removeClass("active");
           $("#usersSyncPanel .panel-heading i").removeClass("fa-gear-animated"); 
           $("#stopButton").hide();
           $("#resumeButton").show();
           $("#startButton").show();
        });
    };
    
    this.startSync= function () {
        $.get("/myeltanalytics/users/startSync", function(data){
            $("#jobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
            $("#jobStatus").html("InProgress");
            $("#usersProgressContainer").addClass("active"); 
            $("#usersSyncPanel .panel-heading i").addClass("fa-gear-animated"); 
            $("#resumeButton").hide();
            $("#startButton").hide();
            $("#stopButton").show();
            UsersSyncHelper.triggerFetchingSyncStatus();
        });
    };
    
    this.resumeSync= function () {
        $.get("/myeltanalytics/users/resumeSync", function(data){
            $("#jobStatus").removeClass("badge-error").removeClass("badge-success").addClass("badge-info");
            $("#jobStatus").html("InProgress");
            $("#usersProgressContainer").addClass("active"); 
            $("#usersSyncPanel .panel-heading i").addClass("fa-gear-animated"); 
            $("#resumeButton").hide();
            $("#startButton").hide();
            $("#stopButton").show();
            UsersSyncHelper.triggerFetchingSyncStatus();
        });
    };
    
}