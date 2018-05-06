import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tools {
    public static int[][][] getImageArray(BufferedImage bi) {
        System.out.println("\nRetrieving RGB values for image. . .");

        int[][][] imageArray = new int[bi.getWidth()][bi.getHeight()][3];

        for (int x=0; x<bi.getWidth(); x++) {
            for (int y=0; y<bi.getHeight(); y++) {
                Color c = new Color(bi.getRGB(x, y));
                imageArray[x][y][0] = c.getRed();
                imageArray[x][y][1] = c.getGreen();
                imageArray[x][y][2] = c.getBlue();
            }
        }

        return imageArray;
    }

    public static void getStatistics(int[][][] imageArray) {
        System.out.println("\nGathering statistics. . .");
        int zeros = 0;
        int ones = 0;
        int block = 0;

        System.out.println("\nGenerating chi-square distribution graph:");
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
//                for (int k = 0; k < imageArray[0][0].length; k++) {
                    if (getNthSignificantBit(imageArray[i][j][2], 1)) {
                        ones++;
                    } else {
                        zeros++;
                    }
                    block++;
//                }
            }
            double expected = (double) block / 2;
            double zeroChi = ((zeros - expected)*(zeros - expected))/expected;
            double oneChi = ((ones - expected)*(ones - expected))/expected;
            double chi = zeroChi + oneChi;
            double criticalValue = 3.841; // https://people.richland.edu/james/lecture/m170/tbl-chi.html
            if (chi > criticalValue) { //reject hypothesis
                System.out.print("_");
            } else {
                System.out.print("^");
            }
            //System.out.print(','); //delimiter when I want to extract data to tables
             block = 0;
             zeros = 0;
             ones = 0;
        }

    }

    public static boolean getNthSignificantBit(int value, int index) {
        String currentValue = Integer.toBinaryString(value);

        return Integer.parseInt(String.valueOf(currentValue.charAt(currentValue.length()-index))) == 1;
    }

    public static void buildFilteredImgFromArray(int[][][] imageArray, String newPath) throws IOException {
        System.out.println("\nBuilding new image. . .");

    BufferedImage image = new BufferedImage(imageArray.length, imageArray[0].length, BufferedImage.TYPE_3BYTE_BGR);

        for (int x=0; x<image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int r = 255;
                int g = 255;
                int b = 255;
                if (Tools.getNthSignificantBit(imageArray[x][y][0],1))
                    r = 0;
                if (Tools.getNthSignificantBit(imageArray[x][y][1],1))
                    g = 0;
                if (Tools.getNthSignificantBit(imageArray[x][y][2],1))
                    b = 0;
                image.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        File f = new File(newPath.substring(0,newPath.length()-5)+"Color.png");
        ImageIO.write(image, "png", f);

        for (int loops=0; loops<3; loops++) {
            image = new BufferedImage(imageArray.length, imageArray[0].length, BufferedImage.TYPE_3BYTE_BGR);

            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (Tools.getNthSignificantBit(imageArray[x][y][loops], 1))
                        image.setRGB(x, y, new Color(1, 1, 1).getRGB());
                    else
                        image.setRGB(x, y, new Color(255, 255, 255).getRGB());
                }
            }

            f = new File(newPath.substring(0,newPath.length()-5)+loops+".png");
            ImageIO.write(image, "png", f);
        }
    }

    public static void buildImgFromArray(int[][][] imageArray, String destination) throws IOException {
        BufferedImage image = new BufferedImage(imageArray.length, imageArray[0].length, BufferedImage.TYPE_3BYTE_BGR);

        for (int x=0; x<image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, new Color(imageArray[x][y][0], imageArray[x][y][1], imageArray[x][y][2]).getRGB());
            }
        }

        File f = new File(destination);
        ImageIO.write(image, "png", f);
    }
}
