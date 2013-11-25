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
import org.audenaerde.tagging.MiniTaggingPanel;

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

		final ArrayList<String> test = new ArrayList<String>();
		test.add( "aap" );
		test.add( "noot" );
		test.add( "mies" );
		test.add( "wim" );
		test.add( "zus" );
		test.add( "jet" );
		IModel<List<String>> options = new Model(test);
		IModel<List<String>> selection = new Model(new ArrayList<String>());
		add( new MiniTaggingPanel( "tags", selection, options ));

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
