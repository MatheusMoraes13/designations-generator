package com.coelho.designation.gen.service.function;

import com.coelho.designation.gen.dto.InterfaceInformationDTO;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ReportsServiceFunctions {

    @Value("${TRAFFIC_IMAGE_ISP_PATH}")
    private String TRAFFIC_IMAGE_ISP_PATH;

    @Value("${TESSDATA_PATH}")
    private String TESSDATA_PATH;

    @Value("${IMAGES_REPOSITORY_PATH}")
    private String IMAGES_REPOSITORY_PATH;

    private static final int TARGET_WIDTH = 2500;


/*
Função que será responsável por realizar a leitura do texto da imagem a partir do OCR, e retornar o DTO contendo os
dados do circuito que foram retirados da imagem.
*/
    public InterfaceInformationDTO readImage(MultipartFile trafficImage) throws TesseractException, IOException {
        /*
        Realizando o carregamento do arquivo, criando os diretórios de armazenamento e transformando o arquivo
        "MultipartFile" num arquivo "File" para poder ser utilizado pelo OCR para extração do texto.
        */
        log.info("Realizando a verificação dos diretórios padrões para a geração dos relatórios.");
        String originalFilename = Objects.requireNonNull(trafficImage.getOriginalFilename()).replaceAll("[^a-zA-Z0-9.\\-_]", "_");
        Files.createDirectories(Paths.get(IMAGES_REPOSITORY_PATH));
        File trafficImagePNG = new File(IMAGES_REPOSITORY_PATH + originalFilename);
        log.info("Salvando a imagem de trafego em: {}", trafficImagePNG.getAbsolutePath());

        try {
            trafficImage.transferTo(trafficImagePNG);
        } catch (IOException ex) {
            log.error("Erro ao salvar a imagem: {}", ex.getMessage());
            throw ex;
        }


        /*
        Realizando o redimensionamento do print enviado para melhorar a assertividade do OCR na hora de reconhecer
        os textos na imagem.
        */
        BufferedImage resizedImage = preprocessImageForOcr(trafficImagePNG);
        boolean success = ImageIO.write(resizedImage, "png", trafficImagePNG);
        if (success) {
            log.info("Imagem de trafego, tratada, salva com sucesso.");
        }


        /*
        Realizando a modificação da imagem enviada na requisição, para que ela seja salva e utilizada pelo HTML, como
        "traffic.png" e apagando do diretório de armazenamento temporário de imagens.
        */
        copyImageToHtml(trafficImagePNG, TRAFFIC_IMAGE_ISP_PATH);
        Files.delete(Paths.get(trafficImagePNG.getPath()));


        /*
        Utilizando a biblioteca de reconhecimento Optico para a leitura da imagem e extração dos textos.
        */
        Tesseract tess4j = new Tesseract();
        tess4j.setDatapath(TESSDATA_PATH);
        tess4j.setLanguage("por");
        tess4j.setPageSegMode(6);
        String result = tess4j.doOCR(resizedImage);
        log.debug("Resultado retornado pelo OCR: {}", result);


        /*
        Realizando a normalização dos dados recebidos pelo OCR e mapeando para um objeto de transferência de dados
        para poder ser utilizado na criação do PDF.
        */
        InterfaceInformationDTO resultInformation = normalizeOcrTrafficData(result);
        if (resultInformation == null) {
            log.error("Erro ao realizar a normalização dos dados do OCR.");
            return null;
        }
        log.info("Normalização dos dados realizada.");
        return resultInformation;
    }


/*
Metodo responsável por realizar a normalização dos dados recebidos pelo OCR.
Esse metodo irá realizar a captura dos dados da interface de Upload, presente na imagem, e extrair os dados
e assim alimentando o DTO que será retornado pela função.

Dados extraídos:
NOME DO EQUIPAMENTO | TIPO DA INTERFACE (upload, download) | MAX | MIN | 95PERCENTIL.
*/
    public InterfaceInformationDTO normalizeOcrTrafficData(String ocrText) {
        String[] lines = ocrText.split("\\R");
        InterfaceInformationDTO resultInterfaceInformation = null;

        Pattern dataPattern = Pattern.compile(
                "(?i).*?(Upload)\\s+" +                // grupo 1: tipo
                        "([0-9.,]+)\\s*(MB|KB|GB|TB)\\s+" +               // grupo 2 e 3: last value + unidade
                        "([0-9.,]+)\\s*(MB|KB|GB|TB)\\s+" +               // grupo 4 e 5: min value + unidade
                        "([0-9.,]+)\\s*(MB|KB|GB|TB)\\s+" +               // grupo 6 e 7: max value + unidade
                        "([0-9.,]+)\\s*(MB|KB|GB|TB)"                     // grupo 8 e 9: 95º + unidade
        );



        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            line = line.replaceAll("\\s+", " ").trim();
            Matcher dataMatcher = dataPattern.matcher(line);

            if (dataMatcher.find()) {
                try {

                    String equipment = null;
                    String type = dataMatcher.group(1).trim();
                    String lastValue = dataMatcher.group(2).trim();
                    String lastUnit = dataMatcher.group(3).trim();
                    String minValue = dataMatcher.group(4).trim();
                    String minUnit = dataMatcher.group(5).trim();
                    String maxValue = dataMatcher.group(6).trim();
                    String maxUnit = dataMatcher.group(7).trim();
                    String percentile95Value = dataMatcher.group(8).trim();
                    String percentile95Unit = dataMatcher.group(9).trim();

                    resultInterfaceInformation = new InterfaceInformationDTO(
                            type,
                            lastValue, lastUnit,
                            minValue, minUnit,
                            maxValue, maxUnit,
                            percentile95Value, percentile95Unit
                    );

                    log.info("Dados de tráfego extraídos com sucesso da linha: {} \nDTO Retornado: {}",line, resultInterfaceInformation);
                } catch (Exception e) {
                    log.error("Erro ao processar a linha de dados: {}. \nErro: {}", line, e.getMessage());
                }
            }
        }

        if (resultInterfaceInformation == null) {
            log.error("Nenhuma correspondência de dados de tráfego encontrada no texto do OCR.");
            return null;
        } else {
            log.info("Dados de tráfego extraídos com sucesso!");
        }


        return resultInterfaceInformation;
    }


