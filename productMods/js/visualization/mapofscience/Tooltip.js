/* $This file is distributed under the terms of the license in /doc/license.txt$ */
/** 
 * This tooltip source is modified based on the example of Google map V3. The demo html 
 * is at http://philmap.000space.com/gmap-api/poly-hov.html
 * 
 * Modification:
 * 1. Init container at constructor class
 * 2. Add feature functions: setHtml, setWidth, setPosition
 */
var Tooltip = function(o) {
	var me = this;
	var id = 'tt';
	var top = 3;
	var left = 3;
	var maxw = 300;
	var speed = 10;
	var fadeInTimer = 10;
	var fadeOutTimer = 0;
	var endalpha = 85;
	var alpha = 0;
	var preferredWidth;
	var tt, t, c, b, h, w;
	var opts = {};
	var ie = document.all ? true : false;

	opts = $.extend({}, opts, o);
	tt = document.createElement('div');
	tt.setAttribute('id', id);
	t = document.createElement('div');
	t.setAttribute('id', id + 'top');
	c = document.createElement('div');
	c.setAttribute('id', id + 'cont');
	b = document.createElement('div');
	b.setAttribute('id', id + 'bot');
	tt.appendChild(t);
	tt.appendChild(c);
	tt.appendChild(b);
	document.body.appendChild(tt);
	tt.style.opacity = 0;
	tt.style.filter = 'alpha(opacity=0)';
	
	return {
		show: function(){
				document.onmousemove = this.pos;
			
			var w = preferredWidth;
			tt.style.display = 'block';
			tt.style.width = w ? w + 'px' : 'auto';
			if(!w && ie){
				t.style.display = 'none';
				b.style.display = 'none';
				tt.style.width = tt.offsetWidth;
				t.style.display = 'block';
				b.style.display = 'block';
			}
			if(tt.offsetWidth > maxw) { tt.style.width = maxw + 'px' }
			h = parseInt(tt.offsetHeight) + top;
			clearInterval(tt.timer);
			var me = this;
			tt.timer = setInterval( function() { me.fade(1) }, fadeInTimer);
		},
		pos:function(e){
			var u = ie ? event.clientY + document.documentElement.scrollTop : e.pageY;
			var l = ie ? event.clientX + document.documentElement.scrollLeft : e.pageX;
			tt.style.top = (u - h) + 'px';
			tt.style.left = (l + left) + 'px';
		},
		fade:function(d){
			var a = alpha;
			if((a != endalpha && d == 1) || (a != 0 && d == -1)){
				var i = speed;
				if(endalpha - a < speed && d == 1){
					i = endalpha - a;
				}else if(alpha < speed && d == -1){
					i = a;
				}
				alpha = a + (i * d);
				tt.style.opacity = alpha * .01;
				tt.style.filter = 'alpha(opacity=' + alpha + ')';
			}else{
				clearInterval(tt.timer);
				if(d == -1){tt.style.display = 'none'}
			}
		},
		hide:function(){
			clearInterval(tt.timer);
			var me = this;
			tt.timer = setInterval( function(){ me.fade(-1) }, fadeOutTimer);
		},
		setHtml: function(v) {
			c.innerHTML = v;
		},
		setWidth: function(w) {
			preferredWidth = w;
		},
		setPosition: function(x, y) {
			tt.style.top = x + 'px';
			tt.style.left = y + 'px';
		}
	};
};