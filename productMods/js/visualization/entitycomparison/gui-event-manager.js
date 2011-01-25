/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function() {
        
        /* This is used to cache the current state whether the user is allowed to select more entities from 
        the datatable or not. Once Max number of entity selection is reached the user can no longer select 
        more & this variable will be set to false. */
        $("#datatable").data("isEntitySelectionAllowed", true);
        
        $("#organizationLabel").text(organizationLabel).css("color", "#2485ae");
        $("#organizationMoniker").text(organizationLabel);
        $("#organizationMoniker").attr("href", organizationVIVOProfileURL);
        
        $notificationContainer = $("#notification-container").notify();
        
        var jsonObject = {
                prepare : function(arg1){
                    loadData(arg1);
                }
            };
        
        graphContainer = $("#graphContainer");
        tableDiv = $('#paginatedTable');

        // initial display of the grid when the page loads
        init(graphContainer);
        

        //click event handler for clear button
        $("a.clear-selected-entities").click(function(){
            clearRenderedObjects();
        }); 
        
        /*
         * When the intra-entity parameters are clicked,
         * update the status accordingly.   
         */         
        
        $("select.comparisonValues").change(function(){
        
            var selectedValue = $("select.comparisonValues option:selected").val();
            
            var selectedParameter;
            
            $.each(COMPARISON_PARAMETERS_INFO, function(index, parameter) {
            	
                if (parameter.value === selectedValue) {
                	selectedParameter = parameter;
                    window.location = parameter.viewLink;
                }
            	
            });
            
            $("#body").empty().html("Loading " + selectedValue + " ...");
            
            /*
             * This piece of code is not executed at all because the redirect happens before there is a chance 
             * to render the below contents.
             * */
            
            /*
            
            $("#comparisonParameter").text("Total Number of " + selectedValue);
            $('#yaxislabel').html("Number of " + selectedValue).mbFlipText(false);
            $('#yaxislabel').css("color", "#595B5B");
            $('#comparisonHeader').html(selectedValue).css('font-weight', 'bold');
            
            
            */
        
        });
        
        $("input[type=checkbox].easyDeselectCheckbox").live('click', function(){
            
            var checkbox = $(this);
            var checkboxValue = $(this).attr("value");
            var linkedCheckbox = labelToCheckedEntities[checkboxValue];
            var entityToBeRemoved = labelToEntityRecord[checkboxValue];

            if(!checkbox.is(':checked')){
                //console.log("Easy deselect checkbox is unclicked!");
                updateRowHighlighter(linkedCheckbox);
                removeUsedColor(entityToBeRemoved);
                removeEntityUnChecked(renderedObjects, entityToBeRemoved);                          
                removeLegendRow(linkedCheckbox);
                removeCheckBoxFromGlobalSet(linkedCheckbox);
                $(linkedCheckbox).attr('checked', false);
                checkIfColorLimitIsReached();
                displayLineGraphs();
                updateCounter();                
            }
        });
                            
        //parse the json object and pass it to loadData
        jsonObject.prepare(jQuery.parseJSON(jsonString));
        
        function performEntityCheckboxUnselectedActions(entity, checkboxValue, checkbox) {
            
            removeUsedColor(entity);
            removeEntityUnChecked(renderedObjects, entity);
            removeLegendRow(checkbox);
            removeCheckBoxFromGlobalSet(checkbox);
            
            checkbox.closest("tr").removeClass('datatablerowhighlight');
        
        }
        
        function performEntityCheckboxSelectedActions(entity, checkboxValue, checkbox) {
                    
            getNextFreeColor(entity);

            //Generate the bar, checkbox and label for the legend.
            createLegendRow(entity, $("#bottom"));

            renderLineGraph(renderedObjects, entity);
            labelToCheckedEntities[checkboxValue] = checkbox;
            
            /*
             * To highlight the rows belonging to selected entities. 
             * */
            checkbox.closest("tr").addClass('datatablerowhighlight');
                    
        }
        
        function performEntityCheckboxClickedRedrawActions() {
        
            setTickSizeOfAxes();
            checkIfColorLimitIsReached();
            displayLineGraphs();
            updateCounter();
        
        }
        
        /* 
         *  function to populate the labelToEntityRecord object with the
         *  values from the json file and
         *  dynamically generate checkboxes
         */
        function loadData(jsonData) {
        
            // var yearRange;
            $.each(jsonData, function (index, val) {
                setOfLabels.push(val.label);
                labelToEntityRecord[val.label] = val;
            });
        
            getEntityVisMode(jsonData);
            prepareTableForDataTablePagination(jsonData);
            setEntityLevel();
        
            $(".disabled-checkbox-event-receiver").live("click", function () {
        
                if ($(this).next().is(':disabled')) {
        
                    createNotification("warning-notification", {
                        title: 'Error',
                        text: 'A Maximum 10 entities can be compared. Please remove some & try again.'
                    }, {
                        custom: true,
                        expires: false
                    });
        
                }
        
            });
            
                    /*
                     * When the elements in the paginated div
                     * are clicked this event handler is called
                     */
            $("input.if_clicked_on_school").live('click', function () {
        
                var checkbox = $(this);
                var checkboxValue = $(this).attr("value");
                var entity = labelToEntityRecord[checkboxValue];
                
                if (checkbox.is(':checked')) {
                
                    performEntityCheckboxSelectedActions(entity, checkboxValue, checkbox);
                
                } else {
                
                    performEntityCheckboxUnselectedActions(entity, checkboxValue, checkbox);        
        
                }
                
                performEntityCheckboxClickedRedrawActions();
        
            });
        }

        /*
        This will make sure that top 3 entites are selected by default when the page loads.
        
        */      
        $.each($("input.if_clicked_on_school"), function(index, checkbox) {
                
                    if (index > 2) {
                        return false;
                    }
                
                    $(this).attr('checked', true);
                    
                    var checkboxValue = $(this).attr("value");
                    var entity = labelToEntityRecord[checkboxValue];
                    
                    performEntityCheckboxSelectedActions(entity, checkboxValue, $(this));
                    
                    performEntityCheckboxClickedRedrawActions();
                    
                });

});