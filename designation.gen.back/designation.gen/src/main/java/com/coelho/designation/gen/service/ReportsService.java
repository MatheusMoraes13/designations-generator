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
        tess4j.setPageSegMode(3);

        String result = tess4j.doOCR(resizedImage);
        System.out.println("Resultado OCR:\n" + result);
    }
}
