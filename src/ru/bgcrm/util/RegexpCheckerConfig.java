package ru.bgcrm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ru.bgcrm.model.BGMessageException;

public class RegexpCheckerConfig
	extends Config
{
	private static final Logger log = Logger.getLogger( RegexpCheckerConfig.class );
	
	private List<Pattern> regexpList;
	private String regexpTitles = "";
	
	public RegexpCheckerConfig( ParameterMap config )
	{
		super( config );
		
		for( ParameterMap value : config.subIndexed( "regexp." ).values() )
		{
			String title = value.get( "title" );
			String regexp = value.get( "regexp" );
			
			if( Utils.isBlankString( title ) )
			{
				continue;
			}
			
			try
            {
				Pattern pattern = Pattern.compile( regexp );
				if( regexpList == null )
				{
					regexpList = new ArrayList<Pattern>();
				}
				regexpList.add( pattern );				
				
				if( Utils.notBlankString( regexpTitles ) )
				{
					regexpTitles += "\n";
				}				
				regexpTitles += title;
            }
            catch( Exception e )
            {
	            log.error( "Regexp pattern load: " + e.getMessage() + ". Title: " + title + "; regexp: " + regexp );
            }
		}
	}
	
	public void checkValue( String value )
		throws BGMessageException
	{
		if( regexpList != null )
		{
			for( Pattern p : regexpList )
			{
				if( p.matcher( value ).matches() )
				{
					return;
				}
			}
			throw new BGMessageException( "Значение должно соответствовать одному из шаблонов:\n" + regexpTitles );
		}
	}
}