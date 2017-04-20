package sample.mainbody;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.util.*;

public class PathTwo implements Controlling {
    private final int diplacement;
    private PathOne one ;
    private String FileName;
    private String delims = "[ ]+";
    private boolean breakflag=false;
    private FileInputStream fstream = null;
    private String strLine;
    private ArrayList<String> TRecord;
    private SymbolicTable SymbTab;
    private InstructionFormate InstructionSet;
    private ObjectCode ObjectCode;
    private int PC;
    private boolean indexed =false;
    private final int BASE;
    private BufferedReader reader;
    private ArrayList<String>modify;
    private ArrayList<String>Adresses;
    private String fileName="COPY";
    private final int start;
    private ArrayList<String> lastlist;
    Formatter updatedFile;
    public PathTwo() {
        this.one = new PathOne("E:\\Ahmed\\GITHUB_RES\\Git2\\sic-xe\\src\\sample\\files/code.txt");
        if(one.getSymboltable().getRowInformmation().get("Bse")!=null)
             BASE = one.getSymboltable().getAddress(one.getSymboltable().getBase());
        else
             BASE = 0;
        modify=new ArrayList<>();
        Adresses=new ArrayList<>();
        diplacement=one.getDispalcement();
        fileName=one.getProjectName();
        start=one.getStart();
    }

    public PathTwo (String FileName)
    {   this();
        this.FileName=FileName;
        TRecord= new ArrayList<String>();
        SymbTab=SymbolicTable.getTable();
        InstructionSet=PathOne.getInstructionSet();
        System.out.println(SymbolicTable.getTable().getRowInformmation().toString());
        if(SymbolicTable.getTable().getValue("ErrOrS")==0&&checkUndefinedAddress(one.getSymboltable()))  {
            onStart();
        }else{
            System.err.println("WE couldnot formate object code because there is errors");
        }

    }


    @Override
    public void onStart() {
        try {
            reader=new BufferedReader(new FileReader(FileName));
        } catch (FileNotFoundException e) {
            System.err.println("Wrong File Path or File does not exist!");
            e.printStackTrace();
        }
        onRead();
    }
    public String[] concat(String[] a, String[] b) {
        int aLen = a.length-1;
        int bLen = b.length;
        String[] c= new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
    @Override
    public void onRead() {
        try {
            mainloop:
            while ((strLine = reader.readLine()) != null)   {
                indexed=false;
                String[] data1 = strLine.split("[ ]+");
                String []data2=data1[data1.length-1].split(",");
                String [] tokens  =concat(data1, data2);
                int location=0;
                while(InstructionFormate.getInstructionTable().getInstructionMap().get(tokens[location])==null)
                    location++;
                 if(tokens[location].equals("END"))
                     break mainloop;

                if(tokens[tokens.length-1].equals("X")&&InstructionFormate.getInstructionTable().getFormate(tokens[location])>=3) {
                    tokens = Arrays.copyOfRange(tokens, 0, tokens.length - 1);
                    indexed=true;
                }
                if(InstructionFormate.getInstructionTable().getFormate(tokens[location])==4) {
                 String tem=tokens[location+1].substring(1);
                    try{
                        int temp=Integer.parseInt(tem);
                    }catch (Exception ex){
                        modify.add(tokens[0]);
                    }
                }
                if(InstructionFormate.getInstructionTable().getFormate(tokens[location])!=0||(tokens[location].equals("BYTE")||tokens[location].equals("WORD"))
                        ||(tokens[location].equals("RESB")||tokens[location].equals("RESW"))) {
                    Adresses.add(tokens[0]);
                }
                ObjectCode=new ObjectCode(tokens,location,InstructionFormate.getInstructionTable().getFormate(tokens[location
                        ]),BASE,indexed?"X":"l");
                TRecord.add(ObjectCode.getObjectCode());
            }
        } catch (IOException e) {
            System.err.println("Empty File!");
            e.printStackTrace();
        }
        updateTRecord();
//        modifyList();
    }
private void modifyList(){
    lastlist=new ArrayList<>();
        Iterator<String> it = TRecord.iterator();
    while (it.hasNext()) {
     String tem=it.next();
     if(tem==null)
         it.remove();
}
    Iterator<String> i = Adresses.iterator();
    Iterator<String> t = TRecord.iterator();
    while (i.hasNext()) {
        String tem=i.next();
        String te1=t.next();
        lastlist.add(tem+"#"+te1);

    }
}
    private void updateTRecord() {
        modifyList();
        openFile();
           String start=Integer.toHexString(this.start);
           while(start.length()<6)
               start="0"+start;
           String disp=Integer.toHexString(diplacement);
        while(disp.length()<6)
            disp="0"+disp;
           String line="H"+"^"+fileName+"^"+start+"^"+disp;
           addUpdate(line);
        String []temp;
        String rec="^";
        boolean state=false;
        String beginadress = null;
        Iterator<String> List = lastlist.iterator();
        loop:
        while (List.hasNext())
        {
            String val=List.next();
            temp=val.split("#");

            if(!state){
                beginadress=temp[0];
                while(beginadress.length()<6){
                    beginadress="0"+beginadress;
                }
                state=true;
            }
            if(!temp[1].equals("Sep")&&(countRecord(rec)+temp[1].length())/2<=30){
                rec+=temp[1]+"^";
            }
            else if(countRecord(rec)!=0){
                String temper=rec;
                String length=Integer.toHexString(countRecord(rec)/2);
                length=length.length()!=2?"0"+length:length;
                String trec="T"+"^"+beginadress+"^"+length+rec;
                trec=trec.substring(0,trec.length()-1);
                addUpdate(trec);
                state=false;
                rec="^";
                if((countRecord(temper)+temp[1].length())/2>30) {
                beginadress=temp[0];
                state=true;
                rec+=temp[1]+"^";
                }
            }else  state=false;
            }
          if(countRecord(rec)!=0){
              String length=Integer.toHexString(countRecord(rec)/2);
              length=length.length()!=2?"0"+length:length;
              String trec="T"+"^"+beginadress+"^"+length+rec;
              trec=trec.substring(0,trec.length()-1);
              addUpdate(trec);

          }


        Iterator<String> l = modify.iterator();
        while (l.hasNext()) {
            Integer i=new Integer(l.next());
            i++;
            String loc=Integer.toString(i);
            while(loc.length()<6){
                loc="0"+loc;
            }
            String mod="M^"+loc+"^05";
            addUpdate(mod);


        }

    }
private int countRecord(String line){
       int i=0;
       int counter=0;
       while (i<line.length()){
           if(line.charAt(i)!='^')
               counter++;
        i++;
       }
       return counter;
}

    private boolean checkUndefinedAddress(SymbolicTable symboltable) {
        for(Map.Entry entery:symboltable.getRowInformmation().entrySet()){

            if(symboltable.getAddress(entery.getKey().toString())==-1) {
                System.err.println("There is undifined label  "+entery.getKey());

                return false;
            }
        }
        return true;

    }
    private void openFile(){
        try {

             updatedFile = new Formatter(new BufferedWriter(new FileWriter("Record.txt", true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void addUpdate(String line){
        PrintWriter outputFile= null;
        try {
            outputFile = new PrintWriter(new FileWriter("Record.txt", true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputFile.printf("%s",line);
        outputFile.println();
        outputFile.close();

    }
    private void closefile(){
        updatedFile.close();
    }


}
