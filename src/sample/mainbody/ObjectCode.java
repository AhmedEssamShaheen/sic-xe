package sample.mainbody;

public class ObjectCode {

    private String Mnemonic;
    private String Operand;
    private char AddressingMode;
    private int NumberOfOperands;
    private int NumberOfRegisters;
    private int Format;
    private String ObjectCode;
    private InstructionFormate InstructionSet;
    private int PC;
    private int BASE;
    private String [] Operands;
    private SymbolicTable SymTab;
    private final static String ZEROES3 = "000";
    private final static String ZEROES5 = "00000";

    public ObjectCode (String mnemonic, int format)
    {
        Mnemonic=mnemonic;
        Format=format;
        InstructionSet = PathOne.getInstructionSet();
        Start();
    }

    public ObjectCode (String mnemonic , String operand, char addressingmode , int format, int Nop ,
                       int Nor ,int pc , int base)
    {
        Mnemonic=mnemonic;
        Operand=operand;
        AddressingMode=addressingmode;
        NumberOfOperands=Nop;
        NumberOfRegisters=Nor;
        Format=format;
        PC=pc;
        BASE = base;
        Operands = Operand.split("[,]");
        InstructionSet = PathOne.getInstructionSet();
        SymTab=SymbolicTable.getTable();
        Start();
    }

    private void Start()
    {
        switch(Format)
        {
            case 1: ObjectCode = InstructionSet.getOppCode(Mnemonic);
            case 2:
            {
            try{if (NumberOfOperands==2){
                if (NumberOfRegisters ==2) ObjectCode= InstructionSet.getOppCode(Mnemonic)
                        +  InstructionFormate.Register.valueOf(Operands[0])
                        +  InstructionFormate.Register.valueOf(Operands[1]);
                else ObjectCode= InstructionSet.getOppCode(Mnemonic) +  InstructionFormate.Register.valueOf(Operands[0])
                        + Integer.toHexString(Integer.parseInt(Operands[1])) ;}
            else if(NumberOfOperands==1){
                if(NumberOfRegisters==1)
                    ObjectCode= InstructionSet.getOppCode(Mnemonic) +   InstructionFormate.Register.valueOf(Operands[0])
                            + "0000" ;
                else ObjectCode= InstructionSet.getOppCode(Mnemonic)  + "0"
                        +Integer.toHexString(Integer.parseInt(Operands[1])) ;}}catch (Exception e){
                System.err.println("this register doesnot exists");
            }}
            case 3:
            {
                int xbpe =0;
                if (NumberOfOperands==1)
                {
                    int Address = SymTab.getAddress(Operands[0]);
                    xbpe = handleX(Operands) + handleB(Address) + handleP(Address);
                    if (handleP(Address)!=0)
                    {
                        String displacement = Integer.toHexString(Address - PC);
                        ObjectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(Mnemonic), 16)
                                + handleNI(Operands[0])))
                                + Integer.toHexString(xbpe) +
                                (displacement.length()< 3 ? ZEROES3.substring(displacement.length())+ displacement
                                        : displacement) ;
                    }
                    else if (handleB(Address)!=0)
                    {
                        String displacement = Integer.toHexString(Address - BASE);
                        ObjectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(Mnemonic), 16)
                                + handleNI(Operands[0])))
                                + Integer.toHexString(xbpe) +
                                (displacement.length()< 3 ? ZEROES3.substring(displacement.length())+ displacement
                                        : displacement) ;
                    }
                    else
                        ObjectCode = "     **********ERROR! Displacement exceeds limit**********";

                }
                else ObjectCode = Integer.toHexString(Integer.parseInt(InstructionSet.getOppCode(Mnemonic), 16))
                        + "0000";

            }

            case 4: {
                if (Operands != null) {
                    int Address = SymTab.getAddress(Operands[0]);
                    int xbpe = 1 + handleX(Operands);
                    if (handleP(Address) != 0) {
                        ObjectCode = Integer.toHexString((Integer.parseInt(InstructionSet.getOppCode(Mnemonic), 16)
                                + handleNI(Operands[0])))
                                + Integer.toHexString(xbpe) +
                                (Integer.toHexString(Address).length() < 5 ?
                                        ZEROES5.substring(Integer.toHexString(Address).length())
                                                + Address : Address);
                    }
                }
            }
        }

    }


    private int handleNI (String Operand)
    {
        if (Operand.charAt(0)=='@') return 2;
        else if (Operand.charAt(0)=='#') return 1;
        else return 3;
    }
    private int handleX (String [] Operands)
    {
        if(Operands.length>=2&&Operands[1]!=null)
            if(Operands[1].charAt(0) == 'x') return 8;
        return 0;
    }

    private int handleP(int TA) {
        int displacement =  (TA) - PC;
        if (displacement>= ((-1)*2048) && displacement <= 2047) return 2;
        return 0;
    }

    private int handleB(int TA) {
        if (handleP(TA) == 0)
        {
            int displacement = (TA) - BASE;
            if (displacement>= 0 && displacement <= 4095 ) return 4;
        }
        return 0;
    }

    public String getObjectCode()
    {
        return ObjectCode;
    }

}
