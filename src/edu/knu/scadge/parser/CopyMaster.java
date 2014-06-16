package edu.knu.scadge.parser;

import java.io.*;

/**
 * Created by scadge on 6/12/14.
 */
public class CopyMaster
{
  private static final String root = "/home/scadge/wikipages/";
  private static final String DIR_TEMPLATE = "wikipages-";

  public static void main( String[] args ) throws IOException
  {
    System.out.println(new File("assets/important/").listFiles().length);
//    Set<String> words = new HashSet<String>();
//    File wordsim = new File( "assets/wordsim353-terms.txt" );
//    BufferedReader br = new BufferedReader(new FileReader(wordsim));
//    try {
//      StringBuilder sb = new StringBuilder();
//      String line;
//
//      while ((line = br.readLine() )!= null) {
//        words.add( line.toLowerCase() );
//      }
//
//    } finally {
//      br.close();
//    }
//
//    int count = 0;
//    File rootDir = new File(root);
//    for( File folder : rootDir.listFiles() )
//    {
//      System.out.println("Processed: " + count*10000);
//      for( File file : folder.listFiles() )
//      {
//        String article = file.getName().substring( file.getName().indexOf( " " ) + 1, file.getName().length() - 4 );
//        if( words.contains( article.toLowerCase() ) )
//        {
//          Files.copy(file.toPath(), new File("assets/important/" + article).toPath());
//        }
//      }
//      count++;
//    }
  }
}
