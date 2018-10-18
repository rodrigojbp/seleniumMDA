package br.gov.mda.sead;

public class Util {

	public enum UF {

		AC(12), AL(27), AM(13), AP(16), BA(29), CE(23), DF(53), ES(32), GO(52), MA(21), MG(31), MS(50), MT(51), PA(15),
		PB(25), PE(26), PI(22), PR(41), RJ(33), RN(24), RO(11), RR(14), RS(43), SC(42), SE(28), SP(35), TO(17);

		private final int valor;

		UF(int valorOpcao) {
			valor = valorOpcao;
		}

		public int getValor() {
			return valor;
		}
	}
	
	public static void main(String[] args) {
		
		Util.UF[] ufs = Util.UF.values();
		for (int i = 0; i < ufs.length; i++) {
			System.out.println(ufs[i].getValor());
		}
		System.out.println(	UF.valueOf("AC").getValor());
	}

}
