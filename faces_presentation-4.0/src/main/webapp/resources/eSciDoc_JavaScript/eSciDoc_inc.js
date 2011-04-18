if(typeof(cookieVersion) == 'undefined') {
	var cookieVersion = "1.1";
}
if (typeof(jsfURL) == 'undefined') {
	var jsfURL = './';
}
if(typeof(jsURL) == 'undefined') {
	var jsURL = './resources/eSciDoc_JavaScript/';
}
if(typeof(coneURL) == 'undefined') {
	/* var coneURL = '../../cone/'; */
	var coneURL = 'http://pubman.mpdl.mpg.de/cone/';
}


function appendScript(link) {
	var script = document.createElement('script');
	script.setAttribute("type", "text/javascript");
	script.setAttribute("language", "JavaScript");
	script.setAttribute("src", link);
	document.getElementsByTagName('head')[0].appendChild(script);
}

appendScript(jsfURL + 'jsf/a4j/g/3_3_3.Final/org/ajax4jsf/framework.pack.js');
appendScript(coneURL + 'js/jquery.suggest.js');
appendScript(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_ext_paginator.js');
appendScript(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_item_list.js');
appendScript(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_full_item.js');
appendScript(jsURL + 'eSciDoc_component_JavaScript/eSciDoc_single_elements.js');
appendScript(jsURL + 'theme_cookie.js');
appendScript(jsURL + 'jquery.shiftcheckbox.js');
appendScript(jsURL + 'eSciDoc_javascript.js');
appendScript(jsURL + 'main.js');
