package edu.knu.scadge.semantics; /**
 * @fileOverview DB.java
 * @author Serhiy Chupov
 * @version 1.0
 * @date July 4, 2013
 * @modified September 27, 2013
 * @modifiedby Serhiy Chupov
 * @param Created in Taras Shevchenko National University of Kyiv (Cybernetics) under a contract between
 * @param LLC "Samsung Electronics Ukraine Company" (Kiev Ukraine) and
 * @param Taras Shevchenko National University of Kyiv
 * @param Copyright: Samsung Electronics, Ltd. All rights reserved.
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

//import org.apache.log4j.Logger;

/**
 * Database access object.
 * Used for database operations.
 */
public class DAO {

    private String url = "jdbc:postgresql://localhost:5432/postgres";
    private String user = "postgres";
    private String password = "postgres";
    private Connection con = null;
//    private PreparedStatement setArticle;
    private PreparedStatement setWord;
    private PreparedStatement saveArticleStatement;
    private PreparedStatement saveWordStatement;
    private PreparedStatement updateWordStatement;
    private PreparedStatement selectWordStatement;
    private PreparedStatement getRelation;
    private PreparedStatement saveRelation;
    private PreparedStatement updateRelation;
  private PreparedStatement getWordId;

    private PreparedStatement selectArticleStatement;
    private PreparedStatement deleteArticleStatement;

  private PreparedStatement insertIntoWords;
  private PreparedStatement setArticle;


  private static int batch_size = 100;
  private static int count = 0;

    private static DAO instance = new DAO();

    private DAO() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(true);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }
            rs.close();
            st.close();

