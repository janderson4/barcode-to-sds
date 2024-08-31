package com.example.main_activity.backend;

import android.util.Log;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class ParsePdf {
    public static void pdf_parser(String[] urls, int[] backup_scores, int index, String path_of_pdf, boolean from_pdf, Data data1) {
        Log.d("check", "about to parse pdf at "+path_of_pdf);
        PDFBoxResourceLoader.init(data1.current_activity);
        File file = new File(path_of_pdf);
        PDFTextStripper pdfStripper = null;

        try {
            Log.d("check", "just started trying parse for "+file.toString());
            PDDocument document = PDDocument.load(file);
            PDDocument document1pg;
            pdfStripper = new PDFTextStripper();
            Log.d("check", "path: " + path_of_pdf);
            String text;
            try {
                document1pg = new PDDocument();
                document1pg.addPage((PDPage)document.getPages().get( 0 ));
                Log.d("check", "shortened page");
                text = pdfStripper.getText(document1pg);
                document1pg.close();
            }catch(Exception e){
                Log.d("check", "failed to shorten page");
                text = pdfStripper.getText(document);
            }
            document.close();
            AnalyzePdf.pdf_analysis(urls, backup_scores, index, text, from_pdf, data1);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            Log.d("check", "didn't manage to parse that pdf");
            Log.d("check","about to call fail_routine from parser for index: "+index);
            PdfFailRoutine.pdf_fail_routine(urls,backup_scores,index,from_pdf, data1);
        }
    }
}
