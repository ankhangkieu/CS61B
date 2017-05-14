import org.junit.Test;

public class HorribleSteve {
	@Test
	public void testFlisk () {
		int i = 0;
		for (int j = 0; i < 500; ++i, ++j) {
			if (!Flik.isSameNumber(i, j)) {
          break; // break exits the for loop!
			}
		}
		System.out.println("i is " + i);
	}
} 