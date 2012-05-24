package com.idivine.solr.analysis;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.PatternReplaceFilterFactory;
import org.apache.solr.analysis.TrimFilterFactory;

public class ExactAnalyzer extends Analyzer {
	private static final int BUFFER_SIZE = 1024;
	private Map<String, String> patternMap = new HashMap<String, String>();
	private PatternReplaceFilterFactory patternReplaceFilterFactory = null;
	private LowerCaseFilterFactory lowerCaseFilterFactory = null;
	private TrimFilterFactory trimFilterFactory = null;

	public ExactAnalyzer() {
		// settings for PatternReplaceFilterFactory
        patternMap.put("pattern" , "([^a-z0-9\\s])");
        patternMap.put("replacement" , "");
        patternMap.put("replace" , "all");
        patternReplaceFilterFactory = new PatternReplaceFilterFactory();
		patternReplaceFilterFactory.init(patternMap);
		
		// instantiate lowercasefilterfactory
		lowerCaseFilterFactory = new LowerCaseFilterFactory();
		// instantiate trimfilterfactory
		trimFilterFactory = new TrimFilterFactory();
		
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		//Get the input token String
		char[] buffer = new char[BUFFER_SIZE];
		int charRead = 0;
		StringBuffer inputTokenString = new StringBuffer();
		try {
			while((charRead = reader.read(buffer)) != -1 ) {
				inputTokenString.append(String.valueOf(buffer, 0, charRead));
			}
		} catch (IOException ioe) {
			System.out.println("IOException while reading the inputstream for ExactAnalyzer.");
			ioe.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception while reading the inputstream for ExactAnalyzer.");
			ex.printStackTrace();
		}
		
		//Form the output token string
		Pattern p = Pattern.compile("([0-9]+|[A-Za-z]+)");
		Matcher m = p.matcher(inputTokenString.toString());
		
		StringBuffer outputTokenString = new StringBuffer("");
		while(m.find()) {
			outputTokenString.append(m.group()).append(" ");
		}
		
		//Form the TokenStream
		reader = new StringReader(outputTokenString.toString().toLowerCase());

		TokenStream tokenStream = new KeywordTokenizer(reader);
		tokenStream = trimFilterFactory.create(tokenStream);
		return tokenStream;
	}
	
	public static void main(String [] args) {
		Analyzer analyzer = new ExactAnalyzer();
		TokenStream tokenStream = analyzer.tokenStream("testfield", new StringReader(" NewYork2 23as    df34assdf$#asdSDRfd"));
	}

}
