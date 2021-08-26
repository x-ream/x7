package x7;

import io.xream.x7.base.util.ClassFileReader;

import java.util.Set;

/**
 * @Author Sim
 */
public class BeanReaderTest {

    public static void main(String[] args) {
        Set<Class<?>> set = ClassFileReader.getClasses("x7.demo");
    }
}
