import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class Simulator {
    private static final int REGCAP = 8;
    private static final int MEMCAP = 5;

    private int[] reg;
    private int[] mem;
    private int pc;
    public Simulator()
    {
        reg = new int[REGCAP];
        mem = new int[MEMCAP];
        pc=0;
        for(int i=0;i<reg.length;i++)
        {
            reg[i]=0;
        }
    }

    public void r_type(int opcode, int field1, int field2, int field3) {
        //got field in form of address, use field number to access stored value via mem
        //print_state before instruction
        print_state();

        switch (opcode)
        {
            //add reg[field3]=reg[field1]+reg[field2]
            case 0:
                reg[field3] = reg[field1]+reg[field2];
                break;
            //nand reg[field3]=reg[field1] nand reg[field2]
            case 1:
                reg[field3] = ~(reg[field1]&reg[field2]);
                break;
        }
        pc++;
    }

    public void i_type(int opcode, int field1, int field2, int field3) {
        //  reg[B]=memory[reg[A]+offsetfield]
        //  memory[reg[A]+offsetfield]=reg[B]
        //  pc=pc+offsetfield+1

    /*  if(offsetfield > 32767){

    offsetfield = (offsetfield-32768)+(-32768);




    }

    */

    }

    public void j_type(int opcode, int field1, int field2) {

    }

    public void o_type(int opcode) {

    }

    public void print_state() {
        Simulator obj = new Simulator();
        System.out.println("@@@ \nState:\n\tPC  " + pc + "\n\tmemory:");

        for (int j = 0; j < mem.length; j++) {
            System.out.println("\t\tmem [ " + j + " ] " + mem[j]);
            //Stream.of(obj.getMem());

        }

        System.out.println("\tregisters:");
        for (int j = 0; j < reg.length; j++) {
            System.out.println("\t\treg [ " + j + " ] " + reg[j]);
            //Stream.of(obj.getMem());

        }

        System.out.println("end state\n");
    }

    public void testCase()
    {
        mem[0]=reg[0]=0;
        mem[1]=reg[1]=5;
        mem[2]=reg[2]=31;
    }
}
