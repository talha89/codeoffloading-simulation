package ee.ut.simulation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utils {

    public static double getSize(Object o) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length * 0.00000008; // return size in Megabits
    }

}
