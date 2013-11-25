package org.audenaerde.tagging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.audenaerde.HomePage;
import org.audenaerde.jqautocomplete.JQueryAutocompleteBehavior;

public class MiniTaggingPanel extends Panel
{
	Form form;
	public MiniTaggingPanel(String id, final IModel<List<String>> selection, final IModel<List<String>> options)
	{
		super(id);
		final IModel<String> newTag = Model.of( "" );
		final TextField<String> field = new TextField<String>( "field", newTag );
		field.setOutputMarkupId( true );

		form = new Form( "f" )
		{
			
			@Override
			public void renderHead( IHeaderResponse response )
			{
				super.renderHead( response );
				
				//we add a on click to focus the textbox if we are clicked.
				String js = String.format( "$('#%s').click (function() { $('#%s').focus() } );", this.getMarkupId(), field.getMarkupId() );
				response.render( OnDomReadyHeaderItem.forScript( js ) );
			}

		};
		form.setOutputMarkupId( true );


		//Small fragment for a Tag (a link and a label in a span, and javascript on the onclick)
		class TagFragment extends Fragment
		{
			private static final long serialVersionUID = 1L;

			public TagFragment( String id, final ListItem<String> item )
			{
				super( id, "tagFragment", MiniTaggingPanel.this );
				this.add( new Label( "label", item.getModel() ) );
				AjaxLink a = new AjaxLink<Void>( "link" )
				{
					@Override
					public void onClick( AjaxRequestTarget target )
					{
						selection.getObject().remove( item.getIndex() );
						target.add( form );
						target.appendJavaScript( "$('#" + field.getMarkupId() + "').focus();" );
					}
				};
				this.add( a );
			}
		}

		form.add( new ListView( "tags", selection )
		{
			@Override
			protected void populateItem( ListItem item )
			{
				item.add( new TagFragment( "tag", item ).setRenderBodyOnly( true ) );
			}
		} );


		field.add( new JQueryAutocompleteBehavior( form )
		{
			@Override
			protected void onSelect( AjaxRequestTarget target, String val )
			{
				super.onSelect( target, val );
				selection.getObject().add( val );
				newTag.setObject( "" );
				target.add( form );
				target.appendJavaScript( "$('#" + field.getMarkupId() + "').focus();" );
			}

			@Override
			protected Iterator getChoices( String input )
			{
			
				List<String> res = new ArrayList<String>();
				for ( String s : options.getObject() )
				{
					if ( s.startsWith( input.toLowerCase() ) )
						res.add( s );
				}
				return res.iterator();
			}
		} );
	
		form.add( field );
		AjaxSubmitLink asl = new AjaxSubmitLink( "sb" )
		{

			@Override
			protected void onSubmit( AjaxRequestTarget target, Form<?> form )
			{
				String newTagString = newTag.getObject();
				if ( options.getObject().contains( newTagString ))
				{
					selection.getObject().add( newTagString );
				}
				newTag.setObject( "" );
				onUpdate( target );
			}
		};
		form.add( asl );
		form.setDefaultButton( asl );
		add( form );
	}
	
	protected void onUpdate(AjaxRequestTarget target)
	{
		target.add( form );
		target.appendJavaScript( "$('#" + form.getMarkupId() + "').focus();" );
	}
}
