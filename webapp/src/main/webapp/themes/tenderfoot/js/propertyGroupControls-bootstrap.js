$(document).ready(function(){
    $("#viewAllTab").on("click",function(){
        $('[data-toggle="tab"]').parent().removeClass("active");
        $("#viewAllTab").parent().addClass("active");
        $("#viewAllTab").addClass("active");
        $(".tab-pane").addClass("fade active in");
    });
});
