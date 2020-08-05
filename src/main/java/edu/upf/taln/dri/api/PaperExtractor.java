package edu.upf.taln.dri.api;

import edu.upf.taln.dri.lib.Factory;
import edu.upf.taln.dri.lib.exception.DRIexception;
import edu.upf.taln.dri.lib.model.Document;
import edu.upf.taln.dri.lib.model.ext.*;
import edu.upf.taln.dri.lib.util.ModuleConfig;
import edu.upf.taln.dri.lib.util.PDFtoTextConvMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

// IMPORTANT: Before running, edit run configurations and add the following to VM arguments: -Xmx8096m
// This is done to prevent out of memory exceptions and the application failing from garbage collector taking up too many resources

public class PaperExtractor {
    public static void main(String[] args) {

        // Set resource path in properties file!
        // TODO: change to relative path in project instead of absolute path
        Factory.setDRIPropertyFilePath("D:/Libraries/Documents/Uni/Informatik/Master/Masterarbeit/drinventor/DRIconfig.properties");

        // Settings for the extraction process
        Factory.setPDFtoTextConverter(PDFtoTextConvMethod.GROBID);

        // Read descriptions in the ModuleConfig documentation
        ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.setEnableBibEntryParsing(false);
        moduleConfig.setEnableBabelNetParsing(true);
        moduleConfig.setEnableHeaderParsing(false);
        moduleConfig.setEnableTerminologyParsing(true);
        moduleConfig.setEnableGraphParsing(false);
        moduleConfig.setEnableCoreferenceResolution(false);
        moduleConfig.setEnableCausalityParsing(true);
        moduleConfig.setEnableRhetoricalClassification(true);

        Factory.setModuleConfig(moduleConfig);

        // Library init
        try {
            Factory.initFramework();
        } catch (DRIexception drIexception) {
            System.out.println("Error initializing the framework.");
            drIexception.printStackTrace();
        }

        System.out.println("Importing PDF...");

        // Loading PDF from URL
        Document pdfPaper = null;
        try {
            pdfPaper = Factory.getPDFloader().parsePDF(new URL("https://arxiv.org/pdf/2004.01136v2.pdf"));
        } catch (MalformedURLException e) {
            System.out.println("Error with URL format while importing PDF.");
            e.printStackTrace();
        } catch (DRIexception drIexception) {
            System.out.println("Error importing a PDF from URL.");
            drIexception.printStackTrace();
        }

        // Preprocessing
        System.out.println("Starting preprocessing...");
        try {
            pdfPaper.preprocess();
        } catch (DRIexception drIexception) {
            System.out.println("Error while preprocessing PDF.");
            drIexception.printStackTrace();
        }


        // Extractions
        System.out.println("Extracting information...");
        try {
            List<Sentence> abstractSentenceList = pdfPaper.extractSentences(SentenceSelectorENUM.ALL);
            for (Sentence sentence : abstractSentenceList) {
                System.out.println("Abstract Sentence: " + sentence.asString(true));
            }

            List<Citation> citationList = pdfPaper.extractCitations();
            for (Citation citation : citationList) {
                System.out.println("Citations: " + citation.asString(true));
            }

            List<Section> sectionList = pdfPaper.extractSections(true);
            for (Section section : sectionList) {
                System.out.println("Root Section: " + section.asString(true));
            }

            List<Sentence> sentenceSummaryTitile = pdfPaper.extractSummary(15, SummaryTypeENUM.TITILE_SIM);
            for (Sentence sentence : sentenceSummaryTitile) {
                System.out.println(sentence.getText());
            }

            List<Sentence> sentenceSummaryLex = pdfPaper.extractSummary(15, SummaryTypeENUM.LEX_RANK);
            for (Sentence sentence : sentenceSummaryLex) {
                System.out.println(sentence.getText());
            }

        } catch (DRIexception e) {
            System.out.println("Error while extracting information from PDF.");
            e.printStackTrace();
        }

    }
}
