package be.umons.projetjava.visitor;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
@Component
public class PdfReportExporter {
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public Path exportToPdf(String visitorName, String title, String content) {
        try {
            Path reportsDir = Path.of("reports");
            Files.createDirectories(reportsDir);
            Path outputFile = reportsDir.resolve("rapport_" + visitorName + "_" + LocalDateTime.now().format(FILE_TIME) + ".pdf");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputFile.toFile()));
            document.open();
            document.add(new Paragraph(title, new Font(Font.HELVETICA, 16, Font.BOLD)));
            document.add(new Paragraph("Genere le : " + LocalDateTime.now().format(DISPLAY_TIME), new Font(Font.HELVETICA, 10, Font.ITALIC)));
            document.add(new Paragraph(" "));
            Font bodyFont = new Font(Font.COURIER, 10, Font.NORMAL);
            for (String line : content.split("\\R")) document.add(new Paragraph(line.isEmpty() ? " " : line, bodyFont));
            document.close();
            return outputFile;
        } catch (IOException ex) {
            throw new RuntimeException("Impossible d'ecrire le fichier PDF", ex);
        }
    }
}
