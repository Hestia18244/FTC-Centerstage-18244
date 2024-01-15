package org.firstinspires.ftc.teamcode.opmodes.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;


@Autonomous
public class AutonRed extends LinearOpMode {

    // List of motors and servos
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor backRight;
    private Servo claw;

    /**
     * The position of our object
     */
    private double horizontalPos = 154.2;


    /**
     * The threshold for our object to either be on the left or the right side
     */
    private static double threshold = 270;

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    /**
     * Variable to store the confidence of our model
     */
    private double confidence = 0;

    /**
     * Variable to store the number of recognitions
     */
    private int numRecognitions = 0;

    /**
     * The labels we are looking for
     */
    private static final String[] LABELS = {"Prop"};

    // When the opMode is initialized, this function is called
    public void runOpMode(){

        // Hardware mapping of our motors and servos
        frontRight = hardwareMap.dcMotor.get("frontRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        claw = hardwareMap.servo.get("claw");

        // Locking of our motors to reduce slip
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reversing the direction of our right side motors
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        // Setting the claw to an initial position
        claw.setPosition(.42);

        // Call this function to set up our tfod processor and VisionPortal
        initTfod();

        // While the opMode is not active, we will scan for objects
        while (!opModeIsActive()){
          scanForObjects();
        }

        // Once the opMode is active, we execute the following code
        waitForStart();

        // Currently, our red model is not so accurate.
        // It detects the tape as our model and thus, we have to use the confidence of our model
        // and the number of recognitions to determine where the object is

        // If the confidence of our detection is low, that must mean we only have one (inaccurate) detection
        // This means that our object is on the side where our camera is not
        if (confidence < .8 || horizontalPos == -100000){

            // TODO: test this code and adjust these values
            // do something if object on left is detected

            // move forward to the square with all the spikes
            move(-1200, 0, 0, 1, 500);

            // Turn towards the proper spike
            move(0,0,-875, 1, 500);



            // Move forward and drop our pixel
            move(100, 0, 0, 1, 500);
            claw.setPosition(.72);
            sleep(500);

            // Basically the inverse of all our movements to move back to beginning

            move(250, 0, 0, 1, 500);
            move(0, 0, 875, 1, 500);

            move(1100, 0, 0, 1, 500);

            // Move to the backstage to park
            move(0, 2000, 0, 1, 500);
        }
        // If we have two recognitions, it means our object is in the center,
        // as the second detection is likely the tape
        else if (horizontalPos < threshold || numRecognitions == 2){
            // TODO: take the values from here and paste them into other files
            // do something if object is in the center

            // Move forward and place the object on the spike
            move(-1200, 0, 0, 1, 500);
            claw.setPosition(.72);
            sleep(500);

            // Move back to the starting position
            move(1100, 0, 0,1, 500);

            // Move to the backstage
            move(0, 2200, 0, 1, 1000);
        }
        // Otherwise, if there is only one detection, our object must be on the right
        else if (horizontalPos > threshold) {
            // TODO: Test the values here to ensure accuracy
            // do something if object is on the right

            // Strafe towards the spike
            move(0,550,0, 1, 500);

            // Move forward to the spike and place the pixel
            move(-900, 0, 0, 1, 500);
            claw.setPosition(.72);
            sleep(500);

            // move backwards to align with the wall
            move(775, 0, 0, 1, 500);

            // Strafe towards the backstage
            move(0, 1275, 0, 1, 1000);
        }

//        move(0, 2100, 0, 1, 1000);
//
//
//        claw.setPosition(.72);

        sleep(29000);

    }

    /**
     * Function that moves our motors using encoders
     * @param forward How many ticks we want to move forward (0 if we do not want to move forward)
     * @param strafe How many ticks we want to strafe (0 if we do not want to strafe)
     * @param turn How many ticks we want to turn (0 if we do not want to turn)
     * @param power How much power we want the motors to move at
     * @param ms How much delay we want before our next action in milliseconds
     */
    public void move(int forward, int strafe, int turn, double power, int ms){
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setTargetPosition(forward - strafe - turn);
        frontRight.setTargetPosition(forward + strafe + turn);
        backRight.setTargetPosition(forward - strafe + turn);
        backLeft.setTargetPosition(forward + strafe - turn);

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while (frontLeft.isBusy() && frontRight.isBusy() && backRight.isBusy() && backLeft.isBusy()){

        }

        sleep(ms);

    }

    /**
     * Function that intializes our tfod processor and VisionPortal with necessary settings
     */
    private void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // Use setModelAssetName() if the TF Model is built in as an asset.
                // Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                //.setModelAssetName(TFOD_MODEL_ASSET)
                .setModelFileName("red.tflite")

                .setMaxNumRecognitions(1)
                .setTrackerMaxOverlap(0.25f)
                .setModelLabels(LABELS)
                .setNumDetectorThreads(1)
                .setNumExecutorThreads(1)
//            .setIsModelTensorFlow2(true)
                //.setIsModelQuantized(true)
                //.setModelInputSize(300)
                //.setModelAspectRatio(16.0 / 9.0)

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).

        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));


        // Choose a camera resolution. Not all cameras support all resolutions.
        //builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        //builder.enableCameraMonitoring(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        //builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        //tfod.setMinResultConfidence(0.75f);

        // Disable or re-enable the TFOD processor at any time.
        //visionPortal.setProcessorEnabled(tfod, true);

    }   // end method initTfod()

    /**
     * Function that scans for objects and assigns values to variables to use for decision making
     * @return Returns true if an object is found, returns false otherwise. Mainly used for breaking out of the loop
     */
    private boolean scanForObjects(){
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        numRecognitions = currentRecognitions.size();

        if (currentRecognitions.isEmpty()){

            horizontalPos = 154.2;

            return false;
        }

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            horizontalPos = (recognition.getLeft() + recognition.getRight()) / 2 ;
            confidence = recognition.getConfidence();

            telemetry.addData(""," ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f", horizontalPos);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
            break;
        }   // end for() loop

        return true;

    }
}
