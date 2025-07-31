package com.coelho.designation.gen.service.function;

import com.coelho.designation.gen.dto.InterfaceInformationDTO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class ReportsServiceFunctions {


    private static final String TRAFFIC_IMAGE_ISP_PATH = "src/main/resources/templates/isp-client-form/images/traffic.png";
    private static final String TESSDATA_PATH = "C:/Program Files/Tesseract-OCR/tessdata/";
    private static final String IMAGES_REPOSITORY_PATH = "C:/storage/images/images-to-read/";
    private static final int TARGET_WIDTH = 2500;


/*
Função que será responsável por realizar a leitura do texto da imagem a partir do OCR, e retornar o DTO contendo os
dados do circuitos que foram retirados da imagem.
*/
    public InterfaceInformationDTO readImage(MultipartFile trafficImage) throws TesseractException, IOException {
        /*
        Realizando o carregamento do arquivo, criando os diretórios de armazenamento e transformando o arquivo
        "MultipartFile" em um arquivo "File" para que possa ser utilizado pelo OCR para extração do texto.
        */
        String originalFilename = Objects.requireNonNull(trafficImage.getOriginalFilename()).replaceAll("[^a-zA-Z0-9.\\-_]", "_");
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
        BufferedImage resizedImage = preprocessImageForOcr(trafficImagePNG);
        boolean success = ImageIO.write(resizedImage, "png", trafficImagePNG);
        if (success) {
            System.out.println("imagem salva.");
        }


        /*
        Realizando a modificação da imagem enviada na requisição, para que ela seja salva e utilizada pelo HTML, como
        "traffic.png".
        */
        copyImageToHtml(trafficImagePNG, TRAFFIC_IMAGE_ISP_PATH);


        /*
        Utilizando a biblioteca de reconhecimento Optico para a leitura da imagem e extração dos textos.
        */
        Tesseract tess4j = new Tesseract();
        tess4j.setDatapath(TESSDATA_PATH);
        tess4j.setLanguage("por");
        tess4j.setPageSegMode(6);
        String result = tess4j.doOCR(resizedImage);
        System.out.println(result);


        /*
        Realizando a normalização dos dados recebidos pelo OCR e mapeando para um objeto de transferência de dados
        para que possa ser utilizado na criação do PDF.
        */
        InterfaceInformationDTO resultInformation = normalizeOcrTrafficData(result);
        if (resultInformation == null) {
            System.out.println("Erro ao realizar a normalização dos dados do OCR.");
            return null;
        }
        System.out.printf("Resultado OCR:\n%s%n", Objects.requireNonNull(resultInformation).toString());
        return resultInformation;
    }


/*
Metodo responsável por tealizar a normalização dos dados recebidos pelo OCR.
Esse metodo irá realizar a captura dos dados da interface de Upload, presente na imagem, e extrair os dados
e assim alimentando o DTO que será retornado pela função.

Dados extraidos:
NOME DO EQUIPAMENTO | TIPO DA INTERFACE (upload, download) | MAX | MIN | 95PERCENTIL.
*/
    public InterfaceInformationDTO normalizeOcrTrafficData(String ocrText) {
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

                    System.out.println("Dados de tráfego extraídos com sucesso da linha: " + line);
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


/*
Função para realizar a cópia da imagem enviada na requisição para dentro do html, isso acontece devido ao arquivo ser
copiado e renomeado para dentro do diretório o qual o HTML template irá ler o arquivo de imagem.
*/
    public void copyImageToHtml(File fileToCopy, String pathDestination) throws IOException {
        Path sourcePath = fileToCopy.toPath();
        Path destinationPath = Paths.get(pathDestination);
        Files.createDirectories(destinationPath.getParent());

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image copiada com sucesso para resources: " + destinationPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao copiar a Imagem enviada para resources: " + e.getMessage());
            throw e;
        }
    }


/*
Função responsável pela realização do redimensionamento da imagem, para que o OCR possa realizar o reconhecimento
correto das imagens.
*/
    public BufferedImage preprocessImageForOcr(File imageToResize) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageToResize);
        if (originalImage == null || !imageToResize.getAbsolutePath().contains(".png")) {
            throw new IOException("Imagem não pôde ser lida. Verifique o formato do arquivo.");
        }
        return Scalr.resize(
                originalImage,
                Scalr.Method.ULTRA_QUALITY,
                Scalr.Mode.FIT_TO_WIDTH,
                TARGET_WIDTH
        );
    }


/*
Função que irá realizar o calculo do valor a ser cobrado pelo circuito, com base no valor por MB do contrato, e realizar
o calculo de acordo com a unidade de medida que foi apresentada no gráfico.
*/
    public String calcTotalValue (String valueMb, String percentile, String percentileUnit){

        float total;
        float percentileValue = Float.parseFloat(percentile);
        float valueMbValue = Float.parseFloat(valueMb);

        if (percentileUnit.equalsIgnoreCase("GB")){
            total = valueMbValue * (percentileValue * 1000);
        } else if (percentileUnit.equalsIgnoreCase("KB")) {
            total = valueMbValue * (percentileValue / 1000);
        } else {
            total = valueMbValue * percentileValue;
        }

        return Float.toString(total);
    }


/*
Função para a geração do nome padronizado do PDF final.
*/
    public String genReportName (String clientName){
        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        Year currentYear = Year.of(currentDate.getYear());
        Locale brazilianLocale = new Locale("pt", "BR");

        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yy", brazilianLocale);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM", brazilianLocale);

        String yearAbbreviation = currentDate.format(yearFormatter);
        String monthAbbreviation = currentMonth.getDisplayName(java.time.format.TextStyle.SHORT, brazilianLocale).toUpperCase();

        return String.format("RELATORIO %s %s%s", clientName, monthAbbreviation, yearAbbreviation);
    }
}
