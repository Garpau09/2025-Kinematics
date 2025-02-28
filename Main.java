import java.util.Scanner;

/**
 * 
 * OnShape: https://cad.onshape.com/documents/0eb11a58606ee3c3dda8aa0d/w/d1c684d1c568543878764fb7/e/4c2980432bfd825f337a321f?renderMode=0&uiState=678c52c70a7cb65a2aa773cc
 * 
 * The robot operates on three segments: TOWER_CHASSIS_HEIGHT_METERS, SHOULDER_LENGTH_METERS, and ELBOW_LENGTH_METERS
 * Although only the final two move, the origin of the coordinate system sits on the ground at the back
 * of the robot.
 * 
 * When running on relative encoders within the robot, the final segment (ELBOW_LENGTH_METERS) operates on an angle relative 
 * to the horizontal/Earth
 * 
 * Absoulte encoders are fixed to the previous segment.  Measuring with absolute, the final segment (ELBOW_LENGTH_METERS)
 * operates at an angle relative to second segment (SHOULDER_LENGTH_METERS)
 */
public class Main {
    // Coordinates of end effector destination
    // Meaasured from the rear of the frame touching the ground
    // Positive x toward the front of the robot
    static double x = 0;
    static double y = 0;
    // Ground to fixed rotation point between segments 1 and 2
    static double h = 31.25;
    // Horizontally, rear of robot to the segment 1/2 rotation point
    static double d = 9.25;
    // Length of each segment
    static double TOWER_CHASSIS_HEIGHT_METERS = 27.375;
    static double SHOULDER_LENGTH_METERS = 22.75;
    static double ELBOW_LENGTH_METERS = 24.75;

    static double L1 = TOWER_CHASSIS_HEIGHT_METERS;
    static double L2 = SHOULDER_LENGTH_METERS;
    static double L3 = ELBOW_LENGTH_METERS;

    //Unknowns
    // Straight line drawn from segment 1/2 to x/y coordinate
    static double L4 = 0;
    // Straight line drawn from frame/segment 1 point to segment 2/3 rotation point
    static double L5 = 0;
    // Straight line drawn from frame/segment 1 point to x/y coordinate
    static double L6 = 0;
    // Angle between vertical and segment 2
    static double theta1 = 0;
    // Relative - angle between horizontal and segment 3
    static double relativeTheta2 = 0;
    // Absolute - angle between segment 3 and an extension of segment 2
    static double absoluteTheta2 = 0;
    // Angle between L4 and segment 2
    static double theta3 = 0;
    // Angle across from L4
    static double thetaL4 = 0;

    static double theta6 = 0;

    static double[] values = new double[7];

    public static void main(String[] args) {
        enterCoordinates();
        calculateTargetAngle();
    }

    public static void calculateTargetAngle() {

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

        thetaL4 = Math.acos(
          (Math.pow(L2, 2) + Math.pow(L3, 2) - Math.pow(L4, 2))/
          (2 * L2 * L3)
        );

        calculateTheta6(x, y);

        theta1 = theta6 - theta3 - Math.PI/2;

        // Relative
        relativeTheta2 = Math.PI + theta1  - thetaL4;

        // Absolute
        absoluteTheta2 = Math.PI - thetaL4;

        try {
            validateState(theta1);
        } catch (InvalidArmState e) {
            System.out.println(e.getMessage());
            throw e;
        }

        System.out.println("theta1: " + Math.toDegrees(theta1));
        //System.out.println("absolute theta2: " + Math.toDegrees(absoluteTheta2));
        System.out.println("relative theta2: " + Math.toDegrees(relativeTheta2));
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

    public static void calculateTheta6(double x, double y) {

      if (x < d) {
        theta6 = (2 * Math.PI) - 
          (Math.acos(
            (Math.pow(L1, 2) + Math.pow(L4, 2) - Math.pow(L6, 2)) /
            (2 * L1 * L4))
          );
      } else {
        theta6 = Math.acos(
          (Math.pow(L1, 2) + Math.pow(L4, 2) - Math.pow(L6, 2)) /
          (2 * L1 * L4)
        );
      }
    }

    public static void validateState(double theta) throws InvalidArmState {
        if (theta > Math.PI) {
            throw new InvalidArmState("ARM SEGMENT 2 CANNOT EXTEND PAST 180 DEG");
        }

        values[0] = L4;
        values[1] = L5;
        values[2] = L6;
        values[3] = theta1;
        values[4] = relativeTheta2;
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
