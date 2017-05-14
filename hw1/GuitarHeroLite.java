import synthesizer.GuitarString;

import java.util.ArrayList;

/** A client that uses the synthesizer package to replicate a plucked guitar string sound */
public class GuitarHeroLite {
    private static final double CONCERT_A = 440.0;
    private static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    public static void testing() {
        String keyboard = new String("q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ");
        ArrayList<GuitarString> piano = new ArrayList<>(keyboard.length());
        for (int i = 0; i < keyboard.length(); i++) {
            piano.add(new GuitarString(440 * Math.pow(2, (i - 24) / 12)));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                System.out.println(key);
                if (keyboard.indexOf(key) == -1) {
                    continue;
                }
                piano.get(keyboard.indexOf(key)).pluck();
            }

            /* compute the superposition of samples */
            double sample = 0;
            for (GuitarString x: piano) {
                sample += x.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString x: piano) {
                x.tic();
            }
        }
    }

    public static void main(String[] args) {
        testing();
        /* create two guitar strings, for concert A and C */
        GuitarString stringA = new GuitarString(CONCERT_A);
        GuitarString stringC = new GuitarString(CONCERT_C);

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'a') {
                    stringA.pluck();
                } else if (key == 'c') {
                    stringC.pluck();
                }
            }

        /* compute the superposition of samples */
            double sample = stringA.sample() + stringC.sample();

        /* play the sample on standard audio */
            StdAudio.play(sample);

        /* advance the simulation of each guitar string by one step */
            stringA.tic();
            stringC.tic();
        }
    }
}

