package identificadorBacterias;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap;


public class ImageAnalyzer {

    // Define main colors for comparison
	private static final Map<String, Color> MAIN_COLORS = createMainColors();

	private static Map<String, Color> createMainColors() {{
		Map<String, Color> colors = new HashMap<>();
		colors.put("Purpur", new Color(160, 32, 240));
	    colors.put("Yellow", new Color(255, 255, 0));
	    colors.put("Red", new Color(255, 0, 0));
	    colors.put("Copper-ish", new Color(203, 109, 81));
	    colors.put("Blue", new Color(0, 0, 255));
	    colors.put("Green", new Color(0, 255, 0));
	    colors.put("Pink", new Color(100, 75, 80));
	    colors.put("Orange", new Color(255, 165, 0));
	    colors.put("White", new Color(255, 255, 255));
	    return colors;
    }};

    public static void main(String[] args) throws IOException {
        // Load the image
        BufferedImage image = ImageIO.read(new File("C:\\Users\\perez\\OneDrive\\Escritorio\\Identificador Bacterias\\prueba_TodasPositivas.jpg"));

        // Fixed number of columns and rows
        int columns = 10;
        int rows = 2;

        // Calculate dynamic grid size
        int gridWidth = image.getWidth() / columns;
        int gridHeight = image.getHeight() / rows;

        // Analyze only the second row (index 1)
        int rowIndex = 1;
        for (int x = 0; x < image.getWidth(); x += gridWidth) {
            analyzeGrid(image, x, rowIndex * gridHeight, gridWidth, gridHeight);
        }
    }

    private static void analyzeGrid(BufferedImage image, int startX, int startY, int width, int height) {
        int redTotal = 0, greenTotal = 0, blueTotal = 0, pixelCount = 0;

        for (int y = startY; y < startY + height && y < image.getHeight(); y++) {
            for (int x = startX; x < startX + width && x < image.getWidth(); x++) {
                Color pixel = new Color(image.getRGB(x, y));

                // Ignore black grid lines
                if (pixel.getRed() < 20 && pixel.getGreen() < 20 && pixel.getBlue() < 20) {
                    continue;
                }
                redTotal += pixel.getRed();
                greenTotal += pixel.getGreen();
                blueTotal += pixel.getBlue();
                pixelCount++;
            }
        }

        if (pixelCount == 0) return;

        // Calculate average color of the grid
        Color avgColor = new Color(redTotal / pixelCount, greenTotal / pixelCount, blueTotal / pixelCount);
        System.out.printf("Grid (%d, %d): %s -> %s\n", startX, startY, getColorName(avgColor), getTop2ColorSimilarities(avgColor));
    }

    private static String getColorName(Color color) {
        return String.format("R:%d G:%d B:%d", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static String getTop2ColorSimilarities(Color color) {
        List<Map.Entry<String, Double>> similarities = new ArrayList<>();

        for (Map.Entry<String, Color> entry : MAIN_COLORS.entrySet()) {
            double similarity = calculateSimilarity(color, entry.getValue());
            similarities.add(new AbstractMap.SimpleEntry<>(entry.getKey(), similarity));
        }

        // Sort by similarity in descending order
        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Get top 2 similarities
        return String.format("%s: %.2f%%, %s: %.2f%%", 
                similarities.get(0).getKey(), similarities.get(0).getValue() * 100,
                similarities.get(1).getKey(), similarities.get(1).getValue() * 100);
    }

    private static double calculateSimilarity(Color c1, Color c2) {
        double distance = Math.sqrt(
                Math.pow(c1.getRed() - c2.getRed(), 2) +
                Math.pow(c1.getGreen() - c2.getGreen(), 2) +
                Math.pow(c1.getBlue() - c2.getBlue(), 2)
        );
        return 1 - (distance / Math.sqrt(3 * Math.pow(255, 2))); // Normalize to percentage
    }
}
