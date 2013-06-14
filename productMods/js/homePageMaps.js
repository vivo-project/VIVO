/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    
    var globalMapBuilt = false;
    var usMapBuilt = false;
    var stateMapBuilt = false;
    var researchAreas = { "type": "FeatureCollection", "features": []};
    
    $.extend(this, urlsBase);
    $.extend(this, i18nStrings);
    
    getGeoJsonForMaps();
    
    $('a#globalLink').click(function() {
        buildGlobalMap();
        $(this).addClass("selected");
        $('a#usLink').removeClass("selected");
        $('a#nyLink').removeClass("selected");
    });

    $('a#usLink').click(function() {
        buildUSMap();
        $(this).addClass("selected");
        $('a#globalLink').removeClass("selected");
        $('a#nyLink').removeClass("selected");
    });

    $('a#stateLink').click(function() {
        buildStateMap();
        $(this).addClass("selected");
        $('a#usLink').removeClass("selected");
        $('a#globalLink').removeClass("selected");
    });
    
    function getLatLong(country) {
        var lat = [];
        latLongJson.map(function (json) {
            if ( json.name == country) {
                lat.push(json.data["longitude"]);
                lat.push(json.data["latitude"]);
            }
        });
        if (lat.length == 0) {
            lat.push(0.0);
            lat.push(0.0);
        }
        return(lat);
    }

    function getMapType(country) {
        var mt = "";
        latLongJson.map(function (json) {
            if ( json.name == country) {
                mt = json.data["mapType"];
            }
        });
        return(mt);
    }

    function getGeoClass(country) {
        var gc = "";
        latLongJson.map(function (json) {
            if ( json.name == country) {
                gc = json.data["geoClass"];
            }
        });
        return(gc);
    }

	function onEachFeature(feature, layer) {
		var popupContent = "";
        var uri = "";
        
		if (feature.properties && feature.properties.popupContent) {
			popupContent += feature.properties.popupContent;
		}
		if (feature.properties && feature.properties.html) {
		    if ( feature.properties.html == "1") {
			    popupContent += ": " + feature.properties.html + " " + i18nStrings.researcherString;
			}
		    else {
			    popupContent += ": " + feature.properties.html + " " + i18nStrings.researchersString;
			}
		}
        layer.on('mouseover', function(e) {
            e.target.bindPopup(popupContent,{closeButton:false}).openPopup();
        });
        layer.on('mouseout', function(e) {
            e.target.closePopup();
        });

        if (feature.properties && feature.properties.uri) {
            uri += feature.properties.uri;
            layer.on('click', function(e) {
                document.location.href = urlsBase + "/individual?uri=" + uri + "&#map";
            });
        }
	}

	function getDivIcon(feature) {
		var htmlContent = "";
		var myIcon;

		if (feature.properties && feature.properties.html) {
			htmlContent += feature.properties.html;
		}
		if ( htmlContent > 99 ) {
		    myIcon = L.divIcon({className: 'divIconCountPlus', html: htmlContent});
	    }
	    else {
		    myIcon = L.divIcon({className: 'divIconCount', html: htmlContent});
		}
		return myIcon;
	}

	function getMarkerRadius(feature) {
		var radiusContent;

		if (feature.properties && feature.properties.radius) {
			radiusContent = feature.properties.radius;
		}
		return radiusContent;
	}

	function getMarkerFillColor(feature) {
		var geoClass = "";
		var fillColor;

		if (feature.properties && feature.properties.radius) {
			geoClass = feature.properties.geoClass;
		}
		if ( geoClass == "region") {
		    fillColor = "#abf7f8"; 
		}
		else {
		    fillColor = "#fdf9cd"
		}
		return fillColor;
	}

    function checkGlobalCoordinates(feature, layer) {
        var theLatLng = new L.LatLng(feature.geometry.coordinates[0],feature.geometry.coordinates[1]);
        var mt = feature.properties.mapType;
        if ( !theLatLng.equals([0,0]) && mt == "global" ) {
		    return true;
		}
		return false;
	}

    function checkUSCoordinates(feature, layer) {
        var theLatLng = new L.LatLng(feature.geometry.coordinates[0],feature.geometry.coordinates[1]);
        var mt = feature.properties.mapType;
        if ( !theLatLng.equals([0,0]) && mt == "US" ) {
		    return true;
		}
		return false;
	}

    function checkStateCoordinates(feature, layer) {
        var theLatLng = new L.LatLng(feature.geometry.coordinates[0],feature.geometry.coordinates[1]);
        var mt = feature.properties.mapType;
        if ( !theLatLng.equals([0,0]) && mt == "state" ) {
		    return true;
		}
		return false;  
	}

    function buildGlobalMap() {
        $('div#mapGlobal').show();
        $('div#mapUS').hide();
        $('div#mapState').hide();
        
        if ( !globalMapBuilt ) {
        
            var mapGlobal = L.map('mapGlobal').setView([25.25, 23.20], 2);
                L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile\/{z}\/{y}\/{x}.png', {
    		        maxZoom: 12,
    		        minZoom: 1,
    		        boxZoom: false,
			        doubleClickZoom: false,
    		        attribution: 'Tiles &copy; <a href="http://www.esri.com/">Esri</a>'
            }).addTo(mapGlobal);

	        L.geoJson(researchAreas, {
    		
		        filter: checkGlobalCoordinates,
		        onEachFeature: onEachFeature,

			    pointToLayer: function(feature, latlng) {
		            return L.circleMarker(latlng, {
        		        radius: getMarkerRadius(feature),
        		        fillColor: getMarkerFillColor(feature), 
        		        color: "none",
        		        weight: 1,
        		        opacity: 0.8,
        		        fillOpacity: 0.8
        	        });
        	    }       		
            }).addTo(mapGlobal);

    	    L.geoJson(researchAreas, {

		        filter: checkGlobalCoordinates, 
    	        onEachFeature: onEachFeature,

		        pointToLayer: function(feature, latlng) {
		            return L.marker(latlng, {
				        icon: getDivIcon(feature)
			        });
			    }	
            }).addTo(mapGlobal);
            
            globalMapBuilt = true;
        }
    
        getResearcherCount("global");
        appendLegendToLeafletContainer();
    } // Canvas/World_Light_Gray_Base

    function buildUSMap() {
        $('div#mapGlobal').hide();
        $('div#mapState').hide();
        $('div#mapUS').show();
        
        if ( !usMapBuilt ) {

            var mapUS = L.map('mapUS').setView([46.0, -97.0], 3);
                L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile\/{z}\/{y}\/{x}.png', {
        		maxZoom: 30,
        		minZoom: 1,
        		boxZoom: false,
        		zIndex: 1,
    			doubleClickZoom: false,
        		attribution: 'Tiles &copy; <a href="http://www.esri.com/">Esri</a>'
            }).addTo(mapUS);

        	L.geoJson(researchAreas, {

    		    filter: checkUSCoordinates,
    		    onEachFeature: onEachFeature,

    			pointToLayer: function(feature, latlng) {
    		        return L.circleMarker(latlng, {
            		    radius: getMarkerRadius(feature),
            		    fillColor: "#fdf9cd", //fdf38a", 
            		    color: "none",
            		    weight: 1,
            		    opacity: 0.8,
            		    fillOpacity: 0.8
            	    });
            	}       		
            }).addTo(mapUS);

        	L.geoJson(researchAreas, {

    		    filter: checkUSCoordinates, 
        	    onEachFeature: onEachFeature,

    		    pointToLayer: function(feature, latlng) {
    		        return L.marker(latlng, {
    				    icon: getDivIcon(feature)
    			    });
    			}	
            }).addTo(mapUS);
            
            usMapBuilt = true;
        }
            
        getResearcherCount("US");
    } // Canvas/World_Light_Gray_Base - services/Reference/World_Boundaries_and_Places_Alternate/MapServer

    function buildStateMap() {
        $('div#mapGlobal').hide();
        $('div#mapUS').hide();
        $('div#mapState').show();
        
        if ( !stateMapBuilt ) {
            
            // CHANGE THE setView COORDINATES SO THAT THE STATE YOU WANT TO DISPLAY IS CENTERED CORRECTLY.
            // THE COORDINATES BELOW ARE FOR NEW YORK.
            var mapState = L.map('mapState').setView([42.83, -75.50], 7);
                L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile\/{z}\/{y}\/{x}.png', {
        		maxZoom: 12,
        		minZoom: 1,
        		boxZoom: false,
    			doubleClickZoom: false,
        		attribution: 'Tiles &copy; <a href="http://www.esri.com/">Esri</a>'
            }).addTo(mapState);

            L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/Reference/World_Boundaries_and_Places_Alternate/MapServer/tile\/{z}\/{y}\/{x}.png', {
		        maxZoom: 12,
		        minZoom: 1,
		        boxZoom: false,
		        doubleClickZoom: false
            }).addTo(mapState);

        	L.geoJson(researchAreas, {

    		    filter: checkStateCoordinates,
    		    onEachFeature: onEachFeature,

    			pointToLayer: function(feature, latlng) {
    		        return L.circleMarker(latlng, {
            		    radius: getMarkerRadius(feature) + 3,
            		    fillColor: "#fdf9cd", 
            		    color: "none",
            		    weight: 1,
            		    opacity: 0.8,
            		    fillOpacity: 0.8
            	    });
            	}       		
            }).addTo(mapState);

        	L.geoJson(researchAreas, {

    		    filter: checkStateCoordinates, 
        	    onEachFeature: onEachFeature,

    		    pointToLayer: function(feature, latlng) {
    		        return L.marker(latlng, {
    				    icon: getDivIcon(feature)
    			    });
    			}	
            }).addTo(mapState);
                        
            stateMapBuilt = true;
        }

        getResearcherCount("state");
    }

    function getGeoJsonForMaps() {
        $.ajax({
            url: urlsBase + "/homePageAjax",
            dataType: "json",
            data: {
                action: "getGeoFocusLocations",
            },
            complete: function(xhr, status) {
                
                var results = $.parseJSON(xhr.responseText);
                if ( results.length == 0 ) {
                    var html = i18nStrings.currentlyNoResearchers;
                    $('section#home-geo-focus div#timeIndicator span').html(html);
                    $('section#home-geo-focus').css("height","175px");
                    $('section#home-geo-focus div#timeIndicator').css("margin-top","50px");
                    $('section#home-geo-focus div#mapGlobal').hide();
                    $('section#home-geo-focus div#mapUS').hide();
                    $('section#home-geo-focus div#mapState').hide();
                }
                else {
                    $.each(results, function() {
                        var locale = this.properties.popupContent;
                        this.geometry.coordinates = getLatLong(locale);
                        this.properties.mapType = getMapType(locale);
                        this.properties.geoClass = getGeoClass(locale);
                        researchAreas["features"].push(this);
                    });
                    buildGlobalMap();
                    $('div#timeIndicatorGeo').hide();
                }
            }
       });        
    }

    function getResearcherCount(area) {
        
        var researcherCount = 0;
        var areaCount = 0;
        var text = "";
        if ( area == "global" ) {
            text = " " + i18nStrings.countriesAndRegions;
        }
        else if ( area == "US" ) {
            text = " " + i18nStrings.stateString;
        }
        else {
            text = " " + i18nStrings.statewideLocations;
        }

        $.each(researchAreas.features, function() {
            if ( this.properties.mapType == area ) {
                researcherCount = researcherCount + this.properties.html ;
                areaCount = areaCount + 1; 
            }
        });

        if ( areaCount == 1 && text == " states.") {
            text = " " + i18nStrings.stateString;
        }

        $('div#researcherTotal').html("<font style='font-size:1.05em;color:#167093'>" 
                                        + researcherCount.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2') 
                                        + "</font> " + i18nStrings.researchersInString + " <font style='font-size:1.05em;color:#167093'>" 
                                        + areaCount + "</font>" + text);
    }
    function appendLegendToLeafletContainer() {
        var htmlString = "<div class='leaflet-bottom leaflet-left' style='padding:0 0 8px 12px'><ul><li>"
                        + "<img alt='" + i18nStrings.regionsString + "' src='" + urlsBase 
                        + "/images/map_legend_countries.png' style='margin-right:5px'><font style='color:#555'>" 
                        + i18nStrings.countriesString + "</font></li><li><img alt='" + i18nStrings.regionsString 
                        + "' src='" + urlsBase 
                        + "/images/map_legend_regions.png' style='margin-right:5px'><font style='color:#555'>" 
                        + i18nStrings.regionsString + "</font></li></ul></div>";
        $('div.leaflet-control-container').append(htmlString);       
    }//659667
    
}); 
