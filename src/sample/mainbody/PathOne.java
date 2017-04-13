package sample.mainbody;

import org.w3c.dom.css.Counter;

import javax.management.Query;
import java.io.*;
import java.nio.file.Files;
import java.util.Formatter;

/**
 * Created by hp-laptop on 4/13/2017.
 */
public class PathOne implements Controlling{
    private int programCounter=0;
    private Integer opperandLocation=null;
    private String file;
    private BufferedReader reader;
    private String line;
    private Boolean intiallocation=false;
    private InstructionFormate formates=null;
    private SymbolicTable symboltable=null;
    private int Start=0;
    private int End=0;
    private Formatter updatedFile;
    public String[] concat(String[] a, String[] b) {
        int aLen = a.length-1;
        int bLen = b.length;
        String[] c= new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public int gettheCodeOffset(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return End-Start;}
    public PathOne(String FileName) {
        file=FileName;
        formates=InstructionFormate.getInstructionTable();
        symboltable=SymbolicTable.getTable();
        onStart();
    }

    @Override
    public void onStart() {
        try{
            reader=new BufferedReader(new FileReader("E:\\Ahmed\\GITHUB_RES\\Git2\\sic-xe\\src\\sample\\files/code.txt"));
            onRead();
        }catch(Exception ex){
//            System.err.println("The file of Code does not exist 2");
//            System.exit(1);
        }

    }

    @Override
    public void onRead() {

        try {
            while((line =reader.readLine())!=null){
                openFile();
                String[] data1 = line.split("[ ]+");
                String []data2=data1[data1.length-1].split(",");
                String data []=concat(data1, data2);
                if(intiallocation) {
                    addUpdate(programCounter, line);
                }handdleLine(data,line);
//                System.out.println("***********************");
//                System.out.println(symboltable.getRowInformmation());
//                System.out.println("***********************");
            }
            closefile();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void handdleLine(String[] data,String line) {
        if(onlyOneInstruct(data)){
            if(!intiallocation) {
                getStartCounter(data);
                addUpdate(programCounter,line);
            }
              search4Lapel(data);
            checkOperandNumber(data);
            defineMiddelLabel(data);
            reservedInstruction(data);
        }else {
            System.err.println("Syntax error");
            System.exit(1);
        }
    }

    private void reservedInstruction(String[] data) {
     switch(data[opperandLocation]){
         case "BYTE":
             if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                 System.err.println("Syntax error in number of opperand");
                 System.exit(1);
             }
             String val[]=data[opperandLocation+1].split("'");
             switch(val[0]){
                 case"C":programCounter+=val[1].length();
                 //                     symboltable.setValue(data[opperandLocation],Integer.parseInt(convertStringtoHexa(val[1]),16));
                 break;
                 case"X":programCounter+=Math.ceil(val[1].length()/2);
                     break;
             }
             break;
         case "WORD":
             if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                 System.err.println("Syntax error in number of opperand");
                 System.exit(1);
             }
             try{int value=Integer.parseInt(data[opperandLocation+1]);
             programCounter+=3;
             }catch (Exception ex){
                 System.err.println("you should add immediate value");

             }
             break;

         case "RESW":
             if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                 System.err.println("Syntax error in number of opperand");
                 System.exit(1);
             }
             try{int value=Integer.parseInt(data[opperandLocation+1]);
                 programCounter+=value*3;
             }catch (Exception ex){
                 System.err.println("you should add immediate value");
             }
             break;


         case "RESB":
             if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                 System.err.println("Syntax error in number of opperand");
                 System.exit(1);
             }
             try{int value=Integer.parseInt(data[opperandLocation+1]);
                 programCounter+=value;
             }catch (Exception ex){
                 System.err.println("you should add immediate value");
             }
             break;



     }
    }

    private void defineMiddelLabel(String[] data) {
    int iCounter=data.length-1-opperandLocation;
    int subopperanlaocation=opperandLocation;
    if(formates.getFormate(data[opperandLocation])!=2&&!data[opperandLocation].equals("BYTE"))
    while(iCounter!=0){
        subopperanlaocation++;
       try{int i=Integer.parseInt(data[subopperanlaocation]) ;}catch (NumberFormatException ex) {
           if(symboltable.getRowInformmation().get(data[subopperanlaocation])==null)
           symboltable.setRow(data[subopperanlaocation], 0);
       }
           iCounter--;

    }

    }

    private void checkOperandNumber(String[] data) {

        switch(formates.getFormate(data[opperandLocation])){
    case 0:
       if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
           System.err.println("Syntax error in number of opperand");
           System.exit(1);
       }
    break;
    case 1:
if(data.length!=opperandLocation+1){
    System.err.println("Syntax error in number of opperand");
    System.exit(1);
}

        break;
    case 2:
        if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&(data.length!=opperandLocation+2||data.length!=opperandLocation+3)){
            System.err.println("Syntax error in number of opperand");
            System.exit(1);
        }if(data.length==opperandLocation+2) {
        try{
            InstructionFormate.Register.valueOf(data[opperandLocation+1]);}catch (IllegalArgumentException ex){
            System.err.println("this register doesnot exist");
            System.exit(1);
        }
    }
        if(data.length==opperandLocation+3) {
            try{
                InstructionFormate.Register.valueOf(data[opperandLocation+1]);
                InstructionFormate.Register.valueOf(data[opperandLocation+2]);    }catch (IllegalArgumentException ex){
                System.err.println("this register doesnot exist");
                System.exit(1);
            }
        }
    break;
    case 3:
        if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
            System.err.println("Syntax error in number of opperand");
            System.exit(1);
        }

        break;
            case 4:
                if(formates.getNumberOfRegister(data[opperandLocation])!=data.length-opperandLocation-1&&data.length!=opperandLocation+2){
                    System.err.println("Syntax error in number of opperand");
                    System.exit(1);
                }

                break;
}

    }


    private void search4Lapel(String[] data) {
        if (formates.getInstructionMap().get(data[0]) == null) {
            if(symboltable.getRowInformmation().get(data[0])==null)
            symboltable.setRow(data[0], programCounter);
            else
                symboltable.setAddress(data[0],programCounter);
            if (formates.getInstructionMap().get(data[1]) == null) {
                System.err.println("syntax error");
                System.exit(1);
            } else {
                programCounter += formates.getFormate(data[1]);
              opperandLocation=1;
// handel synatex error
            }
        }else if(data[0].equals("END")){
            programCounter=symboltable.getAddress(data[1]);
             opperandLocation=0;
             End=programCounter;
        }else{

            programCounter += formates.getFormate(data[0]);
            opperandLocation=0;
        }
    }

    private void getStartCounter(String[] data) {
     if(data[1].equals("START")){
         programCounter=Integer.parseInt(data[2],16);
         Start=programCounter;
         intiallocation=true;
     }}

    private String convertStringtoHexa(String s) {
        String value="";
        for(int i=0;i<s.length();i++){
            value+=new Integer(s.charAt(i));
        }
        return value;
    }

    private boolean onlyOneInstruct(String[] data) {
    int counter=0;
        for(String instruct:data )
        if(formates.getInstructionMap().get(instruct)!=null) counter++;

        return counter==1?true:false;
    }
    private void openFile(){
        try {

            updatedFile=
            new Formatter(new BufferedWriter(new FileWriter("UpdateCode.txt", true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void addUpdate(int addres,String line){
        String code = Integer.toHexString(addres);


        PrintWriter outputFile= null;
        try {
            outputFile = new PrintWriter(new FileWriter("UpdateCode.txt", true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFile.printf("%s   %s\n",code,line);
        outputFile.println();
        outputFile.close();

    }
    private void closefile(){
        updatedFile.close();
    }

}

