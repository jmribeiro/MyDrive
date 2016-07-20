package pt.tecnico.grupo6.myDrive.service;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;

import java.io.File;
import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class ImportXmlFileService extends Service{

    private String _nome;

    public ImportXmlFileService(String filename) {
        _nome = filename;
    }

    @Override
    protected void dispatch() {

    	MyDrive md = Service.getMyDrive();
    	md.xmlScan(new java.io.File(_nome));
    	
    }
}