package pt.tecnico.grupo6.myDrive.domain;
import pt.tecnico.grupo6.myDrive.exception.*;
import org.jdom2.*;
import java.util.*;
import org.joda.time.LocalDate;
import java.lang.*;
import java.io.*;
import java.lang.reflect.*;

public class Application extends Application_Base {
    
    private Application() {
        super();
        setMethod("");
        
    }

    public Application(String name, User owner, Directory parent) throws InvalidFileNameException, UserDoesntExistException{
        init(name, owner, parent);
    	setMethod("");
    }

    public Application(String name, User owner, Directory parent, String method) throws InvalidFileNameException, UserDoesntExistException{
        init(name, owner, parent);
        setMethod(method);
    }

    public Application(Element applicationNode, Directory parent) throws InvalidFileNameException, UserDoesntExistException{
        super();
        exportNode(applicationNode, parent);
        String method = applicationNode.getChildText("method");
        setMethod(method);
    }   

    @Override
    public void setMethod(String method){
        if(method.equals("")){
            super.setMethod(method);
        }else{
            String[] parts = method.split("\\(");
               
            String[] partsWs = parts[0].split("\\.");
                                                                                    
            for(String part : partsWs){
                if(!isValidJavaIdentifier(part)){
                    throw new InvalidJavaMethodException(method);
                }else{ 
                    super.setMethod(method);}
            }
        }
    }

    public final static boolean isValidJavaIdentifier(String s){
     // an  empty or null string cannot be a valid identifier 
     if (s == null || s.length() == 0){
        return false;
     }

     char[] c = s.toCharArray();
     if (!Character.isJavaIdentifierStart(c[0])){
        return false;
     }

     for (int i = 1; i < c.length; i++){
        if (!Character.isJavaIdentifierPart(c[i])){
           return false;
        }
     }

     return true;
    }

    public void changeMethod(String method) {
    	setMethod(method);
    }

    public void removeMethod() {
        setMethod("");
    }

    public void addMethod(String method) {
        setMethod(method);
    }


    public void execute(String[] args) throws InstantiationException, NoSuchMethodException,IllegalAccessException,InvocationTargetException, ClassNotFoundException   {
            
        Class<?> cls;
        Method meth;
        try { // name is a class: call main()
            cls = Class.forName(getMethod());
            meth = cls.getMethod("main", String[].class);
        } catch (ClassNotFoundException cnfe) { 
            int pos;
            if ((pos = getMethod().lastIndexOf('.')) < 0) throw cnfe;
            cls = Class.forName(getMethod().substring(0, pos));
            meth = cls.getMethod(getMethod().substring(pos+1), String[].class);
        }
        meth.invoke(null, (Object)args); // static method (ignore return)
      
    }
    
    @Override
    public Element xmlExport() {
        
        Element application = new Element("application");

        fillNode(application);

        Element method = new Element("method");
        method.addContent(getMethod());
        application.addContent(method);

        return application;
    }

}