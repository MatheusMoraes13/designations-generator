package com.coelho.designation.gen.service;

import com.coelho.designation.gen.dto.InterfaceInformationDTO;
import com.coelho.designation.gen.dto.ReportRequestInformationDTO;
import com.coelho.designation.gen.service.function.ReportsServiceFunctions;
import com.lowagie.text.DocumentException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.openpdf.pdf.ITextRenderer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@AllArgsConstructor
@Slf4j
public class ReportsService {

    private static final String LIBRARY_BASE_PATH = "library/";
    private static final String ISP_TEMPLATE_BASE_PATH = "src/main/resources/templates/isp-client-form/";

    ReportsServiceFunctions reportsFunctions;

/*
Função responsável por realizar a leitura da imagem, através do OCR e extrair as informações do texto, com a utilização
da função de normalização dos dados.
*/
    public ResponseEntity<?> genReportIspPdf(MultipartFile trafficImage, ReportRequestInformationDTO reportRequestInformation) throws TesseractException, IOException {
        log.info("Executando a API de geração de PDF para o cliente: {}", reportRequestInformation.clientName());

        /*
        Realizando a leitura dos textos da imagem enviada, através do OCR, e realizando a normalização dos dados obtidos.
         */
        InterfaceInformationDTO dataOfImage = reportsFunctions.readImage(trafficImage);

        //Checando os diretórios de armazenamento dos arquivos.
        File baseDir = new File(LIBRARY_BASE_PATH);
        Files.createDirectories(Paths.get(LIBRARY_BASE_PATH));

        try {
            /*
            Definindo o diretório de saida, o template a ser utilizado e criando o renderizador de HTLM, que será
            responsável pela criação do PDF.
             */
            String BASE_OUTPUT_URL = new File(ISP_TEMPLATE_BASE_PATH).toURI().toURL().toString();
            String htmlContent = new String(Files.readAllBytes(Paths.get(ISP_TEMPLATE_BASE_PATH + "ispClientPDF.html")));
            ITextRenderer renderer = new ITextRenderer();

            //Definindo o nome do arquivo final.
            String outPutFile = "library/" + reportsFunctions.genReportName(reportRequestInformation.clientName()) + ".pdf";


            /*
            Realizando a troca dos valores do HTML para que contenha os valores enviados na requisição, nessa função foi
            utilizado o modelo com Pattern compilado e o Matcher garantindo uma implementação mais rápida.
             */
            Pattern pattern = Pattern.compile("\\{\\{(.+?)}}");
            StringBuilder resultHtml = new StringBuilder();
            Matcher matcher = pattern.matcher(htmlContent);

            Map<String, String> replacements = new HashMap<>();

            replacements.put("{{CLIENT_NAME}}", reportRequestInformation.clientName());
            replacements.put("{{INITIAL_DATE}}", reportRequestInformation.initialDate());
            replacements.put("{{FINAL_DATE}}", reportRequestInformation.finalDate());
            replacements.put("{{CLIENT_LINK}}", reportRequestInformation.clientLink());
            replacements.put("{{CIRCUIT_DESIGNATION}}", reportRequestInformation.circuitDesignation());
            replacements.put("{{CIRCUIT_VLAN}}", reportRequestInformation.circuitVlan());
            replacements.put("{{VALUE_MB}}", reportRequestInformation.valueMb());
            replacements.put("{{PERCENTILE}}", dataOfImage.percentile());
            replacements.put("{{PERCENTILE_UNIT}}", dataOfImage.percentileUnit());
            replacements.put("{{TOTAL_VALUE}}", reportsFunctions.calcTotalValue(reportRequestInformation.valueMb(), dataOfImage.percentile(), dataOfImage.percentileUnit()));

            log.debug("Realizando a normalização dos dados retornados pelo OCR.");
            while (matcher.find()){
                String key = matcher.group(1);
                String value = replacements.get(String.format("{{%s}}", key));

                if (value != null) {
                    matcher.appendReplacement(resultHtml, Matcher.quoteReplacement(value));
                } else {
                    log.warn("Nenhuma correspondência encontrada para: {}", key);
                    continue;
                }
            }
            matcher.appendTail(resultHtml);
            log.debug("Normalização dos dados retornados pelo OCR foi finalizada!");

            try (FileOutputStream outputStream = new FileOutputStream(outPutFile)) {
                log.debug("Gerando o relatório");

                renderer.setDocumentFromString(resultHtml.toString(), BASE_OUTPUT_URL);
                renderer.layout();
                renderer.createPDF(outputStream);

                log.info("PDF de relatório gerado com sucesso para o cliente {}.", reportRequestInformation.clientName());
            }

            return ResponseEntity.ok().build();
        } catch (IOException | DocumentException e){
            log.error("Erro ao gerar o relatório em PDF {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar o relatório em PDF" + e.getMessage());
        }
    }

}