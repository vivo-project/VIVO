$(document).ready(function(){
    $("#viewAllTab").on("click",function(){
        $('[data-toggle="tab"]').parent().removeClass("active");
        $("#viewAllTab").parent().addClass("active");
        $("#viewAllTab").addClass("active");
        $(".tab-pane").addClass("fade active in");
    });

    if (location.hash) {
        $('li[href=\'' + location.hash + '\']').tab('show');
    }
    var activeTab = localStorage.getItem('activeTab');
    if (activeTab) {
        $('li[href="' + activeTab + '"]').tab('show');
    }

    $('body').on('click', 'li[data-toggle=\'tab\']', function (e) {
        e.preventDefault()
        var tab_name = this.getAttribute('href')
        localStorage.setItem('activeTab', tab_name)

        $(this).tab('show');
        return false;
    });
    $(window).on('popstate', function () {
        var anchor = location.hash ||
            $('li[data-toggle=\'tab\']').first().attr('href');
        $('li[href=\'' + anchor + '\']').tab('show');
    });
});
