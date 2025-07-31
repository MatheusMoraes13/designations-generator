package com.coelho.designation.gen.service;

import com.coelho.designation.gen.dto.InterfaceInformationDTO;
import com.coelho.designation.gen.dto.ReportRequestInformationDTO;
import com.coelho.designation.gen.service.function.ReportsServiceFunctions;
import com.lowagie.text.DocumentException;
import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.openpdf.pdf.ITextRenderer;
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
public class ReportsService {

    private static final String LIBRARY_BASE_PATH = "library/";
    private static final String ISP_TEMPLATE_BASE_PATH = "src/main/resources/templates/isp-client-form/";

    ReportsServiceFunctions reportsFunctions;

/*
Função responsável por realizar a leitura da imagem, através do OCR e extrair as informações do texto, com a utlização
da função de normalização dos dados.
*/
    public ResponseEntity<?> genReportPdf(MultipartFile trafficImage, ReportRequestInformationDTO reportRequestInformation) throws TesseractException, IOException {
        InterfaceInformationDTO dataOfImage = reportsFunctions.readImage(trafficImage);
        File baseDir = new File(LIBRARY_BASE_PATH);
        Files.createDirectories(Paths.get(LIBRARY_BASE_PATH));

        try {
            String BASE_OUTPUT_URL = new File(ISP_TEMPLATE_BASE_PATH).toURI().toURL().toString();
            String htmlContent = new String(Files.readAllBytes(Paths.get(ISP_TEMPLATE_BASE_PATH + "ispClientPDF.html")));
            ITextRenderer renderer = new ITextRenderer();

            String outPutFile = "library/" + reportsFunctions.genReportName(reportRequestInformation.clientName()) + ".pdf";

            Pattern pattern = Pattern.compile("\\{\\{(.+?)}}");
            StringBuilder resultHtml = new StringBuilder();
            Matcher matcher = pattern.matcher(htmlContent);
            Map<String, String> replacements = new HashMap<>();

            String clientName = reportRequestInformation.clientName();
            String initialDate = reportRequestInformation.initialDate();
            String finalDate = reportRequestInformation.finalDate();
            String clientLink = reportRequestInformation.clientLink();
            String circuitDesignation = reportRequestInformation.circuitDesignation();
            String valueMb = reportRequestInformation.valueMb();
            String circuitVlan = reportRequestInformation.circuitVlan();
            String percentile = dataOfImage.percentile();
            String percentileUnit = dataOfImage.percentileUnit();
            String totalValue = reportsFunctions.calcTotalValue(valueMb, percentile, percentileUnit);

            replacements.put("{{CLIENT_NAME}}", clientName);
            replacements.put("{{INITIAL_DATE}}", initialDate);
            replacements.put("{{FINAL_DATE}}", finalDate);
            replacements.put("{{CLIENT_LINK}}", clientLink);
            replacements.put("{{CIRCUIT_DESIGNATION}}", circuitDesignation);
            replacements.put("{{CIRCUIT_VLAN}}", circuitVlan);
            replacements.put("{{VALUE_MB}}", valueMb);
            replacements.put("{{PERCENTILE}}", percentile);
            replacements.put("{{PERCENTILE_UNIT}}", percentileUnit);
            replacements.put("{{TOTAL_VALUE}}", totalValue);

            while (matcher.find()){
                String key = matcher.group(1);
                String value = replacements.get(String.format("{{%s}}", key));

                if (value != null) {
                    matcher.appendReplacement(resultHtml, Matcher.quoteReplacement(value));
                } else {
                    System.out.println("Nenhuma correspondencia encontrada para: " + key);
                    continue;
                }
            }
            matcher.appendTail(resultHtml);

            try (FileOutputStream outputStream = new FileOutputStream(outPutFile)) {

                renderer.setDocumentFromString(resultHtml.toString(), BASE_OUTPUT_URL);
                renderer.layout();
                renderer.createPDF(outputStream);

                System.out.println("PDF de relatório gerado com sucesso");
            }

            return ResponseEntity.ok().build();
        } catch (IOException | DocumentException e){
            throw new RuntimeException("Erro ao gerar o relatório em PDF", e);
        }
    }

}