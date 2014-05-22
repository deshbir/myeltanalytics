/*! kibana - v3.0.0 - 2014-05-22
 * Copyright (c) 2014 Rashid Khan; Licensed Apache License */

(function(){var root=this,timezoneJS;timezoneJS="undefined"!=typeof exports?exports:root.timezoneJS={},timezoneJS.VERSION="0.4.4";for(var $=root.$||root.jQuery||root.Zepto,fleegix=root.fleegix,_arrIndexOf,DAYS=timezoneJS.Days=["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],MONTHS=timezoneJS.Months=["January","February","March","April","May","June","July","August","September","October","November","December"],SHORT_MONTHS={},SHORT_DAYS={},EXACT_DATE_TIME={},TZ_REGEXP=new RegExp("^[a-zA-Z]+/"),i=0;i<MONTHS.length;i++)SHORT_MONTHS[MONTHS[i].substr(0,3)]=i;for(i=0;i<DAYS.length;i++)SHORT_DAYS[DAYS[i].substr(0,3)]=i;_arrIndexOf=Array.prototype.indexOf||function(a){if(null===this)throw new TypeError;var b=Object(this),c=b.length>>>0;if(0===c)return-1;var d=0;if(arguments.length>1&&(d=Number(arguments[1]),d!=d?d=0:0!==d&&1/0!==d&&d!==-1/0&&(d=(d>0||-1)*Math.floor(Math.abs(d)))),d>=c)return-1;for(var e=d>=0?d:Math.max(c-Math.abs(d),0);c>e;e++)if(e in b&&b[e]===a)return e;return-1};var _fixWidth=function(a,b){if("number"!=typeof a)throw"not a number: "+a;var c=a.toString();if(a.length>b)return a.substr(a.length-b,a.length);for(;c.length<b;)c="0"+c;return c},_transport=function(a){if(!(fleegix&&"undefined"!=typeof fleegix.xhr||$&&"undefined"!=typeof $.ajax))throw new Error("Please use the Fleegix.js XHR module, jQuery ajax, Zepto ajax, or define your own transport mechanism for downloading zone files.");if(a){if(!a.url)throw new Error("URL must be specified");return"async"in a||(a.async=!0),a.async?fleegix&&fleegix.xhr?fleegix.xhr.send({url:a.url,method:"get",handleSuccess:a.success,handleErr:a.error}):$.ajax({url:a.url,dataType:"text",method:"GET",error:a.error,success:a.success}):fleegix&&fleegix.xhr?fleegix.xhr.doReq({url:a.url,async:!1}):$.ajax({url:a.url,async:!1}).responseText}};timezoneJS.Date=function(){var a=Array.prototype.slice.apply(arguments),b=null,c=null,d=[];switch("[object Array]"===Object.prototype.toString.call(a[0])&&(a=a[0]),"string"==typeof a[a.length-1]&&TZ_REGEXP.test(a[a.length-1])&&(c=a.pop()),a.length){case 0:b=new Date;break;case 1:b=new Date(a[0]);break;default:for(var e=0;7>e;e++)d[e]=a[e]||0;b=new Date(d[0],d[1],d[2],d[3],d[4],d[5],d[6])}this._useCache=!1,this._tzInfo={},this._day=0,this.year=0,this.month=0,this.date=0,this.hours=0,this.minutes=0,this.seconds=0,this.milliseconds=0,this.timezone=c||null,d.length?this.setFromDateObjProxy(b):this.setFromTimeProxy(b.getTime(),c)},timezoneJS.Date.prototype={getDate:function(){return this.date},getDay:function(){return this._day},getFullYear:function(){return this.year},getMonth:function(){return this.month},getYear:function(){return this.year-1900},getHours:function(){return this.hours},getMilliseconds:function(){return this.milliseconds},getMinutes:function(){return this.minutes},getSeconds:function(){return this.seconds},getUTCDate:function(){return this.getUTCDateProxy().getUTCDate()},getUTCDay:function(){return this.getUTCDateProxy().getUTCDay()},getUTCFullYear:function(){return this.getUTCDateProxy().getUTCFullYear()},getUTCHours:function(){return this.getUTCDateProxy().getUTCHours()},getUTCMilliseconds:function(){return this.getUTCDateProxy().getUTCMilliseconds()},getUTCMinutes:function(){return this.getUTCDateProxy().getUTCMinutes()},getUTCMonth:function(){return this.getUTCDateProxy().getUTCMonth()},getUTCSeconds:function(){return this.getUTCDateProxy().getUTCSeconds()},getTime:function(){return this._timeProxy+60*this.getTimezoneOffset()*1e3},getTimezone:function(){return this.timezone},getTimezoneOffset:function(){return this.getTimezoneInfo().tzOffset},getTimezoneAbbreviation:function(){return this.getTimezoneInfo().tzAbbr},getTimezoneInfo:function(){if(this._useCache)return this._tzInfo;var a;return a=this.timezone?"Etc/UTC"===this.timezone||"Etc/GMT"===this.timezone?{tzOffset:0,tzAbbr:"UTC"}:timezoneJS.timezone.getTzInfo(this._timeProxy,this.timezone):{tzOffset:this.getLocalOffset(),tzAbbr:null},this._tzInfo=a,this._useCache=!0,a},getUTCDateProxy:function(){var a=new Date(this._timeProxy);return a.setUTCMinutes(a.getUTCMinutes()+this.getTimezoneOffset()),a},setDate:function(a){return this.setAttribute("date",a),this.getTime()},setFullYear:function(a,b,c){return void 0!==c&&this.setAttribute("date",1),this.setAttribute("year",a),void 0!==b&&this.setAttribute("month",b),void 0!==c&&this.setAttribute("date",c),this.getTime()},setMonth:function(a,b){return this.setAttribute("month",a),void 0!==b&&this.setAttribute("date",b),this.getTime()},setYear:function(a){return a=Number(a),a>=0&&99>=a&&(a+=1900),this.setUTCAttribute("year",a),this.getTime()},setHours:function(a,b,c,d){return this.setAttribute("hours",a),void 0!==b&&this.setAttribute("minutes",b),void 0!==c&&this.setAttribute("seconds",c),void 0!==d&&this.setAttribute("milliseconds",d),this.getTime()},setMinutes:function(a,b,c){return this.setAttribute("minutes",a),void 0!==b&&this.setAttribute("seconds",b),void 0!==c&&this.setAttribute("milliseconds",c),this.getTime()},setSeconds:function(a,b){return this.setAttribute("seconds",a),void 0!==b&&this.setAttribute("milliseconds",b),this.getTime()},setMilliseconds:function(a){return this.setAttribute("milliseconds",a),this.getTime()},setTime:function(a){if(isNaN(a))throw new Error("Units must be a number.");return this.setFromTimeProxy(a,this.timezone),this.getTime()},setUTCFullYear:function(a,b,c){return void 0!==c&&this.setUTCAttribute("date",1),this.setUTCAttribute("year",a),void 0!==b&&this.setUTCAttribute("month",b),void 0!==c&&this.setUTCAttribute("date",c),this.getTime()},setUTCMonth:function(a,b){return this.setUTCAttribute("month",a),void 0!==b&&this.setUTCAttribute("date",b),this.getTime()},setUTCDate:function(a){return this.setUTCAttribute("date",a),this.getTime()},setUTCHours:function(a,b,c,d){return this.setUTCAttribute("hours",a),void 0!==b&&this.setUTCAttribute("minutes",b),void 0!==c&&this.setUTCAttribute("seconds",c),void 0!==d&&this.setUTCAttribute("milliseconds",d),this.getTime()},setUTCMinutes:function(a,b,c){return this.setUTCAttribute("minutes",a),void 0!==b&&this.setUTCAttribute("seconds",b),void 0!==c&&this.setUTCAttribute("milliseconds",c),this.getTime()},setUTCSeconds:function(a,b){return this.setUTCAttribute("seconds",a),void 0!==b&&this.setUTCAttribute("milliseconds",b),this.getTime()},setUTCMilliseconds:function(a){return this.setUTCAttribute("milliseconds",a),this.getTime()},setFromDateObjProxy:function(a){this.year=a.getFullYear(),this.month=a.getMonth(),this.date=a.getDate(),this.hours=a.getHours(),this.minutes=a.getMinutes(),this.seconds=a.getSeconds(),this.milliseconds=a.getMilliseconds(),this._day=a.getDay(),this._dateProxy=a,this._timeProxy=Date.UTC(this.year,this.month,this.date,this.hours,this.minutes,this.seconds,this.milliseconds),this._useCache=!1},setFromTimeProxy:function(a,b){var c,d=new Date(a);c=b?timezoneJS.timezone.getTzInfo(d,b).tzOffset:d.getTimezoneOffset(),d.setTime(a+6e4*(d.getTimezoneOffset()-c)),this.setFromDateObjProxy(d)},setAttribute:function(a,b){if(isNaN(b))throw new Error("Units must be a number.");var c=this._dateProxy,d="year"===a?"FullYear":a.substr(0,1).toUpperCase()+a.substr(1);c["set"+d](b),this.setFromDateObjProxy(c)},setUTCAttribute:function(a,b){if(isNaN(b))throw new Error("Units must be a number.");var c="year"===a?"FullYear":a.substr(0,1).toUpperCase()+a.substr(1),d=this.getUTCDateProxy();d["setUTC"+c](b),d.setUTCMinutes(d.getUTCMinutes()-this.getTimezoneOffset()),this.setFromTimeProxy(d.getTime()+6e4*this.getTimezoneOffset(),this.timezone)},setTimezone:function(a){var b=this.getTimezoneInfo().tzOffset;this.timezone=a,this._useCache=!1,this.setUTCMinutes(this.getUTCMinutes()-this.getTimezoneInfo().tzOffset+b)},removeTimezone:function(){this.timezone=null,this._useCache=!1},valueOf:function(){return this.getTime()},clone:function(){return this.timezone?new timezoneJS.Date(this.getTime(),this.timezone):new timezoneJS.Date(this.getTime())},toGMTString:function(){return this.toString("EEE, dd MMM yyyy HH:mm:ss Z","Etc/GMT")},toLocaleString:function(){},toLocaleDateString:function(){},toLocaleTimeString:function(){},toSource:function(){},toISOString:function(){return this.toString("yyyy-MM-ddTHH:mm:ss.SSS","Etc/UTC")+"Z"},toJSON:function(){return this.toISOString()},toString:function(a,b){a||(a="yyyy-MM-dd HH:mm:ss");var c=a,d=b?timezoneJS.timezone.getTzInfo(this.getTime(),b):this.getTimezoneInfo(),e=this;b&&(e=this.clone(),e.setTimezone(b));var f=e.getHours();return c.replace(/a+/g,function(){return"k"}).replace(/y+/g,function(a){return _fixWidth(e.getFullYear(),a.length)}).replace(/d+/g,function(a){return _fixWidth(e.getDate(),a.length)}).replace(/m+/g,function(a){return _fixWidth(e.getMinutes(),a.length)}).replace(/s+/g,function(a){return _fixWidth(e.getSeconds(),a.length)}).replace(/S+/g,function(a){return _fixWidth(e.getMilliseconds(),a.length)}).replace(/M+/g,function(a){var b=e.getMonth(),c=a.length;return c>3?timezoneJS.Months[b]:c>2?timezoneJS.Months[b].substring(0,c):_fixWidth(b+1,c)}).replace(/k+/g,function(){return f>=12?(f>12&&(f-=12),"PM"):"AM"}).replace(/H+/g,function(a){return _fixWidth(f,a.length)}).replace(/E+/g,function(a){return DAYS[e.getDay()].substring(0,a.length)}).replace(/Z+/gi,function(){return d.tzAbbr})},toUTCString:function(){return this.toGMTString()},civilToJulianDayNumber:function(a,b,c){var d;b++,b>12&&(d=parseInt(b/12,10),b%=12,a+=d),2>=b&&(a-=1,b+=12),d=Math.floor(a/100);var e=2-d+Math.floor(d/4),f=Math.floor(365.25*(a+4716))+Math.floor(30.6001*(b+1))+c+e-1524;return f},getLocalOffset:function(){return this._dateProxy.getTimezoneOffset()}},timezoneJS.timezone=new function(){function invalidTZError(a){throw new Error('Timezone "'+a+'" is either incorrect, or not loaded in the timezone registry.')}function builtInLoadZoneFile(a,b){var c=_this.zoneFileBasePath+"/"+a;return b&&b.async?_this.transport({async:!0,url:c,success:function(a){return _this.parseZones(a)&&"function"==typeof b.callback&&b.callback(),!0},error:function(){throw new Error('Error retrieving "'+c+'" zoneinfo files')}}):_this.parseZones(_this.transport({url:c,async:!1}))}function getRegionForTimezone(a){var b,c,d=regionExceptions[a];if(d)return d;if(b=a.split("/")[0],c=regionMap[b])return c;var e=_this.zones[a];return"string"==typeof e?getRegionForTimezone(e):_this.loadedZones.backward?void invalidTZError(a):(_this.loadZoneFile("backward"),getRegionForTimezone(a))}function parseTimeString(a){var b=/(\d+)(?::0*(\d*))?(?::0*(\d*))?([wsugz])?$/,c=a.match(b);return c[1]=parseInt(c[1],10),c[2]=c[2]?parseInt(c[2],10):0,c[3]=c[3]?parseInt(c[3],10):0,c}function processZone(a){if(a[3]){var b=parseInt(a[3],10),c=11,d=31;a[4]&&(c=SHORT_MONTHS[a[4].substr(0,3)],d=parseInt(a[5],10)||1);var e=a[6]?a[6]:"00:00:00",f=parseTimeString(e);return[b,c,d,f[1],f[2],f[3]]}}function getZone(a,b){for(var c="number"==typeof a?a:new Date(a).getTime(),d=b,e=_this.zones[d];"string"==typeof e;)d=e,e=_this.zones[d];if(!e){if(!_this.loadedZones.backward)return _this.loadZoneFile("backward"),getZone(a,b);invalidTZError(d)}if(0===e.length)throw new Error('No Zone found for "'+b+'" on '+a);for(var f=e.length-1;f>=0;f--){var g=e[f];if(g[3]&&c>g[3])break}return e[f+1]}function getBasicOffset(a){var b=parseTimeString(a),c="-"===a.charAt(0)?-1:1;return b=1e3*c*(60*(60*b[1]+b[2])+b[3]),b/60/1e3}function getRule(a,b,c){var d="number"==typeof a?new Date(a):a,e=b[1],f=b[0],g=e.match(/^([0-9]):([0-9][0-9])$/);if(g)return[-1e6,"max","-","Jan",1,parseTimeString("0:00"),60*parseInt(g[1])+parseInt(g[2]),"-"];var h,i=function(a,b,c){var d=0;if("u"===b||"g"===b||"z"===b)d=0;else if("s"===b)d=f;else{if("w"!==b&&b)throw"unknown type "+b;d=getAdjustedOffset(f,c)}return d*=6e4,new Date(a.getTime()+d)},j=function(a,b){var c,d=a[0],e=a[1],f=e[5];if(EXACT_DATE_TIME[d]||(EXACT_DATE_TIME[d]={}),EXACT_DATE_TIME[d][e])c=EXACT_DATE_TIME[d][e];else{if(isNaN(e[4])){var g,h;"last"===e[4].substr(0,4)?(c=new Date(Date.UTC(d,SHORT_MONTHS[e[3]]+1,1,f[1]-24,f[2],f[3],0)),g=SHORT_DAYS[e[4].substr(4,3)],h="<="):(c=new Date(Date.UTC(d,SHORT_MONTHS[e[3]],e[4].substr(5),f[1],f[2],f[3],0)),g=SHORT_DAYS[e[4].substr(0,3)],h=e[4].substr(3,2));var j=c.getUTCDay();c.setUTCDate(">="===h?c.getUTCDate()+(g-j+(j>g?7:0)):c.getUTCDate()+(g-j-(g>j?7:0)))}else c=new Date(Date.UTC(d,SHORT_MONTHS[e[3]],e[4],f[1],f[2],f[3],0));EXACT_DATE_TIME[d][e]=c}return b&&(c=i(c,f[4],b)),c},k=function(a,b){for(var c=[],d=0;b&&d<b.length;d++)b[d][0]<=a&&(b[d][1]>=a||b[d][0]===a&&"only"===b[d][1]||"max"===b[d][1])&&c.push([a,b[d]]);return c},l=function(a,b,d){var e,f;return a.constructor!==Date?(e=a[0],f=a[1],a=!d&&EXACT_DATE_TIME[e]&&EXACT_DATE_TIME[e][f]?EXACT_DATE_TIME[e][f]:j(a,d)):d&&(a=i(a,c?"u":"w",d)),b.constructor!==Date?(e=b[0],f=b[1],b=!d&&EXACT_DATE_TIME[e]&&EXACT_DATE_TIME[e][f]?EXACT_DATE_TIME[e][f]:j(b,d)):d&&(b=i(b,c?"u":"w",d)),a=Number(a),b=Number(b),a-b},m=d.getUTCFullYear();h=k(m,_this.rules[e]),h.push(d),h.sort(l),_arrIndexOf.call(h,d)<2&&(h=h.concat(k(m-1,_this.rules[e])),h.sort(l));var n=_arrIndexOf.call(h,d);return n>1&&l(d,h[n-1],h[n-2][1])<0?h[n-2][1]:n>0&&n<h.length-1&&l(d,h[n+1],h[n-1][1])>0?h[n+1][1]:0===n?null:h[n-1][1]}function getAdjustedOffset(a,b){return-Math.ceil(b[6]-a)}function getAbbreviation(a,b){var c,d=a[2];if(d.indexOf("%s")>-1){var e;e=b?"-"===b[7]?"":b[7]:"S",c=d.replace("%s",e)}else c=d.indexOf("/")>-1?d.split("/",2)[b[6]?1:0]:d;return c}var _this=this,regionMap={Etc:"etcetera",EST:"northamerica",MST:"northamerica",HST:"northamerica",EST5EDT:"northamerica",CST6CDT:"northamerica",MST7MDT:"northamerica",PST8PDT:"northamerica",America:"northamerica",Pacific:"australasia",Atlantic:"europe",Africa:"africa",Indian:"africa",Antarctica:"antarctica",Asia:"asia",Australia:"australasia",Europe:"europe",WET:"europe",CET:"europe",MET:"europe",EET:"europe"},regionExceptions={"Pacific/Honolulu":"northamerica","Atlantic/Bermuda":"northamerica","Atlantic/Cape_Verde":"africa","Atlantic/St_Helena":"africa","Indian/Kerguelen":"antarctica","Indian/Chagos":"asia","Indian/Maldives":"asia","Indian/Christmas":"australasia","Indian/Cocos":"australasia","America/Danmarkshavn":"europe","America/Scoresbysund":"europe","America/Godthab":"europe","America/Thule":"europe","Asia/Yekaterinburg":"europe","Asia/Omsk":"europe","Asia/Novosibirsk":"europe","Asia/Krasnoyarsk":"europe","Asia/Irkutsk":"europe","Asia/Yakutsk":"europe","Asia/Vladivostok":"europe","Asia/Sakhalin":"europe","Asia/Magadan":"europe","Asia/Kamchatka":"europe","Asia/Anadyr":"europe","Africa/Ceuta":"europe","America/Argentina/Buenos_Aires":"southamerica","America/Argentina/Cordoba":"southamerica","America/Argentina/Tucuman":"southamerica","America/Argentina/La_Rioja":"southamerica","America/Argentina/San_Juan":"southamerica","America/Argentina/Jujuy":"southamerica","America/Argentina/Catamarca":"southamerica","America/Argentina/Mendoza":"southamerica","America/Argentina/Rio_Gallegos":"southamerica","America/Argentina/Ushuaia":"southamerica","America/Aruba":"southamerica","America/La_Paz":"southamerica","America/Noronha":"southamerica","America/Belem":"southamerica","America/Fortaleza":"southamerica","America/Recife":"southamerica","America/Araguaina":"southamerica","America/Maceio":"southamerica","America/Bahia":"southamerica","America/Sao_Paulo":"southamerica","America/Campo_Grande":"southamerica","America/Cuiaba":"southamerica","America/Porto_Velho":"southamerica","America/Boa_Vista":"southamerica","America/Manaus":"southamerica","America/Eirunepe":"southamerica","America/Rio_Branco":"southamerica","America/Santiago":"southamerica","Pacific/Easter":"southamerica","America/Bogota":"southamerica","America/Curacao":"southamerica","America/Guayaquil":"southamerica","Pacific/Galapagos":"southamerica","Atlantic/Stanley":"southamerica","America/Cayenne":"southamerica","America/Guyana":"southamerica","America/Asuncion":"southamerica","America/Lima":"southamerica","Atlantic/South_Georgia":"southamerica","America/Paramaribo":"southamerica","America/Port_of_Spain":"southamerica","America/Montevideo":"southamerica","America/Caracas":"southamerica"};this.zoneFileBasePath=null,this.zoneFiles=["africa","antarctica","asia","australasia","backward","etcetera","europe","northamerica","pacificnew","southamerica"],this.loadingSchemes={PRELOAD_ALL:"preloadAll",LAZY_LOAD:"lazyLoad",MANUAL_LOAD:"manualLoad"},this.loadingScheme=this.loadingSchemes.LAZY_LOAD,this.loadedZones={},this.zones={},this.rules={},this.init=function(a){var b,c={async:!0},d=this.loadingScheme===this.loadingSchemes.PRELOAD_ALL?this.zoneFiles:this.defaultZoneFile||"northamerica",e=0;for(var f in a)c[f]=a[f];if("string"==typeof d)return this.loadZoneFile(d,c);b=c.callback,c.callback=function(){e++,e===d.length&&"function"==typeof b&&b()};for(var g=0;g<d.length;g++)this.loadZoneFile(d[g],c)},this.loadZoneFile=function(a,b){if("undefined"==typeof this.zoneFileBasePath)throw new Error("Please define a base path to your zone file directory -- timezoneJS.timezone.zoneFileBasePath.");if(!this.loadedZones[a])return this.loadedZones[a]=!0,builtInLoadZoneFile(a,b)},this.loadZoneJSONData=function(url,sync){var processData=function(data){data=eval("("+data+")");for(var z in data.zones)_this.zones[z]=data.zones[z];for(var r in data.rules)_this.rules[r]=data.rules[r]};return sync?processData(_this.transport({url:url,async:!1})):_this.transport({url:url,success:processData})},this.loadZoneDataFromObject=function(a){if(a){for(var b in a.zones)_this.zones[b]=a.zones[b];for(var c in a.rules)_this.rules[c]=a.rules[c]}},this.getAllZones=function(){var a=[];for(var b in this.zones)a.push(b);return a.sort()},this.parseZones=function(a){for(var b,c=a.split("\n"),d=[],e="",f=null,g=null,h=0;h<c.length;h++)if(b=c[h],b.match(/^\s/)&&(b="Zone "+f+b),b=b.split("#")[0],b.length>3)switch(d=b.split(/\s+/),e=d.shift()){case"Zone":if(f=d.shift(),_this.zones[f]||(_this.zones[f]=[]),d.length<3)break;d.splice(3,d.length,processZone(d)),d[3]&&(d[3]=Date.UTC.apply(null,d[3])),d[0]=-getBasicOffset(d[0]),_this.zones[f].push(d);break;case"Rule":g=d.shift(),_this.rules[g]||(_this.rules[g]=[]),d[0]=parseInt(d[0],10),d[1]=parseInt(d[1],10)||d[1],d[5]=parseTimeString(d[5]),d[6]=getBasicOffset(d[6]),_this.rules[g].push(d);break;case"Link":if(_this.zones[d[1]])throw new Error("Error with Link "+d[1]+". Cannot create link of a preexisted zone.");_this.zones[d[1]]=d[0]}return!0},this.transport=_transport,this.getTzInfo=function(a,b,c){if(this.loadingScheme===this.loadingSchemes.LAZY_LOAD){var d=getRegionForTimezone(b);if(!d)throw new Error("Not a valid timezone ID.");this.loadedZones[d]||this.loadZoneFile(d)}var e=getZone(a,b),f=e[0],g=getRule(a,e,c);g&&(f=getAdjustedOffset(f,g));var h=getAbbreviation(e,g);return{tzOffset:f,tzAbbr:h}}}}).call(this);