

links = $('.individual-urls').find('a');
for (k = 0; k < links.length; k++)
{

  if (links[k].href.includes("figshare"))
     {//console.log(links[k].href);
      var figsharecontent;
      var figshareyql = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%27"+ encodeURI(links[k].href)+"%27%20and%20xpath%3D%27%2F%2Fscript%5B%40id%20%3D%20%22app-data%22%5D%27&format=json";
      $.getJSON(figshareyql,
                function(res) { figsharecontent = res.query.results.script.content;
                                //console.log(figsharecontent);

                                figItems = JSON.parse(figsharecontent).items.items;
                                for (i = 0; i < figItems.length; i++) {
                                     // console.log(figItems[i].data.id);
                                     $.getJSON("https://api.figshare.com/v2/articles/"+figItems[i].data.id,
                                      function(res) {
                                            //console.log(res);
                                            tags = res.tags;
                                           //console.log(tags);
                                            for (j = 0; j < tags.length; j++) {
                                                // console.log(tags[i]);
                                                if (tags[j] == "Quantum Statistical Mechanics"){
                                                    $('<div/>', {
                                                        id:       'showcase',
                                                        class: 'embed-responsive embed-responsive-4by3'
                                                    }).appendTo('#overview');
                                                    $('<iframe/>', {
                                                          src: 'https://widgets.figshare.com/articles/'+res.id+'/embed?show_title=1',
                                                          class: 'embed-responsive-item',
                                                          frameborder: '0'
                                                    }).appendTo('#showcase');
                                                 }
                                            }
                                      }, "jsonp");
                                 }
                  }, "jsonp" );


    }
   if (links[k].href.includes("theconversation"))
   {
       $('#individualNavTabs').append($('<li/>',{id: 'theConversationTab'}));
       $('#theConversationTab').append( $('<a/>', {
             id:       'theConversationTab',
             href:  '#theConversation',
             'aria-controls': 'The Conversation',
             class: 'tab-pane',
             role: 'tab',
             'data-toggle': 'tab',
             text: 'The Conversation'
             })
        );
       $('#individualNavTabsContent').append('<div id="theConversation" class="tab-pane" role="tabpanel">' +
                              '<div class="panel panel-default">' +
                                      '<div class="panel-heading">' +
                                              '<h3 class="panel-title">The Conversation</h3>' +
                                      '</div>'+
                                      '<div class="panel-body">'+
                                              '<div class="row">'+
                                                      '<div id="rssResults" class="col-xs-12 col-md-8"></div>'+
                                                      '<div id="rssTitles"  class="col-xs-6 col-md-4"></div>'+
                                             '</div>'+
                                        '</div>'+
                                 '</div>'+
                       '</div>');
       var yql = "https://query.yahooapis.com/v1/public/yql?q=select%20title%2C%20%20content%2C%20link%20from%20atom%20where%20url%3D%22" +
                 encodeURI(links[k].href + '/articles.atom')+
                 "%22&format=json&callback=";
       $.getJSON(yql, function(res) {      var entries = [];
                                           if (typeof res.query.results.entry.length == "undefined" ) {entries = [res.query.results.entry]; console.log(entries);}
                                                else entries = res.query.results.entry;
                                           console.log("entries[0]"); console.log(entries[0]);
                                           for (i = 0; i < entries.length; i++) { console.log(i);
                                                 console.log(entries[i].title)
                                                  if ( i== 0 ) {
                                                          $( "#rssResults" ).append( "<h2>"+entries[i].title+"</h2>" );
                                                          $( "#rssResults" ).append( entries[i].content.content );
                                                   }
                                                   var row = $('<div/>',{class:'row'});
                                                   var titleCol = $('<div/>',{class:'col-sm-8'});
                                                   titleCol.append( "<p><a href='"+entries[i].link.href+"'>"+ entries[i].title + "</a></p>" );
                                                   row.append(titleCol);
                                                   var altmetricCol =$('<div/>',{class:'col-sm-4'});
                                                   $('<div/>', {
                                                                'data-badge-popover': 'left',
                                                                'data-badge-type': 'donut',
                                                                'data-uri': entries[i].link.href,
                                                                'data-hide-no-mentions': 'true',
                                                                class: 'altmetric-embed'
                                                                  }).appendTo(altmetricCol);
                                                                 row.append(altmetricCol);
                                                                 row.appendTo("#rssTitles");
                                                           }
                                                  _altmetric_embed_init();
                      }, "jsonp");



   }

}
