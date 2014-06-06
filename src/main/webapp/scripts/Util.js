var Util = new function() {

    this.showError = function (message) {
        var errorMessage = "<div class=\"alert alert-danger alert-dismissable\">";
        errorMessage += "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>"; 
        errorMessage += "<strong>Error! </strong>"
        errorMessage += message;
        errorMessage += "</div>";
        $("#errorMessage").html(errorMessage);
    };

}