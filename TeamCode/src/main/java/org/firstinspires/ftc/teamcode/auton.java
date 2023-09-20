package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous
public class auton extends LinearOpMode {


    DcMotor frontRight;
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor backRight;

    public void runOpMode(){


        frontRight = hardwareMap.dcMotor.get("frontRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        frontRight.setDirection((DcMotorSimple.Direction.REVERSE));
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();



    }

    public void move(double forward, double strafe, double turn, int delay){

        frontRight.setPower(forward + strafe + turn);
        frontLeft.setPower(forward - strafe - turn);
        backRight.setPower(forward - strafe + turn);
        backLeft.setPower(forward + strafe - turn);

        sleep(delay);

    }

}
