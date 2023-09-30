package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class testBlinkin extends OpMode {

    /**
     * Used to control our led driver
     */
    private RevBlinkinLedDriver lights;

    /**
     * variable used to track our joystick movements
     */
    private double strafe;


    public void init(){

        // Hardware mapping and default setting of led driver
        lights = hardwareMap.get(RevBlinkinLedDriver.class, "lights");

        // Default pattern of led driver
        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);


    }

    public void loop(){

        // Default pattern of led driver
        lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);

        // Variable to track joystick movements
        strafe = gamepad1.left_stick_x;

        // If we are going left
        if (strafe < -0.1){

            // Set the pattern of one side to be whatever this is
            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_LIGHT_CHASE);


        }
        // If we are going right
        else if (strafe > 0.1){
            // Set the pattern of the other side to be equal to this
            lights.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP2_LIGHT_CHASE);
        }

    }

}
