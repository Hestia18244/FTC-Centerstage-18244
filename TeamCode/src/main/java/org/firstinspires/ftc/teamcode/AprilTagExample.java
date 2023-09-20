package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;

@Autonomous
public class AprilTagExample extends LinearOpMode {


    // Allocate memory for our processors
    // Think of the AprilTagProcessor object as you dedicating a small part of the cpu
    // solely to detection of AprilTags

    /**
     * This object is used for processing our AprilTags
     */
    private AprilTagProcessor aprilTagProcessor;

    /**
     * This object is used for enabling the camera and taking use of our processor(s)
     */
    private VisionPortal visionPortal;

    /**
     * This is the tag we are looking for
     */
    private final int DESIRED_TAG = 1;

    /**
     * This will be used to track whether or not our tag has been found
     */
    private boolean tag_found = false;

    // This is where our opmode actually occurs
    @Override
    public void runOpMode(){

        // The easiest thing to do is create our processor with the defaults
        // There are attributes that you can change, but as of right now we don't have a reason to do so
        aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();

        // Similarly, we can create this one with defaults.
        // In the future it may be useful to edit things like the resolution of the camera, but for now the default is fine
        // You also hardware map the camera within this method now, which is different than last year
        visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTagProcessor);

        // Blocking method to prevent our opmode from moving on to the following code
        waitForStart();

        // All this opmode does is constantly look for apriltags
        // While the opmode is active, the following code is executed
        while (opModeIsActive()){

            // We set our tag_found value to false by default
            tag_found = false;

            // From our aprilTagProcessors current detections, we create a list of these detections
            ArrayList<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();

            // This adds to the telemetry the number of apriltags found by returning the size of the list
            telemetry.addData("# AprilTags Detected", currentDetections.size());

            // This for loop checks each detection within our list of current detections
            for (AprilTagDetection detection : currentDetections) {

                // As long as we get metadata from the tag, we execute the code within the block
                // What is metadata?
                // Metadata is, for our purpose, data associated with our apriltag
                if (detection.metadata != null) {

                    // Within this code block, we print various metadata surrounding our apriltag

                    // If we have a detection that matches our desired tag, we assign our tag_found variable a value of true
                    if (detection.id == DESIRED_TAG){
                        tag_found = true;
                    }

                    // This line prints our id (which is the number associated with the tag), and the name of the tag
                    telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));

                    // This line prints the distance in inches between the camera and the apriltag on multiple axes
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                }
                // This should not run, as we aren't going to use apriltags outside of the default library
                else {
                    telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                    telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
                }
            }   // end for() loop

            // If we found our desired tag at some point during that for loop
            // we add some telemetry to tell us
            if (tag_found){
                telemetry.addLine("Desired Tag Found: True");
            }

            // To add all of these lines to the telemetry, we call this method
            telemetry.update();

            // Because I want yall to see how this works, we will delay each new round by 10 seconds
            sleep(10000);
        }
    }

}
