package de.tudarmstadt.ukp.dkpro.keyphrases.core.evaluator;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class KeyphraseDatasetStatisticsTest
{

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    public void testCollectionProcessComplete()
        throws UIMAException, IOException, InterruptedException
    {
        final CollectionReader reader = createReader(createReaderDescription(TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "src/test/resources/keyphrase/evaluator",
                TextReader.PARAM_PATTERNS, TextReader.INCLUDE_PREFIX + "*.txt"));
        final AnalysisEngine analysisEngine = createEngine(createEngineDescription(
                createEngineDescription(BreakIteratorSegmenter.class),
                createEngineDescription(KeyphraseDatasetStatistics.class)));

        final StringBuilder actual = new StringBuilder();
        final StringBuilderOutputStream fout = new StringBuilderOutputStream(actual);

        final MultiOutputStream multiOut = new MultiOutputStream(System.out, fout);

        final PrintStream stdout = new PrintStream(multiOut);

        System.setOut(stdout);
        SimplePipeline.runPipeline(reader, analysisEngine);
        final String[] lines = actual.toString().split(LINE_SEPARATOR);
        actual.delete(0, actual.length());
        for (int i = 1; i < lines.length; ++i) {
            actual.append(lines[i]);
        }
        final String expected = "# Documents:               1Tokens / Document:         "
                + "5.0(+/- 0.0) Median: 5.0)# Keyphrases:              1Keyphrases / "
                + "Document:     1.0(+/- 0.0)Characters / Keyphrase:    4.0(+/- 0.0)Tokens / "
                + "Keyphrase:        1.0(+/- 0.0)Upper bound (P@10):        0.09999999999999998"
                + "Upper bound (R@10):        1.0Pearson Correlation between document size and "
                + "the number of gold keyphrases:1:1";

        Assert.assertThat(actual.toString(), CoreMatchers.is(expected));

    }

}
