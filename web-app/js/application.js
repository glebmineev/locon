if (typeof jQuery !== 'undefined') {
    (function ($) {
        $('#spinner').ajaxStart(function () {
            $(this).fadeIn();
        }).ajaxStop(function () {
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

    $('#mega-menu').dcMegaMenu();

    $(function() {
        $('#navigation a').stop().animate({'marginLeft':'-85px'},1000);

        $('#navigation > li').hover(
            function () {
                $('a',$(this)).stop().animate({'marginLeft':'-2px'},200);
            },
            function () {
                $('a',$(this)).stop().animate({'marginLeft':'-85px'},200);
            }
        );
    });

});


$(function () {
    $('#navigation a').stop().animate({'marginLeft': '-85px'}, 1000);

    $('#navigation > li').hover(
        function () {
            $('a', $(this)).stop().animate({'marginLeft': '-2px'}, 200);
        },
        function () {
            $('a', $(this)).stop().animate({'marginLeft': '-85px'}, 200);
        }
    );
});

