package es.upm.dit.adsw.teleconote;

/**
 * @author cif
 * @version 20130426
 */
public class Cifrador {

	private final int[] clave;
	private int kpos;

	/**
	 * Conjunto de caracteres que admite el cifrador como entrada (y como
	 * salida).
	 */
	public static final char[] ALFABETO = {
        // ----// 0 1 2 3 4 5 6 7 8 9
        /* 0x */'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        /* 1x */'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        /* 2x */'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
        /* 3x */'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        /* 4x */'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
        /* 5x */'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
        /* 6x */'8', '9', '\u00C1', '\u00C9', '\u00CD', '\u00D3', '\u00DA', '\u00DC', '\u00D1', '\u00E1',
        /* 7x */'\u00E9', '\u00ED', '\u00F3', '\u00FA', '\u00FC', '\u00F1', ' ', '_', '!', '?',            /* 8x */'(', ')', '[', ']', '<', '>', '+', '-', '*', '/',
        /* 9x */'=', '@', '.', ',', ';', ':'
        // ----// 0 1 2 3 4 5 6 7 8 9
	}; 
	
	 public Cifrador(String clave) {
	        if (clave == null || clave.length() == 0)
	            throw new IllegalArgumentException(clave);
	        this.clave = new int[clave.length()];
	        for (int i = 0; i < clave.length(); i++)
	            this.clave[i] = codifica(clave.charAt(i));
	        kpos = 0;
	    }
	
	  private int codifica(char c) {
	        for (int p = 0; p < ALFABETO.length; p++)
	            if (ALFABETO[p] == c)
	                return p;
	        throw new IllegalArgumentException("caracter no cifrable: '" + c + "'");
	    }

	    private char descodifica(int x) {
	        if (x < 0 || x > ALFABETO.length - 1)
	            throw new IllegalArgumentException("código inexistente: " + x);
	        return ALFABETO[x];
	    }

	    /**
	     * Dado un texto en claro, produce el texto cifrado.
	     *
	     * @param texto texto en claro.
	     * @return criptograma (texto cifrado).
	     * @throws IllegalArgumentException si algún carácter no pertenece al alfabeto.
	     */
	    public String cifra(String texto) {
	        StringBuilder buffer = new StringBuilder(texto.length());
	        for (int i = 0; i < texto.length(); i++) {
	            char c2 = cifraCaracter(texto.charAt(i));
	            buffer.append(c2);
	        }
	        return buffer.toString();
	    }

		public  char cifraCaracter(char c1) {			
			int x1 = codifica(c1);
			int x2 = (x1 + clave[kpos]) % ALFABETO.length;
			clave[kpos] = x1;
			kpos = (kpos + 1) % clave.length;
			char c2 = descodifica(x2);
			return c2;
		}
	    	    
	    /**
	     * Dado un texto cifrado, reproduce el texto original.
	     *
	     * @param criptograma texto cifrado.
	     * @return texto original (en claro).
	     * @throws IllegalArgumentException si algún carácter no pertenece al alfabeto.
	     */
	    public  String descifra(String criptograma) {
	        StringBuilder buffer = new StringBuilder(criptograma.length());
	        for (int i = 0; i < criptograma.length(); i++) {
	            char c1 = criptograma.charAt(i);
	            char c2 = descifraCaracter(c1);
	            buffer.append(c2);
	        }
	        return buffer.toString();
	    }

		public  char descifraCaracter(char c1) {
			int x1 = codifica(c1);
			int k = clave[kpos];
			while (x1 - k < 0)
			    x1 += ALFABETO.length;
			int x2 = (x1 - k) % ALFABETO.length;
			clave[kpos] = x2;
			kpos = (kpos + 1) % clave.length;
			char c2 = descodifica(x2);
			return c2;
		}

}
