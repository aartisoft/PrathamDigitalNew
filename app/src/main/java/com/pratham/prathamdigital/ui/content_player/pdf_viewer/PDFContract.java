package com.pratham.prathamdigital.ui.content_player.pdf_viewer;

import android.graphics.Bitmap;

import java.util.ArrayList;

public interface PDFContract {
    interface pdf_View {
        void recievedBitmaps(ArrayList<Bitmap> bitmaps);
    }

    interface pdfPresenter {
        void setView(Fragment_PdfViewer activity_pdfViewer);

        void generateImageFromPdf(String pdfPath);

        void addScoreToDB(String resId, String startTime, int pageSelected);
    }
}
