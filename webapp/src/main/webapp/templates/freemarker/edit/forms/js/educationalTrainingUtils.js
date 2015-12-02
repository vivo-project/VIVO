/* $This file is distributed under the terms of the license in /doc/license.txt$ */


var educationalTrainingUtils = {

    onLoad: function(href, blankSentinel) {
        this.initObjectReferences();                 
        this.bindEventListeners();
        $.extend(this, customFormData);
    },

    initObjectReferences: function() {
    
        this.form = $('#personHasEducationalTraining');
        this.aDLabel = $('#awardedDegreeLabel');
        this.degreeSelector = $('#degreeUri');
    },
    
    bindEventListeners: function() {
        
        this.degreeSelector.change(function() {
            educationalTrainingUtils.setAwardedDegreeLabel(); 
        });
                        
    },
    
    setAwardedDegreeLabel: function() {
        var degreeLabel = this.subjectName + ": " + this.degreeSelector.find(":selected").text();
        this.aDLabel.val(degreeLabel);
    }
}
