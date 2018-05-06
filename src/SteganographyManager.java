import javax.imageio.ImageIO;
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
}