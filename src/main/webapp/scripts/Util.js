var Util = new function() {
    var iframeWidth = $(window).width() - 100;
    var iframeHeight = $(window).height() - 100;
    this.openReport = function (url) {
        $.fancybox({
            href: url,
            type: "iframe",
            autoSize: false,
            minWidth:iframeWidth,
            minHeight:iframeHeight,
            keys : {
                close  : null
            },
            afterShow: function() {
                $('<div class="expander"></div>').appendTo(this.inner).click(function() {
                    $('iframe').contents().toggleFullScreen();
                });
            },
            afterClose: function() {
                $('iframe').contents().fullScreen(false);
            }
        })
    };

    $('iframe').contents().bind("fullscreenerror", function() {
        alert("Browser rejected fullscreen");
    });

}