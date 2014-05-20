/*! kibana - v3.0.0 - 2014-05-20
 * Copyright (c) 2014 Rashid Khan; Licensed Apache License */

define("css-embed",function(){function a(a){var b=document.getElementsByTagName("head")[0],c=document.createElement("style"),d=document.createTextNode(a);c.type="text/css",c.styleSheet?c.styleSheet.cssText=d.nodeValue:c.appendChild(d),b.appendChild(c)}return a}),define("css!panels/query/query.css",["css-embed"],function(a){return a(".short-query{display:inline-block;margin-right:10px}.short-query input.search-query{width:280px}.begin-query{position:absolute;left:10px;top:5px}.end-query{position:absolute;right:10px;top:5px}.end-query i,.begin-query i{margin:0}.panel-query{padding-left:25px!important;height:31px!important;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box}.query-disabled{opacity:.3}.form-search:hover .has-remove{padding-left:40px!important}.remove-query{opacity:0}.last-query{padding-right:45px!important}.form-search:hover .remove-query{opacity:1}.query-panel .pinned{margin-right:5px}"),!0}),define("panels/query/module",["angular","app","lodash","css!./query.css"],function(a,b,c){var d=a.module("kibana.panels.query",[]);b.useModule(d),d.controller("query",["$scope","querySrv","$rootScope","dashboard","$q","$modal",function(a,b,d,e,f,g){a.panelMeta={status:"Stable",description:"Manage all of the queries on the dashboard. You almost certainly need one of these somewhere. This panel allows you to add, remove, label, pin and color queries"};var h={query:"*",pinned:!0,history:[],remember:10};c.defaults(a.panel,h),a.querySrv=b,a.dashboard=e,a.queryTypes=b.types;var i=g({template:"./app/panels/query/helpModal.html",persist:!0,show:!1,scope:a});a.init=function(){},a.refresh=function(){j(c.pluck(a.dashboard.current.services.query.list,"query")),e.refresh()},a.render=function(){d.$broadcast("render")},a.toggle_pin=function(a){e.current.services.query.list[a].pin=e.current.services.query.list[a].pin?!1:!0},a.queryIcon=function(a){return b.queryTypes[a].icon},a.queryConfig=function(a){return"./app/panels/query/editors/"+(a||"lucene")+".html"},a.queryHelpPath=function(a){return"./app/panels/query/help/"+(a||"lucene")+".html"},a.queryHelp=function(b){a.help={type:b},f.when(i).then(function(a){a.modal("show")})},a.typeChange=function(a){var c={id:a.id,type:a.type,query:a.query,alias:a.alias,color:a.color};e.current.services.query.list[c.id]=b.defaults(c)};var j=function(b){if(a.panel.remember>0){a.panel.history=c.union(b.reverse(),a.panel.history);var d=a.panel.history.length;d>a.panel.remember&&(a.panel.history=a.panel.history.slice(0,a.panel.remember))}};a.init()}])});