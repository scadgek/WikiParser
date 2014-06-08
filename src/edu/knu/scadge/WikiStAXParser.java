package edu.knu.scadge;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Date;

public class WikiStAXParser implements WikiXMLParser
{
  private static final String LAST_ARTICLE_FILE = "assets/last_processed_article.txt";

  private long totalTime = 0;
  private String currentFolder = "wikipages-0";
  private int currentFolderNumber = 0;
  private static int count = 0;
  private Date lastDate = new Date();
  private double averageTimeForArticle = 0;
  private PageHandler innerHandler;
  private StringBuffer currentPage = new StringBuffer();
  private StringBuffer currentTitle = new StringBuffer();
  private boolean isInText = false;
  private boolean isInTitle = false;
  private String savedInit;
  private boolean articleShouldBeProcessed = false;

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
  public void parse( InputStream stream, WikiPageHandler wikiPageHandler )
  {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = null;
    try
    {
      reader = factory.createXMLStreamReader( stream );
    }
    catch( XMLStreamException e )
    {
      System.err.println( e );
    }

    try
    {
      while( reader.hasNext() )
      {
        int event = reader.next();

        switch(event)
        {
          case XMLStreamConstants.START_ELEMENT:
            if( reader.getLocalName().equalsIgnoreCase( "text" ) )
            {
              currentPage.delete( 0, currentPage.length() );
              isInText = true;
            }
            else if( reader.getLocalName().equalsIgnoreCase( "title" ) )
            {
              currentTitle.delete( 0, currentTitle.length() );
              isInTitle = true;
            }
            break;
          case XMLStreamConstants.CHARACTERS:
            if( isInText )
            {
              currentPage.append( reader.getText() );
            }
            else if( isInTitle )
            {
              currentTitle.append( reader.getText() );
            }
            break;
          case XMLStreamConstants.END_ELEMENT:
            if( reader.getLocalName().equalsIgnoreCase( "text" ) )
            {
              if( articleShouldBeProcessed )
              {

                try
                {
                  if(count > currentFolderNumber*10000) {
                    currentFolderNumber++;
                    currentFolder = "wikipages-" + currentFolderNumber;
                    new File( "/home/scadge/wikipages/" + currentFolder ).mkdirs();
                  }
                  BufferedWriter out = new BufferedWriter( new FileWriter( "/home/scadge/wikipages/".concat( currentFolder ).concat( "/" ).concat( String.valueOf( count ) ).concat( ". " + currentTitle.toString().replaceAll( "/", " " ) + ".txt" ) ) );
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
            else if( reader.getLocalName().equalsIgnoreCase( "title" ) )
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

              count++;
              if( count % 1000 == 0 )
              {
                Date newDate = new Date();
                long secondsPassed = (newDate.getTime() - lastDate.getTime()) / 1000;
                totalTime += secondsPassed;
                averageTimeForArticle = (double) totalTime / count;
                System.out.println( newDate + ": Processed " + count + " articles, " + secondsPassed + " seconds passed. Average time for article: " + new DecimalFormat( "##.####").format( averageTimeForArticle ) + " seconds" );
                lastDate = newDate;
              }
            }
            break;
        }
      }
    }
    catch( XMLStreamException e )
    {
      System.err.println( e );
    }
  }
}
