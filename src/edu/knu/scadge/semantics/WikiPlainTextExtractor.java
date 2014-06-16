package edu.knu.scadge.semantics;

import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

/**
 * Created by scadge on 6/13/14.
 */
public class WikiPlainTextExtractor
{
  private WikiModel wikiModel = new WikiModel( "http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}" );

  private static WikiPlainTextExtractor instance = new WikiPlainTextExtractor();

  public static WikiPlainTextExtractor getInstance()
  {
    return instance;
  }

  public String extractPlainText( String text )
  {
    if( text.startsWith( "#REDIRECT " ) )
    {
      return "";
    }

    // Zap headings ==some text== or ===some text===

    // <ref>{{Cite web|url=http://tmh.floonet.net/articles/falseprinciple.html |title="The False Principle of our Education" by Max Stirner |publisher=Tmh.floonet.net |date= |accessdate=2010-09-20}}</ref>
    // <ref>Christopher Gray, ''Leaving the Twentieth Century'', p. 88.</ref>
    // <ref>Sochen, June. 1972. ''The New Woman: Feminism in Greenwich Village 1910Р1920.'' New York: Quadrangle.</ref>

    // String refexp = "[A-Za-z0-9+\\s\\{\\}:_=''|\\.\\w#\"\\(\\)\\[\\]/,?&%Р-]+";
    String wikiText = text.
            replaceAll( "[=]+[A-Za-z+\\s-]+[=]+", " " ).
            replaceAll( "\\{\\{[A-Za-z0-9+\\s-]+\\}\\}", " " ).
            replaceAll( "(?m)<ref>.+</ref>", " " ).
            replaceAll( "(?m)<ref name=\"[A-Za-z0-9\\s-]+\">.+</ref>", " " ).
            replaceAll( "<ref>", " <ref>" );

    // Remove text inside {{ }}
    String plainStr = wikiModel.render( new PlainTextConverter(), wikiText ).replaceAll( "\\{\\{[A-Za-z+\\s-]+\\}\\}", " " ).replaceAll( "\\[\\[[A-Za-z+\\s-]+\\]\\]", " " );

    return plainStr.trim();
  }
}
