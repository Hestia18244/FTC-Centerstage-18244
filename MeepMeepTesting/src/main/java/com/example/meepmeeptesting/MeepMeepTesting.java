package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 18.05)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(12, -60, Math.toRadians(90)))

                                // Drive forwards toward the tile with all of the spikes
                                .forward(26)

                                // Turn towards the spike on the right
                                .turn(Math.toRadians(80))

                                .waitSeconds(1)

                                // Open the servo above the right spike
                                .addTemporalMarker(4,()->{
                                })
                                .waitSeconds(1)

                                .back(4)

                                // Turn to face forwards again
                                .turn(Math.toRadians(-170))

                                .addTemporalMarker(6.2, ()->{
                                    //marker for arm to move
                                })

                                .strafeTo(new Vector2d(47, -25))

                                .addTemporalMarker(9,()->{
                                    //marker for servo
                                })

                                .waitSeconds(1)

                                // Back away from the tile with all of the pixels
                                .back(22)

                                // Strafe to the exact parking coordinates
                                .strafeTo(new Vector2d(50, -57))




                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}