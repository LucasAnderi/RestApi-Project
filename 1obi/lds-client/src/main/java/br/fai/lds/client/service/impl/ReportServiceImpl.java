package br.fai.lds.client.service.impl;

import br.fai.lds.client.service.ReportService;
import br.fai.lds.client.service.UserService;
import br.fai.lds.model.entities.UserModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserService<UserModel> userService;

    private enum TYPE {
        PDF,
        HTML,
        XML
    }


    @Override
    public String generateAndGetPdfFilePath() {

        String home = System.getProperty("user.home");

        // c:/users/lab/Documents/fai/reports/
        String basePath = home
                + File.separator
                + "Documents"
                + File.separator
                + "fai"
                + File.separator
                + "reports"
                + File.separator;

        List<UserModel> users = userService.find();

        if (users == null || users.isEmpty()) {
            return "";
        }

        File file;
        try {
            file = ResourceUtils
                    .getFile("classpath:reports/users-report-fai.jrxml");// armazena o conteudo do arquivo do classpath fornecido , entao ele guarda o template pdf
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        JasperReport jasperReport;
        try {
            jasperReport = JasperCompileManager // transforma num formulario pronto pra preenchimento baseado no template do File acima
                    .compileReport(file.getAbsolutePath()); //JasperCompileManager - Facade class for compiling report designs into the ready-to-fill form and for getting the XML representation of report design objects for storage or network transfer.
        } catch (JRException e) {
            e.printStackTrace();
            return "";
        }

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users); // encapsula a lista de usuarios que vai ser printada no template
//        A data source implementation that wraps a collection of JavaBean objects.
        //JavaBeans are classes that encapsulate many objects into a single object
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("MODIFIED_TITLE", "FAI - 2022"); // seta titulo do template

        JasperPrint jasperPrint; // An instance of this class represents a page-oriented document that can be viewed, printed or exported to other formats.
        try {
            jasperPrint = JasperFillManager
                    .fillReport(jasperReport, parameters, dataSource);//JasperFillManager.fill report - Facade class for filling compiled report designs with data from report data sources
        } catch (JRException e) {
            e.printStackTrace();
            return "";
        }

        String filePath = generateFilePath(basePath); // cria no metodo abaixo opath para onde vai salvar + o nome do  report criado

        try {
            JasperExportManager //Facade class for exporting generated reports into more popular formats such as PDF, HTML and XML.
                    .exportReportToPdfFile(jasperPrint, filePath); // exporta o file criado para o path
            return filePath;
        } catch (JRException e) {
            e.printStackTrace();
            return "";
        }
    }

    // dica para preencher esse cara user.home - basePath - users - File.Resource - JasperReport-CompileManage - JRBean(users) -Map -jasperPirint.Fill - filePath.generate -JasperExportManager return filePath
    private String generateFilePath(String basePath) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss"); // formata a data
        Date date = new Date(System.currentTimeMillis());//pega a data atual
        String filename = formatter.format(date) + "-report." + TYPE.PDF; // nome do aquivo vai ser a data e do tipo pdf

        return basePath + filename;
    }
}
