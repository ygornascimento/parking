package com.mballem.demoparkapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JasperService {
    private final ResourceLoader resourceLoader;
    private final DataSource dataSource;

    private Map<String, Object> parameters = new HashMap<>();
    private static final String JASPER_DIRETORIO = "classpath:reports/";

    public void addParams(String key, Object value) {
        this.parameters.put("IMAGEM_DIRETORIO", JASPER_DIRETORIO);
        this.parameters.put("REPOSRT_LOCALE", new Locale("pt", "BR"));
        this.parameters.put(key, value);
    }

    public byte[] gerarPdf() {
        byte[] bytes = null;
        try {
            Resource resource = resourceLoader.getResource(JASPER_DIRETORIO.concat("estacionamentos.jasper"));
            InputStream inputStream = resource.getInputStream();
            JasperPrint print = JasperFillManager.fillReport(inputStream, parameters, dataSource.getConnection());
            bytes = JasperExportManager.exportReportToPdf(print);
        } catch (IOException | JRException | SQLException exception) {
            log.error("Jasper Reports ::::", exception.getCause());
            throw new RuntimeException(exception);
        }
        return bytes;
    }
}
