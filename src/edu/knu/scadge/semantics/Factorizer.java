package edu.knu.scadge.semantics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: developer
 * Date: 26.05.14
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public class Factorizer {
    public void process(String article_lemma) {
        Map<String, Integer> scores = DAO.getInstance().getScoresForArticle(article_lemma);
        addToFile(article_lemma, scores);
    }

    public void addToFile(String article_lemma, Map<String, Integer> scores) {
        String[] splitted = article_lemma.split(" ");
        if(splitted.length > 1) {
            StringBuilder sb = new StringBuilder();
            for(String item : splitted)
            {
                sb.append(item+" ");
            }
            article_lemma = sb.toString().trim();
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter("D:/fact.txt", true)));
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                out.println(article_lemma + " " + entry.getKey() + " " + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (out != null) out.close();
        }
    }
}
