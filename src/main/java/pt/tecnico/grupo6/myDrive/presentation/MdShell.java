package pt.tecnico.grupo6.myDrive.presentation;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;
import java.util.*;
import java.io.*;

import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

import pt.ist.fenixframework.*;

public class MdShell extends Shell {

    private Long _currentToken;
    private String _currentUsername;
    private Map<String, Long> _loggedUsers;

    public static void main(String[] args) throws Exception {
        
        MdShell shell;

        if(args.length==1){
            shell = new MdShell(args[0]);
        }else{
            shell = new MdShell();
        }

        shell.execute();
    }

    public MdShell(String xmlFile){
        super("MyDrive");
        init();
        importFileSystem(xmlFile);
        printXmlExport();
        loginAsGuest();
        new Login(this);
        new ChangeWorkingDirectory(this);
        new List(this);
        new Execute(this);
        new Write(this);
        new Environment(this);
        new Key(this);
        new ExportXmlFile(this);
    }

    public MdShell() {
        super("MyDrive");
        init();
        printXmlExport();
        loginAsGuest();
        new Login(this);
        new ChangeWorkingDirectory(this);
        new List(this);
        new Execute(this);
        new Write(this);
        new Environment(this);
        new Key(this);
        new ExportXmlFile(this);
    }

    private void importFileSystem(String filename){
        ImportXmlFileService service = new ImportXmlFileService(filename);
        service.execute();
    }

    private void init(){
        _loggedUsers = new TreeMap<String, Long>();
    }

    public void addUser(Long token, String username){
        _currentUsername = username;
        _currentToken = token;
        _loggedUsers.put(username, token);
    }

    public Long getToken(String username){
        return _loggedUsers.get(username);
    }

    public String getUsernameByToken(Long token){
        for(String key : _loggedUsers.keySet()){
            if(_loggedUsers.get(key).equals(token)){
                return key; 
            }
        }
        return null;
    }

    public void setCurrentToken(Long token){
        _currentToken = token;
    }

    public void setCurrent(Long token, String username){
        _currentToken = token;
        _currentUsername = username;
    }

    public Long getCurrentToken(){
        return _currentToken;
    }

    private void loginAsGuest(){
        LoginUserService service = new LoginUserService("nobody", "");
        service.execute();
        setCurrentToken(((TokenDto)service.result()).getToken());
        addUser(getCurrentToken(), "nobody");
    }

    @Atomic
    public static void printXmlExport(){
        try{
            Document doc = MyDrive.getInstance().xmlExport();
            XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());

            xmlOutput.output(doc, new PrintStream(System.out));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
