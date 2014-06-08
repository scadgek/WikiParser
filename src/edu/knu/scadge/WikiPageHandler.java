package edu.knu.scadge;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;

public class WikiPageHandler extends DefaultHandler
{
  private static final String LAST_ARTICLE_FILE = "assets/last_processed_article.txt";

  private PageHandler innerHandler;
  private StringBuffer currentPage = new StringBuffer();
  private StringBuffer currentTitle = new StringBuffer();
  private boolean isInText = false;
  private boolean isInTitle = false;
  private String savedInit;
  private boolean articleShouldBeProcessed = false;

  public WikiPageHandler( PageHandler innerHandler )
  {
    this.innerHandler = innerHandler;
    savedInit = getLast();
  }

  private String getLast()
  {
    try
    {
      BufferedReader fr = new BufferedReader( new FileReader( LAST_ARTICLE_FILE ) );
      String retVal = fr.readLine();
      fr.close();
      return retVal;
    }
    catch( IOException e )
    {
      System.err.println( e );
      return null;
    }
  }

  private void setLast( String title )
  {
    try
    {
      FileWriter fw = new FileWriter( LAST_ARTICLE_FILE );
      fw.write( title );
      fw.close();
    }
    catch( IOException e )
    {
      System.err.println( e );
    }
  }

  @Override
  public void startElement( String uri, String localName, String qName, Attributes attributes )
  {
    if( qName.equalsIgnoreCase( "text" ) )
    {
      currentPage.delete( 0, currentPage.length() );
      isInText = true;
    }
    else if( qName.equalsIgnoreCase( "title" ) )
    {
      currentTitle.delete( 0, currentTitle.length() );
      isInTitle = true;
    }
  }

  @Override
  public void endElement( String uri, String localName, String qName )
  {
    if( qName.equalsIgnoreCase( "text" ) )
    {
      if( articleShouldBeProcessed )
      {

        try
        {
          BufferedWriter out = new BufferedWriter(new FileWriter("/home/scadge/wikipages/".concat( currentTitle.toString().replaceAll( "/", " " ) ).concat( ".txt" )));
          out.write( currentPage.toString() );
          out.close();
        }
        catch( IOException e )
        {
          System.err.println( e );
        }
      }

      isInText = false;
    }
    else if( qName.equalsIgnoreCase( "title" ) )
    {
      if( savedInit == null || savedInit.equals( currentTitle.toString() ) )
      {
        articleShouldBeProcessed = true;
      }

      if( articleShouldBeProcessed )
      {
        setLast( currentTitle.toString() );
      }

      isInTitle = false;
    }
  }

  @Override
  public void characters( char[] ch, int start, int length )
  {
    if( isInText )
    {
      currentPage.append( ch, start, length );
    }
    else if( isInTitle )
    {
      currentTitle.append( ch, start, length );
    }
  }
}
