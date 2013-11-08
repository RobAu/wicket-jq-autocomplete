;(function (undefined) {
        "use strict";

        // introduce a namespace
        if (typeof (Wicket.getRobEventMarkupId) === "undefined") {

                Wicket.getRobEventMarkupId = function(attrs, markupId) {

                        return attrs.event.target.id;
                };
        }
})();