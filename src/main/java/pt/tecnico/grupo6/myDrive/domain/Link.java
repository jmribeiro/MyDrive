package pt.tecnico.grupo6.myDrive.domain;

import pt.tecnico.grupo6.myDrive.exception.*;

import org.jdom2.Element;
import org.jdom2.Document;
import org.joda.time.LocalDate;

public class Link extends Link_Base {
    
    private Link() {
        super();
        setPath("");
    }

    public Link(String name, User owner, Directory parent, String absolutePath) throws InvalidFileNameException, UserDoesntExistException, InvalidPathException {
    	super();
    	init(name, owner, parent);
    	setPath(absolutePath);
    }

    public Link(Element linkNode, Directory parent) throws InvalidFileNameException, UserDoesntExistException{
        
        super();

        exportNode(linkNode, parent);

        String path = linkNode.getChildText("path");
        
        setPath(path);
    }

    @Override
    public Element xmlExport() {
        
        Element link = new Element("linkFile");

        fillNode(link);
        
        Element path = new Element("path");
        path.addContent(getPath());
        link.addContent(path);

        return path;
    }

    @Override
    public void setPath(String path){
        if(path.length()>=1024){
            throw new InvalidPathException();
        }else{
            super.setPath(path);
        }
    }
}