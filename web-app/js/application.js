if (typeof jQuery !== 'undefined') {
	(function($) {
		$('#spinner').ajaxStart(function() {
			$(this).fadeIn();
		}).ajaxStop(function() {
			$(this).fadeOut();
		});
	})(jQuery);
}

$(document).ready(function () {

    $('#nav li').hover(
        function () {
            //show its submenu
            $('ul', this).slideDown(100);

        },
        function () {
            //hide its submenu
            $('ul', this).slideUp(100);
        }
    );

});