/*
Função para realizar a cópia da imagem enviada na requisição para dentro do html, isso acontece devido ao arquivo ser
copiado e renomeado para dentro do diretório o qual o HTML template irá ler o arquivo de imagem.
*/
    public void copyImageToHtml(File fileToCopy, String pathDestination) throws IOException {
        log.info("Adicionando a imagem ao PDF.");

        Path sourcePath = fileToCopy.toPath();
        Path destinationPath = Paths.get(pathDestination);
        Files.createDirectories(destinationPath.getParent());

        String jpgFilePath = pathDestination.replace(".png", ".jpg");

        try {
            //Copiando a imagem para o diretório de imagens do template.
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            //Criando a imagem no buffer para fazer a conversão de imagem .png para .jpg.
            BufferedImage bufferedImage = ImageIO.read(destinationPath.toFile());

            //Criando uma imagem RGB no buffer para retirar a transparencia da imagem PNG
            BufferedImage rgbImage = new BufferedImage(
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            /*
            Redesenhando a imagem para RGB, para retirar a transparência da imagem PNG, pois o JPG não aceita
            transparência.
            */

            Graphics2D g = rgbImage.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();

            /*
            Convertendo a imagem para JPJ, para ficar mais leve, impactando significativamente no tamanho do PDF
            gerado, fazendo com que não ultrapasse os 200kb.
            */

            File jpgFile = new File(jpgFilePath);
            ImageIO.write(rgbImage, "jpg", jpgFile);
            Files.delete(destinationPath);
            log.info("Imagem adicionada com sucesso ao PDF!");

        } catch (IOException e) {
            log.error("Erro ao adicionar a imagem ao PDF.", e);
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
            log.error("Imagem não pôde ser lida. Verifique o formato do arquivo enviado.");
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
Função que irá realizar o cálculo do valor a ser cobrado pelo circuito, com base no valor por MB do contrato, e realizar
o calculo conforme a unidade de medida que foi apresentada no gráfico.
*/
    public String calcTotalValue (String valueMb, String percentile, String percentileUnit){

        float total;
        float percentileValue = Float.parseFloat(percentile);
        float valueMbValue = Float.parseFloat(valueMb);

        /*
        Realizando o cálculo do valor final a ser cobrado, com base na unidade de tráfego utilizada, realizando sempre a
        conversão para megas e multiplicando pelo valor definido por mega.
        */
        if (percentileUnit.equalsIgnoreCase("GB")){
            total = valueMbValue * (percentileValue * 1000);
        } else if (percentileUnit.equalsIgnoreCase("KB")) {
            total = valueMbValue * (percentileValue / 1000);
        } else {
            total = valueMbValue * percentileValue;
        }

        /*
        Modificando a saida para conter apenas dois números após a vírgula, e ser separado por ","
         */
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

        return df.format(total);
    }


/*
Função para a geração do nome padronizado do PDF final.
*/
    public String genReportName (String clientName){
        LocalDate currentDate = LocalDate.now();
        Month currentMonth = currentDate.getMonth();
        Year currentYear = Year.of(currentDate.getYear());
        Locale brazilianLocale = new Locale("pt", "BR");

        //Criando o padrão de formatação da data para mês e ano.
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yy", brazilianLocale);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM", brazilianLocale);

        //Formatando a saida de data e mês para ficar apenas os dois algarismos finais do ano, e as 3 letras iniciais do mês.
        String yearAbbreviation = currentDate.format(yearFormatter);
        String monthAbbreviation = currentMonth.getDisplayName(java.time.format.TextStyle.SHORT, brazilianLocale).toUpperCase();

        //Gerando o nome do arquivo PDF.
        String reportName = "RELATORIO " + clientName + " " + monthAbbreviation + yearAbbreviation;

        return reportName.toUpperCase();
    }
}
