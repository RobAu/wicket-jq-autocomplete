package org.audenaerde.onchildeventbehavior;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class OnChildEventBehavior extends AjaxEventBehavior
{
	private static final String MARKUPID = "MARKUPID";

	private static final ResourceReference JS = new JavaScriptResourceReference( OnChildEventBehavior.class, "OnChildEventBehavior.js" );

	public OnChildEventBehavior()
	{
		super( "click" );
	}

	public OnChildEventBehavior( String event )
	{
		super( event );
	}

	@Override
	public void renderHead( Component component, IHeaderResponse response )
	{
		super.renderHead( component, response );

		response.render( JavaScriptHeaderItem.forReference( JS ) );
	}

	@Override
	protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
	{
		super.updateAjaxAttributes( attributes );

		attributes.getDynamicExtraParameters().add( String.format(	"return {'MARKUPID' : Wicket.getRobEventMarkupId(attrs, '%s')}",
																	this.getComponent().getMarkupId() ) );
	}

	@Override
	protected void onEvent( AjaxRequestTarget target )
	{
		final RequestCycle requestCycle = RequestCycle.get();

		final String markupId = requestCycle.getRequest().getRequestParameters().getParameterValue( MARKUPID ).toString( "" );

		MarkupContainer mc = ( (MarkupContainer) this.getComponent() );
		List<Component> allChilds = mc.visitChildren( Component.class ).toList();
		for ( Component c : allChilds )
		{
			if ( markupId.equals( c.getMarkupId() ) )
			{
				this.handleEvent( target, c );
				return;
			}
		}

	}

	protected void handleEvent( AjaxRequestTarget target, Component c )
	{
	}

}
