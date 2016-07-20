package pt.tecnico.grupo6.myDrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;
import pt.tecnico.grupo6.myDrive.presentation.Hello;

import java.lang.reflect.*;
import mockit.Expectations;
import mockit.Verifications;


import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import java.lang.reflect.Method;

@RunWith(JMockit.class)
public class ExecuteFileServiceTest extends ServiceTest{

	private Long validToken, invalidToken;
    private Long nullToken = new Long(0);
	private String pathParaFicheiro, pathParaDirectoria, pathParaAplicacao1, pathParaAplicacao2, emptyPath, brokenPath, invalidPath;
    private Link linkParaFicheiro, linkParaDirectoria, linkParaAplicacao1, invalidLink, emptyLink;
    private String[] args, argsVazios;

    
    protected void populate() {

            MyDrive md = MyDrive.getInstance();
            md.reset();

            
            md.newUser("user1");
            md.newUser("user2");

            User user1 = md.getUserByUsername("user1");
            User user2 = md.getUserByUsername("user2");

            // TOKENS
            validToken = md.login("user1", "12345678");
            invalidToken = md.login("user2", "12345678");
            md.logout(invalidToken); // logout para ficar invalido

            // vai buscar o loggin
            Login login = md.getLoginByToken(validToken);

            // vai buscar o cwd do login
            Directory cwd = login.getCurrentWorkingDirectory();

            // CREATE FILES
            user1.createPlainFile("ficheiro", cwd);
            user1.createApplication("aplicacao1", cwd);
            user1.createApplication("aplicacao2", cwd);
            user1.createApplication("app", cwd);
            
            // vai buscar o ficheiro
            PlainFile ficheiro = (PlainFile)cwd.getFileByName("ficheiro");
            Application aplicacao1 = (Application)cwd.getFileByName("aplicacao1");
            Application aplicacao2 = (Application)cwd.getFileByName("aplicacao2");

                        
           
            // PATHS
            pathParaFicheiro = ficheiro.getAbsolutePath();
            pathParaDirectoria = cwd.getAbsolutePath();
            pathParaAplicacao1 = aplicacao1.getAbsolutePath();
            pathParaAplicacao2 = aplicacao2.getAbsolutePath();
            brokenPath = " home / cenas/naoExisto";
            invalidPath = " / home   /cenas/#$%&%$#/naoPossoExistir";
            emptyPath = "";

            // LINKS
            linkParaFicheiro = new Link("linkParaFicheiro", user1, cwd, pathParaFicheiro);
            linkParaDirectoria= new Link("linkParaDirectoria", user1, cwd, pathParaDirectoria);
            linkParaAplicacao1 = new Link("linkParaAplicacao", user1, cwd, pathParaAplicacao1);
            invalidLink = new Link("invalidLink", user1, cwd, invalidPath);
            emptyLink = new Link("emptyLink", user1, cwd, invalidPath);
 

            // Args
            args = new String[1];
            args[0] = "argumento";
            argsVazios = new String[1];
  

            // mete linhas no plainFile
            ficheiro.addLine(pathParaAplicacao1 + " " + args);
            ficheiro.addLine(pathParaAplicacao2 + " " + args);
            // mete class de teste nas apps
            aplicacao1.addMethod("pt.tecnico.grupo6.myDrive.presentation.Hello");
            aplicacao2.addMethod("pt.tecnico.grupo6.myDrive.presentation.Hello.bye");

                 

    }
    
    
      /**
     * Teste 1
     **/
     @Test
     public void ExecutePlainFileSuccess() throws InstantiationException, NoSuchMethodException,IllegalAccessException,InvocationTargetException, ClassNotFoundException{

         ExecuteFileService service = new ExecuteFileService(validToken, pathParaFicheiro);
         service.execute();
      

         new Verifications() {
             {
                
                Hello.greet(args);
                
             }
         };

     }

       /**
     * Teste 2
     **/
     @Test
     public void ExecutePlainFileLinkSuccess() throws NoSuchMethodException, ClassNotFoundException{
        

         ExecuteFileService service = new ExecuteFileService(validToken, linkParaFicheiro.getAbsolutePath());
         service.execute();

         new Verifications() {
             {
                
                 Hello.bye(args);
                
             }
         };
    }

