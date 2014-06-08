package edu.knu.scadge;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

public interface WikiXMLParser
{
  public void parse( InputStream stream, WikiPageHandler wikiPageHandler );
}
