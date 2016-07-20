package pt.tecnico.grupo6.myDrive.domain;

import java.util.Scanner;
import org.joda.time.LocalDate;

import pt.tecnico.grupo6.myDrive.exception.*;

import org.jdom2.Element;
import org.jdom2.Document;

public class PlainFile extends PlainFile_Base {
    
    protected PlainFile() {
        super();
        setContent("");
    }

    public PlainFile(String name, User owner, Directory parent) throws InvalidFileNameException, UserDoesntExistException {
        init(name, owner, parent);
        setContent("");
    }

    public PlainFile(String name, User owner, Directory parent, String content) throws InvalidFileNameException, UserDoesntExistException{
        init(name, owner, parent);
        setContent(content);
    }

    public PlainFile(Element plainFileNode, Directory parent) throws InvalidFileNameException, UserDoesntExistException{
        super();
        exportNode(plainFileNode, parent);
        String content = plainFileNode.getChildText("content");
        setContent(content);
    }

    void addContent(String content){
        setContent(getContent()+content);
    }

    public void addLine(String line){
        addContent(line + "\n");
    }
    
    public String print(){
        return getContent();
    }

    @Override
    public Element xmlExport() {
        
        Element plainFile = new Element("plainFile");

        fillNode(plainFile);

        Element content = new Element("content");
        content.addContent(getContent());
        plainFile.addContent(content);

        return plainFile;
    }

}


