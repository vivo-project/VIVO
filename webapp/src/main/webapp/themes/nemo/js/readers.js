
doi = $('.altmetric-embed').filter(":first").attr("data-doi")

if (doi.includes('figshare')){
    article_id = doi.split('.')[3]
    figsharewidget = '<iframe src="https://widgets.figshare.com/articles/' + article_id +'/embed?show_title=1" class="embed-responsive-item" frameborder="0"></iframe>'
     $(".nav-tabs").children().filter(':last').after($('<li/>',{id: 'figshareTab'}));
       $('#figshareTab').append( $('<a/>', {
             id:       'figshareaTab',
             href:  '#figshare',
             'aria-controls': 'Figshare',
             class: 'tab-pane',
             role: 'tab',
             'data-toggle': 'tab',
             text: 'Figshare'
             })
        );
       $(".tab-content").children().filter(':last').after('<div id="figshare" class="tab-pane" role="tabpanel">' +
                              '<div class="panel panel-default">' +
                                      '<div class="panel-heading">' +
                                              '<h3 class="panel-title">Figshare</h3>' +
                                      '</div>'+
                                      '<div class="panel-body">'+
                                              '<div class="row">'+
                                                      '<div id="figContent" class="embed-responsive embed-responsive-4by3">' + 
                                                          figsharewidget +
                                                      '</div>'+
                                             '</div>'+
                                        '</div>'+
                                 '</div>'+
                       '</div>');
      $( "#figshareTab" ).click(function() {
              $('iframe').each(function() {$(this).attr('src', $(this).attr('src'));});
      });
      $('.nav a[href="#' + "figshare" + '"]').tab('show');
    }

$.getJSON( "https://api.crossref.org/v1/works/http://dx.doi.org/"+doi, 
           function(data)

                {
                  readcubeWidget =  '<iframe src="https://www.readcube.com/articles/' + doi + '?ssl=1" class="embed-responsive-item"></iframe>'
                  readcube = 0; 
                  publisher = data.message.publisher;
                  console.log(publisher);
                  if (publisher.includes('PLoS') || publisher.includes('PeerJ') ||publisher.includes('Nature') || publisher.includes('Wiley') || publisher.includes('Springer') ){
                      $(".nav-tabs").children().filter(':last').after($('<li/>',{id: 'readcubeTab'}));
                      $('#readcubeTab').append( $('<a/>', {
                          id:       'readcubeTab',
                           href:  '#readcube',                    
                          'aria-controls': 'Readcube',
                          class: 'tab-pane',
                          role: 'tab',
                          'data-toggle': 'tab',
                          text: 'Readcube'
                          })
                       );
                    $(".tab-content").children().filter(':last').after('<div id="readcube" class="tab-pane" role="tabpanel">' +
                              '<div class="panel panel-default">' +
                                      '<div class="panel-heading">' +
                                              '<h3 class="panel-title">Readcube</h3>' +
                                      '</div>'+
                                      '<div class="panel-body">'+
                                              '<div class="row">'+
                                                      '<div id="readcubeContent" class="embed-responsive embed-responsive-4by3">' + 
                                                          readcubeWidget +
                                                      '</div>'+
                                             '</div>'+
                                        '</div>'+
                                 '</div>'+
                       '</div>');
                   $( "#readcubeTab" ).click(function() {
                   $('iframe').each(function() {$(this).attr('src', $(this).attr('src'));});
                   });
                   $('.nav a[href="#' + "readcube" + '"]').tab('show');
                  }
                
                }
      
         )
