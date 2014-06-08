package edu.knu.scadge;

import java.io.InputStream;

public interface WikiXMLParser
{
  public void parse( InputStream stream, WikiPageHandler wikiPageHandler );
}
