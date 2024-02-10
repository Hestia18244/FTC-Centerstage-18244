package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class dumperTester extends OpMode {

    private Servo dumper;

    @Override
    public void init(){
        dumper = hardwareMap.servo.get("dumper");
    }

    @Override
    public void loop(){

        if (gamepad1.a){
            dumper.setPosition(0.85); // this releases the pixel
        }
        if (gamepad1.b){
            dumper.setPosition(1); // this is the starting position
        }

    }

}
