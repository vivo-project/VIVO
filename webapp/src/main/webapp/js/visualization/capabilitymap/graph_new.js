/**
 * Global variables
 */
var g; // global graph variable
var queryQueue = [];
var fullResultsQueue = [];
var updatedPeople = [];
var hidden = true;
var detailsPane;
var progressBar;
var expandLastQuery = 0;
var vis;
var force;
var force2;
var nodedict = {};
var linkdict = {};
var labelAnchorDict = {};
var labelAnchorLinkDict = {};
var demos = [[
    "telecommunications", '"grid computing"','"community resilience"',
    '"emergency response"','"disaster management"','"natural disaster"',
    '"natural hazard"','"incident response"',"tsunami","fire","bushfire",
    '"environmental risk"',"earthquake", "flood", "cyclone",
    '"environmental stress"','"spatial data"','"environmental law"'
]];
if (!console) var console = {
    log : function() {}
};
var schemes = {
    "white" : {
        "backgroundcolor" : "#FFFFFF",
        "capabilitycolor" : "#FFA500",
        "nodestroke" : "#FFFFFF",
        "fontcolor" : "#555",
        "linkcolor" : "#B8B8B8",
        "gradient" : function(percent) {
            return percent
        }
    },
    "black" : {
        "backgroundcolor" : "#333333",
        "capabilitycolor" : "#FFA500",
        "nodestroke" : "#333333",
        "fontcolor" : "#FFF",
        "linkcolor" : "#777",
        "gradient" : function(percent) {
            return -percent + 125;
        }
    }
};
var scheme = schemes["white"];

/**
 * The Capability prototype represents a search term. It does not directly store the results of the
 * search term, rather,the top-level unit is a group of People (see the Graph prototype).
 */
var Capability = function(term, cutoff, numpeople) {
    this.term = term;
    this.cutoff = cutoff;
    this.numpeople = numpeople;
}

/**
 * An individual person and their information.
 */
var Person = function(info) {
    this.id = info["md_1"];
    this.setInfo(info);
}
Person.prototype.fullname = function() {
    return this.info["md_A"] + " " + this.info["md_B"] + ", " + this.info["md_Z"];
}
Person.prototype.queryText = function(capabilities) {
    return ("\"" + this.info["md_A"] + "+" + this.info["md_B"] + "\"+[" + capabilities.map(function(a) { return decodeURIComponent(a.term); }).join("+") + "]").replace(/<\/?strong>/g, "");
}
Person.prototype.setInfo = function(info) {
    this.info = info;
    if (this.info["md_Z"] === undefined) { this.info["md_Z"] = ""; } else { this.info["md_Z"] = this.info["md_Z"].replace(/^.*\|/g, ""); } // stop things like "PROFTAYLOR|PROF"
    if (this.info["md_A"] === undefined) { this.info["md_A"] = ""; } else { this.info["md_A"] = this.info["md_A"].replace(/<\/?strong>/g, ""); }
    if (this.info["md_B"] === undefined) { this.info["md_B"] = ""; } else { this.info["md_B"] = this.info["md_B"].replace(/<\/?strong>/g, ""); }
    if (this.info["md_4"] === undefined) { this.info["md_4"] = ""; }
    this.fullInfo = {};
}

/**
 * A group of people, and their capabilities.
 */
var Group = function(g, c, people) {
    this.graph = g;
    this.people = people;
    this.capabilities = c;
}
/**
 * Merge a group with a new capability. This function is called on every group whenever a new
 * capability is added, in order to `distribute' the new people among existing groups where applicable.
 */
Group.prototype.merge = function(capability, people) {
    var that = this; // scoping trick...
    var common = this.people.filter(function(a) { return people.indexOf(a) != -1; }); // this.people & people
    var unmerged = people.filter(function(a) { return that.people.indexOf(a) == -1; });
    this.people = this.people.filter(function(a) { return people.indexOf(a) == -1; });
    if (!this.people.length) this.graph.removeGroup(this);
    if (common.length) g.addGroup(new Group(this.graph, this.capabilities.concat(capability), common));
    return unmerged;
}
/**
 * Removes a person from the group. Will destroy the group if the last person is removed.
 */
Group.prototype.removePerson = function(person) { 
    this.people = this.people.filter(function(a) {
        return (a != person && a.id != person.id);
    });
    delete this.graph.people[person.id];
    if (!this.people.length) this.graph.removeGroup(this);
}

/**
 * And finally, the Graph prototype. This represents all the data associated with the capability map,
 * including search terms ("capabilities") as well as people. The representation is hierarchical,
 * with the data structure being composed of multiple Groups, with each group containing some
 * capabilities. This way, the graph itself is completely oblivious to what capabilities exist in it,
 * and must ask the groups about it when necessary. Nevertheless, everything is passed by reference
 * and so equality between groups and capabilities will always hold.
 */
