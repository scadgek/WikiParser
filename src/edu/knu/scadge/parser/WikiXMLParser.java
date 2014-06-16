package edu.knu.scadge.parser;

import java.io.InputStream;

public interface WikiXMLParser
{
  public void parse( InputStream stream, WikiPageHandler wikiPageHandler );
}
