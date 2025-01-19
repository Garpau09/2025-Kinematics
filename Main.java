import java.util.Scanner;

import javax.swing.tree.AbstractLayoutCache;


/**
 * The robot operates on three segments: L1, L2, and L3
 * Although only the final two move, the origin of the coordinate system sits on the ground at the back
 * of the robot.
 * 
 * When running on relative encoders within the robot, the final segment (L3) operates on an angle relative 
 * to the horizontal/Earth
 * 
 * Absoulte encoders are fixed to the previous segment.  Measuring with absolute, the final segment (L3)
 * operates at an angle relative to second segment (L2)
 */
public class Main {
    // Coordinates of end effector destination
    // Meaasured from the rear of the frame touching the ground
    // Positive x toward the front of the robot
    static double x = 0;
    static double y = 0;
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

    static enum Encoder{
        ABSOLUTE,
        RELATIVE
    };

    static double[] values = new double[7];

    static final Encoder encoder = Encoder.ABSOLUTE;

    public static void main(String[] args) {
        enterCoordinates();

        switch (encoder) {
            case ABSOLUTE:
                calculateAbsolute();
            break;

            case RELATIVE:
                calculateRelative();
            break;
        }
        
    }

    public static void calculateAbsolute(){
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

        L5 = Math.sqrt(
            Math.pow(L1, 2) + Math.pow(L2, 2) - (2* L1 * L2) * Math.cos(theta1)
        );

        thetaL4 = Math.acos(
            (Math.pow(L2, 2) + Math.pow(L3, 2) - Math.pow(L4, 2))/
            (2 * L2 * L3)
        );

        theta2 = Math.PI - thetaL4;

        try {
            validateState(theta1);
        } catch (InvalidArmState e) {
            System.out.println(e.getMessage());
            throw e;
        }


        System.out.println("ABSOLUTE VALUES");
        System.out.println("    theta1: " + Math.toDegrees(theta1) + " degrees");
        System.out.println("    theta2: " + Math.toDegrees(theta2) + " degrees");
        System.out.println("    theta3 = " + Math.toDegrees(theta3));
        System.out.println("    thetaL4 = " + Math.toDegrees(thetaL4) + " degrees");
        System.out.println("    L4 = " + L4);
        System.out.println("    L5 = " + L5);
        System.out.println("    L6 = " + L6);

    }

    public static void calculateRelative() {
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

        L5 = Math.sqrt(
            Math.pow(L1, 2) + Math.pow(L2, 2) - (2* L1 * L2) * Math.cos(theta1)
        );

        thetaL4 = Math.acos(
            (Math.pow(L2, 2) + Math.pow(L3, 2) - Math.pow(L4, 2))/
            (2 * L2 * L3)
        );

        theta2 = (Math.PI/2) + theta1  - thetaL4;

        try {
            validateState(theta1);
        } catch (InvalidArmState e) {
            System.out.println(e.getMessage());
            throw e;
        }

        System.out.println("RELATIVE VALUES");
        System.out.println("    theta1: " + Math.toDegrees(theta1) + " degrees");
        System.out.println("    theta2: " + Math.toDegrees(theta2) + " degrees");
        System.out.println("    theta3 = " + Math.toDegrees(theta3));
        System.out.println("    thetaL4 = " + Math.toDegrees(thetaL4) + " degrees");
        System.out.println("    L4 = " + L4);
        System.out.println("    L5 = " + L5);
        System.out.println("    L6 = " + L6);
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

        values[0] = L4;
        values[1] = L5;
        values[2] = L6;
        values[3] = theta1;
        values[4] = theta2;
        values[5] = theta3;
        values[6] = thetaL4;

        for(int i =0; i <7; i++) {
            if(!Double.isFinite(values[i])){
                throw new InvalidArmState("ARM OUT OF BOUNDS - INVALID X AND Y");
            }
        }
    }

    public static class InvalidArmState extends RuntimeException {

        public InvalidArmState(String m) {
            super(m);
        }
    }

}
