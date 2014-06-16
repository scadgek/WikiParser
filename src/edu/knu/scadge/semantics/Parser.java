package edu.knu.scadge.semantics;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by scadge on 2/9/14.
 */
public class Parser
{
  public static void main( String[] args ) throws ParserConfigurationException, SAXException, IOException
  {
//        InputStream stream = new BZip2CompressorInputStream(new FileInputStream("D:/enwiki-latest-pages-articles.xml.bz2"));
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        SAXParser newSAXParser = factory.newSAXParser();
//        newSAXParser.parse(stream, new WikiPageHandler(new ReferenceCounter()));
//    File root = new File( "assets/important/" );
//    int count = 0;
//    for( File file : root.listFiles() )
//    {
//      count++;
//      String article = file.getName();
//      String text = new String( Files.readAllBytes( file.toPath() ), StandardCharsets.UTF_8 );
//      String plainText = WikiPlainTextExtractor.getInstance().extractPlainText( text );
//      String lemmatizedText = Lemmatizer.getInstance().lemmatize( plainText );
////      for( String lemma : lemmatizedText.split( " " ) )
////      {
////        DAO.getInstance().insertWord( lemma, false );
////      }
//
//      String lemmatizedArticle = Lemmatizer.getInstance().lemmatize( article );
////      DAO.getInstance().setArticle( lemmatizedArticle );
//
//      if( !lemmatizedText.isEmpty() )
//      {
//        Map<String, Integer> points = new HashMap<>();
//        for( String lemma : lemmatizedText.split( " " ) )
//        {
//          if( points.containsKey( lemma ) )
//          {
//            points.put( lemma, points.get( lemma ) + 1 );
//          }
//          else
//          {
//            points.put( lemma, 1 );
//          }
//        }
//
//        DAO.getInstance().relate2( lemmatizedArticle, points );
//        if( count % 100 == 0 )
//        {
//          System.out.println( new Date() + ": processed " + count + " articles" );
//        }
//      }
//    }
//    DAO.getInstance().writeToFile();


  }

}
