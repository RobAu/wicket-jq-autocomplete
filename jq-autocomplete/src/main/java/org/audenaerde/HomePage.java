package org.audenaerde;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.audenaerde.jqautocomplete.JQueryAutocompleteBehavior;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import edu.emory.mathcs.backport.java.util.Collections;

public class HomePage extends WebPage
{
	private static final long serialVersionUID = 1L;

	public static class ValueWithDescription implements Serializable
	{
		public ValueWithDescription( String value, String desc )
		{
			this.value = value;
			this.desc = desc;
		}

		String value;
		String desc;
	}

	public static class Aap implements Serializable
	{
		public String naam = "initial value";

		public String uh;
	}

	public HomePage( final PageParameters parameters )
	{
		super( parameters );

		IModel<Aap> aap = Model.of( new Aap() );
		Form f = new Form( "f", aap )
		{

		};
		f.add( new TextField( "tf", new PropertyModel<String>( aap, "naam" ) ).add( newAutoCompleteBehavior() ) );
		f.add( new TextField( "tf2", new PropertyModel<String>( aap, "uh" ) ).add( newNattyBehaviour() ) );
		add( f );

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
				super( id, "tagFragment", HomePage.this );
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

		final List<String> test = new ArrayList<String>();
		test.add( "aap" );
		test.add( "noot" );
		test.add( "mies" );
		test.add( "wim" );
		test.add( "zus" );
		test.add( "jet" );
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
				for ( String s : test )
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
				if ( test.contains( newTagString ))
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

		// TODO Add your page's components here

	}

	public JQueryAutocompleteBehavior<ValueWithDescription> newNattyBehaviour()
	{
		return new JQueryAutocompleteBehavior<ValueWithDescription>()
		{
			@Override
			protected Iterator<ValueWithDescription> getChoices( String input )
			{
				String value = input;
				//CalendarSource.setBaseDate( new Date( 80, 6, 16 ) );
				List<DateGroup> groups = new Parser().parse( input );
				Date d = null;
				for ( DateGroup group : groups )
				{
					d = group.getDates().get( 0 );
				}
				String dateString = ( d == null ) ? "No valid date expression found" : d.toString();
				ValueWithDescription data = new ValueWithDescription( value, dateString );
				return Collections.singleton( data ).iterator();
			}
		};
	}

	public JQueryAutocompleteBehavior<ValueWithDescription> newAutoCompleteBehavior()
	{
		return new JQueryAutocompleteBehavior<ValueWithDescription>()
		{
			@Override
			protected Iterator<ValueWithDescription> getChoices( String input )
			{
				List<ValueWithDescription> statesLike = new ArrayList<ValueWithDescription>();
				statesLike.add( new ValueWithDescription( "Today", "Vandaag, uit excel" ) );
				statesLike.add( new ValueWithDescription( "Tomorrow", "Morgen, uit excel" ) );
				statesLike.add( new ValueWithDescription( "Yesterday", "Gisteren" ) );
				String lowercaseInput = input.toLowerCase();
				List<ValueWithDescription> results = new ArrayList<ValueWithDescription>();
				for ( ValueWithDescription stat : statesLike )
				{
					if ( stat.value.toLowerCase().contains( lowercaseInput ) )
					{
						results.add( stat );
					}
					if ( results.size() > 9 )
					{
						break;
					}
				}
				return results.iterator();
			}
		};
	}
}
