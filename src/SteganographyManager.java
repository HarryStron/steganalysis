import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;

public class SteganographyManager {

    public static int[][][] hide(String imgPath, byte[] fileBytes) throws IOException {
        System.out.println("\nSteganography proceeding. . .");
        BufferedImage bi = ImageIO.read(new File(imgPath));
        int[][][] imgArray = Tools.getImageArray(bi);
        int[][][] modifiedImgArray = encrypt(fileBytes, imgArray);

        return modifiedImgArray;
    }

    private static int[][][] encrypt(byte[] bytesIn, int[][][] imageArray) throws IOException {
        int bitsNeeded = bytesIn.length*8;
        int bitsAvailable = imageArray.length*imageArray[0].length*imageArray[0][0].length;
        if (bitsAvailable < bitsNeeded) {
            throw new IOException("File too large");
        }
        System.out.println("\nHiding file in image. . .");
        BitSet msg = BitSet.valueOf(bytesIn);
        int msgIndex = 0;

        BitSet sizeInBits = BitSet.valueOf(new long[]{Long.valueOf(msg.length())});
        int sizeIndex = 0;

        //encode size of file
        for (int j=imageArray[0].length-1; j>=0; j--) {
            for (int k=imageArray[0][0].length-1; k>=0; k--) {
                if (sizeIndex < sizeInBits.length()) {
                    imageArray[0][j][k] = changeNthSignificantBit(imageArray[0][j][k], 1, sizeInBits.get(sizeIndex));
                    sizeIndex++;
                } else {
                    imageArray[0][j][k] = changeNthSignificantBit(imageArray[0][j][k], 1, false);
                }
            }
        }

        int i = 1;
        int j = 0;
        int k = 0;
        boolean finished = false;
        while(!finished && i<imageArray.length) {
            while(!finished && j<imageArray[0].length) {
                while(!finished && k<imageArray[0][0].length) {
                    if (msgIndex < msg.length()) {
                        imageArray[i][j][k] = changeNthSignificantBit(imageArray[i][j][k], 1, msg.get(msgIndex));
                        msgIndex++;
                    } else {
                        finished = true;
                    }
                    k++;
                }
                k = 0;
                j++;
            }
            j = 0;
            i++;
        }

        return imageArray;
    }

    private static int changeNthSignificantBit(int x, int n, boolean newValue) { //boolean representing 1 or 0
        String currentValue = Integer.toBinaryString(x);

        if (n==1) {
            return Integer.parseInt(currentValue.substring(0, currentValue.length() - 1) + (newValue ? 1 : 0), 2);
        } else if (n==2) {
            return Integer.parseInt(currentValue.substring(0, currentValue.length() - 2) + (newValue ? 1 : 0) + currentValue.substring(currentValue.length() - 1), 2);
        } else if (n==3) {
            return Integer.parseInt(currentValue.substring(0, currentValue.length() - 3) + (newValue ? 1 : 0) + currentValue.substring(currentValue.length() - 2), 2);
        } else {
            return -1;
        }
    }

    public static File buildFilteredImgFromArray(int[][][] imageArray, String newPath) throws IOException {
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

        File f = new File(newPath);
        ImageIO.write(image, "png", f);

        return f;
    }

    public static File buildImgFromArray(int[][][] imageArray, String destination) throws IOException {
        BufferedImage image = new BufferedImage(imageArray.length, imageArray[0].length, BufferedImage.TYPE_3BYTE_BGR);

        for (int x=0; x<image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, new Color(imageArray[x][y][0], imageArray[x][y][1], imageArray[x][y][2]).getRGB());
            }
        }

        File f = new File(destination);
        ImageIO.write(image, "png", f);

        return f;
    }
}