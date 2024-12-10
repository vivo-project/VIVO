$(document).ready(function(){
    $("#viewAllTab").on("click",function(){
        showViewAll();
    });

    if (location.hash) {
        $('li[href=\'' + location.hash + '\']').tab('show');
        if (location.hash == "#viewAll") {
            showViewAll();
        }
    }

    var activeTab = localStorage.getItem('activeTab');
    if (activeTab) {
        $('li[href="' + activeTab + '"]').tab('show');
        if (activeTab == "#viewAll") {
            showViewAll();
        }
    }

    $('body').on('click', 'li[data-toggle=\'tab\']', function (e) {
        e.preventDefault()
        var tab_name = this.getAttribute('href')
        localStorage.setItem('activeTab', tab_name)

        $(this).tab('show');
        return false;
    });
    $(window).on('popstate', function () {
        var anchor = location.hash || $('li[data-toggle=\'tab\']').first().attr('href');
        $('li[href=\'' + anchor + '\']').tab('show');
        if (anchor == "#viewAll") {
            showViewAll();
        }
    });

    function showViewAll() {
        $('[data-toggle="tab"]').parent().removeClass("active");
        $("#viewAllTab").parent().addClass("active");
        $("#viewAllTab").addClass("active");
        $(".tab-pane").addClass("active");
    }
});
