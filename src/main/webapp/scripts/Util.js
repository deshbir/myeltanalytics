var Util = new function() { 
    var iframeWidth = $(window).width() - 100; 
    var iframeHeight = $(window).height() - 100; 
    this.openReport = function (url) {
        $.fancybox({
            href: url,
            type: "iframe",
            autoSize: false,
            minWidth:iframeWidth,
            minHeight:iframeHeight
        })         
    };   
}