import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Simulator {
    static int[] reg = new int[8];
    static int[] mem = new int[65536];
    static int pc = 0;
    static boolean isHalt = false;
    static int instructionCount = 0;
    static ArrayList<String> lines = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        BufferedReader inFile = new BufferedReader(new FileReader("Test_MachineCode.txt"));
        String line;

        while ((line = inFile.readLine()) != null){
            lines.add(line);
        }

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

        // calculate offsetfield for negative value

        if(field3 > 32767){
            field3 = (field3-32768)+(-32768);
        }
        

        if(opcode == 2) {             //opcode = 2 do LW
            // load reg[B] from memory
            // reg[B]=memory[reg[A]+offsetfield]
            reg[field2] = mem[reg[field1] + field3];
        }

        if(opcode == 3) {             //opcode = 3 do SW
            // store reg[B] in memory
            // memory[reg[A]+offsetfield]=reg[B]
            mem[reg[field1] + field3] = reg[field2];
        }

        if(opcode == 4) {             //opcode = 4 do beq
            if(reg[field1] == reg[field2]){

                // if reg[A] = reg[B] do jump
                //  pc = pc + offsetfield
                pc = pc + field3 ;

            }

        }

    }

    public static void j_type(int opcode, int field1, int field2) {
        reg[field2] = pc;
        pc = reg[field1];
    }

    public static void o_type(int opcode) {
        if(opcode == 6){
            //add pc++ and tell simulator that it have Halt stage
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

        for (int j = 0; j < ((reg[5] > lines.size())? reg[5]:lines.size()); j++) {
            System.out.println("\t\tmem [ " + j + " ] " + mem[j]);
        }

        System.out.println("\tregisters:");
        for (int j = 0; j < reg.length; j++) {
            System.out.println("\t\treg [ " + j + " ] " + reg[j]);
        }

        System.out.println("end state\n");
    }
}
