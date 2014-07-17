package syntaxhighlighter;

import java.util.List;

import syntaxhighlighter.beans.CodeContainer;
import syntaxhighlighter.brush.Brush;

/**
 * Utility class to create list of code rows with list of code per row
 * using SyntaxHighlighterParser and a given brush. 
 * 
 * @author Holger Schimanski <holger@schimanski-web.de>
 */
public class SyntaxHighlighterParserUtil {

	public static CodeContainer brush(String aText, Brush aBrush) {

		//Remove leading line feed
		if ( aText.startsWith("\n")) {
			aText = aText.substring(1);
		} else if ( aText.startsWith("\r\n")) {
			aText = aText.substring(2);
		}
		
		//Remove trailing line feed
		if ( aText.endsWith("\n")) {
			aText = aText.substring(0, aText.length()-1);
		} else if ( aText.endsWith("\r\n")) {
			aText = aText.substring(0, aText.length()-2);
		}
		
		//Parse text
		SyntaxHighlighterParser aParser = new SyntaxHighlighterParser(aBrush);
		List<ParseResult> aResultList = aParser.parse(null, aText);

		//Create code container based on parse results
		CodeContainer container = new CodeContainer();

		int currentIndex = 0;
		for (ParseResult parseResult : aResultList) {
			if (parseResult.getOffset() >= currentIndex) {

				String plainText = aText.substring(currentIndex, parseResult.getOffset());
				addCodeAndRowElements(container, plainText, "plain");

				String formattedText = aText.substring(parseResult.getOffset(), parseResult.getOffset() + parseResult.getLength());
				addCodeAndRowElements(container, formattedText, parseResult.getStyleKeysString());
				
				currentIndex = parseResult.getOffset() + parseResult.getLength();
			}
		}

		String plainTextRemaining = aText.substring(currentIndex);
		addCodeAndRowElements(container, plainTextRemaining, "plain");

		return container;

	}

	private static void addCodeAndRowElements(CodeContainer container, String plainText, String style) {

		plainText = plainText.replaceAll("\r\n", "\n");
		
		while (!plainText.equals("")) {

			if (!plainText.contains("\n")) {
				container.addCode(style, plainText);
				plainText = "";
			} else if (plainText.startsWith("\n")) {
				container.newCodeRow();
				if (plainText.equals("\n")) {
					plainText = "";
				} else {
					plainText = plainText.substring(1);
				}
			} else {
				container.addCode(style, plainText.substring(0, plainText.indexOf("\n")));
				plainText = plainText.substring(plainText.indexOf("\n"));
			}
		}

	}

}
