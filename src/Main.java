import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException
    {
        Simulator simulator = new Simulator();
        simulator.testCase();
        simulator.r_type(0,1,2,0);
        simulator.print_state();
    }
}
