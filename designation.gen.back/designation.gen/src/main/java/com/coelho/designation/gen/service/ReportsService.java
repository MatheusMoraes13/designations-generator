package com.coelho.designation.gen.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReportsService {

    private static final String TESSDATA_PATH = "C:/Program Files/Tesseract-OCR/tessdata/";
    private static final String IMAGES_REPOSITORY_PATH = "C:/storage/images/images-to-read/";

    public void readImage(MultipartFile trafficImage) throws TesseractException, IOException {
        String originalFilename = trafficImage.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        Files.createDirectories(Paths.get(IMAGES_REPOSITORY_PATH));
        File trafficImagePNG = new File(IMAGES_REPOSITORY_PATH + originalFilename);
        System.out.println("Salvando imagem em: " + trafficImagePNG.getAbsolutePath());

        try {
            trafficImage.transferTo(trafficImagePNG);
        } catch (IOException ex) {
            System.err.println("Erro ao salvar a imagem: " + ex.getMessage());
            throw ex;
        }

        BufferedImage originalImage = ImageIO.read(trafficImagePNG);
        if (originalImage == null) {
            throw new IOException("Imagem não pôde ser lida. Verifique o formato do arquivo.");
        }

        int width = originalImage.getWidth() * 2;
        int height = originalImage.getHeight() * 2;
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        Tesseract tess4j = new Tesseract();
        tess4j.setDatapath(TESSDATA_PATH);
        tess4j.setLanguage("por");
        tess4j.setPageSegMode(6);

        String result = tess4j.doOCR(resizedImage);
        result = normalizeData(result);
        System.out.println("Resultado OCR:\n" + result);
    }


    private String normalizeData(String ocrText) {
        StringBuilder outputBuilder = new StringBuilder();
        String[] lines = ocrText.split("\\R");

        Pattern dataPattern = Pattern.compile(
                "(?i)^\\s*=?\\s*(.+?):" +
                        "(Download|Upload)\\s+" +
                        "([0-9.,]+)\\s*(MB|KB|GB|TB)\\s+" +
                        "([0-9.,]+)\\s*(MB|KB|GB|TB)\\s+" +
                        "([0-9.,]+)\\s*(MB|KB|GB|TB)\\s+" +
                        "([0-9.,]+)\\s*(MB|KB|GB|TB)"
        );

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            Matcher dataMatcher = dataPattern.matcher(line);

            if (dataMatcher.find()) {
                try {
                    String equipment = dataMatcher.group(1).trim();
                    String type = dataMatcher.group(2).trim();
                    String lastValue = dataMatcher.group(3).trim();
                    String lastUnit = dataMatcher.group(4).trim();
                    String minValue = dataMatcher.group(5).trim();
                    String minUnit = dataMatcher.group(6).trim();
                    String maxValue = dataMatcher.group(7).trim();
                    String maxUnit = dataMatcher.group(8).trim();
                    String percentile95Value = dataMatcher.group(9).trim();
                    String percentile95Unit = dataMatcher.group(10).trim();

                    outputBuilder.append(String.format(
                            "Equipamento: %s\nTipo: %s\nLast: %s %s\nMin: %s %s\nMax: %s %s\n95th: %s %s\n",
                            equipment, type, lastValue, lastUnit, minValue, minUnit, maxValue, maxUnit, percentile95Value, percentile95Unit
                    ));
                    outputBuilder.append("------------------------\n");

                } catch (Exception e) {
                    System.err.println("Erro ao processar a linha de dados: '" + line + "'. Erro: " + e.getMessage());
                }
            }
        }

        if (outputBuilder.length() == 0) {
            System.out.println("Nenhuma correspondência de dados de tráfego encontrada no texto do OCR.");
            return "Nenhuma correspondência encontrada.";
        } else {
            System.out.println("Dados de tráfego extraídos com sucesso.");
        }

        return outputBuilder.toString();
    }
}
