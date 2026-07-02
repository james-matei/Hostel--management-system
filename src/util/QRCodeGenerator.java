package util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QRCodeGenerator {

    private static final int SIZE = 300; // QR image size in pixels

    /**
     * Generates a QR code as a JavaFX Image from the given text content.
     */
    public static Image generateQRImage(String content) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);

        WritableImage image = new WritableImage(SIZE, SIZE);
        PixelWriter pw = image.getPixelWriter();

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                pw.setColor(x, y, bitMatrix.get(x, y)
                    ? javafx.scene.paint.Color.BLACK
                    : javafx.scene.paint.Color.WHITE);
            }
        }
        return image;
    }

    /**
     * Saves the QR code as a PNG file to the given path.
     * Returns the saved file, or null on failure.
     */
    public static File saveQRCode(String content, String filePath) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);

        BufferedImage bufferedImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        File file = new File(filePath);
        file.getParentFile().mkdirs(); // create directories if they don't exist
        ImageIO.write(bufferedImage, "PNG", file);
        return file;
    }

    /**
     * Builds the QR code content string from student data.
     * Format:
     *   Name: John Smith
     *   Reg No: 66789
     *   Room: 101
     *   Course: Computer Science
     */
    public static String buildQRContent(String name, String regNumber, String roomId, String course) {
        return "Name: "   + (name      != null ? name      : "N/A") + "\n" +
               "Reg No: " + (regNumber != null ? regNumber : "N/A") + "\n" +
               "Room: "   + (roomId    != null ? roomId    : "Unassigned") + "\n" +
               "Course: " + (course    != null ? course    : "N/A");
    }
}