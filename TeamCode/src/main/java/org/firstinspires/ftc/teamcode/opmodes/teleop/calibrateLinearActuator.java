package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp
public class calibrateLinearActuator extends OpMode {

    private DcMotor linearActuator;
    private double joystick;

    @Override
    public void init(){

        linearActuator = hardwareMap.dcMotor.get("linearActuator");

        linearActuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearActuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }


    @Override
    public void loop(){

        joystick = gamepad2.right_stick_y;

        linearActuator.setPower(joystick);

        telemetry.addLine("Motor ticks: " + linearActuator.getCurrentPosition());

        if (gamepad2.a){
            linearActuator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            linearActuator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

    }

}
