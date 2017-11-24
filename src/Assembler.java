import java.io.*;
import java.util.ArrayList;

import static java.lang.System.exit;

public class Assembler {
    static ArrayList<String[]>  labels = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader inFile = new BufferedReader(new FileReader("Combination_Assembly.txt")); //Assembly file ที่จะทำ
        String line;
        BufferedWriter outFile = new BufferedWriter(new FileWriter("Combination_MachineCode.txt")); //file machinecode ที่จะเอาออก

        while ((line = inFile.readLine()) != null){   //รับ code assembly ทีละบรรทัด
            lines.add(line);
        }

        //Label List
        for (int i = 0; i < lines.size(); i++) {  //check label ในแต่ละบรรทัด
            String[] substr = lines.get(i).split("\\s"); // แยก string ในบรรทัดออกจากกันด้วย white space

            if(substr[0].length()!=0) {
                for (int j = 0; j < labels.size(); j++) {
                    if (labels.get(j)[0].equals(substr[0])) {       //check label ซ้ำกัน
                        System.out.println("Define same label: " + substr[0]);
                        exit(1);
                    }
                }
                String[] label = new String[2];

                label[0] = substr[0];
                label[1] = String.valueOf(i);

                labels.add(label); // เก็บ label คำสั่งกับบรรทัดของ labelนั้นไว้ใน labels
            }
        }

        int lineOut = 0;
        //Opcode + field
        for (int i = 0; i < lines.size(); i++) {
            String[] substr = lines.get(i).split("\\s");   //นำ code บรรทัดนั้นมาแบ่งไว้ใน substr[]
            ArrayList<String> str = new ArrayList<>();
            for (int j = 1; j < substr.length; j++) {
                if(substr[j].length() != 0) str.add(substr[j]); //เอาคำสั่งใส่ใน str ยกเว้นส่วนที่เป็น label
            }
            //check opcode type
            if (lines.get(i).matches("(.*)(\\s)lw(\\s)(.*)") || lines.get(i).matches("(.*)(\\s)sw(\\s)(.*)") || lines.get(i).matches("(.*)(\\s)beq(\\s)(.*)")) {
                lineOut = i_type(str.get(0), str.get(1), str.get(2), str.get(3), i);

            } else if (lines.get(i).matches("(.*)(\\s)add(\\s)(.*)") || lines.get(i).matches("(.*)(\\s)nand(\\s)(.*)")) {
                lineOut = r_type(str.get(0), str.get(1), str.get(2), str.get(3));

            } else if (lines.get(i).matches("(.*)(\\s)jalr(\\s)(.*)")) {
                lineOut = j_type(str.get(0), str.get(1), str.get(2));

            } else if (lines.get(i).matches("(.*)(\\s)halt(\\s||\\n)(.*)") || lines.get(i).matches("(.*)(\\s)noop(\\s||\\n)(.*)")) {
                lineOut = o_type(str.get(0));

            } else if (lines.get(i).matches("(.*)(\\s).fill(\\s)(.*)")){
                if (!str.get(1).matches("(.*)[A-Za-z](.*)")){//เช็คว่า string ที่ได้มาไม่มี character
                    lineOut = Integer.valueOf(str.get(1));
                } else {
                    for (int j = 0; j < labels.size(); j++) {
                        if(labels.get(j)[0].equals(str.get(1))) lineOut = Integer.valueOf(labels.get(j)[1]);
                    }
                }
            } else {
                System.out.println("Don't know this opcode: " + str.get(0));
                exit(1);
            }
            outFile.write(String.valueOf(lineOut));//เขียนโค้ดที่แปลงมาได้ลง output file
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
        boolean isAddr = false;
        int code=0;
        int field1int = Integer.parseInt(field1);
        int field2int = Integer.parseInt(field2);
        //เช็คว่า field3 มี character จะได้ไปหาว่าตรงกับ label ไหน
        if (field3.matches("(.*)[A-Za-z](.*)")) {
            isAddr = true;
            for (int j = 0; j < labels.size(); j++) {
                if(field3.equals(labels.get(j)[0])){ //เช็ค label
                    field3 = labels.get(j)[1];
                }
            }
            if (field3.matches("(.*)[A-Za-z](.*)")) { //แจ้ง error ถ้า field3 ไม่ได้เป็น string ที่เป็นตัวเลขบรรทัดคำสั่ง
                System.out.println("Undefine this label: " + field3);
                exit(1);
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
            //ถ้าเป็น beg offset field จะมีค่าเป็นจำนวน address ที่ห่างจาก address ของคำสั่งปัจจุบัน+1
            if (isAddr) field3int -= i+1;
        }

        if ((field3int > 32767) || (field3int < (-32768))) {//ค่า field3 ใหญ่กว่า 16 bit
            System.out.println("offset field used more than 16 bit: " + field3int);
            exit(1);
        }

        if (field3int < 0) field3int = (int) 65536+field3int;//ค่าเป็นลบ ปรับบิตหน้าสุดเป็น 1 บิตหน้าสุดมีค่า 2^16

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



