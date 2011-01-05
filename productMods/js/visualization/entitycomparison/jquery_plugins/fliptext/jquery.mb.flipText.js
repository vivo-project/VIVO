/*******************************************************************************
 jquery.mb.components
 Copyright (c) 2001-2009. Matteo Bicocchi (Pupunzi); Open lab srl, Firenze - Italy
 email: info@pupunzi.com
 site: http://pupunzi.com
 Licences: MIT, GPL
 ******************************************************************************/

/*
 * jQuery.mb.components: jquery.mb.flipV
 * version: 1.0
 * © 2001 - 2009 Matteo Bicocchi (pupunzi), Open Lab
 *
 */


(function($) {
  var isIE=$.browser.msie;
  jQuery.fn.encHTML = function() {
    return this.each(function(){
      var me   = $(this);
      var html = me.text();
      me.text(html.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/'/g, escape("'")).replace(/"/g,escape('"')));
    });
  };
  $.mbflipText= {
    author:"Matteo Bicocchi",
    version:"1.0",
    flipText:function(tb){
      return this.each(function(){

        var h=$(this).getFlipTextDim(true)[1]+"px";
        var w=$(this).getFlipTextDim(true)[0]+"px";

       if(!isIE) $(this).encHTML();
        var txt= $(this).html();
        var label="";
        var bgcol=$(this).css("background-color")?$(this).css("background-color"):"#fff";
        var fontsize= parseInt($(this).css('font-size'))>0?parseInt($(this).css('font-size')):14;
        var fontfamily=$(this).css('font-family')?$(this).css('font-family').replace(/\'/g, '').replace(/"/g,''):"Arial";
        var fontcolor=$(this).css('color')?$(this).css('color'):"#000";

        if ($.browser.msie){
          if(!tb) $(this).css({'writing-mode': 'tb-rl', height:h, filter: 'fliph() flipv("") ', whiteSpace:"nowrap"}).css('font-weight', 'normal');
          label=$("<span style='writing-mode: tb-rl; whiteSpace:nowrap; height:"+h+"; width:"+w+"'>"+txt+"</span>");
        }else{
          var rot="-90";
          var ta="end";
          var xFix=0;
          var yFix=$.browser.opera ?parseInt(w)-(parseInt(w)/4): $.browser.safari?5:"0";
          if (tb){
            xFix= $.browser.safari?5:0;
            rot="90, "+((parseInt(w)/2)-xFix)+", "+parseInt(w)/2;
            ta="start";
          }
//          var  SafariFix= $.browser.safari? "<rect x=\"0\" y=\"0\" width=\""+w+"\" height=\""+h+"\" fill=\""+bgcol+"\" stroke=\"none\"/>":"";
          label=$("<object class='flip_label' style='height:"+h+"; width:"+w+";' type='image/svg+xml' data='data:image/svg+xml," +
                  "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                  "<rect x=\"0\" y=\"0\" width=\""+w+"\" height=\""+h+"\" fill=\""+bgcol+"\" stroke=\"none\"/>"+ //safari Fix
                  "<text  x=\"-"+xFix+"\" y=\""+(yFix)+"\" font-family=\""+fontfamily+"\"  fill=\""+fontcolor+"\" font-size=\""+fontsize+"\"  style=\"text-anchor: "+ta+"; " +
                  "dominant-baseline: hanging\" transform=\"rotate("+rot+")\" text-rendering=\"optimizeSpeed\">"+txt+"</text></svg>'></object>" +
                  "<div class='pointer' style='position:absolute;top:0;left:0;width:100%;height:100%;background:transparent'/>" +
                  "");
        }
        var cssPos= $(this).css("position")!="absolute" || $(this).css("position")!="fixed"  ?"relative" : $(this).css("position");
        $(this).html(label).css({position:cssPos, width:w});
      });
    },
    getFlipTextDim:function(enc){
      if(!enc && !isIE) $(this).encHTML();
      var txt= $(this).html();
      var fontsize= parseInt($(this).css('font-size'));
      var fontfamily=$(this).css('font-family').replace(/'/g, '').replace(/"/g,'');
      if (fontfamily==undefined) fontfamily="Arial";
      var placeHolder=$("<span/>").css({position:"absolute",top:-100, whiteSpace:"noWrap", fontSize:fontsize, fontFamily: fontfamily});
      placeHolder.html(txt);
      $("body").append(placeHolder);
      var h = (placeHolder.outerWidth()!=0?placeHolder.outerWidth():(16+txt.length*fontsize*.60));
      var w = (placeHolder.outerHeight()!=0?placeHolder.outerHeight()+5:50);
      placeHolder.remove();
      return [w,h];
    }
  };
  $.fn.mbFlipText=$.mbflipText.flipText;
  $.fn.getFlipTextDim=$.mbflipText.getFlipTextDim;

})(jQuery);