package sample.mainbody;

import java.lang.invoke.SwitchPoint;

public class ObjectCode {

    private String mnemonic;
    private String operand;
    private char AddressingMode;
    private int numberOfOperands;
    private int numberOfRegisters;
    private int format;
    private String objectCode;
    private InstructionFormate InstructionSet;
    private int pc;
    private int base;
    private String [] Operands;
    private SymbolicTable SymTab;
    private final static String ZEROES3 = "000";
    private final static String ZEROES5 = "00000";
    private int  locationMnemonic;
    private String []data;
    private String indexed;

    public void setIndexed(String indexed) {
        this.indexed = indexed;
    }

    public ObjectCode (String [] data, int locationMnemonic, int format, int base  )
    {
        this.data=data;
        mnemonic=data[locationMnemonic];
        pc=Integer.valueOf(data[0],16)+format;
        this.locationMnemonic=locationMnemonic;
        this.format=format;
          numberOfOperands=InstructionFormate.getInstructionTable().getNumberOfRegister(mnemonic);
          numberOfRegisters=InstructionFormate.getInstructionTable().getNumberOfRegister2(mnemonic);
        InstructionSet = PathOne.getInstructionSet();
        setInfo();

    }
    private void setformate(String []data){
    }




    public ObjectCode (String [] data, int locationMnemonic, int format, int base, String indexed )
    {
        this(data,locationMnemonic,format,base);
        this.indexed=indexed;
    }
    private void setInfo() {
        switch (format) {
            case 0:
                if (mnemonic.equals("START") || mnemonic.equals("END"))
                    break;
                if (mnemonic.equals("BYTE") || mnemonic.equals("WORD")) {
                    objectCode = Integer.toHexString(SymbolicTable.getTable().getValue(data[1]));
               if (mnemonic.equals("BYTE")&&objectCode.length()%2!=0){
                   objectCode="0"+objectCode;
               }
               while((mnemonic.equals("WORD")&&objectCode.length()<6)){
                   objectCode="0"+objectCode;
               }
                }
                if (mnemonic.equals("RESB") || mnemonic.equals("RESW"))
                    objectCode = "Sep";
                break;
            case 1:
                objectCode = InstructionFormate.getInstructionTable().getOppCode(mnemonic);
                break;
            case 2:
                if (numberOfOperands == 2) {
                    objectCode = InstructionFormate.getInstructionTable().getOppCode(mnemonic)
                            + registers(data[data.length - 2])
                            + registers(data[data.length - 1]);
                } else objectCode = InstructionSet.getOppCode(mnemonic) + registers(data[data.length - 1]) + "0"
                        ;//check if shift greater than F
                break;
            case 3:

                int xbpe =0;
                int Address;
                String label;
                boolean  immediate=false;
                boolean value=false;
                if (numberOfOperands==1)
                {
                    if (reservedchar(data[locationMnemonic + 1].charAt(0))) {
                        label = data[locationMnemonic + 1].substring(1);
                        try{
                            Address = SymbolicTable.getTable().getAddress(label);
                        }catch (NullPointerException EX){
                            Address = Integer.parseInt(label);
                            value=true;
                        }
                        if(data[locationMnemonic+1].charAt(0)=='#'&&value) {
                            xbpe = handleX(indexed);
                            immediate=true;
                        }else
                        xbpe = handleX(indexed) + handleB(Address) + handleP(Address);
                    }else{
                        Address = SymbolicTable.getTable().getAddress(data[data.length-1]);
                        xbpe = handleX( indexed) + handleB(Address) + handleP(Address);
                    }

                    if (handleP(Address)!=0)
                    {
                        String displacement = immediate&&value? Integer.toHexString(Address) :Integer.toHexString(Address - pc);
                        if(displacement.length()>3) {
                            displacement= displacement.substring(displacement.length()-3);
                        }

                        objectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(mnemonic), 16)
                                + handleNI(data[data.length-1])))+ Integer.toHexString(xbpe)+""+(displacement.length()< 3 ? ZEROES3.substring(displacement.length())+ displacement
                                : displacement);
                        while(objectCode.length()<=5)
                            objectCode="0"+objectCode;

                    }
                    else if (handleB(Address)!=0)
                    {
                        String displacement = Integer.toHexString(Address - base);
                        objectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(mnemonic), 16)
                                + handleNI(data[data.length-1])))
                                + Integer.toHexString(xbpe) +
                                (displacement.length()< 3 ? ZEROES3.substring(displacement.length())+ displacement
                                        : displacement) ;
                    }
                    else
                        objectCode = "     **********ERROR! Displacement exceeds limit**********";

                }
                else objectCode = Integer.toHexString(Integer.parseInt(InstructionSet.getOppCode(mnemonic), 16)+3)
                        + "0000";


             break;

        }
        }


    private boolean reservedchar(char c) {
        switch (c){
            case '@':case '#':
                return true;
            default:
                return false;
        }}

