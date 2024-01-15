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
public class AutonBlueFar extends LinearOpMode {

    // List of motors and servos
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor backRight;
    private Servo claw;

    /**
     * The position of our object
     */
    private double horizontalPos = -100000;


    /**
     * The threshold for our object to either be on the left or the right side
     */
    private final double THRESHOLD = 270;

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


        // The accuracy of our blue model is much higher than that of our red model
        // Because of this, we are able use the position of the object for our logic

        // If no object is detected, then we assume its the object on the left
        if (horizontalPos == -100000 || confidence < .8){

            // TODO: test this code and adjust these values
            // do something if object on left is detected

            // Move forward into the tile with all of the spikes
            move(-1050, 0, 0 , 1, 500);

            // Turn towards the spike on the left
            move(0,0,-950, 1, 500);


            claw.setPosition(.72);
            sleep(500);






            // Realign our robot by moving backwards and then strafing into the backstage

        }
        // if our object is on the left side of our threshold, then our object is in the center
        else if (horizontalPos < THRESHOLD){
            // TODO: take the values from here and paste them into other files
            // do something if object is in the center

            // Move forward into the tile with all of the spikes and place the pixel
            move(-1200, 0, 0, 1, 500);
            claw.setPosition(.72);
            sleep(500);





            // Realign our robot by moving backwards and then strafing into the backstage

        }
        // Otherwise, if our object is on the right side of our threshold, then it must be on the right spike
        else if (horizontalPos > THRESHOLD) {
            // TODO: Test the values here to ensure accuracy
            // do something if object is on the right

            // Move forward into the tile with all of the spikes
            move(-1050, 0, 0, 1, 500);

            // Turn towards the spike on the right
            move(0,0,950, 1, 500);


            claw.setPosition(.72);
            sleep(500);




        }


//        move(-2100, 0, 0, 1, 1000);
//        move(0,-3200,0,1,1000);
//        move(1200, 0, 0, 1, 1000);
//        move(0, -1000, 0, 1, 1000);
//        move(0, 0, -1000,1, 1000);




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
                .setModelFileName("blue.tflite")

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

            horizontalPos = -100000;

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
