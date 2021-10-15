$(document).ready(function() {
    /* Publons author widget
     *
     * Insert the Publons author widget if a publons widget div can be found
     * and it contains what looks to be a Publons id.
     *
     * To enable this in a VIVO instance it is necessary to define a data
     * property with the following attributes:
     *
     *   * Public label: Publons widget
     *   * Property group: [whichever group you want the widget to appear in]
     *   * Internal name: publonsWidget
     *   * Domain class: Person (foaf)
     *   * Range datatype: string
     *
     * A Person will then need to populate the property with their Publons url
     * or id. For example, Andrew Preston would create a Publons Widget property
     * which contains one of:
     *   * https://publons.com/a/1/; or
     *   * 1
     *
     * Code (obviously) requires jQuery.
     */
    var panel = $("#publonsWidget").closest(".panel");
    var body = panel.find(".panel-body");
    var id = body.find(".property-list .list-group-item").text();
    var id_number = /\d+/.exec(id);

    if ( id_number ) {
        body.empty();
        body.html(
            '<iframe src="https://publons.com/author/' +
            id_number +
            '/widget/embed/?width=640&height=460" width="100%" height="460" style="border: 0;"></iframe>'
        );
    }
});