var Graph = function() {
    this.groups = [];
    this.people = {};
}
Graph.prototype.addGroup = function(g) {
    this.groups.push(g);
}
Graph.prototype.createPerson = function(info) {
    if (info["md_1"] in this.people) return this.people[info["md_1"]];
    var p = new Person(info);
    this.people[p.id] = p;
    return p;
}
Graph.prototype.addCapability = function(c, people) {
    if ((people = this.groups.reduce(function(ps, group) {
        return group.merge(c, ps);
    }, people)).length) this.groups.push(new Group(this, [c], people));
}
Graph.prototype.hasCapability = function(term) {
    return this.getCapabilities().reduce(function(acc, v) {
        return acc || v.term == term;
    }, false);
}
Graph.prototype.getCapabilities = function() {
    return this.groups.reduce(function(capabilities, group) {
        return capabilities.concat(group.capabilities.reduce(function(cs, c) {
            if (capabilities.indexOf(c) == -1) return cs.concat(c);
            else return cs;
        }, []));
    }, []);
}
Graph.prototype.removeGroup = function(group) {
    var that = this;
    $.each(group.people, function(key, person) { delete that.people[person.id]; });
    this.groups = this.groups.filter(function(a) { return a != group; });
}
Graph.prototype.removeCapability = function(term) {
    var i = 0;
    while (i < g.groups.length) {
        var oldlen = g.groups[i].capabilities.length;
        g.groups[i].capabilities = g.groups[i].capabilities.filter(function(a) {
            return (a.term != term);
        });
        if (g.groups[i].capabilities.length != oldlen) {
            var cs = {};
            var shifted = false;
            $.each(g.groups[i].capabilities, function(key, c) {
                cs[c.term] = c;                
            });
            consideringGroups: for (var j = 0; j < g.groups.length; j++) {
                if (j == i) continue;
                if (g.groups[j].capabilities.length != g.groups[i].capabilities.length) continue;
                for (var k = 0; k < g.groups[j].capabilities.length; k++) {
                    if (!(g.groups[j].capabilities[k].term in cs)) continue consideringGroups;
                }
                g.groups[j].people = g.groups[j].people.concat(g.groups[i].people);
                shifted = true;
                break;
            }
            if (shifted || !g.groups[i].capabilities.length) {
                g.groups[i].people = [];
                this.removeGroup(g.groups[i]);
                i--;
            }
        }
        i++;
    }
}
Graph.prototype.toDOT = function() {
    var nodes = "";
    var edges = "";
    var cs = this.getCapabilities();
    $.each(cs, function(key, c) {
        var term = decodeURIComponent(c.term).replace(/"/g, "\\\"");
        nodes += "    \"" + term + "\" [label=\"" + term + "\", shape=\"square\"];\n";
    });
    this.groups.forEach(function(group, i) {
        nodes += "    G" + i + " [label=\"" + group.people.length + "\"];\n";
        $.each(group.capabilities, function(key, c) {
            edges += "    \"" + decodeURIComponent(c.term).replace(/"/g, "\\\"") + "\" -- G" + i + ";\n";
        });
    });
    return "graph G {\n" + nodes + edges + "}\n";
}
Graph.prototype.tod3 = function() {
    var d3_graph = {"nodes" : [], "labelAnchors" : [], "labelAnchorLinks" : [], "links" : []};
    $.each(g.groups, function(i, group) {
        var k = i;// + " " + group.people.length;
        var node;
        if (nodedict[k]) {
            node = nodedict[k];
            node["value"] = group.people.length;
            node["numcaps"]  = group.capabilities.length;
            node["label"] = group.people.length == 1 ? group.people[0].info["md_B"] : group.people.length;
        } else {
            node = {
                "uid" : Math.random(),
                "identifier" : i,
                "label" : group.people.length == 1 ? group.people[0].info["md_B"] : group.people.length,
                "nodetype" : "group",
                "value" : group.people.length,
                "numcaps" : group.capabilities.length
            };
            nodedict[k] = node;
        }
        d3_graph.nodes.push(node);
        if (!labelAnchorDict[i]) {
            labelAnchorDict[i] = [
                {"uid" : Math.random(), "node" : node}, {"uid" : Math.random(), "node" : node}
            ];
        }
        d3_graph.labelAnchors.push(labelAnchorDict[i][0]);
        d3_graph.labelAnchors.push(labelAnchorDict[i][1]);
    });
    var capabilities = g.getCapabilities();
    $.each(capabilities, function(i, capability) {
        var node;
        if (nodedict[capability.term]) node = nodedict[capability.term];
        else {
            node = {
                "uid" : Math.random(),
                "identifier" : capability.term,
                "label" : decodeURI(capability.term).replace(/\"/g, ""),
                "nodetype" : "capability",
                "value" : capability.numpeople
            };
            nodedict[capability.term] = node;
        }
        d3_graph.nodes.push(node);
        if (!labelAnchorDict[capability.term]) {
            labelAnchorDict[capability.term] = [
                {"uid" : Math.random(), "node" : node}, {"uid" : Math.random(), "node" : node}
            ];
        }
        d3_graph.labelAnchors.push(labelAnchorDict[capability.term][0]);
        d3_graph.labelAnchors.push(labelAnchorDict[capability.term][1]);
    });
    $.each(d3_graph.nodes, function(i, node) {
        var k = d3_graph.labelAnchors[i * 2]["node"]["identifier"];// + " " + d3_graph.labelAnchors[i * 2 + 1]["node"]["label"];
        console.log(k);
        if (!labelAnchorLinkDict[k]) labelAnchorLinkDict[k] =
            {
                "uid" : Math.random(),
                source : d3_graph.labelAnchors[i * 2],
                target : d3_graph.labelAnchors[i * 2 + 1],
                weight : 1
            };
        else {
            labelAnchorLinkDict[k]["source"] = d3_graph.labelAnchors[i * 2];
            labelAnchorLinkDict[k]["target"] = d3_graph.labelAnchors[i * 2 + 1];
        }
        d3_graph.labelAnchorLinks.push(labelAnchorLinkDict[k]);
    });
    $.each(g.groups, function(i, group) {
        $.each(group.capabilities, function(j, c) {
            var k = (i + " ") + (g.groups.length + capabilities.indexOf(c));
            console.log(k);
            if (!linkdict[k]) linkdict[k] = {"uid" : Math.random()};
            linkdict[k]["source"] = d3_graph.nodes[i];
            linkdict[k]["target"] = d3_graph.nodes[g.groups.length + capabilities.indexOf(c)];
            linkdict[k]["weight"] = 1 - 1 / (0.01 + Math.pow(2, group.people.length));
            linkdict[k]["value"] = group.people.length;
            d3_graph.links.push(linkdict[k]);
        });
    });
    return d3_graph;
}
Graph.prototype.export = function() {
    return JSON.stringify(this, function(k, v) {
        if (k == "graph" || k == "fullInfo") return;
        else return v;
    });
}
Graph.prototype.toPersonList = function() {
    var list = "";
    $.each(this.people, function(id, person) {
         list += person.info["md_1"] + "|";
    });
    return list;
}
Graph.import = function(jsonstr) {
    var g = new Graph();
    var json = JSON.parse(jsonstr);
    var capabilities = {};
    $.each(json.people, function(i, person) { g.people[i] = new Person(person.info) });
    $.each(json.groups, function(key, group) {
        var cs = [];
        var ps = [];
        $.each(group.capabilities, function(key, c) {
            if (!(c.term in capabilities)) capabilities[c.term] = c;
            cs.push(capabilities[c.term]);
        });
        $.each(group.people, function(key, person) { ps.push(g.people[person.id]); });
        var newgroup = new Group(g, cs, ps);
        g.groups.push(newgroup);
    });
    return g;
}

/**
 * The ProgressBar prototype manages the functionality of a visual progress bar on the page.
 * Each copy of the ProgressBar represents a single bar on the page, which can be progressed and reset.
 */
var ProgressBar = function(bar, length) {
    this.bar = bar;
    this.length = length;
    this.reset();
}
ProgressBar.prototype.reset = function(length, stage) {
    if (length != undefined) this.length = length;
    this.filled = 0;
    this.bar.css({"width" : "0%", "background-color" : (stage == 1 ? "#99C" : "#C96")});
    this.bar.empty();
}
ProgressBar.prototype.progress = function(amount, callback, text) {
    var that = this;
    this.filled += amount || 1;
    this.bar.html("Loaded " + text + "&hellip;");
    this.bar.css("width", Math.round(this.filled / this.length * 100) + "%");
    if (that.filled >= that.length) this.reset(0, 0);
    callback();
}

/**
 * A FullResultQueryUnit is generated and queued when the full results for a person
 * needs to be retrieved.
 */
var FullResultQueryUnit = function(capabilities, person) {
    this.capabilities = capabilities;
    this.person = person;
}
FullResultQueryUnit.prototype.fetch = function() {
    var jsonurl = contextPath + "/visualizationAjax?vis=capabilitymap&person=" + encodeURI(this.person.id)  + "&callback=ipretFullResults";
    var request = new JSONscriptRequest(jsonurl);
    request.buildScriptTag();
    request.addScriptTag();

/*
    TODO - create a new endpoint
 var query = this.person.queryText(this.capabilities);
 query = encodeURI(query);
    var jsonurl = contextPath + "/search.html?collection=unimelb-researchers&type.max_clusters=40&&topic.max_clusters=40&form=faeJSON&query=" + query + "&num_ranks=1&callback=ipretFullResults"
    var request = new JSONscriptRequest(jsonurl);
    request.buildScriptTag();
    request.addScriptTag();
*/
} 

var showPanel = function(name) {
    $(".titles li").removeClass("activeTab");
    $(".titles li a[href=#" + name + "]").parent().addClass("activeTab");
    $(".result_section").css("display", "none");
    $("#" + name).css("display", "block");
}
/**
 * The DetailsPanel prototype controls the display of information in the sidebar.
 */
var DetailsPanel = function(element) {
    this.panel = element;   
}
DetailsPanel.prototype.clearDetails = function() {
    $(this.panel).empty();
}
DetailsPanel.prototype.showDetails = function(mode, id) {
    showPanel("logg");
    
    var that = this;
    var departments = {};
    var deptNames = [];
    var title;
    if (mode != "group") {
        $(this.panel)
            .empty()
            .append(title = $("<h2>Term: " + decodeURIComponent(id) + "</h2>")
                .bind("click", function() {
                    highlight(id);
                    detailsPane.showDetails(mode, id);
                })
                .css("cursor", "pointer")
                .prepend($("<span/>").addClass("orange-square"))
            )
            .append($("<button>Remove capability</button>")
                .bind("click", function() {
                    g.removeCapability(id);
                    that.clearDetails();
                    render();
                })
            )
            .append($("<span> </span>"))
            .append($("<button>Expand</button>")
                .bind("click", function() {
                    expandLastQuery = 1;
                    addKwd(decodeURIComponent(id));
                })
            ).append(
                g.groups.reduce(function(div, group, i) {
                    if (group.capabilities.map(function(c) { return c.term; }).indexOf(id) != -1) {
                        $.each(group.people, function(key, person) {
                            person.info["md_4"].split("|").forEach(function(i) {
                                if (i !== undefined && i != "") {
                                    if (!departments[i]) {
                                        departments[i] = 0;
                                        deptNames.push(i);
                                    }
                                    departments[i]++;
                                }
                            });
                        });
                        return div.append(that.groupInfo(i, group, mode, id));
                    } else return div;
                }, $("<div/>"))
            );
            title.after(DetailsPanel.makebarchart(deptNames, departments));
    } else $(this.panel).empty().append(this.groupInfo(id, g.groups[id], mode, id));
} 
DetailsPanel.prototype.groupInfo = function(i, group, mode, id) {
    var that = this;
    var departments = {};
    var deptNames = [];
    $.each(group.people, function(key, person) {
        person.info["md_4"].split("|").forEach(function (i) {
            if (i !== undefined && i != "") {
                if (!departments[i]) {
                    departments[i] = 0;
                    deptNames.push(i);
                }
                departments[i]++;
            }
        });
    });
    return $("<div/>")
        .append(
            $("<h2>" + "Group: " + group.capabilities.map(function(c) {
                return decodeURIComponent(c.term);
            }).join(", ") + "</h2>")
                .bind("click", function() {
                    highlight(i);
                    detailsPane.showDetails("group", i);
                })
                .css("cursor", "pointer")
                .prepend($("<span/>").addClass("blue-circle"))
        )
        .append($("<button>Remove group</button>")
            .bind("click", function() {
                g.removeGroup(group);
                that.clearDetails();
                render();
            })
        )
        .append(DetailsPanel.makebarchart(deptNames, departments))
        .append(
            group.people.reduce(function(div, p, i) {
                return div
                    .append($("<div>")
                        .addClass("person_details")
                        .append(!p.info["md_3"] ? $("<span/>") : ($("<img/>")
                            .attr("src", contextPath + p.info["md_3"])
                            .attr("width", 50)
                            .css({"float" : "right", "margin-top" : "10px", "clear" : "both"}))
                        )
                        .append($("<h3/>")
                            .css({"clear" : "none"})
                            .append($("<a>" + p.fullname() + "</a>")
                                .attr("href", contextPath + "/display?uri=" + encodeURI(p.id))
                                .attr("target", "_blank")
                            )
                            .append($("<span> </span>"))
                            .append($("<a>[X]</a>")
                                .css("cursor", "pointer")
                                .bind("click", function(k) {
                                    return function() {
                                        group.removePerson(group.people[k]);
                                        render();
                                        that.showDetails(mode, id);
                                    }
                                }(i))
                            )
                        )
                        .append($("<p>" + p.info["md_4"].replace(/\|/g, " / ") + "</p>").css("font-style", "italic"))
                        .append(DetailsPanel.makeslidedown(p.queryText(group.capabilities), p.fullInfo["md_8"], "grants"))
                        .append(DetailsPanel.makeslidedown(p.queryText(group.capabilities), p.fullInfo["md_U"], "publications"))

                        
                        
                    )
                
            }, $("<div/>"))
        );
}
DetailsPanel.makeslidedown = function(q, l, name) {
    return l != undefined
        ? $("<p>Matching " + name + ": " + l.length + " </p>")
            .append($("<button>+</button>")
                .bind("click", function() {
                    if ($(this).html() == "+") {
                        $(this).parent().children("ul").slideDown();
                        $(this).html("-");
                    } else {
                        $(this).parent().children("ul").slideUp();
                        $(this).html("+");
                    }
                })
            )
            .append((l || []).slice(0, 5).reduce(function(list, grant) {
                return list.append($("<li>" + grant + "</li>"));
            }, $("<ul/>")
                .addClass("publist").css("display", "none"))
                .append(l.length > 5 
                    ? $("<li/>")
                    /*.append(
                        $("<a>(view more)</a>")
                        .attr("href", contextPath + "?query=" + encodeURI(q))
                        .attr("target", "_blank")
                    )*/
                    : ""
                )
            )
        : null
}
DetailsPanel.makebarchart = function(deptNames, departments) {
    var n = 0;
    for (i in departments) n = Math.max(n, departments[i]);
    var div = $("<p/>").addClass("barchart");
    $.each(deptNames.sort(function(a, b) { return departments[b]-departments[a]; }), function(x, i) {
        var percent = Math.ceil(departments[i]/n * 99);
        if (percent < 0.2 * 99) return;
        div
            .append($("<span/>")
                .addClass("bar")
                .css({"width" : percent + "%", "margin-right" : "-" + percent + "%"})
            )
            .append($("<strong>" + i.replace(/<\/?strong>/g, "").substr(0,37) + (i.replace(/<\/?strong>/g, "").length > 37 ? "&hellip;" : "") + ": </strong>")
                .attr("title", i)
            )
            .append($("<span>" + departments[i] + "</span>"))
            .append($("<br/>"));
    });
    return div;
};
            
var loadCapability = function() {
    if (hidden) unhide();
    if (!queryQueue.length) finish();
    else {
        disableSubButton(); 
        var query = queryQueue.pop();
        var jsonurl = contextPath + "/visualizationAjax?vis=capabilitymap&query=" + encodeURIComponent(query) + "&callback=ipretResults"
        var request = new JSONscriptRequest(jsonurl);
        request.buildScriptTag();
        request.addScriptTag();
    }
}
var addKwd = function(kwd) {
    if (kwd !== false) {
        if (!kwd) window.location.hash = encodeURI(queryElem.value + "|" + queryCutoffElem.value + "|" + expandLastQuery);
        queryQueue.push(kwd || queryElem.value);
    }
    loadCapability();
}
var ipretResults = function(results) {
    if (progressBar === undefined) {
        progressBar = new ProgressBar($("#progressbar"));
    }

    var resultlist = results["results"];
    if (!resultlist.length || resultlist[0]["md_1"] === undefined) enableSubButton();
    else {
        var term = resultlist[0]["query"];
        if (!g.hasCapability(term)) {
            var c = new Capability(term, queryCutoffElem.value, resultlist.length);
            var people = [];
            for (var i = 0; i < Math.min(queryCutoffElem.value, resultlist.length); i++) {
                if (resultlist[i]["md_1"] == undefined) continue;
                var person = g.createPerson(resultlist[i]);
                people.push(person);
                updatedPeople.push(person.id);
            }
            g.addCapability(c, people, resultlist.length);
        }
        if (expandLastQuery && resultlist[0]["clusters"]) {
            expandLastQuery = 0;
            resultlist[0]["clusters"].forEach(function(term) {
                queryQueue.push(decodeURIComponent(term));
            });
            progressBar.reset(resultlist[0]["clusters"].length, 1);
        }
    }
    progressBar.progress(1, loadCapability, decodeURI(term || ""));
}

var disableSubButton = function() {
    subButton.disabled = true;
    $("#sExpand").attr("disabled", true);
    $("#resetButton").val("Stop");
}
var enableSubButton = function() {
    subButton.disabled = false;
    $("#sExpand").attr("disabled", false);
    $("#resetButton").val("Reset");
}
var getLinkColor = function() {
    var linkColor = $("#linkColor").val();
    if (linkColor !== undefined) {
        return linkColor;
    }

    return "#B8B8B8";
}
var render = function() {
    if (!force) $("#infovis").empty();
    //if (!g.groups.length) return;
    
    if (force) force.stop();
    
    var d3_graph = g.tod3();
    var nodes = d3_graph.nodes;
    var labelAnchors = d3_graph.labelAnchors;
    var labelAnchorLinks = d3_graph.labelAnchorLinks;
    var links = d3_graph.links;
    var delta = !!force;
    
    
    var w = $("#infovis").width(), h = 600;
    if (schemes[$("#colorScheme").val()] !== undefined && scheme != schemes[$("#colorScheme").val()]) {
        scheme = schemes[$("#colorScheme").val()];
        $("#linkColor").val(scheme["linkcolor"]);
    }
    if (!delta) {
        var outer = d3.select("#infovis").append("svg:svg").attr("width", w).attr("height", h);
        var rescale = function() { vis.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")"); }
        vis = outer
            .append('svg:g')
                .call(d3.behavior.zoom().scaleExtent([1, 5]).on("zoom", rescale))
                .on("dblclick.zoom", null)
            .append("svg:g")
                .on("mousedown", function() {
                    vis.call(d3.behavior.zoom().scaleExtent([1, 5]).on("zoom", rescale))
                });
        vis.append('svg:rect')
            .attr('width', w).attr('height', h).attr('fill', scheme["backgroundcolor"])
            .on("click", unhighlight)
            .on("touchstart", unhighlight);
        
        edge_layer = vis.append("svg:g");
        node_layer = vis.append("svg:g");//.attr('width', w).attr('height', h).attr("fill", "transparent");
        label_layer = vis.append("svg:g");

        var graph_gravity = parseInt($("#graph_gravity").val());
        var graph_charge = parseInt($("#graph_charge").val());
        var graph_linkdistance = parseInt($("#graph_linkdistance").val());

        if (isNaN(graph_gravity)) { graph_gravity = 1; }
        if (isNaN(graph_charge)) { graph_charge = -1500; }
        if (isNaN(graph_linkdistance)) { graph_linkdistance = 40; }

        force = d3.layout
            .force().size([w, h])
                .gravity(graph_gravity)
                .charge(graph_charge)
                .linkDistance(graph_linkdistance)
                .linkStrength(function(x) {
                    return x.weight * 10
                });
        
        force2 = d3.layout
            .force().gravity(0)
            .linkDistance(0).linkStrength(8).charge(-100).size([w, h]);
    }

    transformto(force.nodes(), nodes);
    transformto(force.links(), links);
    transformto(force2.nodes(), labelAnchors);
    transformto(force2.links(), labelAnchorLinks);
    
    if (delta) {
        force.alpha(0.05);
        force2.alpha(0.05);
    }
    force.start();
    force2.start();
    
    link_data = edge_layer.selectAll("line.link").data(links, function(d) { return d.uid; });
    link_data.enter().append("svg:line")
        .attr("class", "link")
        .style("stroke", getLinkColor())
        .style("stroke-opacity", ".6")
        .style("stroke-width", function(d) {
            return Math.sqrt(d.value) + 1;
        });
    link_data.exit().remove();
    link = edge_layer.selectAll("line.link");
    
    var node_data = node_layer.selectAll("g.node").data(force.nodes(), function(d) { return d.uid; });
    var node = node_data.enter().append("svg:g");
    console.log("nodes:");
    console.log(node);
    console.log(node_data.exit());
    node
        .attr("class", "node")
        .style("cursor", "pointer")
        .on("click", function(d) {
            highlight(d.identifier);
            detailsPane.showDetails(d.nodetype, d.identifier);
        })
        .on("touchstart", function(d) {
            highlight(d.identifier);
            detailsPane.showDetails(d.nodetype, d.identifier);        
        })
        .style("stroke", scheme["nodestroke"])
        .style("stroke-width", 2)
        .each(function(d, i) {
            d3.select(this).selectAll("*").remove();
            if (d.nodetype == "group") d3.select(this).append("svg:circle");
            else d3.select(this).append("svg:rect");
            if (d.nodetype == "group") {
                d3.select(this).select("circle")
                    .attr("r", function(d) {
                        return 4 * d.value / queryCutoffElem.value + 4;
                    })
                    .style("fill", function(d) {
                        var p = (85 - Math.min(1, d.numcaps / 4) * 50);
                        var l = scheme["gradient"](p);
                        var c = "hsl(240, " + (85 - p) + "%, " + l + "%)";
                        return c;
                    });
            } else {
                var dim = 2 * (Math.sqrt(d.value / 10) + 5);
                d3.select(this).select("rect")
                    .attr("width", dim).attr("height", dim)
                    .attr("transform", "translate(-" + dim / 2 + ", -" + dim / 2 + ")")
                    .style("fill", scheme["capabilitycolor"]);
            }
        })
        .call(force.drag);
    node_data.exit().remove();
    node = d3.selectAll("g.node");
    
    var anchorLink = label_layer.selectAll("line.anchorLink").data(labelAnchorLinks, function(d) { return d.uid; });
    anchorLink.exit().remove();
    
    var anchor_data = label_layer.selectAll("g.anchorNode").data(force2.nodes(), function(d) { return d.uid; });
    var anchorNode = anchor_data.enter().append("svg:g").attr("class", "anchorNode");
    anchorNode.append("svg:circle").attr("r", 0).style("fill", "#FFF");
    anchorNode.append("svg:text")
        .style("fill", scheme["fontcolor"])
        .style("font-family", "Arial")
        .style("font-size", 10)
        .style("cursor", "pointer")
        .style("font-weight", function(d) { return d.node.nodetype == "group" ? "normal" : "bold"})
        .attr("class", function(d) { return "label-" + d.node.nodetype })
        .style("text-shadow", function(d) {
            return d.node.nodetype == "group" ? "none" :
                "2px 0px " + scheme["nodestroke"] +
                ", -2px 0px " + scheme["nodestroke"] + 
                ", 0px 2px " + scheme["nodestroke"] +
                ", 0px -2px " + scheme["nodestroke"];
        }).on("click", function(d) {
            highlight(d.node.identifier);
            detailsPane.showDetails(d.node.nodetype, d.node.identifier);
        });
    label_layer.selectAll("g.anchorNode").each(function(d, i) {
    	d3.select(this).select("text").text(i % 2 == 0 ? "" : d.node.label)
    });
    anchor_data.exit().remove();
    anchorNode = label_layer.selectAll("g.anchorNode");
    
    var updateLink = function() {
    	this.attr("x1", function(d) {
    		return d.source.x;
    	}).attr("y1", function(d) {
    		return d.source.y;
    	}).attr("x2", function(d) {
    		return d.target.x;
    	}).attr("y2", function(d) {
    		return d.target.y;
    	});
    
    }
    var updateNode = function() {
    	this.attr("transform", function(d) {
    		return "translate(" + d.x + "," + d.y + ")";
    	});
    
    }
    force.on("tick", function() {
        $("#log button:first-child").html("pause");
    	force2.start();
    	node.call(updateNode);
    	anchorNode.each(function(d, i) {
    		if(i % 2 == 0) {
    			d.x = d.node.x;
    			d.y = d.node.y;
    		} else {
    			var b = this.childNodes[1].getBBox();
    			var diffX = d.x - d.node.x;
    			var diffY = d.y - d.node.y;
    			var dist = Math.sqrt(diffX * diffX + diffY * diffY);
    			var shiftX = b.width * (diffX - dist) / (dist * 2);
    			shiftX = Math.max(-b.width, Math.min(0, shiftX));
    			var shiftY = 5;
    			this.childNodes[1].setAttribute("transform", "translate(" + shiftX + "," + shiftY + ")");
    		}
    	});
    	anchorNode.call(updateNode);
    	link.call(updateLink);
    	anchorLink.call(updateLink);
    
    });
    
    // refresh UI
    $("#log").empty().append($("<button>pause</button>")
        .bind("click", function() {
            if ($(this).html() != "resume") {
                $(this).html("resume");
                force.stop();
                force2.stop();
            } else {
                $(this).html("pause");
                force.resume();
                force2.resume();
            }
        })
    ).append(" ").append($("<button>hide group labels</button>")
        .bind("click", function() {
            if ($(this).html() != "show group labels") {
                $(this).html("show group labels");
                $(".label-group").css("visibility", "hidden");
            } else {
                $(this).html("hide group labels");
                $(".label-group").css("visibility", "visible");
            }
        })
    );
    $("#log_printout").empty().append($("<button>Delete selected</button>").bind("click", function() {
        $("input[type=checkbox]:checked").each(function() {
            g.removeCapability($(this).attr("name"));
            $(this).parent().remove();
        });
        render();
    }));
    $.each(g.getCapabilities(), function(i, c) {
        $("#log_printout")
            .append($("<li/>")
                .append($("<a> " + decodeURI(c.term) + "</a>")
                    .bind("click", function() {
                        highlight(c.term);
                        detailsPane.showDetails("capability", c.term);
                    })
                    .css("cursor", "pointer")
                )
                .prepend($("<input/>").attr("type", "checkbox")
                    .attr("name", c.term)                   
                )
            );
    });
}
var highlight = function(identifier) {
    unhighlight();
    d3.selectAll("g.node").each(function(d, i) {
        if (d.identifier == identifier) {
            var that = this;
            d3.select(this).select("*").style("stroke", "#6C9");
            d3.selectAll("line.link").each(function(d, i) {
                var nodeid = d3.select(that).datum().identifier;
                if (d.source.identifier == nodeid || d.target.identifier == nodeid) {
                    var strokewidth = parseFloat(d3.select(this).style("stroke-width").replace(/px/g, ""));
                    d3.select(this).style("stroke", "#6C9")
                        .style("stroke-width", (strokewidth + 2) + "px")
                        .style("stroke-opacity", 1);
                }
            });
        }
    });
}
var unhighlight = function() {
    detailsPane.clearDetails();
    d3.selectAll("g.node").each(function(d, i) {
        d3.select(this).select("*").style("stroke", scheme["nodestroke"]);
    });
    d3.selectAll("line.link").each(function(d, i) {
        //console.log(d.source.identifier);
        //console.log(d.target.identifier);
        //console.log(nodeid);
        console.log(d3.select(this).style("stroke"));
        if (d3.select(this).style("stroke") == "#66cc99" || d3.select(this).style("stroke") == "rgb(102, 204, 153)") {
            var strokewidth = parseFloat(d3.select(this).style("stroke-width").replace(/px/g, ""));
            d3.select(this).style("stroke", getLinkColor())
                .style("stroke-width", (strokewidth - 2) + "px")
                .style("stroke-opacity", 0.6);
        }
    });
}

