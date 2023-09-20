package org.firstinspires.ftc.teamcode;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

// test autonomous mode for apriltags and roadrunner
@Autonomous
public class testAuton extends LinearOpMode {

    /**
     * April Tag processor object
     */
    AprilTagProcessor aprilTag;

    /**
     * Constant: Tag we are searching for
     */
    final int TAG_OF_INTEREST = 1;

    /**
     * Should be true if our tag of interest is found
     */
    boolean tagFound = false;

    /**
     * How far away the April Tag is from the on the x axis
     */
    double horizontalDistance = 0;

    /**
     * How far away the April Tag is from the on the y axis
     */
    double forwardsDistance = 0;

    /**
     * VisionPortal object to use our apriltag processor
     */
    VisionPortal visionPortal;

    /**
     * Trajectory for our robot to move to the tag
     */
    Trajectory moveToTag;

    @Override
    public void runOpMode(){

        // Hardware mapping
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);


        // initializing our apriltag processor and visionportal object
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);

        // look for detections while our tag is not found and in the starting phase of the opmode
        while (tagFound != true){

            // Get a list of the current detections
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();

            // Iterate through the detections and store data if necessary
            for (AprilTagDetection detection: currentDetections){

                if (detection.id == TAG_OF_INTEREST){
                    tagFound = true;
                    horizontalDistance = detection.ftcPose.x;
                    forwardsDistance = detection.ftcPose.y;
                }

            }
        }

        waitForStart();

        if(isStopRequested()) return;

        // Our forwards distance value will be negative, so we change it to positive
        // We also subtract 3 inches from the distance needed to be traveled because
        // we don't need to crash into the backdrop
        forwardsDistance = (-forwardsDistance) - 3;

        // If the tag is to the left of our robot
        if (horizontalDistance < 0){

            // Set horizontal distance to be positive
            horizontalDistance = -horizontalDistance;

            // Creates a trajectory for our robot to follow based off of the data from the apriltag
            moveToTag = drive.trajectoryBuilder(new Pose2d())
                    .strafeLeft(horizontalDistance)
                    .forward(forwardsDistance)
                    .build();
        // If the tag is to the right of our robot
        } else {

            // Creates a trajectory for our robot to follow based off of the data from the apriltag
            // The value of horizontal distance will already be positive so no need to change it
            moveToTag = drive.trajectoryBuilder(new Pose2d())
                    .strafeRight(horizontalDistance)
                    .forward(forwardsDistance)
                    .build();
        }


        // drives to the trajectory
        drive.followTrajectory(moveToTag);

    }
}
