package sample.mainbody;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class PathTwo implements Controlling {
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

    public PathTwo() {
        this.one = new PathOne("E:\\Ahmed\\GITHUB_RES\\Git2\\sic-xe\\src\\sample\\files/code.txt");
        if(one.getSymboltable().getRowInformmation().get("Bse")!=null)
             BASE = one.getSymboltable().getAddress(one.getSymboltable().getBase());
        else
             BASE = 0;


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

                ObjectCode=new ObjectCode(tokens,location,InstructionFormate.getInstructionTable().getFormate(tokens[location
                        ]),BASE,indexed?"X":"x");
                System.out.println(ObjectCode.getObjectCode());
                TRecord.add(ObjectCode.getObjectCode());
            }
        } catch (IOException e) {
            System.err.println("Empty File!");
            e.printStackTrace();
        }
        updateTRecord(TRecord);

    }

    private void updateTRecord(ArrayList<String> tRecord) {




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


}
