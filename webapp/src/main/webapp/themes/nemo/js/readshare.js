// Code for putting Readcube and Figshare Badges where appropriate

$('div[data-doi]').each(function( index ) {
   doi = $( this ).attr("data-doi"); 
   $( this ).parent().prev().append('<span>'+doi+'&nbsp;</span>')  
   if (doi.includes('figshare')){
          $( this ).parent().prev().append('<span><img src="https://web-static.figshare.com/assets/ef6e8637cc252600e22cd4c1d755b4b771120789/public/global/favicon/favicon-32x32.png" width="20"/></span>')
       }   
 
    $.getJSON( "https://api.crossref.org/v1/works/http://dx.doi.org/"+doi,
           function(data)
                {
                  publisher = data.message.publisher;
                  if (publisher.includes('PLoS') || publisher.includes('PeerJ') || publisher.includes('Nature') || publisher.includes('Wiley') || publisher.includes('Springer') ){
                    
                    $('div[data-doi="'+data.message.DOI+'"]').parent().prev().append('<span><img src="https://reader.readcube.com/v3/images/logos/readcube.10c141c8.svg"/></span>')
                    console.log($('div[data-doi="'+data.message.DOI+'"]').parent().prev())                  
                  }

                }
         );
});

