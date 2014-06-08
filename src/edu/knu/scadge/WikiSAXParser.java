package edu.knu.scadge;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

public class WikiSAXParser implements WikiXMLParser
{
  @Override
  public void parse( InputStream stream, WikiPageHandler wikiPageHandler )
  {
    SAXParserFactory factory = SAXParserFactory.newInstance();

    SAXParser saxParser = null;

    try
    {
      saxParser = factory.newSAXParser();
    }
    catch( ParserConfigurationException e )
    {
      System.err.println( e );
      System.exit( 0 );
    }
    catch( SAXException e )
    {
      System.err.println( e );
      System.exit( 0 );
    }

    try
    {
      saxParser.parse( stream, wikiPageHandler );
    }
    catch( SAXException e )
    {
      System.err.println( e );
      System.exit( 0 );
    }
    catch( IOException e )
    {
      e.printStackTrace();
    }
  }
}
