$(document).ready(function() {
    $('#sticky-nav').affix({
        offset: {
			top: $('#sticky-nav').offset().top
		}
	});	
});