package sample.mainbody;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hp-laptop on 4/15/2017.
 */
public class PathTwo {
    private PathOne one ;
    private final int baseLocation;
    public PathTwo() {
        this.one = new PathOne("E:\\Ahmed\\GITHUB_RES\\Git2\\sic-xe\\src\\sample\\files/code.txt");
        checkUndefinedAddress(one.getSymboltable());
if(one.getSymboltable().getRowInformmation().get("Bse")!=null)
    baseLocation = one.getSymboltable().getAddress(one.getSymboltable().getBase());
else
        baseLocation = 0;
        System.out.println(baseLocation);
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
