package edu.knu.scadge.parser;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main
{
  public static void main( String[] args )
  {
    //make file input stream from wiki dump
    FileInputStream wikiFileInputStream = null;
    try
    {
      wikiFileInputStream = new FileInputStream( "/home/scadge/wikidump_20130805.xml.bz2" );
    }
    catch( FileNotFoundException e )
    {
      System.err.println( e );
      System.exit( 0 );
    }

    //make zip stream from wiki dump
    InputStream wikiStream = null;
    try
    {
      wikiStream = new BZip2CompressorInputStream( wikiFileInputStream );
    }
    catch( IOException e )
    {
      System.err.println( e );
      System.exit( 0 );
    }

    WikiXMLParser wikiParser = new WikiSAXParser();
    wikiParser.parse( wikiStream, new WikiPageHandler( null ) );
  }
}
