function initJqAutocomplete(id, callbackurl)
{
	$("#"+id).autocomplete(
			{ source: callbackurl	 
			}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		        var label = item.value;
		        if (item.desc)
		        	{
		        	label = label + "<span class=\"desc\">" + item.desc + "</span>";
		        	}
		
				return $( "<li>" )
				.append( "<a>" + label + "</a>" )
				.appendTo( ul );
				};; 
}
/**
 * Override the resize menu to make it the same size as the input box. taken from: <BR>
 * http://stackoverflow.com/questions/5643767/jquery-ui-autocomplete-width-not-set-correctly
 */
jQuery.ui.autocomplete.prototype._resizeMenu = function () {
	  var ul = this.menu.element;
	  ul.outerWidth(this.element.outerWidth());
}
