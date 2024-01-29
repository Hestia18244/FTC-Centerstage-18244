package org.firstinspires.ftc.teamcode.opmodes.auton;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;


//@Autonomous
public class AutonRedFar extends LinearOpMode {

    // List of servos
    private Servo claw;

    private Servo launcher;

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
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        claw = hardwareMap.servo.get("claw");
        launcher = hardwareMap.servo.get("launcher");
        launcher.setPosition(0);

        drive.setPoseEstimate(new Pose2d(-34, -60, Math.toRadians(90)));

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
        if (horizontalPos == -100000 || confidence < .85){

            // This is our trajectory sequence the robot will follow.
            TrajectorySequence trajectory = drive.trajectorySequenceBuilder(new Pose2d(-34, -60, Math.toRadians(90)))



                    .forward(26)

                    .turn(Math.toRadians(80))


                    // Wait for a second before placing the pixel
                    .waitSeconds(1)

                    // Displacement marker to open the servo above the spike
                    .addTemporalMarker(4,()->{
                        claw.setPosition(.72);
                    })

                    .build();

            // Follow the trajectory sequence above
            drive.followTrajectorySequence(trajectory);
        }
        // if our object is on the left side of our threshold, then our object is in the center
        else if (horizontalPos < THRESHOLD || numRecognitions == 2){

            TrajectorySequence trajectory = drive.trajectorySequenceBuilder(new Pose2d(-34, -60, Math.toRadians(90)))

                    // Move forwards toward the middle spike
                    .forward(30)
                    .back(2)
                    .waitSeconds(1)

                    // Open the servo above the spike
                    .addTemporalMarker(2.5,()->{
                        claw.setPosition(.72);
                    })
                    .build();


            // Follow the trajectory sequence above
            drive.followTrajectorySequence(trajectory);



        }
        // Otherwise, if our object is on the right side of our threshold, then it must be on the right spike
        else if (horizontalPos > THRESHOLD) {

            TrajectorySequence trajectory = drive.trajectorySequenceBuilder(new Pose2d(-34, -60, Math.toRadians(90)))

                    // Drive forwards towards the tile with all of the spikes
                    .forward(26)

                    // Turn towards the right spike
                    .turn(Math.toRadians(-80))

                    .waitSeconds(1)

                    // Place the pixel on the spike
                    .addTemporalMarker(4, ()->{
                        claw.setPosition(0.72);
                    })
                    .build();

            // Follow the trajectory above
            drive.followTrajectorySequence(trajectory);


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
        tfod.setMinResultConfidence(0.8f);

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
