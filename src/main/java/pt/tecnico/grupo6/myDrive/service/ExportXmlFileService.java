package pt.tecnico.grupo6.myDrive.service;

import java.util.Set;
import java.util.HashSet;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;

public class ExportXmlFileService extends Service {
    private Long _token;
    private String _name;

    public ExportXmlFileService(Long token) {
        _token = token;
        _name = "drive";
    }

    public ExportXmlFileService(Long token, String name) {
        _token = token;
        _name = name;
    }

    @Override
    protected void dispatch() {
        MyDrive md = Service.getMyDrive();
        Login login = md.getLoginByToken(_token);

        login.exportXml(_name+".xml");
    }
}