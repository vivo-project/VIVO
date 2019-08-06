/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFDocument {
	public PDFDocument(String authorName,
					   Map<String, Integer> yearToPublicationCount,
					   Document document,
					   PdfWriter pdfWriter) {

//        setPreferredSize(new Dimension(600,400));

		try {

		document.addTitle("PDF Pipeline iText Prototype");
		document.addAuthor(authorName);
		document.addSubject("This example tests text, color, image, transparency & table functionality.");
		document.addKeywords("text, color, image, transparency, table");
		document.addCreator("Standalone PDF Renderer using iText");

		Paragraph header = new Paragraph();

		Font pageHeaderStyle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 15, Font.BOLDITALIC | Font.UNDERLINE);
		Font featureHeaderStyle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, BaseColor.RED);

		header.add(new Chunk("PDF Pipeline Prototype v2 using iText\n",
							 pageHeaderStyle));

		header.setSpacingAfter(15f);


			document.add(header);


		Paragraph content = new Paragraph();

		content.add(new Chunk("Publication Count - Author Name - " + authorName,
							 featureHeaderStyle));

		content.setSpacingAfter(15f);

		document.add(content);
		// step4

		PdfPTable publicationCount = createTable(yearToPublicationCount);

		document.add(publicationCount);

		content = new Paragraph();

		content.add(new Chunk("Transparency of Shapes",
				 featureHeaderStyle));

		content.setSpacingAfter(15f);

		document.add(content);

		createTransparencyShapes(document, pdfWriter);


        createImage(document, pdfWriter, featureHeaderStyle);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }


	private void createImage(Document document, PdfWriter writer,
			Font featureHeaderStyle) throws BadElementException,
			MalformedURLException, IOException, DocumentException {
		Image imageSprite = Image.getInstance(new URL("http://lh3.ggpht.com/_4msVPAgKJv8/SCRYD-pPVKI/AAAAAAAAAYU/zUN963EPoZc/s1024/102_0609.JPG"));
		imageSprite.setAbsolutePosition(400, 500);
		imageSprite.scaleAbsolute(171.0f, 250.0f);
		float imageSpriteY = document.getPageSize().getHeight() * 0.60f;
		float imageSpriteX = document.getPageSize().getWidth() * 0.65f;
		imageSprite.setAlignment(Image.UNDERLYING);

		document.add(imageSprite);

		PdfContentByte cb = writer.getDirectContent();
		ColumnText ct = new ColumnText(cb);
		Chunk imageHeader = new Chunk("Images",
									  featureHeaderStyle);
		ct.addText(imageHeader);
		ct.setAlignment(Element.ALIGN_LEFT);
		ct.setSimpleColumn(imageSpriteX, imageSpriteY - imageSprite.getScaledHeight(),
				   imageSpriteX + imageSprite.getScaledWidth(), imageSpriteY + imageSprite.getScaledHeight() + 20);
		ct.go();

		ct = new ColumnText(cb);
		Chunk imageFooter = new Chunk("Footer to be set for a figure. Similar to 'image cpation'.",
									  FontFactory.getFont(FontFactory.TIMES_ROMAN, 8));
		ct.addText(imageFooter);
		ct.setAlignment(Element.ALIGN_CENTER);
		ct.setSimpleColumn(imageSpriteX, imageSpriteY - 150, imageSpriteX + imageSprite.getScaledWidth(), imageSpriteY);
		ct.go();
	}

	private void createTransparencyShapes(Document document,
			PdfWriter writer) throws Exception {
		PdfContentByte cb = writer.getDirectContent();

		pictureBackdrop(document.leftMargin(), 350, cb);
		cb.saveState();
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(0.5f);
		cb.setGState(gs1);
		pictureCircles(document.leftMargin(), 350, cb);
		cb.restoreState();

        cb.resetRGBColorFill();
	}

    /**
     * Prints a square and fills half of it with a gray rectangle.
     * @param x X coordinate
     * @param y Y coordinate
     * @param cb Content byte
     * @throws Exception
     */
    public void pictureBackdrop(float x, float y, PdfContentByte cb) throws Exception {
        cb.setColorStroke(BaseColor.BLACK);
        cb.setColorFill(BaseColor.GRAY);
        cb.rectangle(x, y, 100, 200);
        cb.fill();
        cb.setLineWidth(2);
        cb.rectangle(x, y, 200, 200);
        cb.stroke();
    }

    /**
     * Prints 3 circles in different colors that intersect with eachother.
     * @param x X coordinate
     * @param y Y coordinate
     * @param cb Content byte
     * @throws Exception
     */
    public void pictureCircles(float x, float y, PdfContentByte cb) throws Exception {

		cb.saveState();
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(1.0f);
		cb.setGState(gs1);
        cb.setColorFill(BaseColor.RED);
        cb.circle(x + 70, y + 70, 50);
        cb.fill();
		cb.restoreState();

        cb.setColorFill(BaseColor.YELLOW);
        cb.circle(x + 100, y + 130, 50);
        cb.fill();
        cb.setColorFill(BaseColor.BLUE);
        cb.circle(x + 130, y + 70, 50);
        cb.fill();
    }

	private PdfPTable createTable(Map<String, Integer> yearToPublicationCount) {

		Font normalContentStyle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11);
		Font summaryContentStyle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLDITALIC);
		BaseColor summaryBackgroundColor = new BaseColor(0xEE, 0xEE, 0xEE);
		BaseColor headerBackgroundColor = new BaseColor(0xC3, 0xD9, 0xFF);
		BaseColor bodyBackgroundColor = BaseColor.WHITE;

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(36.0f);

		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.getDefaultCell().setBorderWidth(0.0f);
		table.setHeaderRows(2);

		PdfPCell cell;
		cell = new PdfPCell(new Phrase("Publications per year", normalContentStyle));
		setTableCaptionStyle(summaryBackgroundColor, cell);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Year", normalContentStyle));
		setTableHeaderStyle(headerBackgroundColor, cell);
		table.addCell(cell);

		cell.setPhrase(new Phrase("Publications", normalContentStyle));
		table.addCell(cell);



		setTableBodyStyle(bodyBackgroundColor, cell);
		int totalPublications = 0;

		for (Entry<String, Integer> currentEntry : yearToPublicationCount.entrySet()) {

			cell.setPhrase(new Phrase(currentEntry.getKey(), normalContentStyle));
			table.addCell(cell);

			cell.setPhrase(new Phrase(currentEntry.getValue().toString(), normalContentStyle));
			table.addCell(cell);

			totalPublications += currentEntry.getValue();
		}

		setTableFooterStyle(summaryBackgroundColor, cell);
		cell.setPhrase(new Phrase("Total", summaryContentStyle));
		table.addCell(cell);

		cell.setPhrase(new Phrase(String.valueOf(totalPublications), summaryContentStyle));
		table.addCell(cell);

		return table;
	}

	private void setTableFooterStyle(BaseColor footerBackgroundColor,
			  PdfPCell cell) {
		cell.setBorderWidth(0.0f);
		cell.setBackgroundColor(footerBackgroundColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPaddingTop(5f);
		cell.setPaddingRight(10f);
		cell.setPaddingBottom(5f);
		cell.setPaddingLeft(10f);
	}

	private void setTableBodyStyle(BaseColor bodyBackgroundColor,
										  PdfPCell cell) {
		cell.setBorderWidth(0.0f);
		cell.setBackgroundColor(bodyBackgroundColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPaddingTop(5f);
		cell.setPaddingRight(10f);
		cell.setPaddingBottom(5f);
		cell.setPaddingLeft(10f);
	}

	private void setTableHeaderStyle(BaseColor headerBackgroundColor,
			PdfPCell cell) {
		cell.setBorderWidth(0.0f);
		cell.setBackgroundColor(headerBackgroundColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPaddingTop(5f);
		cell.setPaddingRight(10f);
		cell.setPaddingBottom(5f);
		cell.setPaddingLeft(10f);
	}

	private void setTableCaptionStyle(BaseColor summaryBackgroundColor,
			PdfPCell cell) {
		cell.setBorderWidth(0.0f);
		cell.setBackgroundColor(summaryBackgroundColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPaddingTop(5.0f);
		cell.setPaddingRight(10.0f);
		cell.setPaddingBottom(5.0f);
		cell.setPaddingLeft(10.0f);
		cell.setColspan(2);
	}

}
