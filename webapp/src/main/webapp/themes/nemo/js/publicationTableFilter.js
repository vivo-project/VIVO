$(document).ready(function () {

    (function ($) {

        $('#publicationFilter').on("keyup", function () {

            var rex = new RegExp($(this).val(), 'i');
            $('.publicationTableRow').hide();
            $('.publicationTableRow').filter(function () {
                return rex.test($(this).text());
            }).show();

        })

    }(jQuery));

});