package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


// Main teleOp code for Hestia
@TeleOp
public class HestiaTeleOp extends OpMode {


    // We have our motors
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;

    private DcMotor towerMotor;

    private DcMotor slider;

    private DcMotor hangingRight;

    private DcMotor hangingLeft;

    private Servo launcher;

    private Servo claw;



    // This is our timer used to track time in seconds
    private ElapsedTime timer = new ElapsedTime();


    // This is our led driver
//    private RevBlinkinLedDriver lights;


    // These variables will be used to track the movements of the joysticks
    private double forward;
    private double strafe;
    private double turn;

    private double tower;

    private double slide;

    private final int SLIDER_THRESHOLD = -170;

    // This variable will help me determine whether or not to reset the time
    private boolean isFirstLoop = true;


    // Function that occurs when init is pressed on the driver's hub
    @Override
    public void init() {

        // Hardware mapping to the drivers hub configuration
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        towerMotor = hardwareMap.dcMotor.get("tower");
        hangingLeft = hardwareMap.dcMotor.get("hangingLeft");
        hangingRight = hardwareMap.dcMotor.get("hangingRight");
        slider = hardwareMap.dcMotor.get("slider");

        launcher = hardwareMap.servo.get("launcher");
        claw = hardwareMap.servo.get("claw");

        slider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slider.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        towerMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        towerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hangingLeft.setDirection(DcMotorSimple.Direction.REVERSE);


        launcher.setPosition(0);


        // Hardware mapping of the driver and setting the default pattern
//        lights = hardwareMap.get(RevBlinkinLedDriver.class,"lights");
//        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_HEARTBEAT_SLOW);

        // Sets the directions of the motors to be correct
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        // Locks all the motors whenever joystick movement is not detected
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

//        launcher.setPosition(.27);


        timer.reset();

    }

    // Code that occurs during the actual opMode running
    @Override
    public void loop() {



        // At the start of the loop, take the values of the joystick and set them to our variables
        forward = Math.pow(gamepad1.left_stick_y, 2) * Math.signum(gamepad1.left_stick_y);
        strafe = Math.pow(gamepad1.left_stick_x, 2) * Math.signum(gamepad1.left_stick_x);
        turn = .75 * (Math.pow(gamepad1.right_stick_x, 2) * Math.signum(gamepad1.right_stick_x));
        slide = .75 * gamepad2.left_stick_y;
        tower = .75 * gamepad2.right_stick_y;


        // If this is the first loop where there is activity
//        if (isFirstLoop && (forward != 0 || strafe != 0 || turn != 0)){
//
//            // Reset the timer and add telemetry to show this
//            timer.reset();
//            telemetry.addLine("Timer has been reset");
//            telemetry.update();
//        }
//
//        // If the timer reaches 60 seconds and is less than 90 seconds, switch to the next pattern
//        if (timer.seconds()>=60 && timer.seconds()<90){
//            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP2_HEARTBEAT_FAST);
//        }
//
//        // If the timer reaches 90 seconds, signal that it is now end game
//        if (timer.seconds()>=90){
//            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_2_SPARKLE_1_ON_2);
//        }


        // Sets the power of our motors to our variables we created earlier
        frontRight.setPower(forward + strafe + turn);
        frontLeft.setPower(forward - strafe - turn);
        backRight.setPower(forward - strafe + turn);
        backLeft.setPower(forward + strafe - turn);


        slider.setPower(slide);
        hangingRight.setPower((-gamepad2.right_trigger + gamepad2.left_trigger));
        hangingLeft.setPower((-gamepad2.right_trigger + gamepad2.left_trigger));

        // Code to launch the servo
        if ((gamepad2.dpad_up)) {
            launcher.setPosition(0);
        }

        // Code to reset the servo
        if (gamepad2.dpad_down) {
            launcher.setPosition(1);
        }

        // Open claw
        if (gamepad2.left_bumper) {
            claw.setPosition(.72);
        }

        // Close claw
        if (gamepad2.right_bumper) {
            claw.setPosition(.42);

        }

        telemetry.addLine("Position of slider: " + slider.getCurrentPosition());
        telemetry.update();

        if (slider.getCurrentPosition() < SLIDER_THRESHOLD){
            towerMotor.setPower(.1 + tower);
        }
        else {
            towerMotor.setPower(tower);
        }

    }

}
