/*! kibana - v3.1.0 - 2014-07-07
 * Copyright (c) 2014 Rashid Khan; Licensed Apache License */

define("panels/derivequeries/module",["angular","app","lodash"],function(a,b,c){var d=a.module("kibana.panels.derivequeries",[]);b.useModule(d),d.controller("derivequeries",["$scope",function(a){a.panelMeta={status:"Deprecated",description:"This panel has been replaced with the 'topN' mode in the query pull down."};var b={loading:!1,label:"Search",query:"*",ids:[],field:"_type",fields:[],spyable:!0,rest:!1,size:5,mode:"terms only",exclude:[],history:[],remember:10};c.defaults(a.panel,b),a.init=function(){a.editing=!1}}])});