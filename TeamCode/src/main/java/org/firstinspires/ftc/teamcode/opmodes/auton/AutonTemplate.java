package org.firstinspires.ftc.teamcode.opmodes.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

@Autonomous
@Disabled
public class AutonTemplate extends LinearOpMode {

    private TfodProcessor tfodProcessor;

    private VisionPortal visionPortal;

    private double coordinates;

    private double leftCoordinatesThreshold;

    private double rightCoordinatesThreshold;



    @Override
    public void runOpMode(){

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        tfodProcessor = new TfodProcessor.Builder()
                .build();

        visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), tfodProcessor);

        while (!opModeIsActive()){

            List<Recognition> recognitions = tfodProcessor.getRecognitions();

            for (Recognition i: recognitions){

                coordinates = i.getLeft();

            }

        }

        waitForStart();


        // If our coordinates of our object are to the left
        if (coordinates < leftCoordinatesThreshold) {

        }


    }

}
