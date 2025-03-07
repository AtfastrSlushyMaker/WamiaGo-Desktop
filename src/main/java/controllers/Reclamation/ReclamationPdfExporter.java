package controllers.Reclamation;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;
import entities.Reclamation;
import entities.Response;
import entities.User;
import javafx.stage.FileChooser;
import services.ResponseService;
import utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Handles the generation of personalized PDF reports for reclamations
 */
public class ReclamationPdfExporter {

    private static final String LOGO_PATH = "src/main/resources/images/logo/wamiaGO.png";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Exports a reclamation with all its responses to a PDF file
     *
     * @param reclamation The reclamation to export
     * @param parentController The parent controller (for displaying file chooser dialog)
     * @return true if export was successful, false otherwise
     */
    public static boolean exportReclamationToPdf(Reclamation reclamation, Object parentController) {
        // Create file chooser for saving the PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Reclamation PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Reclamation_" + reclamation.getIdReclamation() + ".pdf");

        // Get save location from user
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return false; // User canceled the operation
        }

        try {
            // Setup PDF document
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);

            // Add document styling
            document.setMargins(36, 36, 36, 36);

            // Load fonts
            PdfFont titleFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica");

            // Add header with logo
            addHeader(document, reclamation);

            // Add reclamation details
            addReclamationDetails(document, reclamation, titleFont, regularFont);

            // Get responses
            ResponseService responseService = new ResponseService();
            List<Response> responses = responseService.getResponsesByReclamationId(reclamation.getIdReclamation());

            // Add responses section
            addResponses(document, responses, titleFont, regularFont);

            // Add footer
            addFooter(document);

            // Close document
            document.close();

            return true;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a header section to the PDF with logo and title
     */
    private static void addHeader(Document document, Reclamation reclamation) throws IOException {
        // Create a table for header layout
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Add logo
        try {
            Image logo = new Image(ImageDataFactory.create(LOGO_PATH));
            logo.setWidth(100);
            logo.setHeight(60);
            Cell logoCell = new Cell().add(logo).setBorder(null);
            headerTable.addCell(logoCell);
        } catch (Exception e) {
            // If logo loading fails, just add text instead
            Cell logoCell = new Cell().add(new Paragraph("WamiaGO"))
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBorder(null);
            headerTable.addCell(logoCell);
        }

        // Add title section
        Cell titleCell = new Cell();
        titleCell.setBorder(null);
        titleCell.setTextAlignment(TextAlignment.RIGHT);

        String status = reclamation.getStatus() == 0 ? "PENDING" : "RESOLVED";
        DeviceRgb statusColor = reclamation.getStatus() == 0 ?
                new DeviceRgb(209, 47, 47) : new DeviceRgb(56, 142, 60);

        titleCell.add(new Paragraph("RECLAMATION REPORT")
                .setFontSize(18)
                .setBold());

        titleCell.add(new Paragraph("Ref: #" + reclamation.getIdReclamation())
                .setFontSize(12));

        titleCell.add(new Paragraph("Status: " + status)
                .setFontSize(12)
                .setFontColor(statusColor)
                .setBold());

        headerTable.addCell(titleCell);

        // Add to document
        document.add(headerTable);

        // Add separator
        document.add(new Paragraph("")
                .setFixedPosition(36, 760, PageSize.A4.getWidth() - 72)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setMarginBottom(15));
    }

    /**
     * Adds the reclamation details to the PDF
     */
    private static void addReclamationDetails(Document document, Reclamation reclamation,
                                              PdfFont titleFont, PdfFont regularFont) {
        document.add(new Paragraph("RECLAMATION DETAILS")
                .setFont(titleFont)
                .setFontSize(14)
                .setMarginTop(20)
                .setMarginBottom(10));

        // Create a table for details
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        detailsTable.setWidth(UnitValue.createPercentValue(100));

        // Add various details rows
        addDetailRow(detailsTable, "Title:", reclamation.getTitle());
        addDetailRow(detailsTable, "Submitted by:", reclamation.getUser().getName());
        addDetailRow(detailsTable, "Date submitted:", DATE_FORMAT.format(reclamation.getDate()));

        // Add description with full width
        detailsTable.addCell(new Cell(1, 2)
                .add(new Paragraph("Description:").setBold())
                .setBorder(null)
                .setPaddingTop(10));

        detailsTable.addCell(new Cell(1, 2)
                .add(new Paragraph(reclamation.getContent()))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setPadding(10));

        document.add(detailsTable);
    }

    /**
     * Helper method to add a detail row to the table
     */
    private static void addDetailRow(Table table, String label, String value) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setBold())
                .setBorder(null)
                .setPaddingTop(5));

        table.addCell(new Cell()
                .add(new Paragraph(value))
                .setBorder(null)
                .setPaddingTop(5));
    }

    /**
     * Adds the responses section to the PDF
     */
    private static void addResponses(Document document, List<Response> responses,
                                     PdfFont titleFont, PdfFont regularFont) throws SQLException {
        document.add(new Paragraph("RESPONSES")
                .setFont(titleFont)
                .setFontSize(14)
                .setMarginTop(20)
                .setMarginBottom(10));

        if (responses.isEmpty()) {
            document.add(new Paragraph("No responses have been recorded for this reclamation.")
                    .setItalic()
                    .setFontColor(ColorConstants.GRAY));
            return;
        }

        ResponseService responseService = new ResponseService();

        // Create responses table
        Table responsesTable = new Table(UnitValue.createPercentArray(new float[]{100}));
        responsesTable.setWidth(UnitValue.createPercentValue(100));

        for (int i = 0; i < responses.size(); i++) {
            Response response = responses.get(i);
            User responder = responseService.getUserFromResponse(response);

            // Create a cell for each response
            Cell responseCell = new Cell();
            responseCell.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));
            responseCell.setPadding(10);

            // Add response number and metadata
            responseCell.add(new Paragraph("Response #" + (i + 1))
                    .setBold()
                    .setFontSize(12));

            responseCell.add(new Paragraph("From: " + responder.getName())
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY));

            responseCell.add(new Paragraph("Date: " + DATE_FORMAT.format(response.getDate()))
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(5));

            // Add response content
            responseCell.add(new Paragraph(response.getContent())
                    .setFontSize(11));

            responsesTable.addCell(responseCell);

            // Add spacing between responses
            if (i < responses.size() - 1) {
                responsesTable.addCell(new Cell()
                        .setBorder(null)
                        .setHeight(10));
            }
        }

        document.add(responsesTable);
    }

    /**
     * Adds a footer to the PDF
     */
    private static void addFooter(Document document) {
        // Create footer text
        Paragraph footer = new Paragraph(
                "Generated on " + DATE_FORMAT.format(new Date()) + " | " +
                        "WamiaGO Reclamation System | " +
                        "This is an automatically generated document.")
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        // Add footer to document
        document.add(new Paragraph("").setMarginTop(30));
        document.add(footer);
    }
}