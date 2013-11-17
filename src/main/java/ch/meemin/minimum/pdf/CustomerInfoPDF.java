package ch.meemin.minimum.pdf;

import java.awt.Color;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;

import ch.meemin.minimum.Minimum;
import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.settings.SettingImage;
import ch.meemin.minimum.entities.subscriptions.MasterSubscription;
import ch.meemin.minimum.entities.subscriptions.Subscription;
import ch.meemin.minimum.entities.subscriptions.TimeSubscription;
import ch.meemin.minimum.lang.Lang;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BarcodeInter25;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.vaadin.server.VaadinService;

public class CustomerInfoPDF {

	private static final float TOP_M = 35f;
	private static final float BOTTOM_M = 35f;
	private static final float RIGHT_M = 35f;
	private static final float LEFT_M = 35f;

	private Customer customer;
	private Subscription subscription;
	private final Minimum minimum;
	private Lang lang;
	// BaseFont bold, normal;
	Font titleFont, infoFont;

	private CustomerInfoPDF(Minimum minimum) {
		this.minimum = minimum;
		this.lang = minimum.getLang();

		// bold = BaseFont.createFont(DEFAULTFONT, BaseFont.IDENTITY_H, true);
		// normal = BaseFont.createFont(DEFAULTFONT, BaseFont.IDENTITY_H, true);

		// titleFont = new Font(bold, 14f);
		// infoFont = new Font(normal, 14f);
		titleFont = FontFactory.getFont(PdfCreator.DEFAULTFONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14f, Font.BOLD);
		infoFont = FontFactory.getFont(PdfCreator.DEFAULTFONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14f, Font.NORMAL);
	}

	public CustomerInfoPDF(Customer customer, Minimum minimum) {
		this(minimum);
		this.customer = customer;
		this.subscription = customer.getCurrentSubscription();
	}

	public CustomerInfoPDF(Subscription subscription, Minimum minimum) {
		this(minimum);
		this.subscription = subscription;
		this.customer = subscription.getCustomer();
	}

	public void writePDFProtocol(PipedOutputStream out) throws DocumentException, MalformedURLException, IOException {
		Rectangle pageSize = PageSize.A4;
		Rectangle cardSize = PageSize.ID_1;

		Document document = new Document(pageSize, LEFT_M, RIGHT_M, TOP_M, BOTTOM_M);
		PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
		document.open();
		document.newPage();
		SettingImage si = minimum.getSettings().imageByType(Type.PDF_BACKROUND);
		Image background;
		if (si == null) {
			String path = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/pdfbackground.png";
			background = Image.getInstance(path);
		} else {
			background = Image.getInstance(si.getContent());
		}
		pdfWriter.getDirectContentUnder().addImage(background, pageSize.getWidth(), 0, 0, pageSize.getHeight(), 0, 0);

		Font tF = FontFactory.getFont(PdfCreator.DEFAULTFONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 24f, Font.BOLD);
		Phrase title = new Phrase(customer.getName(), tF);
		Paragraph pg = new Paragraph(customer.getName(), tF);
		pg.setLeading(0f, 0f);
		pg.setSpacingAfter(0f);
		pg.setSpacingBefore(0f);
		document.add(pg);

		addInfoTable(document);

		addPhoto(document);

		PdfContentByte cb = pdfWriter.getDirectContent();
		addCard(cardSize, tF.getBaseFont(), cb);

		document.close();
	}

	private void addInfoTable(Document document) throws DocumentException {
		PdfPTable table = new PdfPTable(2);
		table.setSpacingBefore(0f);

		table.getDefaultCell().setBorder(0);
		table.setWidthPercentage(100f);
		if (!StringUtils.isBlank(customer.getAddress()))
			addInfo(lang.getText("address"), customer.getAddress(), table);

		if (!StringUtils.isBlank(customer.getEmail()))
			addInfo(lang.getText("email"), customer.getEmail(), table);

		if (!StringUtils.isBlank(customer.getPhone()))
			addInfo(lang.getText("phone"), customer.getPhone(), table);

		if (minimum.getSettings().isUseBirthDayField() && customer.getBirthDate() != null)
			addInfo(lang.getText("birthDate"), lang.formatDate(customer.getBirthDate()), table);

		if (minimum.getSettings().isUseStudentField())
			addInfo(lang.getText("student"), lang.getText(customer.isStudent() ? "Yes" : "No"), table);

		if (subscription instanceof MasterSubscription)
			addInfo(lang.getText("Subscription"), lang.getText(MasterSubscription.class.getSimpleName()), table);

		if (subscription instanceof TimeSubscription) {
			addInfo(lang.getText("Subscription"), lang.getText(TimeSubscription.class.getSimpleName()), table);
			addInfo(lang.getText("ValidUntil"), lang.formatDate(((TimeSubscription) subscription).getExpiry()), table);
		}

		document.add(table);
	}

