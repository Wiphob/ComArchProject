import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Simulator {
    static int[] reg = new int[8];
    static int[] mem;
    static int pc = 0;
    static boolean isHalt = false;
    static int instructionCount = 0;

    public static void main(String[] args) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader inFile = new BufferedReader(new FileReader("2.txt"));
        String line;

        while ((line = inFile.readLine()) != null){
            lines.add(line);
        }

        mem = new int[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            mem[i] = Integer.valueOf(lines.get(i));
        }

        int opcode;
        int instruction;

        while (!isHalt) {
            print_state();
            instruction = mem[pc];
            pc++;
            opcode = (int) (instruction/Math.pow(2,22));
            System.out.println(instruction);

            if (opcode < 2) {
                r_type(opcode ,(int) (instruction/Math.pow(2,19))%8 ,(int) (instruction/Math.pow(2,16))%8 ,instruction%8);

            }
            else if (opcode < 5) {
                i_type(opcode ,(int) (instruction/Math.pow(2,19))%8 ,(int) (instruction/Math.pow(2,16))%8 ,(instruction%65536));

            }
            else if (opcode == 5) {
                j_type(opcode ,(int) (instruction/Math.pow(2,19))%8 ,(int) (instruction/Math.pow(2,16))%8 );

            }
            else if (opcode < 8) {
                o_type(opcode);
            }

            instructionCount++;
        }

        print_state();
    }

    public static void r_type(int opcode, int field1, int field2, int field3) {
        //got field in form of address, use field number to access stored value via mem
        //print_state before instruction

        switch (opcode) {
            //add reg[field3]=reg[field1]+reg[field2]
            case 0:
                reg[field3] = reg[field1] + reg[field2];
                break;
            //nand reg[field3]=reg[field1] nand reg[field2]
            case 1:
                reg[field3] = ~(reg[field1] & reg[field2]);
                break;
        }
    }

    public static void i_type(int opcode, int field1, int field2, int field3) {
        //  reg[B]=memory[reg[A]+offsetfield]
        //  memory[reg[A]+offsetfield]=reg[B]
        //  pc=pc+offsetfield+1

    /*  if(offsetfield > 32767){

    offsetfield = (offsetfield-32768)+(-32768);




    }

    */

    }

    public static void j_type(int opcode, int field1, int field2) {
        reg[field2] = pc;
        pc = reg[field1];
    }

    public static void o_type(int opcode) {
        if(opcode == 6){
            //add pc and tell simulator that it have Halt stage
            pc++;
            isHalt=true;
        }
        if (opcode == 7){
            //do not thing
        }

    }

    public static void print_state() {
        if(isHalt) {
            System.out.println("machine halted\ntotal of "+ instructionCount +" instructions executed\nfinal state of machine:\n");
        }

        System.out.println("@@@ \nState:\n\tPC  " + pc + "\n\tmemory:");

        for (int j = 0; j < mem.length; j++) {
            System.out.println("\t\tmem [ " + j + " ] " + mem[j]);
        }

        System.out.println("\tregisters:");
        for (int j = 0; j < reg.length; j++) {
            System.out.println("\t\treg [ " + j + " ] " + reg[j]);
        }

        System.out.println("end state\n");
    }
}
