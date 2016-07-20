package pt.tecnico.grupo6.myDrive.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

import pt.tecnico.grupo6.myDrive.*;
import pt.tecnico.grupo6.myDrive.domain.*;
import pt.tecnico.grupo6.myDrive.presentation.*;
import pt.tecnico.grupo6.myDrive.service.*;
import pt.tecnico.grupo6.myDrive.exception.*;
import pt.tecnico.grupo6.myDrive.service.dto.*;

@RunWith(JMockit.class)
public class IntegrationTest extends ServiceTest {

    private static final String username = "testUser", password ="12345678", newDirectory = "newDirectory",
                                newPlainFile = "newPlainFile", newLink = "newLink", newApp = "newApp", newApp2 = "newApp2",
                                variableName = "newVariable", varValue = "/home/", 
                                variableName2 = "newVariable2", varValue2 = "/home/newDirectory",
                                byeMethod = "pt.tecnico.grupo6.myDrive.presentation.bye()",
                                greetMethod = "pt.tecnico.grupo6.myDrive.presentation.greet()";
    String[] arguments = {"1", "2", "3"};
    private Long token; 
    private pt.tecnico.grupo6.myDrive.domain.Login login;

    protected void populate() { // populate mockup

        try{

            MyDrive md = MyDrive.getInstance();
            
            md.reset();
            
            md.newUser(username, password, "testUser", "");
            
            User testUser = md.getUserByUsername(username);
            
            token = md.login(username, password);

            login = md.getLoginByToken(token);
            
        }catch(InvalidUsernameException | UserAlreadyExistsException e){
            //Se cair aqui deviar ser valido la em cima
        }

    }

    @Test
    public void success() throws Exception {
  		
        new LoginUserService(username, password).execute();
        ListDirectoryService listService = new ListDirectoryService(token, login.getCurrentWorkingDirectory().getAbsolutePath());
        listService.execute();
        System.out.println("\n"+((ContentsDto)listService.result()).getContent()+"\n");

        new CreateFileService(token, newDirectory, FileType.DIRECTORY, "").execute();
        listService = new ListDirectoryService(token, login.getCurrentWorkingDirectory().getAbsolutePath());
        listService.execute();
        System.out.println("\n"+((ContentsDto)listService.result()).getContent()+"\n");

        new ChangeDirectoryService(token, login.getCurrentWorkingDirectory().getDirectoryByName(newDirectory).getAbsolutePath()).execute();

        new AddVariableService(token, variableName, varValue).execute();

        new CreateFileService(token, newApp, FileType.APPLICATION, byeMethod).execute();

        new CreateFileService(token, newApp2, FileType.APPLICATION, greetMethod).execute();

        String plainFileContent = "" + login.getCurrentWorkingDirectory().getFileByName(newApp).getAbsolutePath() + " 1 2 3";
        new CreateFileService(token, newPlainFile, FileType.PLAINFILE, plainFileContent).execute();
        listService = new ListDirectoryService(token, login.getCurrentWorkingDirectory().getAbsolutePath());
        listService.execute();
        System.out.println("\n"+((ContentsDto)listService.result()).getContent()+"\n");

        new AddVariableService(token, variableName2, varValue2).execute();

        ListVariablesService listVar = new ListVariablesService(token);
        listVar.execute();
        System.out.println("\n"+((EnvironmentVariablesListDto)listVar.result())+"\n");
        //falta imprimir bem

        ReadFileService readService = new ReadFileService(token, newPlainFile);
        readService.execute();
        System.out.println("\n"+((ContentsDto)readService.result()).getContent()+"\n");

        String plainFileContent2 = "" + login.getCurrentWorkingDirectory().getFileByName(newApp).getAbsolutePath() + " 1 2 3"
        + "\n"+login.getCurrentWorkingDirectory().getFileByName(newApp2).getAbsolutePath() + " 1 2 3";
        WriteFileService writeService = new WriteFileService(token, newPlainFile, plainFileContent2);
        writeService.execute();
        System.out.println("\n"+((ContentsDto)writeService.result()).getContent()+"\n");

        new DeleteFileService(token, newApp2).execute();
        listService = new ListDirectoryService(token, login.getCurrentWorkingDirectory().getAbsolutePath());
        listService.execute();
        System.out.println("\n"+((ContentsDto)listService.result()).getContent()+"\n");

        new ExecuteFileService(token, login.getCurrentWorkingDirectory().getFileByName(newApp).getAbsolutePath(), arguments);

        new MockUp<ExecuteAssociationService>() {
            @Mock
            Dto result() { 
                return new ContentsDto("File "+ newPlainFile + " is running with pdfViewer"); 
            }
        };
        ExecuteAssociationService execFile =  new ExecuteAssociationService(token, login.getCurrentWorkingDirectory().getFileByName(newPlainFile).getAbsolutePath());
        execFile.execute();
        System.out.println("\n"+((ContentsDto)execFile.result()).getContent()+"\n");

        new MockUp<EnvironmentLinkService>() {
            @Mock
            Dto result() { 
                return new ContentsDto("/home/root/a/b/c"); 
            }
        };
        EnvironmentLinkService envLink =  new EnvironmentLinkService(token, "/home/root/$B/c");
        envLink.execute();
        System.out.println("\n"+((ContentsDto)envLink.result()).getContent()+"\n");

        new ExportXmlFileService(token).execute();
    }
}