package com.coelho.designation.gen.service;

import com.coelho.designation.gen.dto.InterfaceInformationDTO;
import com.lowagie.text.DocumentException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReportsService {

    private static final String TESSDATA_PATH = "C:/Program Files/Tesseract-OCR/tessdata/";
    private static final String IMAGES_REPOSITORY_PATH = "storage/images/images-to-read/";
    private static final String LIBRARY_BASE_PATH = "library/";
    private static final String TEMPLATE_BASE_PATH = "src/main/resources/templates/isp-client-form/";
/*
Função responsável por realizar a leitura da imagem, através do OCR e extrair as informações do texto, com a utlização
da função de normalização dos dados.
*/

    public ResponseEntity<?> genReportPdf(){
        File baseDir = new File(LIBRARY_BASE_PATH);
        checkPdfDirectory(baseDir);

        try {
            String BASE_OUTPUT_URL = new File(TEMPLATE_BASE_PATH).toURI().toURL().toString();
            String htmlContent = new String(Files.readAllBytes(Paths.get(TEMPLATE_BASE_PATH + "ispClientPDF.html")));
            ITextRenderer renderer = new ITextRenderer();

            String outPutFile = "library/teste-report.pdf";

            try (FileOutputStream outputStream = new FileOutputStream(outPutFile)) {

                renderer.setDocumentFromString(htmlContent, BASE_OUTPUT_URL);
                renderer.layout();
                renderer.createPDF(outputStream);

                System.out.println("PDF de relatório gerado com sucesso");
            }

            return ResponseEntity.ok().build();
        } catch (IOException | DocumentException e){
            throw new RuntimeException("Erro ao gerar o relatório em PDF", e);
        }
    }

    public void readImage(MultipartFile trafficImage) throws TesseractException, IOException {
        /*
        Realizando o carregamento do arquivo, criando os diretórios de armazenamento e transformando o arquivo
        "MultipartFile" em um arquivo "File" para que possa ser utilizado pelo OCR para extração do texto.
        */
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

        /*
        Realizando o redimensionamento da print enviada para que melhore a acertifidade do OCR na hora de reconhecer
        os textos na imagem.
        */
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

        /*
        Utilizando a biblioteca de reconhecimento Optico para a leitura da imagem e extração dos textos.
        */
        Tesseract tess4j = new Tesseract();
        tess4j.setDatapath(TESSDATA_PATH);
        tess4j.setLanguage("por");
        tess4j.setPageSegMode(6);
        String result = tess4j.doOCR(resizedImage);

        /*
        Realizando a normalização dos dados recebidos pelo OCR e mapeando para um objeto de transferência de dados
        para que possa ser utilizado na criação do PDF.
        */
        InterfaceInformationDTO resultInformation = normalizeData(result);
        System.out.printf("Resultado OCR:\n%s%n", Objects.requireNonNull(resultInformation).toString());
    }

/*
Metodo responsável por tealizar a normalização dos dados recebidos pelo OCR.
Esse metodo irá realizar a captura dos dados da interface de Upload, presente na imagem, e extrair os dados
e assim alimentando o DTO que será retornado pela função.

Dados extraidos:
NOME DO EQUIPAMENTO | TIPO DA INTERFACE (upload, download) | MAX | MIN | 95PERCENTIL.
*/

    private InterfaceInformationDTO normalizeData(String ocrText) {
        String[] lines = ocrText.split("\\R");
        InterfaceInformationDTO resultInterfaceInformation = null;

        Pattern dataPattern = Pattern.compile(
                "(?i)^\\s*=?\\s*(.+?):" +
//                        "(Download|Upload)\\s+" +
                        "(Upload)\\s+" +
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

                    resultInterfaceInformation = new InterfaceInformationDTO(
                            equipment,
                            type,
                            lastValue, lastUnit,
                            minValue, minUnit,
                            maxValue, maxUnit,
                            percentile95Value, percentile95Unit
                    );
                } catch (Exception e) {
                    System.err.printf("Erro ao processar a linha de dados: %s .\nErro: %s", line, e.getMessage());
                }
            }
        }

        if (resultInterfaceInformation == null) {
            System.out.println("Nenhuma correspondência de dados de tráfego encontrada no texto do OCR.");
            return null;
        } else {
            System.out.println("Dados de tráfego extraídos com sucesso.");
        }


        return resultInterfaceInformation;
    }

    private static void checkPdfDirectory(File file) {
        if(file.exists()){
            System.out.println("Diretóorio de armazenamento dos certificados já existe!");
        } else {
            try {
                file.mkdir();
            } catch (Exception e) {
                System.out.println("Erro ao criar o diretório de armazenamento dos certificados: "+ e.getMessage());
            }
        }
    }
}