//            setArticle = con.prepareStatement("UPDATE names SET wordsCount = ? WHERE nameId = ?");
            setWord = con.prepareStatement("UPDATE names SET articlesCount = articlesCount + 1 WHERE nameId = ?");
            saveArticleStatement = con.prepareStatement("INSERT INTO articles_d (name, lemma, words_count) VALUES (?, ?, ?)");
            selectWordStatement = con.prepareStatement("SELECT 1 FROM words_d WHERE lemma = ?");
            saveWordStatement = con.prepareStatement("INSERT INTO words_d (name, lemma, articles_count) VALUES (?, ?, 1)");
            updateWordStatement = con.prepareStatement("UPDATE words_d SET articles_count = articles_count + 1 WHERE lemma = ?");

            getRelation = con.prepareStatement("SELECT 1 FROM semmatrix WHERE article_id = ? AND word_id = ?");
            saveRelation = con.prepareStatement("INSERT INTO semmatrix (article_id, word_id, score, article_lemma, word_lemma) VALUES (?, ?, ?, ?, ?)");
            updateRelation = con.prepareStatement("UPDATE semmatrix SET score = score + ? WHERE article_lemma = ? AND word_lemma = ?");

            selectArticleStatement = con.prepareStatement("SELECT * FROM semmatrix WHERE article_id = ?");
            deleteArticleStatement = con.prepareStatement("DELETE FROM semmatrix WHERE article_id = ?");

          insertIntoWords = con.prepareStatement( "INSERT INTO words (lemma, is_article) SELECT ?, TRUE WHERE NOT EXISTS (SELECT lemma FROM words WHERE lemma = ?)" );
          setArticle = con.prepareStatement( "UPDATE words SET is_article = TRUE WHERE lemma = ?" );
          getWordId = con.prepareStatement( "SELECT id FROM words WHERE lemma = ?" );
        } catch (SQLException ex) {
          ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static DAO getInstance() {
        return instance;
    }

  public void insertWord(String lemma, boolean isArticle)
  {
    try
    {
      insertIntoWords.setString( 1, lemma );
      insertIntoWords.setString( 2, lemma );
      insertIntoWords.addBatch();

      if( ++count % batch_size == 0 )
        insertIntoWords.executeBatch();
    }
    catch( SQLException e )
    {
      e.printStackTrace();
    }
  }

  public long getWordId(String lemma)
  {
    try
    {
      getWordId.setString( 1, lemma );
      ResultSet rs = getWordId.executeQuery();
      rs.next();
      return rs.getLong( "id" );
    }
    catch( SQLException e )
    {
      e.printStackTrace();
      throw new RuntimeException( e);
    }
  }

  public void setArticle(String lemma)
  {
    try
    {
      setArticle.setString( 1, lemma );
      setArticle.executeUpdate();
    }
    catch( SQLException e )
    {
      e.printStackTrace();
    }
  }

    public Map<String, Integer> getScoresForArticle(String article_lemma) {
        Map<String, Integer> scores = new HashMap<String, Integer>();
        try {

            Statement statement = con.createStatement();
            String sql = "select word_lemma, score from semmatrix_d where article_lemma='" + article_lemma + "'";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next())
            {
                String word_lemma = resultSet.getString("word_lemma");
                Integer score = resultSet.getInt("score");
                scores.put(word_lemma, score);
            }
        } catch (SQLException e) {

            System.exit(0);
        }

        return scores;
    }

    public List<String> getDistinctArticles() {
        List<String> result = new ArrayList<String>();
        String sql = "select distinct article_lemma from semmatrix_d";
        try {
            Statement statement = con.createStatement();
//            log.info("Executing query for distinct article lemmas...");
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result.add(resultSet.getString("article_lemma"));
            }
        } catch (SQLException e) {
//            log.error(e);
            System.exit(0);
        }
        return result;
    }

    public void relate(String article_lemma, String word_lemma, int score) {
        try {
            getRelation.setString(1, article_lemma);
            getRelation.setString(2, word_lemma);
            ResultSet rs = getRelation.executeQuery();
            if (rs.next()) {
                updateRelation.setInt(1, score);
                updateRelation.setString(2, article_lemma);
                updateRelation.setString(3, word_lemma);
                updateRelation.executeUpdate();
            } else {
                saveRelation.setString(1, article_lemma);
                saveRelation.setString(2, word_lemma);
                saveRelation.setInt(3, score);
                saveRelation.executeUpdate();
            }
        } catch (SQLException e) {
//            log.error(e);
          e.printStackTrace();
            System.exit(0);
        }
    }

    public void relate2(String article_lemma, Map<String, Integer> points) {
        try {
          long articleId = getWordId( article_lemma );
            selectArticleStatement.setLong(1, articleId);
            ResultSet rs = selectArticleStatement.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                String word_lemma = rs.getString("word_lemma");
                int lastScore = rs.getInt("score");
                int newScore = points.containsKey(word_lemma) ? points.get(word_lemma) : 0;
                int score = lastScore + newScore;
                points.put(word_lemma, score);
            }
            if (found) {
                deleteArticleStatement.setLong(1, articleId);
                deleteArticleStatement.execute();
            }
            for (Map.Entry<String, Integer> entry : points.entrySet()) {
              long wordId = getWordId( entry.getKey() );
                saveRelation.setLong(1, articleId);
                saveRelation.setLong(2, wordId);
                saveRelation.setInt(3, entry.getValue());
              saveRelation.setString( 4, article_lemma );
              saveRelation.setString( 5, entry.getKey() );
                saveRelation.addBatch();
            }
            saveRelation.executeBatch();
        } catch (SQLException e) {
//            log.error(e);
          e.printStackTrace();
            System.exit(0);
        }
    }

  public void writeToFile() throws IOException
  {
    File file = new File("assets/fact.txt");
    if( !file.exists() )
    {
      file.createNewFile();
    }
    PrintWriter pw = new PrintWriter( file );

    long lastId = -1;
    boolean hasNew;
    while( true )
    {
      try
      {
        PreparedStatement stmt = con.prepareStatement( "SELECT * FROM semmatrix WHERE id > ? ORDER BY id LIMIT ?" );
        stmt.setLong(1, lastId);
        stmt.setLong( 2, 100000 );
        ResultSet rs = stmt.executeQuery();
        hasNew = false;
        while( rs.next() )
        {
          hasNew = true;
          long article_id = rs.getLong( "article_id" );
          long word_id = rs.getLong( "word_id" );
          int score = rs.getInt( "score" );
          pw.println(article_id + " " + word_id + " " + score);
          lastId = rs.getLong( "id" );
          if( lastId % 100 == 0 )
          {
            System.out.println(new Date() + ": processed " + lastId + " rows");
          }
        }
        if( !hasNew )
          break;
      }
      catch( SQLException e )
      {
        e.printStackTrace();
        pw.close();
      }
    }
    pw.close();
  }

}