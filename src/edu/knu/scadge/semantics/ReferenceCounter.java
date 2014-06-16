package edu.knu.scadge.semantics;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.util.CoreMap;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;
//import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by scadge on 3/11/14.
 */
public class ReferenceCounter implements PageHandler {
    private WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");
//    private Logger log = Logger.getLogger("Processor");

    private StanfordCoreNLP pipeline;

    public ReferenceCounter() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        this.pipeline = new StanfordCoreNLP(props);
    }

    @Override
    public void handle(String title, String text) {
        Map<String, Integer> points = new HashMap<>();
        Map<String, String> lemmas = new HashMap<>();


        if (!text.startsWith("#REDIRECT ")) {
            // Zap headings ==some text== or ===some text===

            // <ref>{{Cite web|url=http://tmh.floonet.net/articles/falseprinciple.html |title="The False Principle of our Education" by Max Stirner |publisher=Tmh.floonet.net |date= |accessdate=2010-09-20}}</ref>
            // <ref>Christopher Gray, ''Leaving the Twentieth Century'', p. 88.</ref>
            // <ref>Sochen, June. 1972. ''The New Woman: Feminism in Greenwich Village 1910Р1920.'' New York: Quadrangle.</ref>

            // String refexp = "[A-Za-z0-9+\\s\\{\\}:_=''|\\.\\w#\"\\(\\)\\[\\]/,?&%Р-]+";
            String wikiText = text.
                    replaceAll("[=]+[A-Za-z+\\s-]+[=]+", " ").
                    replaceAll("\\{\\{[A-Za-z0-9+\\s-]+\\}\\}", " ").
                    replaceAll("(?m)<ref>.+</ref>", " ").
                    replaceAll("(?m)<ref name=\"[A-Za-z0-9\\s-]+\">.+</ref>", " ").
                    replaceAll("<ref>", " <ref>");

            // Remove text inside {{ }}
            String plainStr = wikiModel.render(new PlainTextConverter(), wikiText).
                    replaceAll("\\{\\{[A-Za-z+\\s-]+\\}\\}", " ");

            if (!plainStr.trim().isEmpty()) {
                Annotation document = new Annotation(plainStr);

                this.pipeline.annotate(document);

                List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

                boolean firstSentence = true;
                for (CoreMap sentence : sentences) {
                    for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                        String word = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                        int score = firstSentence ? 3 : 1;
                        if (pos.startsWith("N") && word.matches("^[A-Za-z0-9]+$")) {
                            if (points.containsKey(word)) {
                                points.put(word, points.get(word) + score);
                            } else {
                                points.put(word, score);
                            }
                            lemmas.put(word, token.value());
                        }
                    }
                    firstSentence = false;
                }
            }
        }
        Annotation document = new Annotation(title);
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

        if (points.size() > 0) {
//            DAO.getInstance().addArticle(title, titleLemma, points.size());
//            log.info("Saved article " + title + " lemma: " + titleLemma);
//            for (Map.Entry<String, Integer> entry : points.entrySet()) {
//                DAO.getInstance().addWord(lemmas.get(entry.getKey()), entry.getKey());
//            }
//            log.info("Saved words for lemma article: " + titleLemma);

            DAO.getInstance().relate2(titleLemma, points);
//            log.info("Saved relations for lemma: " + titleLemma);
        }


    }
}