var generateGraphPersonList = function() {
    $("#graphDetails").attr("value", g.toPersonList());
}
var generateGraphSVG = function() {
    download(
        "<?xml version=\"1.0\" standalone=\"no\"?>\n" + 
        "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" " +  
        "\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
        $("#infovis").html().replace("<svg", "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"")
    , "svg");
}
var importGraphDetails = function() {
    g = Graph.import($("#graphDetails").attr("value"));
    render();
}
var download = function(content, ext) {
    $("#download").attr("action", "http://115.146.84.185/search/download.php?ext=" + ext);
    $("#exportContent").val(content);
    $("#download").submit();
}
var showhideadvanced = function(button) {
    if ($("#advanced_options").data("shown") != true) {
        $("#advanced_options").slideDown();
        $("#advanced_options").data("shown", true);
        $(button).html("Hide advanced");
    } else {
        $("#advanced_options").slideUp();
        $("#advanced_options").data("shown", false);        
        $(button).html("Show advanced");
    }
}
var restoreDefaults = function() {
    $("#colorScheme").val("white");
    $("#graph_gravity").val("1");
    $("#graph_charge").val("-1500");
    $("#graph_linkdistance").val("40");
    $("#linkColor").val("#B8B8B8");
}
var finish = function() {
    render();
    showPanel("demo");
    enableSubButton();
    fullResultsQueue = [];
    $.each(g.groups, function(key, group) {
        $.each(group.people, function(key, person) {
            if (updatedPeople.indexOf(person.id) != -1)
                fullResultsQueue.push(new FullResultQueryUnit(group.capabilities, person));
        })
    });
    updatedPeople = [];
    progressBar.reset(fullResultsQueue.length, 2);
    retrieveFullResults();
}

var retrieveFullResults = function() {
    if (fullResultsQueue.length) fullResultsQueue.pop().fetch();
}
var ipretFullResults = function(result) {
    if (g.people[result["results"][0]["md_1"]] != undefined) { // otherwise reset
        g.people[result["results"][0]["md_1"]].setInfo(result["results"][0]);
        g.people[result["results"][0]["md_1"]].fullInfo = result["results"][0];
        progressBar.progress(1, retrieveFullResults, g.people[result["results"][0]["md_1"]].info["md_B"]);
    }
}

var reset = function() {
    if (queryQueue.length) queryQueue = []; // stop
    else {
        hidden = true;
        g = new Graph();
        force = undefined;
        $("#infovis").html("");
        window.location.hash = "";
        
        $("#log_printout").empty();
        detailsPane.clearDetails();
        $("#graphDetails").attr("value", "");
        $(".result_section").each(function() {
            $(this).html($(this).data("original"));
        });
        $("#resetButton").attr("disabled", "disabled");
        $("#container").css({"box-shadow" : "none", "height" : "auto"});
        $("#container").animate({"width" : "940px", "margin-left" : "0px"}, 500);
        $("#center-container").fadeOut();
        $("#helptext").fadeIn();
    }
    render();
    
}
var unhide = function() {
    hidden = false;
    $("#resetButton").removeAttr("disabled");
    if ($(window).width() > 1230) {
        $("#container").css("box-shadow", "0px 0px 20px -6px #000000");
        $("#container").animate({"height" : "600px", "width" : "1200px", "margin-left" : "-130px"}, 500);
        $("#center-container").css("width", "900px");
    } else {
        $("#container").animate({"height" : "600px"});
        $("#center-container").css("width", "640px");
    }
    $("#helptext").fadeOut();
    $("#center-container").fadeIn();
}
   
function run_demo(demoValues) {
    demoValues.forEach(function(query) {
        queryQueue.push(query); 
    });
    progressBar.reset(demoValues.length, 1);
    addKwd(false);
}

var queryKeyDown = function(e) {
    e.cancelBubble = true;
    if(e.which === 13 || e.keyCode === 13) {
        subButton.click();
        e.returnValue = false;
        e.cancel = true;
        return false;
    }
    return true;
}

$(document).ready(function() {
    // elements global variables
    subButton = document.getElementById('add');
    queryElem = document.getElementById('query');
    queryCutoffElem = document.getElementById('queryCutoff');
    detailsPane = new DetailsPanel('#inner-details');
    g = new Graph();
    
    // results section text backup
    $(".result_section").each(function() {
        $(this).data("original", $(this).html());
    });
    
    // querycutoffelem hadling
    $(queryCutoffElem).bind("keyup", function() {
        var that = this;
        console.log($(this).val());
        if ($(this).data("prev") != $(this).val() && $("#infovis").html() != "") {
            $("#cutofflabel").empty().append($("<img/>")
                .attr("src", contextPath + "/images/visualization/capabilitymap/refresh.png")
                .bind("click", function() {
                    $.each(g.getCapabilities(), function(i, c) {
                        if (c.cutoff != queryCutoffElem.value) {
                            g.removeCapability(c.term);
                            queryQueue.push(decodeURI(c.term));
                        }
                    });
                    $(this).unbind("click");
                    $(this).parent().html("Cutoff:");
                    progressBar.reset(queryQueue.length, 1);
                    addKwd(false);
                    return false;
                })
            );
            setTimeout(function() {
                $("#cutofflabel img").unbind("click");
                $("#cutofflabel").html("Cutoff:");
            }, 5000);
        }
        $(this).data("prev", $(this).val());
    });
    $(queryCutoffElem).data("prev", $(queryCutoffElem).val());
    
    // URL hash reading
    if (window.location.hash != "") {
        var preset = decodeURI(window.location.hash).slice(1).split("|");
        queryElem.value = preset[0];
        queryCutoffElem.value = preset[1];
        if (preset[2] == "1") expandLastQuery = 1;
        addKwd();
    }
    
    // queryfield
    $("#query").bind("focus", function() {
        $(this).data("previous", $(this).val());
        $(this).val("");
    });
    $("#query").bind("blur", function() {
        if ($(this).val() == "") $(this).val($(this).data("previous"));
        else $(this).data("previous", $(this).val());
    });
    $("#query").focus();
    enableSubButton();
    
    // tabs
    $(".tabs div ul li + li + li + li + li").parent().children(":last-child").find("a").trigger("click");
    $(".tabs ul.titles li[class!=\"full\"]").bind("click", function(e) {
        $(this).parent().children("li").removeClass("activeTab");
        $(this).addClass("activeTab");
        $(this).parent().parent().find("div .result_section").css("display", "none");
        $("#" + $(this).find("a").attr("href").slice(1)).css("display", "block");
        return false;
        });
    $(".tabs ul li:first-child").trigger("click");
});

var transformto = function(a, b) { // a = b
    $.each(b, function(i, link) {
        if (a.indexOf(link) == -1) a.push(link);
    });
    var i = 0;
    while (i < a.length) {
        if (b.indexOf(a[i]) == -1) a.splice(i, 1);
        else i++;
    }
    
}
            
