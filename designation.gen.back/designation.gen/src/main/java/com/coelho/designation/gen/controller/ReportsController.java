package com.coelho.designation.gen.controller;

import com.coelho.designation.gen.dto.ReportRequestInformationDTO;
import com.coelho.designation.gen.service.ReportsService;
import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/generate-report")
@AllArgsConstructor
public class ReportsController {

    ReportsService reportsService;

    @PostMapping(path = "/isp",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> generateReport(
            @RequestPart("archive") MultipartFile trafficImage,
            @RequestPart("reportRequestInformationDTO") ReportRequestInformationDTO reportRequestInformationDTO) throws TesseractException, IOException {
        return reportsService.genReportIspPdf(trafficImage, reportRequestInformationDTO);
    }
}