	private void addPhoto(Document document) throws BadElementException, MalformedURLException, IOException,
			DocumentException {
		PdfPTable table;
		if (customer.getPhoto() != null) {
			Image photo = Image.getInstance(customer.getPhoto().getContent());
			float h = photo.getHeight();
			float w = photo.getWidth();
			float wph = h / w;
			table = new PdfPTable(1);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.getDefaultCell().setBorder(0);
			float maxH = (PageSize.A4.getHeight() - TOP_M - BOTTOM_M) / 2 - 20f;
			if (h > maxH)
				table.setWidthPercentage(100 * maxH / h);
			else
				table.setWidthPercentage(100f);
			PdfPCell cell = new PdfPCell(table.getDefaultCell());
			cell.addElement(photo);
			cell.setRowspan(1);
			table.addCell(cell);

			document.add(table);
		}
	}

	private void addCard(Rectangle cardSize, BaseFont font, PdfContentByte cb) throws BadElementException,
			MalformedURLException, IOException, DocumentException {
		cb.setColorStroke(Color.BLACK);
		cb.setLineWidth(Utilities.millimetersToPoints(0.3f));

		// Frontside
		cb.roundRectangle(LEFT_M, BOTTOM_M, cardSize.getWidth(), cardSize.getHeight(), Utilities.millimetersToPoints(3));
		cb.stroke();
		// Backside
		cb.roundRectangle(LEFT_M + cardSize.getWidth(), BOTTOM_M, cardSize.getWidth(), cardSize.getHeight(),
				Utilities.millimetersToPoints(3));
		cb.stroke();
		// cb.saveState();
		cb.beginText();
		float nameH = cardSize.getHeight() * 3 / 4;
		cb.setFontAndSize(font, 18f);
		cb.showTextAligned(0, customer.getName(), LEFT_M + 15f, BOTTOM_M + nameH, 0);
		cb.endText();

		if (subscription instanceof TimeSubscription) {
			cb.setFontAndSize(font, 12f);
			cb.beginText();
			cb.showTextAligned(0, lang.getText("expiry") + ":", LEFT_M + 15f, BOTTOM_M + nameH - 20f, 0);
			cb.endText();
			cb.setFontAndSize(font, 16f);
			cb.beginText();
			String t = lang.formatDate(((TimeSubscription) subscription).getExpiry());
			cb.showTextAligned(0, t, LEFT_M + 15f, BOTTOM_M + nameH - 35f, 0);
			cb.endText();
		}

		if (minimum.getSettings().isShowPhotoOnCard() && customer.getPhoto() != null) {
			float maxH = nameH - 5f;
			float maxW = (cardSize.getWidth() / 2);
			Image photo = Image.getInstance(customer.getPhoto().getContent());
			float hRel = maxH / photo.getHeight();
			float wRel = maxW / photo.getWidth();
			float fact = (hRel < wRel) ? hRel : wRel;
			float w = fact * photo.getWidth();
			cb.addImage(photo, w, 0f, 0f, fact * photo.getHeight(), LEFT_M + cardSize.getWidth() - w - 5f, BOTTOM_M + 2f);
		}

		addBarcode(cb, cardSize);
	}

	private void addBarcode(PdfContentByte cb, Rectangle CardSize) throws DocumentException {
		BarcodeInter25 i25 = new BarcodeInter25();
		String code = "";
		if (minimum.getSettings().isUseSubscriptionID())
			code += customer.getCurrentSubscription().getId().toString();
		else
			code += customer.getId().toString();
		while (code.length() < 14)
			code = "0" + code;
		i25.setCode(code);
		i25.setSize(14);
		i25.setBarHeight(40);
		i25.setBaseline(18);
		i25.setX(1);
		float w = i25.getBarcodeSize().getRight();
		i25.setX(128 / w);
		Image barcode = i25.createImageWithBarcode(cb, Color.BLACK, Color.BLACK);
		barcode.setAbsolutePosition(50f + CardSize.getWidth(), 35f + CardSize.getHeight() / 2);
		cb.addImage(barcode);
		cb.stroke();
	}

	private void addInfo(String title, String info, PdfPTable table) {
		table.addCell(new Phrase(title, titleFont));
		table.addCell(new Phrase(info, infoFont));
	}

}
