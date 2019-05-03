/* jquery-jangle-0.5.js
 *
 * Last modified by: Cory Dorning
 * Last modified on: 08/16/2011
 *
 * jAngle is a jQuery plugin that uses CSS3 (or appropriate
 * IE filters) to rotate an element using the passed parameters.
 *
 * continuous:  determines if an element is rotated from its
 *              current position or its starting position

 * degrees:     is the angle you want to rotate the element

 * duration:    is how long you want the rotation to take

 * interval:    is how often the element is updated during the
 *              specified duration (smoothness of the animation)
 *
 * @TODO
 * =nan-issue:
 *  NaN returns true, need to replace || with tertiary condition
 *
 * =requestAnimation:
 *  Create a polyfill for requestAnimationFrame and use instead of
 *  setInterval when/where possible
 *
 */

(function($) {
    $.fn.jangleUpdate = function($el, deg) {
        var currPos = $el.data('jangle') || 0,
            newPos = currPos + deg

        $el.data('jangle', newPos);

        return newPos;
    };

    $.fn.jangleFeatureDetection = function() {
        // JS properites to check
        var props = ['transformProperty', 'WebkitTransform', 'MozTransform', 'OTransform', 'msTransform'],

            // element to test
            elStyle = this[0] ? this[0].style : {};

        // test for CSS transform support
        for ( var i in props ) {
            if ( elStyle[ props[i] ] !== undefined ) {
                return true;
            }
        }

        return false;
    };

    $.fn.jangleCSSTransform = function(deg) {
        // css transform init
        var styleTransform,

            // CSS vendor prefixes and JS properites
            prefixes = ['-khtml-', '-moz-', '-ms-', '-o-', '-webkit-'];

        for (var i = 0; i <= prefixes.length; i++) {
            styleTransform = prefixes[i] ? prefixes[i] + 'transform' : 'transform';

            this.css(styleTransform, 'rotate(' + deg + 'deg)');

            // jQuery issue with -ms- prefix - http://bugs.jquery.com/ticket/8346
            if (prefixes[i] === '-ms-') {
                $(this).css({msTransform: 'rotate(' + deg + 'deg)'});
            }
        }

        return this;
    };

    $.fn.jangleIETransform = function(deg) {
        // crazy math algorithm voodoo - see http://msdn.microsoft.com/en-us/library/ms533014%28VS.85%29.aspx
        // and http://www.boogdesign.com/b2evo/index.php/element-rotation-ie-matrix-filter?blog=2
        var rotation = Math.PI * (deg < 0 ? deg + 360 : deg) / 180,
            cos = Math.cos(rotation),
            sin = Math.sin(rotation),

            // ie matrix offsets
            setOffsets = function(img) { // set IE origin to match other browsers - http://blog.siteroller.net/cross-browser-css-rotation
                if (img.style.position !== 'absolute' && img.style.position !== 'fixed') {
                    img.style.position = 'relative';
                    img.style.top = (img.clientHeight - img.offsetHeight) / 2;
                    img.style.left = (img.clientWidth - img.offsetWidth) / 2;
                }
            };

        return this.each(function() {
            this.style.filter = "progid:DXImageTransform.Microsoft.Matrix(M11="+cos+",M12="+(-sin)+",M21="+sin+",M22="+cos+",SizingMethod='auto expand')";
            setOffsets(this);
        });
    };

    $.fn.jangleAnimation = function(s) { // =requestAnimation
        var $this = this,

            currPos = this.data('jangle') || 0,  // s.continuous ? this.data('jangle') : 0

            newPos = s.continuous ? $.jangleUpdate(this, s.degrees) : s.degrees,

            cssTransforms = this.jangleFeatureDetection(),

            animation = setInterval(function() {
                if ((currPos += (s.interval * s.degrees / s.duration)) <= newPos) {
                    $this[cssTransforms ? 'jangleCSSTransform' : 'jangleIETransform'](currPos);
                } else {
                    $this[cssTransforms ? 'jangleCSSTransform' : 'jangleIETransform'](newPos);
                    clearInterval(animation);
                }
            }, s.interval);
    };

    $.fn.jangle = function(options) {
        // set defaults
        var defaults = {
                continuous: false, // account for current angle
                degrees: 0, // initialize degrees
                duration: 0, // length of time (in m/s) the animation takes
                interval: 100 // length of time (in m/s) each rotation occurs
            },

            cssTransforms = this.jangleFeatureDetection(),

            // overwrite default options with those set OR copy defaults
            settings = typeof options === 'object' ? $.extend(defaults, options) : defaults; // =nan-issue


        // make sure properties are integers or keep as is
        settings.degrees =  (parseInt(options, 10) || settings.degrees) % 360; // set degrees if 'options' is an integer
        settings.duration = parseInt(settings.duration, 10) || settings.duration;
        settings.interval = parseInt(settings.interval, 10) || settings.interval;

        // animate
        if (settings.duration) {
            this.jangleAnimation(settings);
        } else {
            this[cssTransforms ? 'jangleCSSTransform' : 'jangleIETransform'](settings.continuous ? $.jangleUpdate(this, settings.degrees) : settings.degrees);
        }

        // don't be the weakest link (preserve chainability)
        return this;
    };
})(jQuery);
