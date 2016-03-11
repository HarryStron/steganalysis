import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Analyse {
    private static String imgPath = "./res/img.png";
    private static String imgFilteredPNG = "./res/filteredImg.png";

    private static String newImgPath = "./res/newImg.png";
    private static String newFilteredPNG = "./res/newFilteredImg.png";

    private static String fileToHidePath = "./res/test.txt";

    public static void main(String[] args) throws IOException {
        //generate filtered unedited image
        BufferedImage bi = ImageIO.read(new File(imgPath));
        int[][][] imgArray = Tools.getImageArray(bi);
        Tools.buildFilteredImgFromArray(imgArray, imgFilteredPNG);

        //stego and new image
        File f = new File(fileToHidePath);
        byte[] fileBytes = Files.readAllBytes(f.toPath());
        int[][][] newImgArray = SteganographyManager.hide(imgPath, fileBytes);
        Tools.buildImgFromArray(newImgArray, newImgPath);

        //generate filtered edited image
        BufferedImage bi2 = ImageIO.read(new File(newImgPath));
        int[][][] imgArray2 = Tools.getImageArray(bi2);
        Tools.buildFilteredImgFromArray(imgArray2, newFilteredPNG);

        System.out.println("\nChange rate for original image: " + checkFlipRate(imgPath));
        System.out.println("\nChange rate for modified image: " + checkFlipRate(newImgPath));
    }

    private static double checkFlipRate(String path) throws IOException {
        System.out.println("\nMeasuring bit-flip change rate. . .");

        BufferedImage bi = ImageIO.read(new File(path));
        int[][][] imgArray = Tools.getImageArray(bi);

        return Tools.getFlipRate(imgArray);
    }
}
