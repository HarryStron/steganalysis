import java.awt.*;
import java.awt.image.BufferedImage;

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

    public static double getFlipRate(int[][][] imageArray) {
        int flips = 0;
        int total = 0;
        boolean lastVisited = getNthSignificantBit(imageArray[0][0][0], 1);

        for (int i = 1; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                for (int k = 0; k < imageArray[0][0].length; k++) {
                    if (getNthSignificantBit(imageArray[i][j][k], 1) != lastVisited) {
                        lastVisited = getNthSignificantBit(imageArray[i][j][k], 1);
                        flips++;
                    }
                    total++;
                }
            }
        }

        System.out.println("\nStatistics for LSBs: ");
        System.out.println("total: " + total);
        System.out.println("flips: " + flips);
        double rate = ((double) flips / total) * 100;

        return rate;
    }

    public static boolean getNthSignificantBit(int value, int index) {
        String currentValue = Integer.toBinaryString(value);

        return Integer.parseInt(String.valueOf(currentValue.charAt(currentValue.length()-index))) == 1;
    }


}
