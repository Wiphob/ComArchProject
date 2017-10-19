import java.io.IOException;

public class Simulator {
    int[] reg = new int[8];
    int[] mem = new int[65536];
    int pc=0;

    public static void main(String[] args) throws IOException {


    }

    static void r_type(int opcode, int field1, int field2, int field3) {

    }

    static void i_type(int opcode, int field1, int field2, int field3) {
        //  reg[B]=memory[reg[A]+offsetfield]
        //  memory[reg[A]+offsetfield]=reg[B]
        //  pc=pc+offsetfield+1

    /*  if(offsetfield > 32767){

    offsetfield = (offsetfield-32768)+(-32768);




    }

    */

    }

    static void j_type(int opcode, int field1, int field2) {

    }

    static void o_type(int opcode) {

    }

    static void print_state() {

    }
}
