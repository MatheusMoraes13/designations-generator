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

    private String normalizeData(String input) {
        System.out.println("Texto recebido para regex:\n" + input);
        String regex = "=\\s*([^:]+):\\s*([^:]+):\\s*([\\d.,]+\\s?kB)\\s+([\\d.,]+\\s?kB)\\s+([\\d.,]+\\s?kB)\\s+([\\d.,]+\\s?kB)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        StringBuilder output = new StringBuilder();

        while (matcher.find()) {
            String equipamento = matcher.group(1).trim();
            String interfaceDesc = matcher.group(2).trim();
            String last = matcher.group(3).trim();
            String min = matcher.group(4).trim();
            String max = matcher.group(5).trim();
            String percentile95 = matcher.group(6).trim();

            String result = String.format(
                    "Equipamento: %s\nInterface/Desc: %s\nLast: %s\nMin: %s\nMax: %s\n95th: %s\n",
                    equipamento, interfaceDesc, last, min, max, percentile95
            );
            output.append("------------------------\n").append(result).append("------------------------\n");
        }

        if (output.length() == 0) {
            System.out.println("Nenhuma correspondência encontrada.");
        } else {
            System.out.println(output);
        }

        return output.toString();
    }

}
