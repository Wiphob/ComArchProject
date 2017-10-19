import java.io.*;
import java.util.ArrayList;

public class Assembler {
    static ArrayList<String[]>  labels = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader inFile = new BufferedReader(new FileReader("1.txt"));
        String line;
        BufferedWriter outFile = new BufferedWriter(new FileWriter("2.txt"));
        while ((line = inFile.readLine()) != null){

            lines.add(line);
        }
        //Label List
        for (int i = 0; i < lines.size(); i++) {

            String[] substr = lines.get(i).split("\\s", 4);

            if(substr[0].length()!=0) {
                String[] label = new String[2];

                label[0] = substr[0];
                if(substr[1].equals(".fill"))  label[1] = substr[2];
                else label[1] = String.valueOf(i);

                labels.add(label);
            }
        }

        int lineOut = 0;

        for (int i = 0; i < lines.size(); i++) {

            if (lines.get(i).matches("(.*)(\\s)lw(\\s)(.*)") || lines.get(i).matches("(.*)(\\s)sw(\\s)(.*)") || lines.get(i).matches("(.*)(\\s)beq(\\s)(.*)")) {
                String[] substr = lines.get(i).split("\\s", 6);
                lineOut = i_type(substr[1], substr[2], substr[3], substr[4], i);

            } else if (lines.get(i).matches("(.*)(\\s)add(\\s)(.*)") || lines.get(i).matches("(.*)(\\s)nand(\\s)(.*)")) {
                String[] substr = lines.get(i).split("\\s", 6);
                lineOut = r_type(substr[1], substr[2], substr[3], substr[4]);

            } else if (lines.get(i).matches("(.*)(\\s)jalr(\\s)(.*)")) {
                String[] substr = lines.get(i).split("\\s", 5);
                lineOut = j_type(substr[1], substr[2], substr[3]);

            } else if (lines.get(i).matches("(.*)(\\s)halt(\\s)(.*)") || lines.get(i).matches("(.*)(\\s)noop(\\s||\\n)(.*)")) {
                String[] substr = lines.get(i).split("\\s", 3);
                lineOut = o_type(substr[1]);

            } else if (lines.get(i).matches("(.*)(\\s).fill(\\s)(.*)")){
                String[] substr = lines.get(i).split("\\s", 4);
                if (!substr[2].matches("(.*)[a-z](.*)")){
                    lineOut = Integer.valueOf(substr[2]);

                } else {
                    for (int j = 0; j < labels.size(); j++) {
                        if(labels.get(j)[0].equals(substr[2])) lineOut = Integer.valueOf(labels.get(j)[1]);
                    }
                }
            }
            outFile.write(String.valueOf(lineOut));
            outFile.newLine();
        }
        outFile.flush();
    }

    static int r_type(String opcode, String field1, String field2, String field3) {
        int returnCode=0;
        double code=0;
        // 011 000
        // first 3 bits is 011 = 3
        // 3*2pow3=3*8=24 = 011 000
        //decode opcode
        if(opcode.toLowerCase().equals("add"))
        {
            code = 0;
        }
        else if(opcode.toLowerCase().equals("nand"))
        {
            code = 1;
        }
        code *= Math.pow(2, 22);
        returnCode+=code;
        //decode field1
        code = Double.parseDouble(field1);
        code *= Math.pow(2,19);
        returnCode+=code;
        //decode field2
        code = Double.parseDouble(field2);
        code *= Math.pow(2,16);
        returnCode+=code;
        //decode field3
        code = Double.parseDouble(field3);
        code*= Math.pow(2,0);
        returnCode+=code;

        return returnCode;
    }

    static int i_type(String opcode, String field1, String field2, String field3,int i) {
        int code=0;
        int field1int = Integer.parseInt(field1);
        int field2int = Integer.parseInt(field2);
        while (field3.matches("(.*)[a-z](.*)")){
            for (int j = 0; j < labels.size(); j++) {
                if(field3.equals(labels.get(j)[0])){
                    field3 = Integer.toString(Integer.valueOf(labels.get(j)[1])-i-1);
                }
            }
        }
        int field3int = Integer.parseInt(field3);

        //opcode is lw
        if(opcode.equals("lw")){
            code = 2;
        }

        //opcode is sw
        if(opcode.equals("sw")){
            code = 3;
        }

        //opcode is beq
        if(opcode.equals("beq")){
            code = 4;
        }

        if (field3int < 0) field3int = (int) Math.pow(2,16)+field3int;

        return code*(int) Math.pow(2,22)+field1int* (int) Math.pow(2,19)+field2int* (int) Math.pow(2,16)+field3int;
    }

    static int j_type(String opcode, String field1, String field2) {
        return  5*(int) Math.pow(2,22) + Integer.valueOf(field1)*(int) Math.pow(2,19)+Integer.valueOf(field2)*(int) Math.pow(2,16);
    }

    static int o_type(String opcode) {
        double sum = 0;
        if (opcode.equals("halt")){
            sum = 6*Math.pow(2,22);
        }else if (opcode.equals("noop")){
            sum = 7*Math.pow(2,22);
        }

        return (int) sum;

    }
}


