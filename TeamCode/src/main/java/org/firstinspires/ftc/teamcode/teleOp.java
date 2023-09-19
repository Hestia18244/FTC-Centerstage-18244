package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class teleOp extends OpMode {

    DcMotor frontRight = null;
    DcMotor frontLeft = null;
    DcMotor backRight = null;
    DcMotor backLeft = null;

    DcMotor slide = null;

    Servo rightMount = null;

    Servo leftMount = null;

    Servo right = null;

    Servo left = null;

    double forward;
    double strafe;
    double turn;

    public void init(){

        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        slide = hardwareMap.dcMotor.get("slide");

        rightMount = hardwareMap.servo.get("rightMount");
        leftMount = hardwareMap.servo.get("leftMount");
        right = hardwareMap.servo.get("right");
        left = hardwareMap.servo.get("left");

        frontRight.setDirection((DcMotorSimple.Direction.REVERSE));
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);


    }

    public void loop(){

        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;

        frontRight.setPower(forward + strafe + turn);
        frontLeft.setPower(forward - strafe - turn);
        backRight.setPower(forward - strafe + turn);
        backLeft.setPower(forward + strafe - turn);

        slide.setPower(.35*gamepad2.left_stick_y);

        if (gamepad2.right_bumper){
            //open
            left.setPosition(0);
            right.setPosition(1);
        }

        if (gamepad2.left_bumper){
            left.setPosition(0.50);
            right.setPosition(0.50);
        }

        if (gamepad2.a){

            leftMount.setPosition(0);
            rightMount.setPosition(0);

        }

        if (gamepad2.b){
            leftMount.setPosition(1);
            rightMount.setPosition(.35);
        }

    }
}
