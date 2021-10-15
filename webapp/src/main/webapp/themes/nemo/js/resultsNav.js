/**
 * Created by s.porter on 21/06/2016.
 */
var nextUrl, previousUrl, currentPage, inbasket;
var nextUrlB, previousUrlB, addtobasketB, basketB;

nextUrlB = "<span id='nextB'></span>";
previousUrlB = "<span id='previousB'></span>";
addtobasketB = "<span id='addB'></span>";
basketB = "<span id='basketB'></span>";


$('#basket-controls').append(previousUrlB);
$('#basket-controls').append(basketB);
$('#basket-controls').append(nextUrlB);
$('#basket-controls').append(addtobasketB);

$('#previousB').hide();
$('#basketB').hide();
$('#nextB').hide();

nextUrl = "";
previousUrl = "";
addtobasket = "";
basket = "";

currentPage =  window.location.href 
currentPage = currentPage.split('#')[0];

function processcart(data){
      console.log(data);
      $('#addB').hide();
      inbasket = 0;
      for (i = 0; i < data.length; i++) {

        var compareString;
        compareString = data[i].indexUrl 


        if (compareString == currentPage) {
            inbasket = 1;
            nextUrl = data[(i + 1) % data.length].indexUrl;

            console.log("forward:" + nextUrl);
            if (i > 0){ 
                previousUrl = data[(i - 1) % data.length].indexUrl;
                console.log("back:" + previousUrl);
                previousUrl = '<li id="arrow-left"><a href="'+previousUrl+'"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span></a></li>';
                }
            nextUrl = '<li id="arrow-right"><a href="'+nextUrl+'"><span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span></a></li>';
            }

     }
    basket = '<a href="/s/search.html?collection=vivo-lilliput&form=SavedResults"><span class="glyphicon glyphicon glyphicon-tags"></span><span class="badge" id="basketNum">'+data.length+'</span></a>';

     $('#previousB').html(previousUrl);
     $('#basketB').html(basket);
     $('#nextB').html(nextUrl);

     $('#previousB').show();
     $('#basketB').show();
     if (data.length > 1) $('#nextB').show();

    if ( inbasket == 0 ) {
         addtobasket = '<a id="addtobasket" href="#" title="add this Expert to your basket" class="add-basket"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a>'
         $('#addB').html(addtobasket);
         $('#addB').show();
         $('#addtobasket').click(function(data) {
             $.post("/s/cart.json?collection=vivo-lilliput", {url:currentPage},
                 function(data) {
                  processcart(data)
             }
             );

             }

           )
         }
}


$.getJSON( "/s/cart.json?collection=vivo-lilliput", function( data ) {
  console.log(data);
    processcart(data);
},"jsonp");
