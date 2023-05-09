package omg.simple.account.core;

import org.junit.jupiter.api.ClassDescriptor;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrdererContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;

public class SpringBootTestClassOrderer implements ClassOrderer {
    //https://www.wimdeblauwe.com/blog/2021/02/12/junit-5-test-class-orderer-for-spring-boot/
    @Override
    public void orderClasses(ClassOrdererContext classOrdererContext) {
        classOrdererContext.getClassDescriptors().sort(Comparator.comparingInt(SpringBootTestClassOrderer::getOrder));
    }

    private static int getOrder(ClassDescriptor classDescriptor) {
        if (classDescriptor.findAnnotation(SpringBootTest.class).isPresent())
            return 5;
        if (classDescriptor.findAnnotation(WebMvcTest.class).isPresent())
            return 4;
        if (classDescriptor.findAnnotation(ExtendWith.class).isPresent())
            return 3;
        if (classDescriptor.findAnnotation(DataJpaTest.class).isPresent())
            return 2;

        return 1;
    }
}
