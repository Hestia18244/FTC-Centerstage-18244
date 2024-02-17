package org.firstinspires.ftc.teamcode.opmodes.auton.complex;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;


@Autonomous (group = "complex")
public class BlueLeftComplex extends LinearOpMode {

    // List of servos
    private Servo claw;

    private Servo dumper;

    private DcMotor tower;
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

    private DcMotor slider;



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

        claw = hardwareMap.servo.get("claw");
        dumper = hardwareMap.servo.get("dumper");
        tower = hardwareMap.dcMotor.get("tower");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        slider = hardwareMap.dcMotor.get("slider");

        slider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slider.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        tower.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        tower.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        tower.setDirection(DcMotorSimple.Direction.REVERSE);


        // Setting the claw to an initial position
        claw.setPosition(.42);
        dumper.setPosition(1);

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
        if (horizontalPos == -100000 || confidence < .9){

            move(0, -650, 0, .5, 1000);
            move(-1400, 0, 0, .5, 1000);
            dumper.setPosition(.85);
            sleep(1000);
            move(1150, 0, 0, .5, 1000);
            move(0, 0, -1075, .5, 1000);
            move(-600, 0, 0, .5, 1000);
            macro(.07);
            sleep(1000);
            slideMove();
            sleep(1000);
            move(0, 900, 0, .5, 1000);
            move(-400, 0, 0, .5, 1000);
            claw.setPosition(.6);
            sleep(1000);
            move(500, 0, 0, .5, 1000);
            move(0, -800, 0, .5, 1000);
            move(-400, 0, 0, .5, 1000);


        }
        // if our object is on the left side of our threshold, then our object is in the center
        else if (horizontalPos < THRESHOLD){

            move(-1500, 0, 0, .5, 500);
            dumper.setPosition(.7);
            sleep(1000);
            move(500, 0, 0, .5, 1000);
            move(0, 0, -1000, .5, 1000);
            macro(.07);
            sleep(1000);
            slideMove();
            sleep(1000);
            move(-400, 0, 0, .5, 1000);
            move(0, 300, 0, .5, 1000);
            move(-1250, 0, 0, .5, 1000);
            claw.setPosition(.6);
            sleep(1000);
            move(500, 0, 0, .5, 1000);
            move(0, -1500, 0, .5, 1000);
            move(-350, 0, 0, .5, 1000);



        }
        // Otherwise, if our object is on the right side of our threshold, then it must be on the right spike
        else if (horizontalPos > THRESHOLD) {

            move(-1100, 0, 0, .5, 1000);
            move(0, 0, 700, .5, 1000);
            move(-300, 0, 0, .5, 1000);
            dumper.setPosition(.7);
            sleep(1000);
            move(600, 0, 0, .5, 1000);
            move(0, 0, -1800, .5, 1000);
            macro(.07);
            sleep(1000);
            slideMove();
            sleep(1000);
            move(0, 800, 0, .5, 1000);
            move(-1400, 0, 0, .5, 1000);
            claw.setPosition(.72);
            sleep(1000);
            move(500, 0, 0, .5, 1000);
            move(0, -1400, 0, .5, 1000);
            move(-450, 0, 0, .5, 1000);

        }





        sleep(29000);

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

    private void macro(double turnage){
        double towerTarget = turnage*5281.1;

        tower.setTargetPosition((int)(towerTarget));
        tower.setPower(1);
        tower.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (tower.isBusy()){

        }

    }


    private void move(int forward, int strafe, int turn, double power, int ms){
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

    private void slideMove(){
        slider.setTargetPosition(-400);
        slider.setPower(1);
        slider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

}
