import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

@WebServlet(name = "WatermarkServlet", urlPatterns = {"/WatermarkServlet"})
@MultipartConfig
public class WatermarkServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part pdfPart = request.getPart("pdf");
        String watermarkText = request.getParameter("watermark");

        if (watermarkText == null || watermarkText.isEmpty()) {
            watermarkText = "EQUIPO ECONOMICO LUSCAS BRONICARDI";
        }

        PDDocument document = PDDocument.load(pdfPart.getInputStream());

        for (PDPage page : document.getPages()) {
            PDRectangle pageSize = page.getMediaBox();

            PDPageContentStream cs = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true);

            PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
            gs.setNonStrokingAlphaConstant(0.15f);
            cs.setGraphicsStateParameters(gs);

            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 48);
            cs.setNonStrokingColor(180, 180, 180);
            cs.setTextRotation(Math.toRadians(45),
                    pageSize.getWidth() / 4,
                    pageSize.getHeight() / 2);
            cs.showText(watermarkText);
            cs.endText();
            cs.close();
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=pdf_con_marca.pdf");

        document.save(response.getOutputStream());
        document.close();
    }
}
