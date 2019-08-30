package appersonal.development.com.appersonaltrainer.Controller;

import android.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.commons.lang3.ArrayUtils;

public class Compress {
	private static final int TAMANHO_ARRAY = 1024;

	public static String compacta(String expressao) {
		int tamanhoArray;
		int quant;
		int total = 0;
		Deflater compresser = new Deflater();

		 byte[] aSerCompactado = expressao.getBytes();
		 byte[] output = null;
		 byte[] temp;

		 compresser.setInput(aSerCompactado);
		 compresser.finish();
		 while (true) {
		     temp = new byte[TAMANHO_ARRAY];
		     quant = compresser.deflate(temp);
		     if (quant == 0) {
		         break;
		     }
		     total += quant;
		     output = ArrayUtils.addAll(output, temp);
		 }
		 compresser.end();

		 if ((total * 8) % 24 != 0) {
		     tamanhoArray = (((total * 8) / 24) + 1) * 4;
		 } else {
		     tamanhoArray = ((total * 8) / 24) * 4;
		 }


		 return new String(Base64.encode(output, 0)).substring(0, tamanhoArray);

	}

	public static String descompacta(String expressao) {
		byte[] result;
		int faltante;
		StringBuilder sb = new StringBuilder();
		byte[] aSerDescompactado = Base64.decode(expressao.getBytes(),0);

		 String s;
		 try {
		     Inflater decompresser = new Inflater();
		     decompresser.setInput(aSerDescompactado);

		     while (true) {
		         result = new byte[TAMANHO_ARRAY];
		         faltante = decompresser.inflate(result);
		         if (faltante == 0) {
		             break;
		         }

		         s = new String(result);
		         sb.append(s);
		     }
		     decompresser.end();
		 } catch (DataFormatException ex) {
		     ex.printStackTrace();
		 }
		 return sb.toString().trim();
		}
}