package sample.mainbody;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class PathTwo implements Controlling {
    private PathOne one ;
    private String FileName;
    private String delims = "[ ]+";
    private boolean breakflag=false;
    private FileInputStream fstream = null;
    private String strLine;
    private ArrayList<String> HRecord;
    private SymbolicTable SymbTab;
    private InstructionFormate InstructionSet;
    private ObjectCode ObjectCode;
    private int PC;
    private final int BASE;
    private BufferedReader reader;

    public PathTwo() {
        this.one = new PathOne("E:\\Ahmed\\GITHUB_RES\\Git2\\sic-xe\\src\\sample\\files/code.txt");
        checkUndefinedAddress(one.getSymboltable());
        if(one.getSymboltable().getRowInformmation().get("Bse")!=null)
             BASE = one.getSymboltable().getAddress(one.getSymboltable().getBase());
        else
             BASE = 0;
    }

    public PathTwo (String FileName)
    {   this();
        this.FileName=FileName;
        HRecord= new ArrayList<String>();
        SymbTab=SymbolicTable.getTable();
        InstructionSet=PathOne.getInstructionSet();
        onStart();

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

    @Override
    public void onRead() {

        try {
            mainloop:
            while ((strLine = reader.readLine()) != null)   {
                String[] tokens = strLine.split(delims);

                if(tokens[1].equals("END"))
                {
                    break mainloop;
                }
                else if((SymbTab.exists(tokens[1])&&tokens[2].equals("END")))
                {
                    break;
                }
                else if(tokens[1].equals("START")) continue;
                else if((SymbTab.exists(tokens[1])&&tokens[2].equals("START")) ) continue;

                PC=Integer.valueOf(tokens[0],16);// program counter lazem ykoon int msh hynfa3 ykon 2l value in hexa

                if(SymbTab.exists(tokens[1]))
                {if (InstructionSet.getFormate(tokens[2])==1) handleLine(tokens[2]);
                else handleLine(tokens[2] ,tokens[3]);}
                else if(InstructionSet.Exists(tokens[1])){
                    if (InstructionSet.getFormate(tokens[1])==1) handleLine(tokens[1]);
                    else
                        if(tokens.length>=3)
                        handleLine(tokens[1] , tokens[2]);
                }

            }
        } catch (IOException e) {
            System.err.println("Empty File!");
            e.printStackTrace();
        }

    }

    private void handleLine(String Mnemonic)
    {
        //System.out.println(Mnemonic);
        ObjectCode = new ObjectCode(Mnemonic,1);
        HRecord.add(ObjectCode.getObjectCode());
        //System.out.println(ObjectCode.getObjectCode());
    }
    private void handleLine(String Mnemonic , String Operand) {
        System.out.println(Mnemonic + "  " + InstructionSet.getNumberOfRegister2(Mnemonic));

        ObjectCode = new ObjectCode(Mnemonic, Operand, Operand.charAt(0) ,
                InstructionSet.getFormate(Mnemonic),InstructionSet.getNumberOfRegister(Mnemonic),
                InstructionSet.getNumberOfRegister2(Mnemonic) , PC , BASE);
        HRecord.add(ObjectCode.getObjectCode());
        //System.out.println(ObjectCode.getObjectCode());
    }





    private void checkUndefinedAddress(SymbolicTable symboltable) {
        for(Map.Entry entery:symboltable.getRowInformmation().entrySet()){

            if(symboltable.getAddress(entery.getKey().toString())==-1) {
                System.err.println("There is undifined label  "+entery.getKey());
                break;
            }
        }

    }


}
