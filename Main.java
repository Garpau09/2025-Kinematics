import java.util.Scanner;

public class Main {
    // Coordinates of end effector destination
    // Meaasured from the rear of the robot touching the ground
    // Positive x toward the front of the robot
    static double x = -18;
    static double y = 58.88;
    // Ground to fixed rotation point between segments 1 and 2
    static double h = 32;
    // Horizontally, rear of robot to the segment 1/2 rotation point
    static double d = 6;
    // Length of each segment
    static double L1 = 24;
    static double L2 = 22;
    static double L3 = 24;

    //Unknowns
    // Straight line drawn from segment 1/2 to x/y coordinate
    static double L4 = 0;
    // Straight line drawn from frame/segment 1 point to segment 2/3 rotation point
    static double L5 = 0;
    // Straight line drawn from frame/segment 1 point to x/y coordinate
    static double L6 = 0;
    // Angle between vertical and segment 2
    static double theta1 = 0;
    // Angle between horizontal and segment 3
    static double theta2 = 0;
    // Angle between L4 and segment 2
    static double theta3 = 0;
    // Angle across from L4
    static double thetaL4 = 0;


    public static void main(String[] args) {

        //enterCoordinates();
       
        L4 = Math.sqrt(
            (Math.pow(x-d, 2) + Math.pow(y-h, 2))
        );
     
        L6 = Math.sqrt(
            Math.pow(x-d, 2) + Math.pow(y-h+L1, 2)
        );

        theta3 = Math.acos(
            (Math.pow(L4, 2) + Math.pow(L2, 2) - Math.pow(L3, 2)) /
            (2 * L4 * L2)  
        );

        
        // Robot moves arm behind itself
        if(x > d || x == d){
            theta1 = Math.acos(
                (Math.pow(L4, 2) + Math.pow(L1, 2) - Math.pow(L6, 2))/
                (2 * L4 * L1)  
            ) - theta3;
        } else {
            theta1 = 2* Math.PI - theta3 - Math.acos(
                (Math.pow(L4, 2) + Math.pow(L1, 2) - Math.pow(L6, 2))/
                (2 * L4 * L1));  
        }

        try {
            validateState(theta1);
        } catch (InvalidArmState e) {
            System.out.println(e.getMessage());
            System.out.println("MINIMUM X VALUE EXCEEDED");
            throw e;
        }

        if (L4 == 0 || L6 == 0) {
            System.out.println("WARNING:Length calcualted at zero");
        }

        L5 = Math.sqrt(
            Math.pow(L1, 2) + Math.pow(L2, 2) - (2* L1 * L2) * Math.cos(theta1)
        );

        thetaL4 = Math.acos(
            (Math.pow(L2, 2) + Math.pow(L3, 2) - Math.pow(L4, 2))/
            (2 * L2 * L3)
        );

        theta2 = (Math.PI/2) + theta1  - thetaL4;

        System.out.println("theta1: " + Math.toDegrees(theta1) + " degrees");
        System.out.println("theta2: " + Math.toDegrees(theta2) + " degrees");
        System.out.println("theta3 = " + Math.toDegrees(theta3));
        System.out.println("thetaL4 = " + Math.toDegrees(thetaL4) + " degrees");
        System.out.println("L4 = " + L4);
        System.out.println("L5 = " + L5);
        System.out.println("L6 = " + L6);

    }

    public static void enterCoordinates() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter x coordinate");
        
        double xValue = scanner.nextDouble();
        x = xValue;

        System.out.println("Enter y coordinate");
        y = scanner.nextDouble();

        scanner.close();
    }

    public static void validateState(double theta) throws InvalidArmState {
        if (theta > Math.PI) {
            throw new InvalidArmState("ARM SEGMENT 2 CANNOT EXTEND PAST 180 DEG");
        }
    }

    public static class InvalidArmState extends RuntimeException {

        public InvalidArmState(String m) {
            super(m);
        }
    }
}
