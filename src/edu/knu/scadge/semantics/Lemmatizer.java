package edu.knu.scadge.semantics;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;

/**
 * Created by scadge on 6/13/14.
 */
public class Lemmatizer
{
  private StanfordCoreNLP pipeline;

  private static Lemmatizer instance = new Lemmatizer();

  private Lemmatizer()
  {
    Properties props = new Properties();
    props.put( "annotators", "tokenize, ssplit, pos, lemma" );

    pipeline = new StanfordCoreNLP( props );
  }

  public static Lemmatizer getInstance()
  {
    return instance;
  }

  public String lemmatize( String text )
  {
    text = text.replaceAll( "\\(.*?\\)", "" ).trim();
    if( text.matches( "^[A-Za-z0-9][A-Za-z0-9-– .,']*$" ) )
    {
//      System.out.println("OK: " + text );
      Annotation document = new Annotation( text );
      pipeline.annotate( document );

      List<CoreMap> sentences = document.get( CoreAnnotations.SentencesAnnotation.class );

      StringBuilder lemma = new StringBuilder();

      for( CoreMap sentence : sentences )
      {
        for( CoreLabel token : sentence.get( CoreAnnotations.TokensAnnotation.class ) )
        {
          String word = token.get( CoreAnnotations.LemmaAnnotation.class ).toLowerCase();

          lemma.append( word ).append( " " );
        }
      }
      return lemma.toString().trim();
    }
    else
    {
//      System.out.println("Passing " + text );
      return "";
    }


  }
}
