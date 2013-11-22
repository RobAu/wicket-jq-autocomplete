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
	public MiniTaggingPanel(String id, final IModel<List<String>> options)
	{
		super(id);
		final IModel<String> newTag = Model.of( "" );
		final TextField<String> field = new TextField<String>( "field", newTag );
		field.setOutputMarkupId( true );

		final Form f2 = new Form( "f2" )
		{

			@Override
			public void renderHead( IHeaderResponse response )
			{
				super.renderHead( response );
				String js = String.format( "$('#%s').click (function() { $('#%s').focus() } );", this.getMarkupId(), field.getMarkupId() );
				response.render( OnDomReadyHeaderItem.forScript( js ) );
			}

		};
		f2.setOutputMarkupId( true );

		final List<String> tags = new ArrayList<String>();
		tags.add( "aap" );
		tags.add( "noot" );

		//Small fragment for a PageFieldSelector
		class TagFragment extends Fragment
		{
			/**
			 * 
			 */
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
						tags.remove( item.getIndex() );
						target.add( f2 );
						target.appendJavaScript( "$('#" + field.getMarkupId() + "').focus();" );
					}
				};
				this.add( a );
			}
		}
		;

		f2.add( new ListView( "tags", tags )
		{

			@Override
			protected void populateItem( ListItem item )
			{
				item.add( new TagFragment( "tag", item ).setRenderBodyOnly( true ) );

			}
		} );


		field.add( new JQueryAutocompleteBehavior( f2 )
		{

			@Override
			protected void onSelect( AjaxRequestTarget target, String val )
			{
				super.onSelect( target, val );
				tags.add( val );
				newTag.setObject( "" );
				target.add( f2 );
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
		//		field.add( new AjaxFormComponentUpdatingBehavior("keypress")
		//		{
		//			
		//			@Override
		//			protected void updateAjaxAttributes( AjaxRequestAttributes attributes )
		//			{
		//				super.updateAjaxAttributes( attributes );
		//				attributes.getAjaxCallListeners().add(new AjaxCallListener(){
		//
		//
		//
		//					@Override
		//					public CharSequence getPrecondition( Component component )
		//					{
		//						  return  "var keycode = Wicket.Event.keyCode(attrs.event);" +
		//				                            "if (keycode == 13)" +
		//				                            "    return true;" +
		//				                            "else" +
		//				                            "    return false;";
		//						
		//					}});
		//			}
		//
		//			@Override
		//			protected void onUpdate( AjaxRequestTarget target )
		//			{
		//				
		//				
		//			}
		//		} );
		f2.add( field );
		AjaxSubmitLink asl = new AjaxSubmitLink( "sb" )
		{

			@Override
			protected void onSubmit( AjaxRequestTarget target, Form<?> form )
			{
				String newTagString = newTag.getObject();
				if ( options.getObject().contains( newTagString ))
				{
					tags.add( newTagString );
				}
				newTag.setObject( "" );
				target.add( f2 );
				target.appendJavaScript( "$('#" + field.getMarkupId() + "').focus();" );
			}
		};
		f2.add( asl );
		f2.setDefaultButton( asl );
		add( f2 );
	}
}
