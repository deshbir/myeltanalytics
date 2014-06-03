var Util = new function() { 

    this.openReport = function (filename) {
        var iframeWidth = $(window).width() - 100; 
        var iframeHeight = $(window).height() - 100; 
        var fullURL = "index.html#/dashboard/file/" + filename;
        
        $.fancybox({
            href: fullURL,
            type: "iframe",
            autoSize: false,
            minWidth:iframeWidth,
            minHeight:iframeHeight
        })         
    };
    
}