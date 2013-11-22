function initJqAutocomplete(id, dataCallBack, selectCallBack, parentElement) {
	
	var elem = $("#" + id).autocomplete({
		source : dataCallBack,
		minLength : 0,
		select : function(event, ui) {

			Wicket.Ajax.get({
				'u' : selectCallBack,
				'ep' : {
					'val' : ui.item.label
				}
			});
		}
	});
	if (parentElement)
	{
		elem.autocomplete( "option", "position", { my : "left top", at: "left bottom", of : "#" + parentElement } );
	}
	elem.data("ui-autocomplete")._renderItem = function(ul, item) {
		var label = item.value;
		if (item.desc) {
			label = label + "<span class=\"desc\">" + item.desc + "</span>";
		}

		return $("<li>").append("<a>" + label + "</a>").appendTo(ul);
	};
	;

	if (parentElement)
	{
		elem.data("ui-autocomplete")._resizeMenu = function() {
			var ul = this.menu.element;
			ul.outerWidth($("#" + parentElement).outerWidth());
		}
	}
	elem.focus(function() {
		$(this).autocomplete("search", "");
	});
}
/**
 * Override the resize menu to make it the same size as the input box. taken
 * from: <BR>
 * http://stackoverflow.com/questions/5643767/jquery-ui-autocomplete-width-not-set-correctly
 */
jQuery.ui.autocomplete.prototype._resizeMenu = function() {
	var ul = this.menu.element;
	ul.outerWidth(this.element.outerWidth());
}
