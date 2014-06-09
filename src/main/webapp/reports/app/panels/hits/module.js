/*! kibana - v3.0.0 - 2014-06-09
 * Copyright (c) 2014 Rashid Khan; Licensed Apache License */

define("panels/hits/module",["angular","app","lodash","jquery","kbn","jquery.flot","jquery.flot.pie"],function(a,b,c,d,e){var f=a.module("kibana.panels.hits",[]);b.useModule(f),f.controller("hits",["$scope","querySrv","dashboard","filterSrv",function(b,d,e,f){b.panelMeta={modals:[{description:"Inspect",icon:"icon-info-sign",partial:"app/partials/inspector.html",show:b.panel.spyable}],editorTabs:[{title:"Queries",src:"app/partials/querySelect.html"}],status:"Stable",description:"The total hits for a query or set of queries. Can be a pie chart, bar chart, list, or absolute total of all queries combined"};var g={style:{"font-size":"10pt"},arrangement:"horizontal",chart:"bar",counter_pos:"above",donut:!1,tilt:!1,labels:!0,spyable:!0,queries:{mode:"all",ids:[]}};c.defaults(b.panel,g),b.init=function(){b.hits=0,b.$on("refresh",function(){b.get_data()}),b.get_data()},b.get_data=function(g,h){if(delete b.panel.error,b.panelMeta.loading=!0,0!==e.indices.length){var i=c.isUndefined(g)?0:g,j=b.ejs.Request().indices(e.indices[i]);b.panel.queries.ids=d.idsByMode(b.panel.queries);var k=d.getQueryObjs(b.panel.queries.ids);c.each(k,function(a){var c=b.ejs.FilteredQuery(d.toEjsObj(a),f.getBoolFilter(f.ids()));j=j.facet(b.ejs.QueryFacet(a.id).query(c)).size(0)}),b.inspector=a.toJson(JSON.parse(j.toString()),!0);var l=j.doSearch();l.then(function(a){if(b.panelMeta.loading=!1,0===i&&(b.hits=0,b.data=[],h=b.query_id=(new Date).getTime()),!c.isUndefined(a.error))return void(b.panel.error=b.parse_error(a.error));if(b.query_id===h){var d=0;c.each(k,function(e){var f=a.facets[e.id],g=c.isUndefined(b.data[d])||0===i?f.count:b.data[d].hits+f.count;b.hits+=f.count,b.data[d]={info:e,id:e.id,hits:g,data:[[d,g]]},d++}),b.$emit("render"),i<e.indices.length-1&&b.get_data(i+1,h)}})}},b.set_refresh=function(a){b.refresh=a},b.close_edit=function(){b.refresh&&b.get_data(),b.refresh=!1,b.$emit("render")}}]),f.directive("hitsChart",["querySrv",function(b){return{restrict:"A",link:function(f,g){function h(){g.css({height:f.row.height});try{c.each(f.data,function(a){a.label=a.info.alias,a.color=a.info.color})}catch(a){return}try{"bar"===f.panel.chart&&(f.plot=d.plot(g,f.data,{legend:{show:!1},series:{lines:{show:!1},bars:{show:!0,fill:1,barWidth:.8,horizontal:!1},shadowSize:1},yaxis:{show:!0,min:0,color:"#c8c8c8"},xaxis:{show:!1},grid:{borderWidth:0,borderColor:"#eee",color:"#eee",hoverable:!0},colors:b.colors})),"pie"===f.panel.chart&&(f.plot=d.plot(g,f.data,{legend:{show:!1},series:{pie:{innerRadius:f.panel.donut?.4:0,tilt:f.panel.tilt?.45:1,radius:1,show:!0,combine:{color:"#999",label:"The Rest"},stroke:{width:0},label:{show:f.panel.labels,radius:2/3,formatter:function(a,b){return"<div ng-click=\"build_search(panel.query.field,'"+a+'\') "style="font-size:8pt;text-align:center;padding:2px;color:white;">'+a+"<br/>"+Math.round(b.percent)+"%</div>"},threshold:.1}}},grid:{hoverable:!0,clickable:!0},colors:b.colors}))}catch(a){g.text(a)}}f.$on("render",function(){h()}),a.element(window).bind("resize",function(){h()});var i=d("<div>");g.bind("plothover",function(a,b,c){if(c){var d="bar"===f.panel.chart?c.datapoint[1]:c.datapoint[1][0][1];i.html(e.query_color_dot(c.series.color,20)+" "+c.series.label+" ("+d.toFixed(0)+")").place_tt(b.pageX,b.pageY)}else i.remove()})}}}])});