import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Analyse {
    private static String imgPath = "./cover.png";
    private static String imgFilteredPNG = "./filteredImg.png";

    private static String newImgPath = "./newImg.png";
    private static String newFilteredPNG = "./newFilteredImg.png";

    private static String fileToHidePath = "./embedded";

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

        statistics(imgPath);
        statistics(newImgPath);
    }

    private static void statistics(String path) throws IOException {
        BufferedImage bi = ImageIO.read(new File(path));
        int[][][] imgArray = Tools.getImageArray(bi);

        Tools.getStatistics(imgArray);
    }
}
