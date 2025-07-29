package com.coelho.designation.gen.controller;

import com.coelho.designation.gen.service.ReportsService;
import lombok.AllArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/generate-report")
@AllArgsConstructor
public class ReportsController {

    ReportsService reportsService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateReport(@RequestParam("archive") MultipartFile trafficImage) throws TesseractException, IOException {
        reportsService.readImage(trafficImage);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> generateReportPdf(){
        return reportsService.genReportPdf();
    }

}
