package cpu.nbcdu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class NBCDUAddTest {

	private final NBCDU nbcdu = new NBCDU();
	private DataType src;
	private DataType dest;
	private DataType result;

	@Test
	public void AddTest1() {
		src = new DataType("11000000000000000000000010011000");
		dest = new DataType("11000000000000000000000001111001");
		result = nbcdu.add(src, dest);
		assertEquals("11000000000000000000000101110111", result.toString());
	}

	@Test
	public void AddTest100() {
		Random random = new Random();
		int max=100;
		int min=-100;
		for(int i =0;i<50;i++){
			int num1 = random.nextInt(max-min)-max;
			int num2 = random.nextInt(max-min)-max;
			src = new DataType(Transformer.decimalToNBCD(String.valueOf(num1)));
			dest = new DataType(Transformer.decimalToNBCD(String.valueOf(num2)));
			System.out.println(num1+" "+Transformer.decimalToNBCD(String.valueOf(num1)));
			System.out.println(num2+" "+Transformer.decimalToNBCD(String.valueOf(num2)));
			result = nbcdu.add(src, dest);
			assertEquals(Transformer.decimalToNBCD(String.valueOf(num2+num1)), result.toString());
		}

	}

}
