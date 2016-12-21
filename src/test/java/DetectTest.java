import com.ncdu.circles.CircleDet;
import com.ncdu.circles.CirclesPictureGenerator;
import com.ncdu.circles.CirclesPictureGenerator.Circle;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DetectTest {
    @Test
    public void test1() throws Exception{
        CircleDet detecter = new CircleDet();
        List<Circle> gencircles = CirclesPictureGenerator.generateAndDraw();
        List<Circle> list= detecter.detect("CIRCLES.gif");
        System.out.println(list);
        assertEquals(gencircles.size(), list.size());
        assertTrue(gencircles.containsAll(list));
    }



}
