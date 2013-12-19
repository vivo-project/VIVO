/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    
    var globalMapBuilt = false;
    var countryMapBuilt = false;
    var localMapBuilt = false;
    var legendIsVisible = false;
    var researchAreas = { "type": "FeatureCollection", "features": []};
    var geoResearcherCount = "0";
    
    $.extend(this, urlsBase);
    $.extend(this, i18nStrings);
    
    getGeoFocusResearcherCount();
    
    
    $('a#globalLink').click(function() {
        buildGlobalMap();
        $(this).addClass("selected");
        $('a#countryLink').removeClass("selected");
        $('a#localLink').removeClass("selected");
    });

    $('a#countryLink').click(function() {
        buildCountryMap();
        $(this).addClass("selected");
        $('a#globalLink').removeClass("selected");
        $('a#localLink').removeClass("selected");
    });

    $('a#localLink').click(function() {
        buildLocalMap();
        $(this).addClass("selected");
        $('a#countryLink').removeClass("selected");
        $('a#globalLink').removeClass("selected");
    });
    
    function getLatLong(localName,popup) {
        var lat = [];
        jQuery.map(latLongJson, function (json) {
            if ( json.local == localName) {
                lat.push(json.data["longitude"]);
                lat.push(json.data["latitude"]);
            }
        });
        if (lat.length == 0) {
            jQuery.map(latLongJson, function (json) {
                if ( json.name == popup) {
                    lat.push(json.data["longitude"]);
                    lat.push(json.data["latitude"]);
                }
            });
        }
        if (lat.length == 0) {
            lat.push(0.0);
            lat.push(0.0);
        }
        return(lat);
    }

    function getMapType(localName,popup) {
        var mt = "";
        jQuery.map(latLongJson, function (json) {
            if ( json.local == localName) {
                mt = json.data["mapType"];
            }
        });
        if ( mt.length == 0 ) {
            jQuery.map(latLongJson, function (json) {
                if ( json.name == popup) {
                    mt = json.data["mapType"];
                }
            });            
        }
        return(mt);
    }

    function getGeoClass(localName,popup) {
        var gc = "";
        jQuery.map(latLongJson, function (json) {
            if ( json.local == localName) {
                gc = json.data["geoClass"];
            }
        });
        if ( gc.length == 0 ) { 
            jQuery.map(latLongJson, function (json) {
                if ( json.name == popup) {
                    gc = json.data["geoClass"];
                }
            });
        }
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

    function checkCountryCoordinates(feature, layer) {
        var theLatLng = new L.LatLng(feature.geometry.coordinates[0],feature.geometry.coordinates[1]);
        var mt = feature.properties.mapType;
        if ( !theLatLng.equals([0,0]) && mt == "country" ) {
		    return true;
		}
		return false;
	}

    function checkLocalCoordinates(feature, layer) {
        var theLatLng = new L.LatLng(feature.geometry.coordinates[0],feature.geometry.coordinates[1]);
        var mt = feature.properties.mapType;
        if ( !theLatLng.equals([0,0]) && mt == "local" ) {
		    return true;
		}
		return false;  
	}

    function buildGlobalMap() {
        $('div#mapGlobal').show();
        $('div#mapCountry').hide();
        $('div#mapLocal').hide();
        
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
    } 

    function buildCountryMap() {
        $('div#mapGlobal').hide();
        $('div#mapLocal').hide();
        $('div#mapCountry').show();
        
        if ( !countryMapBuilt ) {

            // CHANGE THE setView COORDINATES SO THAT THE COUNTRY YOU WANT TO 
            // DISPLAY IS CENTERED CORRECTLY.  THE COORDINATES BELOW CENTERS THE MAP ON THE U.S.
            var mapCountry = L.map('mapCountry').setView([46.0, -97.0], 3);
            L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile\/{z}\/{y}\/{x}.png', {
        		maxZoom: 30,
        		minZoom: 1,
        		boxZoom: false,
        		zIndex: 1,
    			doubleClickZoom: false,
        		attribution: 'Tiles &copy; <a href="http://www.esri.com/">Esri</a>'
            }).addTo(mapCountry);

        	L.geoJson(researchAreas, {

    		    filter: checkCountryCoordinates,
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
            }).addTo(mapCountry);

        	L.geoJson(researchAreas, {

    		    filter: checkCountryCoordinates, 
        	    onEachFeature: onEachFeature,

    		    pointToLayer: function(feature, latlng) {
    		        return L.marker(latlng, {
    				    icon: getDivIcon(feature)
    			    });
    			}	
            }).addTo(mapCountry);
            
            countryMapBuilt = true;
        }
            
        getResearcherCount("country");
    } 

    function buildLocalMap() {
        $('div#mapGlobal').hide();
        $('div#mapCountry').hide();
        $('div#mapLocal').show();
        
        if ( !localMapBuilt ) {
            
            // CHANGE THE setView COORDINATES SO THAT THE LOCAL AREA (E.G. A STATE OR PROVINCE) YOU WANT TO 
            // DISPLAY IS CENTERED CORRECTLY.  THE COORDINATES BELOW CENTERS THE MAP ON NEW YORK STATE.
            var mapLocal = L.map('mapLocal').setView([42.83, -75.50], 7);
            L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Shaded_Relief/MapServer/tile\/{z}\/{y}\/{x}.png', {
        		maxZoom: 12,
        		minZoom: 1,
        		boxZoom: false,
    			doubleClickZoom: false,
        		attribution: 'Tiles &copy; <a href="http://www.esri.com/">Esri</a>'
            }).addTo(mapLocal);

            L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/Reference/World_Boundaries_and_Places_Alternate/MapServer/tile\/{z}\/{y}\/{x}.png', {
		        maxZoom: 12,
		        minZoom: 1,
		        boxZoom: false,
		        doubleClickZoom: false
            }).addTo(mapLocal);

        	L.geoJson(researchAreas, {

    		    filter: checkLocalCoordinates,
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
            }).addTo(mapLocal);

        	L.geoJson(researchAreas, {

    		    filter: checkLocalCoordinates, 
        	    onEachFeature: onEachFeature,

    		    pointToLayer: function(feature, latlng) {
    		        return L.marker(latlng, {
    				    icon: getDivIcon(feature)
    			    });
    			}	
            }).addTo(mapLocal);
                        
            localMapBuilt = true;
        }

        getResearcherCount("local");
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
                    $('section#home-geo-focus div#timeIndicatorGeo span').html(html);
                    $('section#home-geo-focus').css("height","175px");
                    $('section#home-geo-focus div#timeIndicatorGeo').css("margin-top","50px");
                    $('section#home-geo-focus div#mapGlobal').hide();
                    $('section#home-geo-focus div#mapCountry').hide();
                    $('section#home-geo-focus div#mapLocal').hide();
                    $('section#home-geo-focus div#mapControls').hide();
                }
                else {
                    $.each(results, function() {
                        var popup = this.properties.popupContent;
                        var localName = this.properties.local;
                        this.geometry.coordinates = getLatLong(localName,popup);
                        this.properties.mapType = getMapType(localName,popup);
                        this.properties.geoClass = getGeoClass(localName,popup);
                        researchAreas["features"].push(this);
                    });
                    buildGlobalMap();
                    $('div#timeIndicatorGeo').hide();
                }
            }
       });        
    }

    function getGeoFocusResearcherCount() {
        $.ajax({
            url: urlsBase + "/homePageAjax",
            dataType: "json",
            data: {
                action: "getGeoFocusResearcherCount",
            },
            complete: function(xhr, status) {
                
                var results = $.parseJSON(xhr.responseText);
                // there will only ever be one key/value pair
                if ( results != null ) {
                    geoResearcherCount = results.count;
                }
                getGeoJsonForMaps();
            }
       });        
    }

    function getResearcherCount(area) {
        
        var localResearcherCount = 0;
        var areaCount = 0;
        var displayCount = "";
        var text = "";
        if ( area == "global" ) {
            text = " " + i18nStrings.countriesAndRegions;
        }
        else if ( area == "country" ) {
            text = " " + i18nStrings.statesString;
        }
        else {
            text = " " + i18nStrings.statewideLocations;
        }

        $.each(researchAreas.features, function() {
            if ( this.properties.mapType == area ) {
                localResearcherCount = localResearcherCount + this.properties.html ;
                areaCount = areaCount + 1; 
            }
        });

        if ( areaCount == 1 && text == " states.") {
            text = " " + i18nStrings.stateString;
        }
        
        if ( area == "global" ) {
            if ( geoResearcherCount == 1 ) {
                researcherText = " " + i18nStrings.researcherString + " " + i18nStrings.inString;
            }
            else {
                researcherText = " " + i18nStrings.researchersString + " " + i18nStrings.inString;
            }
            
            displayCount = geoResearcherCount;
        }
        else {
            if ( localResearcherCount == 1 ) {
                researcherText = " " + i18nStrings.researcherString + " " + i18nStrings.inString;
            }
            else {
                researcherText = " " + i18nStrings.researchersString + " " + i18nStrings.inString;
            }
            
            displayCount = localResearcherCount;
        }

        $('div#researcherTotal').html("<font style='font-size:1.05em;color:#167093'>" 
                                        + displayCount 
                                        + "</font> " + researcherText + " <font style='font-size:1.05em;color:#167093'>" 
                                        + areaCount + "</font>" + text);
    }
    function appendLegendToLeafletContainer() {
        if ( !this.legendIsVisible ) {
            var htmlString = "<div class='leaflet-bottom leaflet-left' style='padding:0 0 8px 12px'><ul><li>"
                        + "<img alt='" + i18nStrings.regionsString + "' src='" + urlsBase 
                        + "/images/map_legend_countries.png' style='margin-right:5px'><font style='color:#555'>" 
                        + i18nStrings.countriesString + "</font></li><li><img alt='" + i18nStrings.regionsString 
                        + "' src='" + urlsBase 
                        + "/images/map_legend_regions.png' style='margin-right:5px'><font style='color:#555'>" 
                        + i18nStrings.regionsString + "</font></li></ul></div>";
            $('div.leaflet-control-container').append(htmlString);
            this.legendIsVisible = true;
        }
    }
    
}); 