     /*
     * Teste 3
     **/
     @Test
     public void ExecuteApplicationSuccess() throws NoSuchMethodException, ClassNotFoundException{
        
         ExecuteFileService service = new ExecuteFileService(validToken, pathParaAplicacao1, args);
         service.execute();

         new Verifications() {
             {
                
                 Hello.bye(args);
                
             }
         };
     }

     /*
     * Teste 4
     **/
     @Test
     public void ExecuteApplicationLinkSuccess() throws NoSuchMethodException, ClassNotFoundException{
        
         ExecuteFileService service = new ExecuteFileService(validToken, linkParaAplicacao1.getAbsolutePath());
         service.execute();


         new Verifications() {
             {
                
                 Hello.bye(args);
                
             }
         };
     }
   

     /**
    * Teste 5
    **/
    @Test (expected = InvalidExecutionException.class)
    public void ExecutePlainFileWithArgs(){
        
        ExecuteFileService service = new ExecuteFileService(validToken, pathParaFicheiro, args);
        service.execute();

    }

      /**
    * Teste 6
    **/
    @Test (expected = InvalidExecutionException.class)
    public void ExecutePlainFileLinkWithArgs(){
        
        ExecuteFileService service = new ExecuteFileService(validToken, linkParaFicheiro.getAbsolutePath(), args);
        service.execute();

    }


    /**
    * Teste 7 
    **/
    @Test(expected = FileDoesntExistException.class)
    public void ExecuteDirectory(){
 
        ExecuteFileService service = new ExecuteFileService(validToken, pathParaDirectoria, argsVazios);
        service.execute();

    }

    /**
    * Teste 8               
    **/
    @Test(expected = FileDoesntExistException.class) 
    public void ExecuteDirectoryLink(){

        ExecuteFileService service = new ExecuteFileService(validToken, linkParaDirectoria.getAbsolutePath(), argsVazios);
        service.execute();

    }


    /**
    * Teste 9
    **/
    @Test(expected = InvalidPathException.class) 
    public void ExecuteFileBrokenPath(){


        ExecuteFileService service = new ExecuteFileService(validToken, brokenPath, argsVazios);
        service.execute();

     }

    /**
    * Teste 10
    **/
    @Test(expected = InvalidPathException.class) 
    public void ExecuteFileInvalidPath(){

        
        ExecuteFileService service = new ExecuteFileService(validToken, invalidPath, argsVazios);
        service.execute();

    }

    /**
    * Teste 11
    **/
    @Test(expected = InvalidPathException.class) 
    public void ExecuteFileEmptyPath(){

        
        ExecuteFileService service = new ExecuteFileService(validToken, emptyPath, argsVazios);
        service.execute();

    }

    /**
    * Teste 12
    **/
    @Test(expected = FileDoesntExistException.class) 
    public void ExecuteFileEmptyLink(){
        
        ExecuteFileService service = new ExecuteFileService(validToken, emptyLink.getAbsolutePath(), argsVazios);
        service.execute();

    }

    /**
    * Teste 13                   // invaldLink é um Link com um invalidPath la dentro ?
    **/
    @Test(expected = FileDoesntExistException.class) 
    public void ExecuteFileInvalidLink(){
        
        ExecuteFileService service = new ExecuteFileService(validToken, invalidLink.getAbsolutePath(), argsVazios);
        service.execute();

    }


    /**
    * Teste 14
    **/
    @Test(expected = InvalidSessionException.class)
    public void ExecuteFileInvalidToken(){


        
        ExecuteFileService service = new ExecuteFileService(invalidToken, "Nao Interessa", argsVazios);
        service.execute();

    }


    /**
    * Teste 15
    **/
    @Test(expected = InvalidSessionException.class) 
    public void ExecuteFileNullToken(){

        
        ExecuteFileService service = new ExecuteFileService(nullToken, "Nao Interessa", argsVazios);
        service.execute();

    }

   
}