package org.audenaerde.jqautocomplete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;

import com.google.gson.Gson;

/**
 * Basic autocomplete behavior.
 * 
 * @author raudenaerde
 * @param <T>
 */
public abstract class JQueryAutocompleteBehavior<T extends Serializable> extends AbstractAjaxBehavior
{
	private static final long serialVersionUID = 4251910817176833598L;

	private static final ResourceReference JS = new JavaScriptResourceReference( JQueryAutocompleteBehavior.class, "jqautocomplete.js" );

	public static final String JQUERY_AUTOCOMPLETE_TERM_VAR = "term";

	private AbstractAjaxBehavior onSelectBehavior;

	private Component elementToAddTo;

	public JQueryAutocompleteBehavior()
	{
		//no component to add the ui to.
	}

	public JQueryAutocompleteBehavior( Form f2 )
	{
		elementToAddTo = f2;
	}

	/** We add onSelectBehaviour as well */
	@Override
	protected void onBind()
	{
		super.onBind();
		onSelectBehavior = new AbstractDefaultAjaxBehavior()
		{
			private static final long serialVersionUID = -1082569150758130051L;

			@Override
			protected void respond( AjaxRequestTarget target )
			{
				final RequestCycle requestCycle = RequestCycle.get();
				final String val = requestCycle.getRequest().getRequestParameters().getParameterValue( "val" ).toString( "" );
				onSelect( target, val );
			}
		};

		this.getComponent().add( onSelectBehavior );
	}

	/*
	 * Convert List to json object
	 */
	private String convertListToJson( List<?> matches )
	{
		Gson gson = new Gson();
		String json = gson.toJson( matches );
		return json;
	}

	@Override
	public void renderHead( Component component, IHeaderResponse response )
	{
		response.render( JavaScriptHeaderItem.forReference( Application.get().getJavaScriptLibrarySettings().getJQueryReference() ) );
		response.render( JavaScriptHeaderItem.forUrl( "jquery-ui.js" ) );
		response.render( JavaScriptReferenceHeaderItem.forReference( JS ) );

		String elementToAddToId = this.elementToAddTo == null ? null : "\"" + this.elementToAddTo.getMarkupId()+"\"";
		
		response.render( OnDomReadyHeaderItem.forScript( "initJqAutocomplete(\"" + component.getMarkupId() + "\",\"" + this.getCallbackUrl()
			+ "\",\"" + this.onSelectBehavior.getCallbackUrl() + "\"," + elementToAddToId + ")" ) );
	}

	@Override
	public void onRequest()
	{
		RequestCycle cycle = RequestCycle.get();
		WebRequest webRequest = (WebRequest) cycle.getRequest();
		@SuppressWarnings( "unused" )
		StringValue term = webRequest.getQueryParameters().getParameterValue( JQUERY_AUTOCOMPLETE_TERM_VAR );

		String stringTerm = term.toString( "" ).toLowerCase();

		List<T> result = new ArrayList<T>();
		Iterator<T> d = getChoices( stringTerm );

		while ( d.hasNext() )
		{
			result.add( d.next() );
		}
		String json = convertListToJson( result );
		cycle.scheduleRequestHandlerAfterCurrent( new TextRequestHandler( "application/json", "UTF-8", json ) );
	}

	@Override
	public boolean getStatelessHint( Component component )
	{
		return false;
	}

	protected abstract Iterator<T> getChoices( String input );

	protected void onSelect( AjaxRequestTarget target, String val )
	{
	}

}