//    public ObjectCode (String mnemonic , String operand, char addressingmode , int format, int Nop ,
//                       int Nor ,int pc , int base)
//    {
//        Mnemonic=mnemonic;
//        Operand=operand;
//        AddressingMode=addressingmode;
//        NumberOfOperands=Nop;
//        NumberOfRegisters=Nor;
//        Format=format;
//        PC=pc;
//        BASE = base;
//        Operands = Operand.split("[,]");
//        InstructionSet = PathOne.getInstructionSet();
//        SymTab=SymbolicTable.getTable();
//        Start();
//    }


//    public void setObjectCode(String objectCode) {
//        ObjectCode = objectCode;
//    }
//
//    private void Start()
//    {
//        switch(Format)
//        {
//            case 1: ObjectCode = InstructionSet.getOppCode(Mnemonic);
//            case 2:
//            {
//            try{if (NumberOfOperands==2){
//                if (NumberOfRegisters ==2) ObjectCode= InstructionSet.getOppCode(Mnemonic)
//                        +  InstructionFormate.Register.valueOf(Operands[0])
//                        +  InstructionFormate.Register.valueOf(Operands[1]);
//                else ObjectCode= InstructionSet.getOppCode(Mnemonic) +  InstructionFormate.Register.valueOf(Operands[0])
//                        + Integer.toHexString(Integer.parseInt(Operands[1])) ;}
//            else if(NumberOfOperands==1){
//                if(NumberOfRegisters==1)
//                    ObjectCode= InstructionSet.getOppCode(Mnemonic) +   InstructionFormate.Register.valueOf(Operands[0])
//                            + "0000" ;
//                else ObjectCode= InstructionSet.getOppCode(Mnemonic)  + "0"
//                        +Integer.toHexString(Integer.parseInt(Operands[1])) ;}}catch (Exception e){
//                System.err.println("this register doesnot exists");
//            }}
//            case 3:
//            {
//                int xbpe =0;
//                if (NumberOfOperands==1)
//                {
//                    int Address = SymTab.getAddress(Operands[0]);
//                    xbpe = handleX(Operands) + handleB(Address) + handleP(Address);
//                    if (handleP(Address)!=0)
//                    {
//                        String displacement = Integer.toHexString(Address - PC);
//
//                        ObjectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(Mnemonic), 16)
//                                + handleNI(Operands[0])))+ Integer.toHexString(xbpe)+""+(displacement.length()< 3 ? ZEROES3.substring(displacement.length())+ displacement
//                                : displacement);
//                        System.out.println(ObjectCode);
//                    }
//                    else if (handleB(Address)!=0)
//                    {
//                        String displacement = Integer.toHexString(Address - BASE);
//                        ObjectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(Mnemonic), 16)
//                                + handleNI(Operands[0])))
//                                + Integer.toHexString(xbpe) +
//                                (displacement.length()< 3 ? ZEROES3.substring(displacement.length())+ displacement
//                                        : displacement) ;
//                    }
//                    else
//                        ObjectCode = "     **********ERROR! Displacement exceeds limit**********";
//
//                }
//                else ObjectCode = Integer.toHexString(Integer.parseInt(InstructionSet.getOppCode(Mnemonic), 16))
//                        + "0000";
//
//            }
//
//            case 4: {
//                if (Operands != null) {
//                    int Address = SymTab.getAddress(Operands[0]);
//                    int xbpe = 1 + handleX(Operands);
//                        ObjectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(Mnemonic), 16)
//                                + handleNI(Operands[0])))
//                                + Integer.toHexString(xbpe) +
//                                (Integer.toHexString(Address).length() < 5 ?
//                                        ZEROES5.substring(Integer.toHexString(Address).length())
//                                                + Address : Address);
//                }
//            }
//        }
//        System.out.println(ObjectCode);
//
//    }
//
    private int registers(String str){
        switch (str){
            case "A":return 0;
            case "X":return 1;
            case "L":return 2;
            case "B":return 3;
            case "S":return 4;
            case "T":return 5;
            case "F":return 6;
            case "SW":return 9;

        }
          return -1;
    }
    private int handleNI (String Operand)
    {
        if (Operand.charAt(0)=='@') return 2;
        else if (Operand.charAt(0)=='#') return 1;
        else return 3;
    }
    private int handleX (String indexed)
    {
        if(indexed!=null)
            if(data[data.length-1].charAt(0) == 'X') return 8;
        return 0;
    }

    private int handleP(int TA) {
        int displacement =  (TA) - pc;
        if (displacement >= ((-1)*2048) && displacement <= 2047) return 2;
        return 0;
    }

    private int handleB(int TA) {
        if (handleP(TA) == 0)
        {
            int displacement = (TA) - base;
            if (displacement>= 0 && displacement <= 4095 ) return 4;
        }
        return 0;
    }

    public String getObjectCode()
    {
        return objectCode;
    }

}
