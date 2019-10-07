package parserchain;

import model.ParagraphComposite;
import model.TextComposite;
import model.TextLeaf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceParser extends BaseTextParser {

    private static final String SENTENCE_REGEX = "([A-Z])(.)+?([\\.]+)";

    @Override
    public TextLeaf parse(String text) {
        TextComposite composite = new ParagraphComposite();

        Pattern pattern = Pattern.compile(SENTENCE_REGEX);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String sSentence = matcher.group();
            TextLeaf tlSentence = nextParse(sSentence);
            composite.add(tlSentence);
        }

        return composite;
    }

}
