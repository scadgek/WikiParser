package edu.knu.scadge.semantics; /**
 * Created by scadge on 2/9/14.
 */

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CoreMap;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;
//import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.net.www.content.text.plain;

/**
 * Processes wikipedia articles file.
 * Parses an article an passes it to words handler.
 */
public class WikiPageHandler extends DefaultHandler {

    public static final String LAST_ARTICLE_FILE = "last_article.txt";

    private StanfordCoreNLP pipeline;
    private Factorizer factorizer;

    private PageHandler innerHandler;
    private boolean isInText = false;
    private boolean isInTitle = false;
    private long count = 0;
    private StringBuffer currentPage = new StringBuffer();
    private StringBuffer currentTitle = new StringBuffer();
    private String savedInit = null;
    private boolean articleShouldBeProcessed = false;

    private String getLast() {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(LAST_ARTICLE_FILE));
            String retVal = fr.readLine();
            fr.close();
            return retVal;
        } catch (IOException e) {
//            log.warn(e.toString());
            return null;
        }
    }

    private void setLast(String title) {
        try {
            FileWriter fw = new FileWriter(LAST_ARTICLE_FILE);
            fw.write(title);
            fw.close();
        } catch (IOException e) {
//            log.error(e.toString());
        }
    }

    public WikiPageHandler(PageHandler innerHandler) {
        this.innerHandler = innerHandler;
        savedInit = getLast();

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        this.pipeline = new StanfordCoreNLP(props);

        factorizer = new Factorizer();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("text")) {
            currentPage.delete(0, currentPage.length());
            isInText = true;
        } else if (qName.equalsIgnoreCase("title")) {
            currentTitle.delete(0, currentTitle.length());
            isInTitle = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("text")) {
            if (articleShouldBeProcessed) {
//                innerHandler.handle(currentTitle.toString(), currentPage.toString());
                Annotation document = new Annotation(currentTitle.toString());
                this.pipeline.annotate(document);
                List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
                StringBuilder lemma = new StringBuilder();
                for (CoreMap sentence : sentences) {
                    for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                        String word = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                        lemma.append(word).append(" ");
                    }
                }
                String titleLemma = lemma.toString().trim();
                factorizer.process(titleLemma);
            }
            isInText = false;
        } else if (qName.equalsIgnoreCase("title")) {
            count++;
            if (savedInit == null || savedInit.equals(currentTitle.toString())) {
                articleShouldBeProcessed = true;
            }
            if (articleShouldBeProcessed) {
                setLast(currentTitle.toString());
//                if (count%100==0)
//                log.info("New page # " + count + " : " + currentTitle.toString());
            }
            else {
//                if (count%100==0)
//                log.info("Old page # " + count + " : " + currentTitle.toString());
            }
            isInTitle = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isInText) {
            currentPage.append(ch, start, length);
        } else if (isInTitle) {
            currentTitle.append(ch, start, length);
        }
    }
}

